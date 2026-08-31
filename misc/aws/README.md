# AWS deployment

This directory contains the CDK application used by the two manual GitHub Actions workflows. It deploys the demo as one all-Fargate CloudFormation stack named `CarsGalore-fargate`.

## GitHub repository secrets

Configure these repository secrets before running the workflows:

- `AWS_ACCESS_KEY_ID`
- `AWS_SECRET_ACCESS_KEY`
- `AWS_REGION` (optional; defaults to `sa-east-1`)
- `GHCR_USERNAME`
- `GHCR_TOKEN` (a GitHub token with package read/write access)

The AWS identity needs permission to bootstrap and deploy CDK, create the VPC/ECS/EFS resources, and update Secrets Manager. The GHCR token is copied into the stack-created Secrets Manager secret so Fargate can pull the private images.

## Deployment order

1. Run **AWS infrastructure**. This creates the VPC, ECS cluster, ALB, EFS filesystems, Cloud Map namespace, and task definitions without starting services.
2. Run **AWS application deployment**. This builds and pushes the images, stores the GHCR pull token in Secrets Manager, starts PostgreSQL and Kafka on Fargate, and then starts Keycloak and the four Quarkus services.

PostgreSQL and Kafka run on Fargate with EFS. Keycloak uses its built-in development database, so it does not need PostgreSQL. This setup is intended for a short-lived demo rather than production.

The workflows expose the ALB on ports 8080 through 8084:

```text
8080 Keycloak
8081 sub3-compradores
8082 sub3-principal
8083 sub3-orquestrador
8084 sub3-financeiro
```

The infrastructure workflow intentionally starts all tasks at zero. The deployment workflow updates the image tag, writes the GHCR credentials, and starts the stateful and application services.

To remove the demo deployment, delete the `CarsGalore-fargate` CloudFormation stack. Its demo resources use destroy policies, so delete it only when the stored data is no longer needed.
