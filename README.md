# Tarefa substitutiva da 5ª atividade

Esse repositório contém uma implementação da atividade substitutiva da fase 5 da pós-graduação em Software Architecture.

## O que não foi implementado

O relatório de segurança de informação não foi feito.

## O que foi implementado

O enunciado pedia para acrescentar ao sistema de compras da atividade substitutiva anterior um sistema de gerenciamento
que levasse em conta o comportamento concorrente dos clientes.

O sistema foi feito em Java usando o framework Quarkus. O repositório contém um projeto maven com quatro
submódulos. Os projetos não podem importar arquivos entre si.

Só trabalhei dessa forma para que os quatro projetos pudessem ser abertos num mesmo contexto da IDE que uso.

O deploy é feito usando CDK na AWS. Por falta de tempo e dado a demora de subir contextos em cloud, não implementei
deploy automatizado. Os deploys tiveram de ser disparados via linha de comando de meu computador de desenvolvimento.

Os passos estão explicados no diretório misc/aws, mas isso não será mostrado no vídeo de demonstração.

As ferramentas da AWS usadas são o Cognito para autenticação de funcionários, financeiro e compradores e cadastro de
compradores, o RDS PostgreSQL como solução de banco de dados e o Fargate como solução serverless de contêineres.

Inicialmente tentei usar o AWS MSK para comunicação entre microsserviços no contexto da SAGA adotada, mas tive problemas
em subir o serviço. Debugar subidas do MSK demorava algo em torno de 30 minutos para ver ele tentar subir e mais 20 para
derrubar o stack. Grande parte do atraso da entrega foi graças a isso.

No final, decidi subir um contêiner usando a imagem kafka-native no Fargate e usar ela internamente mesmo.

### Considerações da implementação

#### Organização do código

A estrutura Clean Architecture ficou melhor do que na primeira versão. Agora o projeto usa ports dentro da parte da
arquitetura. Esses ports são apenas interfaces implementadas por classes fora da pasta de arquitetura.

Os mappers também estão fora da parte de arquitetura e toda transformação de dados ocorre em camadas implementadas
fora da parte de aquitetura.

Estruturalmente, o projeto está muito melhor organizado e com adesão o mais próximo possível aos  princípios de
Clean Architecture em relação a versão anterior.

#### Arquitetura da aplicação

Conforme explicado. Além dos dois serviços implementados, o conjunto de serviços inclui, o Cognito e o RDS Postgres.
Os serviços e o contêiner Kafka foram deployados no Fargate.

O diagrama abaixo mostra o relacionamento entre os serviços:

![Arquitetura](/docs/arch.svg)

Para essa versão usamos um orquestrador de SAGA que também faz a função de Backend for Frontend. Esse serviço concentra
os endpoints dos outros e os chama quando cliente. Ele também é o único serviço exposto e o único cujos endpoints
precisam de autorização. Além disso, faz a orquestração de rollbacks de compras inválidas.

O fluxo de compra para um cliente é que ele reserva um veículo e pode então comprar um veículo reservado.

Compras inválidas são quando um cliente tem uma reserva de veículo ativa e tenta comprar um veículo que já foi comprado
por outro comprador.

A SAGA manda compensação para esse cenário caso a compra seja inválida.

Apenas funcionários podem cadastrar novos carros. Os compradores só podem acessar dados de suas próprias compras.

##### SAGA

Nesse projeto usamos SAGA orquestrada. Um orquestrador fica responsável por receber acknowledgements e enviar
compensações e finalizações de compras de veículos inválidas.

A justificativa para isso é que se trata do único fluxo diferente do projeto anterior e os outros estavam já bem
atendidos pela divisão em microsserviços que cuidem de apenas uma parte do domínio.

#### Bancos de dados

O RDS contém três bancos de dados. Um para cadas serviço que não é o orquestrador: compradores, principal e financeiro.

O primeiro é o banco de compradores. Os clientes fazem o próprio cadastro nesse banco. Uma vez que o cadastro
é autorizado, a conta do cliente é criada no cognito e ele pode usar então usar sua conta para fazer compras
de veículos.

O segundo banco é o do sistema principal. Funcionários fazem o cadastro de carros nesse banco.

O terceiro é o banco financeiro. As reservas e compras são registradas nesse banco.

Cada banco tem apenas uma tabela conforme diagrama abaixo:

compradores (bd compradores):

![compradores](/docs/db.svg)

## Execução local e testes

### Como rodar localmente

O sistema não tem uma maneira de rodar localmente. O docker compose foi feito pensando na versão anterior do sistema
que não usava AWS. Tentei num primeiro momento usar o LocalStack para simular a AWS localmente, mas deu trabalho e o
resultado não foi bom. Dessa forma isso não foi feito.

### Como fazer deploy do sistema

O deploy é dividido em duas partes. Ambas são projetos CDK e se encontram em misc/cloud.

Primeiro é necessário fazer o deploy da infraestrutura. Necessário ter uma CLI aws instalada e logada. Basta então
executar os comandos abaixo:

```shell
cd misc/cloud/infra
export CDK_DEFAULT_REGION=sa-east-1
export CDK_ALLOWED_CIDR="$(curl -4 -s https://checkip.amazonaws.com)/32"
npm ci
npm run build
npm run synth
npm run deploy
```

Ao fazer isso, o Cognito já é criado com as roles necessárias e usuários de exemplo em cada role.

O RDS por sua vez só cria os 3 bancos e é necessário executar as DDLs de criação de cada banco. As DDLs se encontram em
docs/sql.

