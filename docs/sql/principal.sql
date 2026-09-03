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
