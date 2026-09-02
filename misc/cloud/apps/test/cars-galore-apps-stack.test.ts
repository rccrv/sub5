import { test } from 'node:test';
import assert from 'node:assert/strict';
import * as cdk from 'aws-cdk-lib';
import { Match, Template } from 'aws-cdk-lib/assertions';
import { CarsGaloreAppsStack } from '../lib/cars-galore-apps-stack';
import { AppsEnvironment } from '../lib/environment';

const configuration: AppsEnvironment = {
  accountId: '123456789012',
  region: 'sa-east-1',
  stackName: 'CarsGalore-apps-test',
  ghcrOwner: 'example',
  imageTags: {
    compradores: 'sha-compradores',
    principal: 'sha-principal',
    orquestrador: 'sha-orquestrador',
    financeiro: 'sha-financeiro'
  },
  vpcId: 'vpc-0123456789abcdef0',
  publicSubnetIds: ['subnet-0123456789abcdef2', 'subnet-0123456789abcdef3'],
  publicSubnetAzs: ['sa-east-1a', 'sa-east-1c'],
  privateSubnetIds: ['subnet-0123456789abcdef0', 'subnet-0123456789abcdef1'],
  privateSubnetAzs: ['sa-east-1a', 'sa-east-1c'],
  applicationSecurityGroupId: 'sg-0123456789abcdef0',
  rdsEndpoint: 'database.example.amazonaws.com',
  rdsPort: '5432',
  rdsSecretArn: 'arn:aws:secretsmanager:sa-east-1:123456789012:secret:rds-ABC123',
  cognitoUserPoolId: 'sa-east-1_example',
  cognitoIssuerUrl: 'https://cognito-idp.sa-east-1.amazonaws.com/sa-east-1_example',
  cloudMapNamespace: 'sub3.local',
  kafkaImage: 'apache/kafka-native:4.1.2',
  kafkaNodeId: '1',
  kafkaNumPartitions: '3',
  kafkaAutoCreateTopics: 'true',
  runApps: true
};

function template() {
  const app = new cdk.App();
  const stack = new CarsGaloreAppsStack(app, 'CarsGaloreApps', {
    env: { account: configuration.accountId, region: configuration.region },
    configuration
  });
  return Template.fromStack(stack);
}

test('deploys Kafka and four private Fargate applications', () => {
  const synthesized = template();

  synthesized.resourceCountIs('AWS::ECS::Service', 5);
  synthesized.resourceCountIs('AWS::ECS::TaskDefinition', 5);
  synthesized.resourceCountIs('AWS::EFS::FileSystem', 0);
  synthesized.resourceCountIs('AWS::RDS::DBInstance', 0);
  synthesized.resourceCountIs('AWS::Cognito::UserPool', 0);
  synthesized.hasResourceProperties('AWS::ECS::TaskDefinition', {
    ContainerDefinitions: Match.arrayWith([Match.objectLike({
      Image: 'apache/kafka-native:4.1.2',
      Environment: Match.arrayWith([Match.objectLike({ Name: 'KAFKA_ADVERTISED_LISTENERS', Value: 'PLAINTEXT://kafka.sub3.local:9092' })])
    })])
  });
});

test('uses the configured immutable image tag for each application', () => {
  const synthesized = template();
  for (const image of [
    'ghcr.io/example/sub5-sub3-compradores:sha-compradores',
    'ghcr.io/example/sub5-sub3-principal:sha-principal',
    'ghcr.io/example/sub5-sub3-financeiro:sha-financeiro',
    'ghcr.io/example/sub5-sub3-orquestrador:sha-orquestrador'
  ]) {
    synthesized.hasResourceProperties('AWS::ECS::TaskDefinition', {
      ContainerDefinitions: Match.arrayWith([Match.objectLike({ Image: image })])
    });
  }
});

test('exposes only the orchestrator and wires runtime configuration', () => {
  const synthesized = template();

  synthesized.resourceCountIs('AWS::ElasticLoadBalancingV2::Listener', 1);
  synthesized.hasResourceProperties('AWS::ElasticLoadBalancingV2::Listener', { Port: 80 });
  synthesized.hasResourceProperties('AWS::ECS::TaskDefinition', {
    ContainerDefinitions: Match.arrayWith([Match.objectLike({
      Environment: Match.arrayWith([
        Match.objectLike({ Name: 'COGNITO_ISSUER_URL', Value: configuration.cognitoIssuerUrl }),
        Match.objectLike({ Name: 'KAFKA_SERVER', Value: 'kafka.sub3.local:9092' })
      ])
    })])
  });
  const outputs = synthesized.toJSON().Outputs;
  assert.ok(outputs.OrquestradorUrl);
  assert.ok(outputs.GhcrSecretArn);
});

test('grants Cognito administration only to the Compradores task role', () => {
  const synthesized = template();

  synthesized.hasResourceProperties('AWS::IAM::Policy', {
    PolicyDocument: {
      Statement: Match.arrayWith([Match.objectLike({
        Action: Match.arrayWith(['cognito-idp:AdminCreateUser', 'cognito-idp:AdminSetUserPassword']),
        Resource: 'arn:aws:cognito-idp:sa-east-1:123456789012:userpool/sa-east-1_example'
      })])
    }
  });
});
