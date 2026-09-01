import { test } from 'node:test';
import assert from 'node:assert/strict';
import * as cdk from 'aws-cdk-lib';
import { Match, Template } from 'aws-cdk-lib/assertions';
import { CarsGaloreInfraStack } from '../lib/cars-galore-infra-stack';

function infrastructureTemplate() {
  const app = new cdk.App();
  const stack = new CarsGaloreInfraStack(app, 'CarsGaloreInfra', {
    env: { account: '123456789012', region: 'sa-east-1' },
    environmentName: 'test',
    domainPrefix: 'cars-galore-test-123456789012',
    allowedCidr: '203.0.113.10/32'
  });
  return Template.fromStack(stack);
}

test('creates the shared stateful services and network', () => {
  const template = infrastructureTemplate();

  template.resourceCountIs('AWS::EC2::VPC', 1);
  template.resourceCountIs('AWS::EC2::NatGateway', 1);
  template.hasResourceProperties('AWS::RDS::DBInstance', {
    DBInstanceClass: 'db.t4g.micro',
    DBName: 'postgres',
    MultiAZ: false,
    PubliclyAccessible: true,
    DeletionProtection: false
  });
  template.hasResourceProperties('AWS::EC2::SecurityGroup', {
    SecurityGroupIngress: Match.arrayWith([Match.objectLike({
      CidrIp: '203.0.113.10/32',
      FromPort: 5432,
      ToPort: 5432
    })])
  });
});

test('creates Cognito client-credentials authentication', () => {
  const template = infrastructureTemplate();

  template.resourceCountIs('AWS::Cognito::UserPool', 1);
  template.resourceCountIs('AWS::Cognito::UserPoolDomain', 1);
  template.hasResourceProperties('AWS::Cognito::UserPoolClient', {
    AllowedOAuthFlows: ['client_credentials'],
    GenerateSecret: true,
    AllowedOAuthFlowsUserPoolClient: true
  });
  template.hasResourceProperties('AWS::Cognito::UserPool', {
    Policies: {
      PasswordPolicy: {
        MinimumLength: 11,
        RequireNumbers: false
      }
    }
  });
  template.resourceCountIs('AWS::Cognito::UserPoolResourceServer', 1);
  template.resourceCountIs('AWS::CloudFormation::CustomResource', 2);
  template.hasResourceProperties('AWS::CloudFormation::CustomResource', {
    users: '[{"username":"73985377359","group":"comprador"},{"username":"financeiro1","group":"financeiro"},{"username":"funcionario","group":"funcionario"}]'
  });
});

test('creates the database bootstrap custom resource and outputs', () => {
  const template = infrastructureTemplate();

  template.hasResourceProperties('AWS::CloudFormation::CustomResource', {
    databases: 'compradores,principal,financeiro'
  });
  const outputs = template.toJSON().Outputs;
  assert.ok(outputs.PostgresEndpoint);
  assert.ok(outputs.CognitoTokenEndpoint);
  assert.ok(outputs.CognitoUserClientId);
  assert.ok(outputs.CognitoCredentialsSecretArn);
});
