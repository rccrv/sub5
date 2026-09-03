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
