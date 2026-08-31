import * as cdk from 'aws-cdk-lib';
import { Duration, RemovalPolicy, Stack, StackProps, Tags } from 'aws-cdk-lib';
import * as ec2 from 'aws-cdk-lib/aws-ec2';
import * as ecs from 'aws-cdk-lib/aws-ecs';
import * as elbv2 from 'aws-cdk-lib/aws-elasticloadbalancingv2';
import * as efs from 'aws-cdk-lib/aws-efs';
import * as logs from 'aws-cdk-lib/aws-logs';
import * as iam from 'aws-cdk-lib/aws-iam';
import * as secretsmanager from 'aws-cdk-lib/aws-secretsmanager';
import * as servicediscovery from 'aws-cdk-lib/aws-servicediscovery';
import { Construct } from 'constructs';

export interface CarsGaloreStackProps extends StackProps {
  runApps: boolean;
  runStateful: boolean;
  imageTag: string;
  ghcrOwner: string;
}

type ServiceDefinition = {
  id: string;
  image: string;
  discoveryName: string;
  port: number;
  environment: Record<string, string>;
  secrets?: Record<string, ecs.Secret>;
  healthPath: string;
};

type StateConfiguration = {
  appDbSecret: secretsmanager.Secret;
  compradoresJdbcUrl: string;
  principalJdbcUrl: string;
  financeiroJdbcUrl: string;
  kafkaBootstrapServers: string;
};

export class CarsGaloreStack extends Stack {
  private readonly props: CarsGaloreStackProps;
  private readonly vpc: ec2.Vpc;
  private readonly cluster: ecs.Cluster;
  private readonly taskSecurityGroup: ec2.SecurityGroup;
  private readonly namespace: servicediscovery.INamespace;
  private readonly ghcrSecret: secretsmanager.Secret;
  private readonly logGroup: logs.LogGroup;
  private readonly imageTag: string;

