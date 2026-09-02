import * as cdk from 'aws-cdk-lib';
import { Duration, RemovalPolicy, Stack, StackProps } from 'aws-cdk-lib';
import * as ec2 from 'aws-cdk-lib/aws-ec2';
import * as ecs from 'aws-cdk-lib/aws-ecs';
import * as elbv2 from 'aws-cdk-lib/aws-elasticloadbalancingv2';
import * as iam from 'aws-cdk-lib/aws-iam';
import * as logs from 'aws-cdk-lib/aws-logs';
import * as secretsmanager from 'aws-cdk-lib/aws-secretsmanager';
import * as servicediscovery from 'aws-cdk-lib/aws-servicediscovery';
import { Construct } from 'constructs';
import { AppsEnvironment } from './environment';

export interface CarsGaloreAppsStackProps extends StackProps {
  configuration: AppsEnvironment;
}

type AppService = {
  id: string;
  imageName: string;
  imageTag: string;
  discoveryName: string;
  environment: Record<string, string>;
  useDatabase?: boolean;
  cognitoAdmin?: boolean;
};

export class CarsGaloreAppsStack extends Stack {
  public constructor(scope: Construct, id: string, props: CarsGaloreAppsStackProps) {
    super(scope, id, props);
    const config = props.configuration;

    cdk.Tags.of(this).add('project', 'cars-galore');
    cdk.Tags.of(this).add('architecture', 'fargate');

    const vpc = ec2.Vpc.fromVpcAttributes(this, 'ImportedVpc', {
      vpcId: config.vpcId,
      availabilityZones: config.privateSubnetAzs,
      publicSubnetIds: config.publicSubnetIds,
      privateSubnetIds: config.privateSubnetIds
    });
    const publicSubnets = config.publicSubnetIds.map((subnetId, index) => ec2.Subnet.fromSubnetAttributes(this, `PublicSubnet${index + 1}`, {
      subnetId,
      availabilityZone: config.publicSubnetAzs[index]
    }));
    const privateSubnets = config.privateSubnetIds.map((subnetId, index) => ec2.Subnet.fromSubnetAttributes(this, `PrivateSubnet${index + 1}`, {
      subnetId,
      availabilityZone: config.privateSubnetAzs[index]
    }));
    const sharedApplicationSecurityGroup = ec2.SecurityGroup.fromSecurityGroupId(
      this,
      'SharedApplicationSecurityGroup',
      config.applicationSecurityGroupId,
      { mutable: false }
    );
    const taskSecurityGroup = new ec2.SecurityGroup(this, 'TaskSecurityGroup', {
      vpc,
      description: 'Private traffic between Cars Galore Fargate tasks',
      allowAllOutbound: true
    });
    taskSecurityGroup.addIngressRule(taskSecurityGroup, ec2.Port.allTcp(), 'Internal Cars Galore traffic');

    const cluster = new ecs.Cluster(this, 'Cluster', { vpc });
    const namespace = new servicediscovery.PrivateDnsNamespace(this, 'Namespace', {
      vpc,
      name: config.cloudMapNamespace
    });
    const logGroup = new logs.LogGroup(this, 'LogGroup', {
      logGroupName: `/cars-galore/${config.stackName}`,
      retention: logs.RetentionDays.ONE_WEEK,
      removalPolicy: RemovalPolicy.DESTROY
    });
    const ghcrCredentials = new secretsmanager.Secret(this, 'GhcrCredentials', {
      description: 'Private GHCR credentials. Populate with npm run bootstrap-ghcr before starting tasks.',
      generateSecretString: {
        secretStringTemplate: JSON.stringify({ username: 'replace-me' }),
        generateStringKey: 'password',
        excludePunctuation: true
      },
      removalPolicy: RemovalPolicy.DESTROY
    });
    const rdsCredentials = secretsmanager.Secret.fromSecretCompleteArn(this, 'RdsCredentials', config.rdsSecretArn);

    const albSecurityGroup = new ec2.SecurityGroup(this, 'AlbSecurityGroup', {
      vpc,
      description: 'Public HTTP access to the Cars Galore orchestrator',
      allowAllOutbound: true
    });
    albSecurityGroup.addIngressRule(ec2.Peer.anyIpv4(), ec2.Port.tcp(80), 'Public orchestrator HTTP access');
    const alb = new elbv2.ApplicationLoadBalancer(this, 'Alb', {
      vpc,
      internetFacing: true,
      securityGroup: albSecurityGroup,
      vpcSubnets: { subnets: publicSubnets }
    });
    taskSecurityGroup.addIngressRule(albSecurityGroup, ec2.Port.tcp(8080), 'ALB access to orchestrator');

    const kafka = this.addKafka({
      cluster,
      namespace,
      logGroup,
      ghcrCredentials,
      taskSecurityGroups: [sharedApplicationSecurityGroup, taskSecurityGroup],
      privateSubnets,
      config
    });

    const commonDatabaseSecrets = {
      POSTGRES_USER: ecs.Secret.fromSecretsManager(rdsCredentials, 'username'),
      POSTGRES_PASSWORD: ecs.Secret.fromSecretsManager(rdsCredentials, 'password')
    };
    const databaseUrl = (database: string) => `jdbc:postgresql://${config.rdsEndpoint}:${config.rdsPort}/${database}?sslmode=require`;
    const appServices: AppService[] = [
      {
        id: 'Compradores', imageName: 'sub5-sub3-compradores', imageTag: config.imageTags.compradores, discoveryName: 'compradores', useDatabase: true, cognitoAdmin: true,
        environment: {
          POSTGRES_URL: databaseUrl('compradores'),
          AWS_REGION: config.region,
          COGNITO_USER_POOL_ID: config.cognitoUserPoolId,
          COGNITO_COMPRADOR_GROUP: 'comprador'
        }
      },
      {
        id: 'Principal', imageName: 'sub5-sub3-principal', imageTag: config.imageTags.principal, discoveryName: 'principal', useDatabase: true,
        environment: { POSTGRES_URL: databaseUrl('principal') }
      },
      {
        id: 'Financeiro', imageName: 'sub5-sub3-financeiro', imageTag: config.imageTags.financeiro, discoveryName: 'financeiro', useDatabase: true,
        environment: {
          POSTGRES_URL: databaseUrl('financeiro'),
          KAFKA_SERVER: `kafka.${config.cloudMapNamespace}:9092`
        }
      },
      {
        id: 'Orquestrador', imageName: 'sub5-sub3-orquestrador', imageTag: config.imageTags.orquestrador, discoveryName: 'orquestrador',
        environment: {
          COMPRADORES_SERVICE_URL: `http://compradores.${config.cloudMapNamespace}:8080`,
          VEICULOS_SERVICE_URL: `http://principal.${config.cloudMapNamespace}:8080`,
          FINANCEIRO_SERVICE_URL: `http://financeiro.${config.cloudMapNamespace}:8080`,
          COGNITO_ISSUER_URL: config.cognitoIssuerUrl,
          KAFKA_SERVER: `kafka.${config.cloudMapNamespace}:9092`
        }
      }
    ];

    let orchestrator: ecs.FargateService | undefined;
    for (const service of appServices) {
      const ecsService = this.addApplication({
        cluster,
        namespace,
        logGroup,
        ghcrCredentials,
        taskSecurityGroups: [sharedApplicationSecurityGroup, taskSecurityGroup],
        privateSubnets,
        config,
        rdsSecrets: service.useDatabase ? commonDatabaseSecrets : undefined,
        service
      });
      if (service.id === 'Orquestrador') {
        orchestrator = ecsService;
      }
    }

    if (!orchestrator) {
      throw new Error('Orchestrator service was not created.');
    }
    const listener = alb.addListener('HttpListener', { port: 80, open: true });
    listener.addTargets('OrchestratorTargets', {
      port: 8080,
      protocol: elbv2.ApplicationProtocol.HTTP,
      targets: [orchestrator],
      healthCheck: {
        path: '/q/health/ready',
        healthyHttpCodes: '200',
        interval: Duration.seconds(30),
        timeout: Duration.seconds(10)
      }
    });

    new cdk.CfnOutput(this, 'OrquestradorUrl', { value: `http://${alb.loadBalancerDnsName}` });
    new cdk.CfnOutput(this, 'AlbDnsName', { value: alb.loadBalancerDnsName });
    new cdk.CfnOutput(this, 'ClusterName', { value: cluster.clusterName });
    new cdk.CfnOutput(this, 'CloudMapNamespace', { value: config.cloudMapNamespace });
    new cdk.CfnOutput(this, 'GhcrSecretArn', { value: ghcrCredentials.secretArn });
    new cdk.CfnOutput(this, 'KafkaServiceName', { value: kafka.serviceName });
  }

