#!/usr/bin/env node
import * as cdk from 'aws-cdk-lib';
import { appsEnvironment } from '../lib/environment';
import { CarsGaloreAppsStack } from '../lib/cars-galore-apps-stack';

const configuration = appsEnvironment();
const app = new cdk.App();

new CarsGaloreAppsStack(app, 'CarsGaloreApps', {
  stackName: configuration.stackName,
  env: { account: configuration.accountId, region: configuration.region },
  configuration,
  description: 'Cars Galore Fargate applications and Kafka Native'
});