  public constructor(scope: Construct, id: string, props: CarsGaloreStackProps) {
    super(scope, id, props);
    this.props = props;
    this.imageTag = props.imageTag;

    Tags.of(this).add('project', 'cars-galore');
    Tags.of(this).add('architecture', 'fargate');

    this.vpc = new ec2.Vpc(this, 'Vpc', {
      maxAzs: 2,
      natGateways: 1,
      subnetConfiguration: [
        { name: 'public', subnetType: ec2.SubnetType.PUBLIC },
        { name: 'private', subnetType: ec2.SubnetType.PRIVATE_WITH_EGRESS }
      ]
    });

    this.cluster = new ecs.Cluster(this, 'Cluster', {
      vpc: this.vpc,
      containerInsightsV2: ecs.ContainerInsights.ENABLED
    });
    this.namespace = this.cluster.addDefaultCloudMapNamespace({ name: 'sub3.local' });
    this.logGroup = new logs.LogGroup(this, 'LogGroup', {
      logGroupName: '/cars-galore/fargate',
      retention: logs.RetentionDays.ONE_WEEK,
      removalPolicy: RemovalPolicy.DESTROY
    });

    this.ghcrSecret = new secretsmanager.Secret(this, 'GhcrCredentials', {
      description: 'Set to {"username":"...","password":"..."} before starting ECS application tasks.',
      generateSecretString: { secretStringTemplate: JSON.stringify({ username: 'replace-me' }), generateStringKey: 'password', excludePunctuation: true },
      removalPolicy: RemovalPolicy.DESTROY
    });

    const albSecurityGroup = new ec2.SecurityGroup(this, 'AlbSecurityGroup', { vpc: this.vpc });
    albSecurityGroup.addIngressRule(ec2.Peer.anyIpv4(), ec2.Port.tcpRange(8080, 8084));
    const alb = new elbv2.ApplicationLoadBalancer(this, 'Alb', {
      vpc: this.vpc,
      internetFacing: true,
      securityGroup: albSecurityGroup
    });

    this.taskSecurityGroup = new ec2.SecurityGroup(this, 'TaskSecurityGroup', { vpc: this.vpc });
    this.taskSecurityGroup.addIngressRule(this.taskSecurityGroup, ec2.Port.allTcp(), 'Internal service traffic');
    this.taskSecurityGroup.addIngressRule(albSecurityGroup, ec2.Port.tcpRange(8080, 8084), 'ALB traffic');

    const state = this.createFargateState();

    const appDbSecret = state.appDbSecret;
    const keycloakAdminSecret = new secretsmanager.Secret(this, 'KeycloakAdminSecret', {
      generateSecretString: {
        secretStringTemplate: JSON.stringify({ username: 'sub3' }),
        generateStringKey: 'password',
        excludePunctuation: true
      },
      removalPolicy: RemovalPolicy.DESTROY
    });
    const principalClientSecret = new secretsmanager.Secret(this, 'PrincipalClientSecret', {
      generateSecretString: { excludePunctuation: true },
      removalPolicy: RemovalPolicy.DESTROY
    });

    const keycloak = this.addService({
      id: 'Keycloak',
      image: this.image('sub3-keycloak'),
      discoveryName: 'keycloak',
      port: 8080,
      healthPath: '/realms/sub3',
      environment: {
        KC_HTTP_ENABLED: 'true',
        KC_HOSTNAME_STRICT: 'false',
        KC_HEALTH_ENABLED: 'true',
        KC_BOOTSTRAP_ADMIN_USERNAME: 'sub3'
      },
      secrets: {
        KC_BOOTSTRAP_ADMIN_PASSWORD: ecs.Secret.fromSecretsManager(keycloakAdminSecret, 'password'),
        KEYCLOAK_PRINCIPAL_CLIENT_SECRET: ecs.Secret.fromSecretsManager(principalClientSecret)
      }
    }, props.runApps);
    this.attachListener(alb, keycloak, 8080);

    const appServices: ServiceDefinition[] = [
      {
        id: 'Compradores', image: this.image('sub3-compradores'), discoveryName: 'compradores-service', port: 8081,
        healthPath: '/listar',
        environment: {
          KEYCLOAK_INSTANCE: 'http://keycloak.sub3.local:8080/', KEYCLOAK_ADMIN_USER: 'sub3',
          KEYCLOAK_ADMIN_AUTH_REALM: 'master', KEYCLOAK_ADMIN_REALM: 'sub3', KEYCLOAK_ADMIN_CLIENT_ID: 'admin-cli',
          POSTGRES_USER: 'sub3', POSTGRES_URL: state.compradoresJdbcUrl
        },
        secrets: {
          KEYCLOAK_ADMIN_PASSWORD: ecs.Secret.fromSecretsManager(keycloakAdminSecret, 'password'),
          POSTGRES_PASSWORD: ecs.Secret.fromSecretsManager(appDbSecret, 'password')
        }
      },
      {
        id: 'Principal', image: this.image('sub3-principal'), discoveryName: 'principal-service', port: 8082,
        healthPath: '/listar-venda',
        environment: {
          QUARKUS_REST_CLIENT_COMPOUND_DEBUG_URL: 'http://compradores-service.sub3.local:8081/',
          POSTGRES_USER: 'sub3', POSTGRES_URL: state.principalJdbcUrl
        },
        secrets: { POSTGRES_PASSWORD: ecs.Secret.fromSecretsManager(appDbSecret, 'password') }
      },
      {
        id: 'Orquestrador', image: this.image('sub3-orquestrador'), discoveryName: 'orquestrador-service', port: 8083,
        healthPath: '/veiculos/listar-venda',
        environment: {
          COMPRADORES_SERVICE_URL: 'http://compradores-service.sub3.local:8081',
          VEICULOS_SERVICE_URL: 'http://principal-service.sub3.local:8082',
          FINANCEIRO_SERVICE_URL: 'http://financeiro-service.sub3.local:8084',
          KEYCLOAK_REALM_URL: `http://${alb.loadBalancerDnsName}:8080/realms/sub3`, KEYCLOAK_CLIENT_ID: 'principal',
          KAFKA_BOOTSTRAP_SERVERS: state.kafkaBootstrapServers
        },
        secrets: { KEYCLOAK_CLIENT_SECRET: ecs.Secret.fromSecretsManager(principalClientSecret) }
      },
      {
        id: 'Financeiro', image: this.image('sub3-financeiro'), discoveryName: 'financeiro-service', port: 8084,
        healthPath: '/internal/pagamentos',
        environment: {
          POSTGRES_USER: 'sub3', POSTGRES_URL: state.financeiroJdbcUrl,
          KAFKA_BOOTSTRAP_SERVERS: state.kafkaBootstrapServers
        },
        secrets: { POSTGRES_PASSWORD: ecs.Secret.fromSecretsManager(appDbSecret, 'password') }
      }
    ];

    for (const definition of appServices) {
      const service = this.addService(definition, props.runApps);
      this.attachListener(alb, service, definition.port);
    }

    new cdk.CfnOutput(this, 'AlbDnsName', { value: alb.loadBalancerDnsName });
    new cdk.CfnOutput(this, 'ClusterName', { value: this.cluster.clusterName });
    new cdk.CfnOutput(this, 'PrivateSubnetIds', { value: cdk.Fn.join(',', this.vpc.privateSubnets.map(subnet => subnet.subnetId)) });
    new cdk.CfnOutput(this, 'TaskSecurityGroupId', { value: this.taskSecurityGroup.securityGroupId });
    new cdk.CfnOutput(this, 'KeycloakUrl', { value: `http://${alb.loadBalancerDnsName}:8080` });
    new cdk.CfnOutput(this, 'CompradoresUrl', { value: `http://${alb.loadBalancerDnsName}:8081` });
    new cdk.CfnOutput(this, 'PrincipalUrl', { value: `http://${alb.loadBalancerDnsName}:8082` });
    new cdk.CfnOutput(this, 'OrquestradorUrl', { value: `http://${alb.loadBalancerDnsName}:8083` });
    new cdk.CfnOutput(this, 'FinanceiroUrl', { value: `http://${alb.loadBalancerDnsName}:8084` });
    new cdk.CfnOutput(this, 'GhcrSecretArn', { value: this.ghcrSecret.secretArn });
  }

