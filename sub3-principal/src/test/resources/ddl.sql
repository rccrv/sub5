create table veiculos
(
    id            bigserial primary key,
    marca         text           not null,
    modelo        text           not null,
    ano           int            not null,
    placa         text           not null unique check (placa ~ '^[A-Z]{3}[0-9][A-Z][0-9]{2}$'),
    cor           text           not null,
    valor         decimal(12, 2) not null,
    comprador_cpf text           not null,
    pagamento_id  uuid,
    vendido       boolean        not null default false
);
