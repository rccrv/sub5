#!/usr/bin/env node
import * as cdk from 'aws-cdk-lib';
import { CarsGaloreStack } from '../lib/cars-galore-stack';

const app = new cdk.App();

new CarsGaloreStack(app, 'CarsGalore-fargate', {
  runApps: String(app.node.tryGetContext('runApps') ?? 'false') === 'true',
  runStateful: String(app.node.tryGetContext('runStateful') ?? 'false') === 'true',
  imageTag: String(app.node.tryGetContext('imageTag') ?? 'latest'),
  ghcrOwner: String(app.node.tryGetContext('ghcrOwner') ?? 'OWNER'),
  env: {
    account: process.env.CDK_DEFAULT_ACCOUNT,
    region: process.env.CDK_DEFAULT_REGION ?? 'sa-east-1'
  },
  description: 'Cars Galore all-Fargate deployment'
});