  private addApplication(input: {
    cluster: ecs.Cluster;
    namespace: servicediscovery.PrivateDnsNamespace;
    logGroup: logs.LogGroup;
    ghcrCredentials: secretsmanager.Secret;
    taskSecurityGroups: ec2.ISecurityGroup[];
    privateSubnets: ec2.ISubnet[];
    config: AppsEnvironment;
    rdsSecrets?: Record<string, ecs.Secret>;
    service: AppService;
  }): ecs.FargateService {
    const { config, service } = input;
    const executionRole = new iam.Role(this, `${service.id}ExecutionRole`, {
      assumedBy: new iam.ServicePrincipal('ecs-tasks.amazonaws.com'),
      managedPolicies: [iam.ManagedPolicy.fromAwsManagedPolicyName('service-role/AmazonECSTaskExecutionRolePolicy')]
    });
    input.ghcrCredentials.grantRead(executionRole);
    const taskRole = new iam.Role(this, `${service.id}TaskRole`, {
      assumedBy: new iam.ServicePrincipal('ecs-tasks.amazonaws.com')
    });
    if (service.cognitoAdmin) {
      taskRole.addToPrincipalPolicy(new iam.PolicyStatement({
        actions: [
          'cognito-idp:AdminAddUserToGroup',
          'cognito-idp:AdminCreateUser',
          'cognito-idp:AdminDeleteUser',
          'cognito-idp:AdminSetUserPassword'
        ],
        resources: [`arn:aws:cognito-idp:${config.region}:${config.accountId}:userpool/${config.cognitoUserPoolId}`]
      }));
    }
    const taskDefinition = new ecs.FargateTaskDefinition(this, `${service.id}Task`, {
      cpu: 512,
      memoryLimitMiB: 1024,
      executionRole,
      taskRole
    });
    const container = taskDefinition.addContainer('Container', {
      image: ecs.ContainerImage.fromRegistry(`ghcr.io/${config.ghcrOwner}/${service.imageName}:${service.imageTag}`, {
        credentials: input.ghcrCredentials
      }),
      logging: ecs.LogDrivers.awsLogs({ logGroup: input.logGroup, streamPrefix: service.discoveryName }),
      environment: { ...service.environment, QUARKUS_HTTP_PORT: '8080' },
      secrets: input.rdsSecrets
    });
    container.addPortMappings({ containerPort: 8080 });
    return new ecs.FargateService(this, `${service.id}Service`, {
      cluster: input.cluster,
      taskDefinition,
      desiredCount: config.runApps ? 1 : 0,
      assignPublicIp: false,
      securityGroups: input.taskSecurityGroups,
      vpcSubnets: { subnets: input.privateSubnets },
      cloudMapOptions: { cloudMapNamespace: input.namespace, name: service.discoveryName },
      circuitBreaker: { rollback: true },
      minHealthyPercent: 0,
      maxHealthyPercent: 100
    });
  }

