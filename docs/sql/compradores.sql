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
