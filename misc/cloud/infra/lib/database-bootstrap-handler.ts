import { GetSecretValueCommand, SecretsManagerClient } from '@aws-sdk/client-secrets-manager';
import { Client } from 'pg';

type BootstrapProperties = {
  host: string;
  port: string;
  secretArn: string;
  databases: string;
};

const secretsManager = new SecretsManagerClient({});

export async function handler(event: {
  RequestType: 'Create' | 'Update' | 'Delete';
  PhysicalResourceId?: string;
  ResourceProperties: BootstrapProperties;
}) {
  const physicalResourceId = event.PhysicalResourceId ?? `cars-galore-database-bootstrap-${event.ResourceProperties.host}`;

  if (event.RequestType === 'Delete') {
    return { PhysicalResourceId: physicalResourceId };
  }

  const databaseNames = event.ResourceProperties.databases.split(',').map((name) => name.trim());
  if (databaseNames.some((name) => !/^[a-z][a-z0-9_]*$/.test(name))) {
    throw new Error('Database names must contain only lowercase letters, numbers, and underscores.');
  }

  const secretResponse = await secretsManager.send(new GetSecretValueCommand({
    SecretId: event.ResourceProperties.secretArn
  }));
  if (!secretResponse.SecretString) {
    throw new Error('The database credentials secret does not contain a string value.');
  }

  const credentials = JSON.parse(secretResponse.SecretString) as { username: string; password: string };
  const client = new Client({
    host: event.ResourceProperties.host,
    port: Number(event.ResourceProperties.port),
    database: 'postgres',
    user: credentials.username,
    password: credentials.password,
    ssl: true
  });

  await client.connect();
  try {
    for (const databaseName of databaseNames) {
      const result = await client.query('select 1 from pg_database where datname = $1', [databaseName]);
      if (result.rowCount === 0) {
        await client.query(`create database "${databaseName}"`);
      }
    }
  } finally {
    await client.end();
  }

  return { PhysicalResourceId: physicalResourceId };
}
