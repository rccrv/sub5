create table compradores
(
    id             bigserial primary key,
    cpf            text    not null unique check (cpf ~ '^\d{3}\.\d{3}\.\d{3}-\d{2}|\d{11}$'),
    primeiro_nome  text    not null,
    ultimo_nome    text    not null,
    email          text    not null unique,
    telefone       text    not null unique,
    autorizado     boolean not null default false
);