  private image(name: string): string {
    return `ghcr.io/${this.props.ghcrOwner.toLowerCase()}/${name}:${this.imageTag}`;
  }

  private addService(definition: ServiceDefinition, desiredCount: boolean): ecs.FargateService {
    const executionRole = new iam.Role(this, `${definition.id}ExecutionRole`, {
      assumedBy: new iam.ServicePrincipal('ecs-tasks.amazonaws.com'),
      managedPolicies: [iam.ManagedPolicy.fromAwsManagedPolicyName('service-role/AmazonECSTaskExecutionRolePolicy')]
    });
    this.ghcrSecret.grantRead(executionRole);
    const task = new ecs.FargateTaskDefinition(this, `${definition.id}Task`, {
      cpu: 512, memoryLimitMiB: 1024, executionRole
    });
    const container = task.addContainer('Container', {
      image: ecs.ContainerImage.fromRegistry(definition.image, { credentials: this.ghcrSecret }),
      logging: ecs.LogDrivers.awsLogs({ streamPrefix: definition.id.toLowerCase(), logGroup: this.logGroup }),
      environment: definition.environment,
      secrets: definition.secrets,
    });
    container.addPortMappings({ containerPort: definition.port });
    const service = new ecs.FargateService(this, `${definition.id}Service`, {
      cluster: this.cluster,
      taskDefinition: task,
      desiredCount: desiredCount ? 1 : 0,
      assignPublicIp: false,
      securityGroups: [this.taskSecurityGroup],
      vpcSubnets: { subnetType: ec2.SubnetType.PRIVATE_WITH_EGRESS },
      cloudMapOptions: { cloudMapNamespace: this.namespace, name: definition.discoveryName },
      circuitBreaker: { rollback: true },
      minHealthyPercent: 0,
      maxHealthyPercent: 100
    });
    return service;
  }

  private attachListener(alb: elbv2.ApplicationLoadBalancer, service: ecs.FargateService, port: number): void {
    const listener = alb.addListener(`Listener${port}`, { port, protocol: elbv2.ApplicationProtocol.HTTP, open: true });
    listener.addTargets(`Targets${port}`, {
      protocol: elbv2.ApplicationProtocol.HTTP,
      port,
      targets: [service],
      healthCheck: {
        path: port === 8080 ? '/realms/sub3' : '/',
        healthyHttpCodes: '200-499',
        interval: Duration.seconds(30),
        timeout: Duration.seconds(10)
      }
    });
  }

