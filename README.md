# Tarefa substitutiva da 3ª atividade

Esse repositório contém uma implementação da atividade substitutiva da fase 3 da pós-graduação em Software Architecture.

## O que foi implementado

O enunciado elencava e descrevia o sistema simples de revenda de veículos.

Não ficou claro para mim se seria necessário implementar algum sistema de conta financeira para o cliente
para verificar se o cliente contém fundos para comprar um veículo. Após ponderar um pouco, decidi implementar
apenas o sistema de cadastro de usuários, venda e autenticação.

O sistema foi feito em Java usando o framework Quarkus. O repositório contém um projeto maven com dois
submódulos. A divisão em módulos não faz os dois terem conexão. Os projetos não podem importar arquivos entre si.
Só trabalhei dessa forma para que os dois projetos pudessem ser abertos num mesmo contexto da IDE que uso.

Como não queria lidar com ambientes em cloud para essa tarefa, o deploy é feito via manifests Kubernetes
num cluster local. O runner usado na tarefa também roda localmente.

Além dessas ferramentas são usados o PostgreSQL para persistência dos dados dos sistemas e o Keycloak para autenticação
e autorização dos usuários.

O repositório no github contém uma política que previne pushes na branch main. Builds e deloys são disparados
quando há um novo commit nessa branch. Dessa forma, é necessário fazer push em outra branch e abrir um PR contra
a main para que o build ocorra.

### Considerações da implementação

#### Organização do código

Tentei seguir uma estrutura de Clean Architecture. Percebo nesse momento que alguns pontos não ficaram totalmente
aderentes a essa estrutura.

Primeiro, mappers (chamados de adapters) deveriam ter ficado fora da pasta de arquitetura (pasta src/main/java/.../arch)
dos projetos. Como eles referencias elementos que estão fora do núcleo da aplicação, não deveriam estar dentro dela.

Segundo, repositórios de acessos de bancos de dados e serviço de acesso ao Keycloak deveriam implementar interfaces
portas definidas dentro da arquitetura e passados para dentro dos usecases usando-se de polimorfismo.

Terceira, a pasta controller também deveria estar fora ou alterada para que apenas referencie elementos de dentro do
núcleo arquitetural. Em outra tarefa mantive essa pasta fora.

Apesar disso, creio que a estrutura esteja relativamente bem organizada e com adesão o mais próximo possível aos
princípios de Clean Architecture.

#### Arquitetura da aplicação

Conforme explicado. Além dos dois serviços implementados, o conjunto de serviços inclui um postgres e um keycloak.

Basicamente, o diagrama abaixo mostra o relacionamento entre os serviços:

![Arquitetura](/docs/arch.png)

Os serviços comunicam e provêm endpoints que dependem na grande maioria dos casos de autorização provida pelo
keycloak. O serviço de compradores permite aos clientes fazerem o próprio cadastro. Esse cadastro precisa ser
aprovado por um funcionário da empresa.

Uma vez que o cadastro é feito, o cliente pode acessar os endpoints do sistema principal que permite a ele fazer 
compras dos carros listados no sistema.

Apenas funcionários podem cadastrar novos carros.

#### Bancos de dados

O postgres provê dois bancos de dados. Um para cadas serviço.

O primeiro é o banco de compradores. Os clientes fazem o próprio cadastro nesse banco.  Uma vez que o cadastro
é autorizado, a conta do cliente é criada no keycloak e ele pode usar então usar sua conta para fazer compras
nos endpoints do sistema principal.

O segundo banco é o do sistema principal. Funcionários fazem o cadastro de carros nesse banco e os clientes
podem comprar carros usando os endpoints do sistema principal.

Cada banco tem apenas uma tabela que seguem os diagramas abaixo.

compradores (bd compradores):

![compradores](/docs/compradores.png)

veiculos (bd principal):

![veiculos](/docs/veiculos.png)

## Execução local e testes

### Lista de endpoints e roles necessárias para acessá-los

#### Sistema compradores

| Endpoint         | Método | Role        |
|------------------|--------|-------------|
| /cadastrar       | POST   |             |
| /listar          | GET    | funcionario |
| /autorizar/{cpf} | PUT    | funcionario |

#### Sistema principal

| Endpoint         | Método | Role                     |
|------------------|--------|--------------------------|
| /cadastrar       | POST   | funcionario              |
| /editar/{placa}  | PUT    | funcionario              |
| /listar-venda    | GET    | funcionario ou comprador |
| /listar-vendidos | GET    | funcionario              |
| /comprar/{placa} | PUT    | funcionario              |

### Como rodar localmente

Entrar na pasta docker e executar o comando para subir os contêineres necessários (um keycloak e um postgres):

```shell
cd docker
docker compose up -d
```

Depois necessário rodar os dois subprojetos (sub3-compradores e sub3-principal) em desenvolvimento, um em cada terminal:

```shell
cd sub3-compradores
mvn quarkus:dev
```

```shell
cd sub3-principal
mvn quarkus:dev
```

O postgres vai rodar na porta 5432 e o keycloak na porta 8080. O projeto compradores roda na porta 8081 e o
projeto principal na porta 8082.

### Como testar

Para testar o sistema, basta rodar ele localmente. E abrir o conteúdo da pasta misc/tests no software
[Bruno](https://www.usebruno.com/). Os endpoints listados acima têm testes os testes necessários com validação onde
essa se faz necessária (validação de CPF e máscara de placa de carro padrão Mercosul).

**Importante**: é necessário usar o environment dev quando se roda localmente. O vídeo vai mostrar a execução no
environment prod

Os endpoints serão executados na ordem correta no vídeo de apresentação. Logo a sugestão é acompanhar por lá. 

## Outras informações

Link do vídeo

[Vídeo](https://youtu.be/KW7my4eOKzU)