  private addKafka(input: {
    cluster: ecs.Cluster;
    namespace: servicediscovery.PrivateDnsNamespace;
    logGroup: logs.LogGroup;
    ghcrCredentials: secretsmanager.Secret;
    taskSecurityGroups: ec2.ISecurityGroup[];
    privateSubnets: ec2.ISubnet[];
    config: AppsEnvironment;
  }): ecs.FargateService {
    const { config } = input;
    const executionRole = new iam.Role(this, 'KafkaExecutionRole', {
      assumedBy: new iam.ServicePrincipal('ecs-tasks.amazonaws.com'),
      managedPolicies: [iam.ManagedPolicy.fromAwsManagedPolicyName('service-role/AmazonECSTaskExecutionRolePolicy')]
    });
    const taskDefinition = new ecs.FargateTaskDefinition(this, 'KafkaTask', {
      cpu: 1024,
      memoryLimitMiB: 2048,
      executionRole
    });
    const container = taskDefinition.addContainer('Container', {
      image: ecs.ContainerImage.fromRegistry(config.kafkaImage),
      logging: ecs.LogDrivers.awsLogs({ logGroup: input.logGroup, streamPrefix: 'kafka' }),
      environment: {
        KAFKA_NODE_ID: config.kafkaNodeId,
        KAFKA_PROCESS_ROLES: 'broker,controller',
        KAFKA_LISTENERS: 'PLAINTEXT://:9092,CONTROLLER://:9093',
        KAFKA_ADVERTISED_LISTENERS: `PLAINTEXT://kafka.${config.cloudMapNamespace}:9092`,
        KAFKA_CONTROLLER_LISTENER_NAMES: 'CONTROLLER',
        KAFKA_LISTENER_SECURITY_PROTOCOL_MAP: 'CONTROLLER:PLAINTEXT,PLAINTEXT:PLAINTEXT',
        KAFKA_CONTROLLER_QUORUM_VOTERS: `${config.kafkaNodeId}@kafka.${config.cloudMapNamespace}:9093`,
        KAFKA_OFFSETS_TOPIC_REPLICATION_FACTOR: '1',
        KAFKA_TRANSACTION_STATE_LOG_REPLICATION_FACTOR: '1',
        KAFKA_TRANSACTION_STATE_LOG_MIN_ISR: '1',
        KAFKA_GROUP_INITIAL_REBALANCE_DELAY_MS: '0',
        KAFKA_NUM_PARTITIONS: config.kafkaNumPartitions,
        KAFKA_AUTO_CREATE_TOPICS_ENABLE: config.kafkaAutoCreateTopics
      }
    });
    container.addPortMappings({ containerPort: 9092 }, { containerPort: 9093 });
    return new ecs.FargateService(this, 'KafkaService', {
      cluster: input.cluster,
      taskDefinition,
      desiredCount: config.runApps ? 1 : 0,
      assignPublicIp: false,
      securityGroups: input.taskSecurityGroups,
      vpcSubnets: { subnets: input.privateSubnets },
      cloudMapOptions: { cloudMapNamespace: input.namespace, name: 'kafka' },
      circuitBreaker: { rollback: true },
      minHealthyPercent: 0,
      maxHealthyPercent: 100
    });
  }
}
