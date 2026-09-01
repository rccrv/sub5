import * as fs from 'node:fs';
import * as path from 'node:path';

export type AppsEnvironment = {
  accountId: string;
  region: string;
  stackName: string;
  ghcrOwner: string;
  imageTag: string;
  vpcId: string;
  publicSubnetIds: string[];
  publicSubnetAzs: string[];
  privateSubnetIds: string[];
  privateSubnetAzs: string[];
  applicationSecurityGroupId: string;
  rdsEndpoint: string;
  rdsPort: string;
  rdsSecretArn: string;
  cognitoUserPoolId: string;
  cognitoIssuerUrl: string;
  cloudMapNamespace: string;
  kafkaImage: string;
  kafkaNodeId: string;
  kafkaNumPartitions: string;
  kafkaAutoCreateTopics: string;
  runApps: boolean;
};

export function loadDotEnv(filePath = path.join(process.cwd(), '.env')): void {
  if (!fs.existsSync(filePath)) {
    return;
  }

  for (const rawLine of fs.readFileSync(filePath, 'utf8').split(/\r?\n/)) {
    const line = rawLine.trim();
    if (!line || line.startsWith('#')) {
      continue;
    }
    const separator = line.indexOf('=');
    if (separator < 1) {
      throw new Error(`Invalid dotenv entry: ${rawLine}`);
    }
    const key = line.slice(0, separator).trim();
    const value = line.slice(separator + 1).trim().replace(/^['"]|['"]$/g, '');
    process.env[key] ??= value;
  }
}

function required(name: string): string {
  const value = process.env[name]?.trim();
  if (!value) {
    throw new Error(`Missing required environment variable ${name}. Copy .env.example to .env and fill it in.`);
  }
  return value;
}

function list(name: string): string[] {
  const values = required(name).split(',').map(value => value.trim()).filter(Boolean);
  if (values.length === 0) {
    throw new Error(`${name} must contain at least one value.`);
  }
  return values;
}

function boolean(name: string, defaultValue: boolean): boolean {
  const value = process.env[name];
  if (value === undefined) {
    return defaultValue;
  }
  if (value === 'true') {
    return true;
  }
  if (value === 'false') {
    return false;
  }
  throw new Error(`${name} must be true or false.`);
}

export function appsEnvironment(): AppsEnvironment {
  loadDotEnv();
  const publicSubnetIds = list('INFRA_PUBLIC_SUBNET_IDS');
  const publicSubnetAzs = list('INFRA_PUBLIC_SUBNET_AZS');
  const privateSubnetIds = list('INFRA_PRIVATE_SUBNET_IDS');
  const privateSubnetAzs = list('INFRA_PRIVATE_SUBNET_AZS');
  if (publicSubnetIds.length !== publicSubnetAzs.length) {
    throw new Error('INFRA_PUBLIC_SUBNET_IDS and INFRA_PUBLIC_SUBNET_AZS must have the same number of entries.');
  }
  if (privateSubnetIds.length !== privateSubnetAzs.length) {
    throw new Error('INFRA_PRIVATE_SUBNET_IDS and INFRA_PRIVATE_SUBNET_AZS must have the same number of entries.');
  }

  return {
    accountId: required('AWS_ACCOUNT_ID'),
    region: required('AWS_REGION'),
    stackName: required('APPS_STACK_NAME'),
    ghcrOwner: required('GHCR_OWNER').toLowerCase(),
    imageTag: required('IMAGE_TAG'),
    vpcId: required('INFRA_VPC_ID'),
    publicSubnetIds,
    publicSubnetAzs,
    privateSubnetIds,
    privateSubnetAzs,
    applicationSecurityGroupId: required('INFRA_APPLICATION_SECURITY_GROUP_ID'),
    rdsEndpoint: required('RDS_ENDPOINT'),
    rdsPort: required('RDS_PORT'),
    rdsSecretArn: required('RDS_SECRET_ARN'),
    cognitoUserPoolId: required('COGNITO_USER_POOL_ID'),
    cognitoIssuerUrl: required('COGNITO_ISSUER_URL'),
    cloudMapNamespace: required('CLOUD_MAP_NAMESPACE'),
    kafkaImage: required('KAFKA_IMAGE'),
    kafkaNodeId: required('KAFKA_NODE_ID'),
    kafkaNumPartitions: required('KAFKA_NUM_PARTITIONS'),
    kafkaAutoCreateTopics: required('KAFKA_AUTO_CREATE_TOPICS_ENABLE'),
    runApps: boolean('RUN_APPS', false)
  };
}
