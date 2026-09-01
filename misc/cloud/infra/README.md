# Cars Galore cloud infrastructure

This CDK application creates the shared AWS infrastructure for the Cars Galore services. It is intentionally a small development setup; the later application phase will deploy the GHCR images to Fargate using the VPC and security-group outputs from this stack.

## Resources

- A two-AZ VPC with one NAT Gateway.
- A public, single-AZ RDS PostgreSQL `db.t4g.micro` instance restricted to the deployment CIDR.
- The `compradores`, `principal`, and `financeiro` databases, created during deployment.
- A Cognito user pool, OAuth domain, resource server, confidential client-credentials app client, and `comprador`, `funcionario`, and `financeiro` RBAC groups.
- Secrets Manager secrets for RDS and Cognito service credentials.

RDS is public in this development setup so that it can be accessed from the local workstation. Access is restricted to the IPv4 CIDR supplied through `CDK_ALLOWED_CIDR` or the `allowedCidr` CDK context. Kafka will be added later with the Fargate application deployment.

## Prerequisites

Install Node.js and configure AWS credentials for the account that should own the stack. The default region is `sa-east-1`; override it with `CDK_DEFAULT_REGION` if needed.

Set the public IPv4 CIDR allowed to connect before synthesizing or deploying:

```bash
export CDK_ALLOWED_CIDR="$(curl -4 -s https://checkip.amazonaws.com)/32"
```

Install dependencies and validate the generated CloudFormation:

```bash
npm install
npm run build
npm run synth
```

Bootstrap the account once, then deploy:

```bash
npx cdk bootstrap aws://$(aws sts get-caller-identity --query Account --output text)/sa-east-1
npm run deploy
```

To use a globally available Cognito prefix, pass a different value during synthesis/deployment:

```bash
npx cdk deploy -c domainPrefix=my-unique-cars-galore-prefix
```

After deployment, inspect the stack outputs:

```bash
aws cloudformation describe-stacks \
  --stack-name CarsGalore-infra-dev \
  --query 'Stacks[0].Outputs'
```

## Getting an OAuth access token

The Cognito service client uses the OAuth2 client-credentials grant. Retrieve the client credentials from the `CognitoCredentialsSecretArn` output and request a token from the `CognitoTokenEndpoint` output. The requested scope must be one of the custom scopes configured on the client, for example `cars-galore-api/read`.

## Delete everything

This development stack uses destroy removal policies for its stateful resources. When the data is no longer needed, delete the complete stack with:

```bash
npm run destroy
```

Deletion can take several minutes for RDS. Do not run this command if the stored data must be retained.
