# Cars Galore Fargate applications

This CDK project deploys Kafka Native and the four Quarkus services into the VPC, RDS instance, and Cognito user pool created by `misc/cloud/infra`. It does not create Keycloak, PostgreSQL, an EFS volume, or a second VPC.

Only the Orquestrador is public. The other applications and Kafka communicate through the private Cloud Map namespace.

## Configure the project

Copy `.env.example` to `.env` and fill its values from the `CarsGalore-infra-dev` CloudFormation outputs. The local `.env` is ignored by Git. The shared stack outputs the private subnets; obtain the public subnet IDs and Availability Zones from the VPC in the AWS console or `aws ec2 describe-subnets` for the public ALB.

`RDS_SECRET_ARN` must point to the existing RDS credential secret. The task definitions receive its `username` and `password` fields directly from Secrets Manager; do not put the database password in `.env`.

Before deploying applications, create the database tables manually. The shared infrastructure creates only the databases. Use `psql` to apply the required tables from `docker/ddl.sql`, or confirm they already exist:

```bash
psql "host=$RDS_ENDPOINT port=$RDS_PORT dbname=compradores user=sub3 sslmode=require" -c '\dt'
psql "host=$RDS_ENDPOINT port=$RDS_PORT dbname=principal user=sub3 sslmode=require" -c '\dt'
psql "host=$RDS_ENDPOINT port=$RDS_PORT dbname=financeiro user=sub3 sslmode=require" -c '\dt'
```

## Build and push images

Build each application before starting the tasks. Use the corresponding image tag in `.env`:

```bash
mvn -B package
docker build -f sub3-compradores/src/main/docker/Dockerfile.jvm -t ghcr.io/$GHCR_OWNER/sub5-sub3-compradores:$COMPRADORES_IMAGE_TAG sub3-compradores
docker build -f sub3-principal/src/main/docker/Dockerfile.jvm -t ghcr.io/$GHCR_OWNER/sub5-sub3-principal:$PRINCIPAL_IMAGE_TAG sub3-principal
docker build -f sub3-orquestrador/src/main/docker/Dockerfile.jvm -t ghcr.io/$GHCR_OWNER/sub5-sub3-orquestrador:$ORQUESTRADOR_IMAGE_TAG sub3-orquestrador
docker build -f sub3-financeiro/src/main/docker/Dockerfile.jvm -t ghcr.io/$GHCR_OWNER/sub5-sub3-financeiro:$FINANCEIRO_IMAGE_TAG sub3-financeiro
```

Push the four tags to private GHCR after logging in with a token that has package write access.

## Deploy

Install dependencies once:

```bash
npm install
```

Keep `RUN_APPS=false` for the first deployment. This creates the ECS resources and the GHCR credential secret without starting tasks:

```bash
npm run deploy
npm run bootstrap-ghcr
```

Set `RUN_APPS=true` in `.env` and deploy again. This starts Kafka and the four applications:

```bash
npm run deploy
```

The `OrquestradorUrl` stack output is the public endpoint. Kafka uses Fargate ephemeral storage, so its topics and messages are lost when its task is replaced.
