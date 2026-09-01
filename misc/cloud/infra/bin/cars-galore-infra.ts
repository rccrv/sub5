#!/usr/bin/env node
import * as cdk from 'aws-cdk-lib';
import { CarsGaloreInfraStack } from '../lib/cars-galore-infra-stack';

const app = new cdk.App();
const environmentName = String(app.node.tryGetContext('environmentName') ?? 'dev');
const domainPrefix = String(app.node.tryGetContext('domainPrefix') ?? `cars-galore-${cdk.Aws.ACCOUNT_ID}`);
// Keep synthesis and destroy safe even when the deployment shell no longer has its old public IP.
// Deployments should override this with the current workstation CIDR.
const allowedCidr = process.env.CDK_ALLOWED_CIDR
  ?? app.node.tryGetContext('allowedCidr')
  ?? '127.0.0.1/32';

new CarsGaloreInfraStack(app, 'CarsGaloreInfra', {
  stackName: `CarsGalore-infra-${environmentName}`,
  env: {
    account: process.env.CDK_DEFAULT_ACCOUNT,
    region: process.env.CDK_DEFAULT_REGION ?? 'sa-east-1'
  },
  environmentName,
  domainPrefix,
  allowedCidr: String(allowedCidr),
  description: 'Cars Galore shared AWS infrastructure'
});
