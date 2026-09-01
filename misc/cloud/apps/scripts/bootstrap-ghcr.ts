import { execFileSync } from 'node:child_process';
import { appsEnvironment, loadDotEnv } from '../lib/environment';

loadDotEnv();
const username = process.env.GHCR_USERNAME?.trim();
const password = process.env.GHCR_TOKEN?.trim();
if (!username || !password) {
  throw new Error('GHCR_USERNAME and GHCR_TOKEN must be set in misc/cloud/apps/.env.');
}

const config = appsEnvironment();
const query = "Stacks[0].Outputs[?OutputKey=='GhcrSecretArn'].OutputValue";
const secretArn = execFileSync('aws', [
  'cloudformation', 'describe-stacks',
  '--stack-name', config.stackName,
  '--region', config.region,
  '--query', query,
  '--output', 'text'
], { encoding: 'utf8' }).trim();

if (!secretArn || secretArn === 'None') {
  throw new Error(`Stack ${config.stackName} does not expose GhcrSecretArn. Deploy it once with RUN_APPS=false first.`);
}

execFileSync('aws', [
  'secretsmanager', 'put-secret-value',
  '--secret-id', secretArn,
  '--secret-string', JSON.stringify({ username, password }),
  '--region', config.region
], { stdio: 'inherit' });