  private createFargateState(): StateConfiguration {
    const appDbSecret = new secretsmanager.Secret(this, 'ApplicationDbSecret', {
      secretStringValue: cdk.SecretValue.unsafePlainText(JSON.stringify({ username: 'sub3', password: 'sub3' })), removalPolicy: RemovalPolicy.DESTROY
    });
    const postgres = this.addStatefulService('Postgres', 'sub3-postgres', 5432, {
      POSTGRES_DB: 'postgres', POSTGRES_USER: 'postgres', POSTGRES_PASSWORD: 'postgres', POSTGRES_APP_PASSWORD: 'sub3'
    }, this.props.runStateful);
    const kafka = this.addStatefulService('Kafka', 'sub3-kafka', 9092, {
      KAFKA_NODE_ID: '1', KAFKA_PROCESS_ROLES: 'broker,controller', KAFKA_LISTENERS: 'PLAINTEXT://:9092,CONTROLLER://:9093',
      KAFKA_ADVERTISED_LISTENERS: 'PLAINTEXT://sub3-kafka.sub3.local:9092', KAFKA_CONTROLLER_LISTENER_NAMES: 'CONTROLLER',
      KAFKA_LISTENER_SECURITY_PROTOCOL_MAP: 'CONTROLLER:PLAINTEXT,PLAINTEXT:PLAINTEXT', KAFKA_CONTROLLER_QUORUM_VOTERS: '1@sub3-kafka.sub3.local:9093',
      KAFKA_OFFSETS_TOPIC_REPLICATION_FACTOR: '1', KAFKA_TRANSACTION_STATE_LOG_REPLICATION_FACTOR: '1',
      KAFKA_TRANSACTION_STATE_LOG_MIN_ISR: '1', KAFKA_GROUP_INITIAL_REBALANCE_DELAY_MS: '0', KAFKA_NUM_PARTITIONS: '3',
      KAFKA_AUTO_CREATE_TOPICS_ENABLE: 'true'
    }, this.props.runStateful);
    postgres.node.addDependency(this.namespace); kafka.node.addDependency(this.namespace);
    return {
      appDbSecret,
      compradoresJdbcUrl: 'jdbc:postgresql://sub3-postgres.sub3.local:5432/compradores',
      principalJdbcUrl: 'jdbc:postgresql://sub3-postgres.sub3.local:5432/principal',
      financeiroJdbcUrl: 'jdbc:postgresql://sub3-postgres.sub3.local:5432/financeiro', kafkaBootstrapServers: 'sub3-kafka.sub3.local:9092'
    };
  }

  private addStatefulService(id: string, imageName: string, port: number, environment: Record<string, string>, start: boolean): ecs.FargateService {
    const sg = this.taskSecurityGroup;
    const fileSystem = new efs.FileSystem(this, `${id}FileSystem`, { vpc: this.vpc, removalPolicy: RemovalPolicy.DESTROY, securityGroup: sg, encrypted: true });
    const accessPoint = fileSystem.addAccessPoint(`${id}AccessPoint`, { createAcl: { ownerGid: '0', ownerUid: '0', permissions: '0755' }, path: `/${id.toLowerCase()}` });
    const executionRole = new iam.Role(this, `${id}ExecutionRole`, { assumedBy: new iam.ServicePrincipal('ecs-tasks.amazonaws.com'), managedPolicies: [iam.ManagedPolicy.fromAwsManagedPolicyName('service-role/AmazonECSTaskExecutionRolePolicy')] });
    this.ghcrSecret.grantRead(executionRole);
    const task = new ecs.FargateTaskDefinition(this, `${id}Task`, { cpu: 1024, memoryLimitMiB: 2048, executionRole });
    task.addVolume({ name: 'data', efsVolumeConfiguration: { fileSystemId: fileSystem.fileSystemId, transitEncryption: 'ENABLED', authorizationConfig: { accessPointId: accessPoint.accessPointId, iam: 'DISABLED' } } });
    const container = task.addContainer('Container', {
      image: ecs.ContainerImage.fromRegistry(this.image(imageName), { credentials: this.ghcrSecret }),
      environment,
      healthCheck: id === 'Postgres' ? {
        command: ['CMD-SHELL', 'pg_isready -U postgres -d postgres'],
        interval: Duration.seconds(30), timeout: Duration.seconds(5), retries: 5, startPeriod: Duration.seconds(30)
      } : undefined,
      logging: ecs.LogDrivers.awsLogs({ streamPrefix: id.toLowerCase(), logGroup: this.logGroup })
    });
    container.addPortMappings({ containerPort: port });
    container.addMountPoints({ sourceVolume: 'data', containerPath: id === 'Kafka' ? '/var/lib/kafka/data' : '/var/lib/postgresql/data', readOnly: false });
    return new ecs.FargateService(this, `${id}Service`, { cluster: this.cluster, taskDefinition: task, desiredCount: start ? 1 : 0, assignPublicIp: false, securityGroups: [sg], vpcSubnets: { subnetType: ec2.SubnetType.PRIVATE_WITH_EGRESS }, cloudMapOptions: { cloudMapNamespace: this.namespace, name: id === 'Postgres' ? 'sub3-postgres' : 'sub3-kafka' }, circuitBreaker: { rollback: true }, minHealthyPercent: 0, maxHealthyPercent: 100 });
  }
}
