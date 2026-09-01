import * as path from 'node:path';
import * as cdk from 'aws-cdk-lib';
import { Duration, RemovalPolicy, Stack, StackProps, Tags } from 'aws-cdk-lib';
import * as cognito from 'aws-cdk-lib/aws-cognito';
import * as ec2 from 'aws-cdk-lib/aws-ec2';
import * as iam from 'aws-cdk-lib/aws-iam';
import * as lambda from 'aws-cdk-lib/aws-lambda';
import * as lambdaNodejs from 'aws-cdk-lib/aws-lambda-nodejs';
import * as rds from 'aws-cdk-lib/aws-rds';
import * as secretsmanager from 'aws-cdk-lib/aws-secretsmanager';
import * as customResources from 'aws-cdk-lib/custom-resources';
import { Construct } from 'constructs';

export interface CarsGaloreInfraStackProps extends StackProps {
  environmentName: string;
  domainPrefix: string;
  allowedCidr: string;
}

export class CarsGaloreInfraStack extends Stack {
  public constructor(scope: Construct, id: string, props: CarsGaloreInfraStackProps) {
    super(scope, id, props);

    Tags.of(this).add('project', 'cars-galore');
    Tags.of(this).add('environment', props.environmentName);
    Tags.of(this).add('managed-by', 'cdk');

    const vpc = new ec2.Vpc(this, 'Vpc', {
      maxAzs: 2,
      natGateways: 1,
      subnetConfiguration: [
        { name: 'public', subnetType: ec2.SubnetType.PUBLIC },
        { name: 'private', subnetType: ec2.SubnetType.PRIVATE_WITH_EGRESS }
      ]
    });

    const applicationSecurityGroup = new ec2.SecurityGroup(this, 'ApplicationSecurityGroup', {
      vpc,
      description: 'Security group for the future Fargate application services',
      allowAllOutbound: true
    });
    const databaseSecurityGroup = new ec2.SecurityGroup(this, 'DatabaseSecurityGroup', {
      vpc,
      description: 'Security group for the RDS PostgreSQL instance',
      allowAllOutbound: true
    });
    const databaseBootstrapSecurityGroup = new ec2.SecurityGroup(this, 'DatabaseBootstrapSecurityGroup', {
      vpc,
      description: 'Security group for the database initialization Lambda',
      allowAllOutbound: true
    });

    databaseSecurityGroup.addIngressRule(applicationSecurityGroup, ec2.Port.tcp(5432), 'Application PostgreSQL access');
    databaseSecurityGroup.addIngressRule(databaseBootstrapSecurityGroup, ec2.Port.tcp(5432), 'Database bootstrap access');
    databaseSecurityGroup.addIngressRule(
      ec2.Peer.ipv4(props.allowedCidr),
      ec2.Port.tcp(5432),
      'Temporary development workstation PostgreSQL access'
    );
    const databaseCredentials = new secretsmanager.Secret(this, 'DatabaseCredentials', {
      description: 'Cars Galore RDS PostgreSQL credentials',
      generateSecretString: {
        secretStringTemplate: JSON.stringify({ username: 'sub3' }),
        generateStringKey: 'password',
        excludePunctuation: true
      },
      removalPolicy: RemovalPolicy.DESTROY
    });

    const postgres = new rds.DatabaseInstance(this, 'Postgres', {
      engine: rds.DatabaseInstanceEngine.postgres({ version: rds.PostgresEngineVersion.VER_16 }),
      instanceType: ec2.InstanceType.of(ec2.InstanceClass.T4G, ec2.InstanceSize.MICRO),
      credentials: rds.Credentials.fromSecret(databaseCredentials),
      databaseName: 'postgres',
      vpc,
      vpcSubnets: { subnetType: ec2.SubnetType.PUBLIC },
      securityGroups: [databaseSecurityGroup],
      multiAz: false,
      publiclyAccessible: true,
      allocatedStorage: 20,
      storageType: rds.StorageType.GP3,
      storageEncrypted: true,
      backupRetention: Duration.days(0),
      deletionProtection: false,
      deleteAutomatedBackups: true,
      removalPolicy: RemovalPolicy.DESTROY
    });

    const databaseBootstrap = new lambdaNodejs.NodejsFunction(this, 'DatabaseBootstrapFunction', {
      runtime: lambda.Runtime.NODEJS_22_X,
      entry: path.join(__dirname, 'database-bootstrap-handler.ts'),
      handler: 'handler',
      timeout: Duration.minutes(5),
      memorySize: 256,
      vpc,
      vpcSubnets: { subnetType: ec2.SubnetType.PRIVATE_WITH_EGRESS },
      securityGroups: [databaseBootstrapSecurityGroup],
      environment: {
        NODE_EXTRA_CA_CERTS: '/var/runtime/ca-cert.pem'
      },
      bundling: {
        minify: true,
        sourceMap: true,
        externalModules: ['@aws-sdk/*']
      }
    });
    databaseCredentials.grantRead(databaseBootstrap);

    const databaseBootstrapProvider = new customResources.Provider(this, 'DatabaseBootstrapProvider', {
      onEventHandler: databaseBootstrap
    });
    const initializedDatabases = new cdk.CustomResource(this, 'InitializedDatabases', {
      serviceToken: databaseBootstrapProvider.serviceToken,
      properties: {
        host: postgres.dbInstanceEndpointAddress,
        port: postgres.dbInstanceEndpointPort,
        secretArn: databaseCredentials.secretArn,
        databases: 'compradores,principal,financeiro'
      }
    });
    initializedDatabases.node.addDependency(postgres);

    const userPool = new cognito.UserPool(this, 'UserPool', {
      userPoolName: `cars-galore-${props.environmentName}`,
      selfSignUpEnabled: false,
      signInAliases: { username: true, email: true },
      passwordPolicy: {
        minLength: 11,
        requireLowercase: false,
        requireUppercase: false,
        requireDigits: false,
        requireSymbols: false
      },
      accountRecovery: cognito.AccountRecovery.EMAIL_ONLY,
      removalPolicy: RemovalPolicy.DESTROY
    });

    const compradorGroup = new cognito.CfnUserPoolGroup(this, 'CompradorGroup', {
      groupName: 'comprador',
      description: 'Application role for buyer users',
      userPoolId: userPool.userPoolId
    });
    const funcionarioGroup = new cognito.CfnUserPoolGroup(this, 'FuncionarioGroup', {
      groupName: 'funcionario',
      description: 'Application role for employee users',
      userPoolId: userPool.userPoolId
    });
    const financeiroGroup = new cognito.CfnUserPoolGroup(this, 'FinanceiroGroup', {
      groupName: 'financeiro',
      description: 'Application role for finance users',
      userPoolId: userPool.userPoolId
    });

    const cognitoUserBootstrap = new lambdaNodejs.NodejsFunction(this, 'CognitoUserBootstrapFunction', {
      runtime: lambda.Runtime.NODEJS_22_X,
      entry: path.join(__dirname, 'cognito-user-bootstrap-handler.ts'),
      handler: 'handler',
      timeout: Duration.minutes(5),
      memorySize: 256,
      bundling: {
        minify: true,
        sourceMap: true,
        externalModules: ['@aws-sdk/*']
      }
    });
    cognitoUserBootstrap.addToRolePolicy(new iam.PolicyStatement({
      actions: [
        'cognito-idp:AdminAddUserToGroup',
        'cognito-idp:AdminCreateUser',
        'cognito-idp:AdminDeleteUser',
        'cognito-idp:AdminSetUserPassword'
      ],
      resources: [userPool.userPoolArn]
    }));
    const cognitoUserBootstrapProvider = new customResources.Provider(this, 'CognitoUserBootstrapProvider', {
      onEventHandler: cognitoUserBootstrap
    });
    const seededUsers = new cdk.CustomResource(this, 'SeededCognitoUsers', {
      serviceToken: cognitoUserBootstrapProvider.serviceToken,
      properties: {
        userPoolId: userPool.userPoolId,
        users: JSON.stringify([
          { username: '73985377359', group: 'comprador' },
          { username: 'financeiro1', group: 'financeiro' },
          { username: 'funcionario', group: 'funcionario' }
        ])
      }
    });
    seededUsers.node.addDependency(compradorGroup, funcionarioGroup, financeiroGroup);

    const readScope = new cognito.ResourceServerScope({
      scopeName: 'read',
      scopeDescription: 'Read Cars Galore API resources'
    });
    const writeScope = new cognito.ResourceServerScope({
      scopeName: 'write',
      scopeDescription: 'Change Cars Galore API resources'
    });
    const resourceServer = userPool.addResourceServer('ApiResourceServer', {
      identifier: 'cars-galore-api',
      scopes: [readScope, writeScope]
    });
    const serviceClient = userPool.addClient('ServiceClient', {
      userPoolClientName: `cars-galore-${props.environmentName}-service`,
      generateSecret: true,
      oAuth: {
        flows: { clientCredentials: true },
        scopes: [
          cognito.OAuthScope.resourceServer(resourceServer, readScope),
          cognito.OAuthScope.resourceServer(resourceServer, writeScope)
        ]
      }
    });
    const userClient = userPool.addClient('UserClient', {
      userPoolClientName: `cars-galore-${props.environmentName}-users`,
      generateSecret: false,
      authFlows: {
        userPassword: true,
        userSrp: true
      }
    });
    const cognitoDomain = userPool.addDomain('OAuthDomain', {
      cognitoDomain: { domainPrefix: props.domainPrefix }
    });
    const cognitoCredentials = new secretsmanager.Secret(this, 'CognitoServiceCredentials', {
      description: 'Cars Galore Cognito service-client credentials',
      secretObjectValue: {
        userPoolId: cdk.SecretValue.unsafePlainText(userPool.userPoolId),
        clientId: cdk.SecretValue.unsafePlainText(serviceClient.userPoolClientId),
        clientSecret: serviceClient.userPoolClientSecret
      },
      removalPolicy: RemovalPolicy.DESTROY
    });

    new cdk.CfnOutput(this, 'VpcId', { value: vpc.vpcId });
    new cdk.CfnOutput(this, 'PrivateSubnetIds', {
      value: cdk.Fn.join(',', vpc.privateSubnets.map((subnet) => subnet.subnetId))
    });
    new cdk.CfnOutput(this, 'ApplicationSecurityGroupId', { value: applicationSecurityGroup.securityGroupId });
    new cdk.CfnOutput(this, 'PostgresEndpoint', { value: postgres.dbInstanceEndpointAddress });
    new cdk.CfnOutput(this, 'PostgresPort', { value: postgres.dbInstanceEndpointPort });
    new cdk.CfnOutput(this, 'PostgresCredentialsSecretArn', { value: databaseCredentials.secretArn });
    new cdk.CfnOutput(this, 'CognitoUserPoolId', { value: userPool.userPoolId });
    new cdk.CfnOutput(this, 'CognitoIssuerUrl', {
      value: `https://cognito-idp.${this.region}.amazonaws.com/${userPool.userPoolId}`
    });
    new cdk.CfnOutput(this, 'CognitoDiscoveryUrl', {
      value: `https://cognito-idp.${this.region}.amazonaws.com/${userPool.userPoolId}/.well-known/openid-configuration`
    });
    new cdk.CfnOutput(this, 'CognitoTokenEndpoint', {
      value: `https://${cognitoDomain.domainName}.auth.${this.region}.amazoncognito.com/oauth2/token`
    });
    new cdk.CfnOutput(this, 'CognitoServiceClientId', { value: serviceClient.userPoolClientId });
    new cdk.CfnOutput(this, 'CognitoUserClientId', { value: userClient.userPoolClientId });
    new cdk.CfnOutput(this, 'CognitoCredentialsSecretArn', { value: cognitoCredentials.secretArn });
  }
}