Para conectar com o banco postgres use os comandos abaixo (necessário ter o psql e o jq instalados):
 
```shell
export PGHOST="$(aws cloudformation describe-stacks \
  --stack-name CarsGalore-infra-dev \
  --region sa-east-1 \
  --query 'Stacks[0].Outputs[?OutputKey==`PostgresEndpoint`].OutputValue' \
  --output text)"

export PGPORT=5432
export PGDATABASE=postgres

SECRET_ARN="$(aws cloudformation describe-stacks \
  --stack-name CarsGalore-infra-dev \
  --region sa-east-1 \
  --query 'Stacks[0].Outputs[?OutputKey==`PostgresCredentialsSecretArn`].OutputValue' \
  --output text)"

SECRET_JSON="$(aws secretsmanager get-secret-value \
  --secret-id "$SECRET_ARN" \
  --region sa-east-1 \
  --query SecretString \
  --output text)"

export PGUSER="$(echo "$SECRET_JSON" | jq -r .username)"
export PGPASSWORD="$(echo "$SECRET_JSON" | jq -r .password)"
export PGSSLMODE=require

export PGDATABASE=principal
psql
```

Dentro do banco, use os comandos abaixo:

```sql
-- principal
CREATE TABLE public.veiculos (
  id bigserial NOT NULL,
  marca text NOT NULL,
  modelo text NOT NULL,
  ano integer NOT NULL,
  placa text NOT NULL,
  cor text NOT NULL,
  valor numeric(12,2) NOT NULL,
  comprador_cpf text NOT NULL,
  pagamento_id uuid,
  vendido boolean DEFAULT false NOT NULL,
  CONSTRAINT veiculos_placa_check CHECK ((placa ~ '^[A-Z]{3}[0-9][A-Z][0-9]{2}$'::text))
);
\c compradores
-- compradores
CREATE TABLE public.compradores (
  id bigserial NOT NULL,
  cpf text NOT NULL,
  primeiro_nome text NOT NULL,
  ultimo_nome text NOT NULL,
  email text NOT NULL,
  telefone text NOT NULL,
  autorizado boolean DEFAULT false NOT NULL,
  CONSTRAINT compradores_cpf_check CHECK ((cpf ~ '^\d{3}\.\d{3}\.\d{3}-\d{2}|\d{11}$'::text))
);
\c financeiro
-- financeiro
CREATE TABLE public.pagamentos (
  id bigserial NOT NULL,
  correlation_id uuid NOT NULL,
  cpf text NOT NULL,
  placa text NOT NULL,
  endereco text NOT NULL,
  cep text NOT NULL,
  pix_code text NOT NULL,
  quoted_amount numeric(12,2) NOT NULL,
  settled_amount numeric(12,2),
  status varchar(32) NOT NULL,
  created_at timestamp without time zone NOT NULL,
  expires_at timestamp without time zone NOT NULL,
  updated_at timestamp without time zone,
  version bigint NOT NULL DEFAULT 0,
  CONSTRAINT pagamentos_pkey PRIMARY KEY (id),
  CONSTRAINT pagamentos_correlation_id_key UNIQUE (correlation_id),
  CONSTRAINT pagamentos_pix_code_key UNIQUE (pix_code)
);
```

Após fazer o deploy da infraestrutura, podemos subir os serviços:
```shell
# Necessário configurar o .env corretamente
cd misc/cloud/apps
npm ci
npm run build
RUN_APPS=false npm run deploy
npm run bootstrap-ghcr
RUN_APPS=true npm run deploy
```

Com esses comandos, a infraestrutura subirá e estará pronta para os testes.

Alguns dados são importantes para conseguirmos fazer uso dos endpoints do aplicativo. Client ID usado pelo Cognito e URL
do orquestrador.

Para obter o Client ID do Cognito, basta executar:

```shell
aws cloudformation describe-stacks \
  --stack-name CarsGalore-infra-dev \
  --region sa-east-1 \
  --query "Stacks[0].Outputs[?OutputKey=='CognitoUserClientId'].OutputValue" \
  --output text
```

Para obter a URL do orquestrador deployado basta usar:

```shell
aws cloudformation describe-stacks \
  --stack-name CarsGalore-apps-dev \
  --region sa-east-1 \
  --query "Stacks[0].Outputs[?OutputKey=='OrquestradorUrl'].OutputValue" \
  --output text
```

### Lista de endpoints e roles necessárias para acessá-los

#### Sistema orquestrador

| Endpoint                     | Método | Role necessária          |
|------------------------------|--------|--------------------------|
| /compradores/cadastrar       | POST   | público (sem role)       |
| /compradores/autorizar/{cpf} | PUT    | funcionario              |
| /compradores/listar          | GET    | funcionario              |
| /veiculos/cadastrar          | POST   | funcionario              |
| /veiculos/editar/{placa}     | PUT    | funcionario              |
| /veiculos/listar-venda       | GET    | comprador ou funcionario |
| /veiculos/listar-vendidos    | GET    | funcionario              |
| /veiculos/comprar/{placa}    | PUT    | comprador                |
| /pagamentos/reservas         | POST   | comprador                |
| /pagamentos/{id}/pagar       | POST   | comprador                |
| /pagamentos/{id}             | GET    | comprador                |

### Como testar

Para testar o sistema abrir o conteúdo da pasta misc/tests no software [Bruno](https://www.usebruno.com/).

Os endpoints serão executados na ordem correta no vídeo de apresentação. Logo a sugestão é acompanhar por lá. 

## Outras informações

Link do vídeo

[Vídeo](https://youtu.be/WdPUo1LfHeo)
