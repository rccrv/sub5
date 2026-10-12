--
-- PostgreSQL database cluster dump
--

\restrict HhUo5BMuXHfX41nSSnAfgWClFvNytULWNHi55JO7pkuFeBgPDhIVi1VVuB3zwaM

SET default_transaction_read_only = off;

SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;

--
-- Roles
--

--CREATE ROLE postgres;
--ALTER ROLE postgres WITH SUPERUSER INHERIT CREATEROLE CREATEDB LOGIN REPLICATION BYPASSRLS PASSWORD 'SCRAM-SHA-256$4096:veHnimPW0rePaU7njaz1oA==$+c/EJ3+y4WZFI2mVQ9WX4QAPXV8+bD09dLwUSod7aoY=:QVm59Drkrc8WnGRUOUK6jRHJmvl3fei3bK6V5kDZSPU=';
CREATE ROLE sub3;
ALTER ROLE sub3 WITH NOSUPERUSER INHERIT NOCREATEROLE NOCREATEDB LOGIN NOREPLICATION NOBYPASSRLS PASSWORD 'SCRAM-SHA-256$4096:ifvbVRWOHz2wtiFYkUl4Dg==$ENLJiKc40DfPMQz+lp7hp2gIOt+XcLGLH+zZe6nFaIo=:VvbauyjJZ1SgONeupmDoxNDb46bdXqwKSolQfN5VFyI=';

--
-- User Configurations
--








\unrestrict HhUo5BMuXHfX41nSSnAfgWClFvNytULWNHi55JO7pkuFeBgPDhIVi1VVuB3zwaM

--
-- Databases
--

--
-- Database "template1" dump
--

\connect template1

--
-- PostgreSQL database dump
--

\restrict goMH7lcZMPA8nPRRE14MeMepT9MystbD0xfNZmTsQKhGgPA5S4AoDLxdTEX4dYi

-- Dumped from database version 18.4 (Debian 18.4-1.pgdg13+1)
-- Dumped by pg_dump version 18.6

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET transaction_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

--
-- PostgreSQL database dump complete
--

\unrestrict goMH7lcZMPA8nPRRE14MeMepT9MystbD0xfNZmTsQKhGgPA5S4AoDLxdTEX4dYi

--
-- Database "compradores" dump
--

--
-- PostgreSQL database dump
--

\restrict SCKgw04rljBJyudOuukm2cK7Ye3WyCHLoyPfIpgc2z4qF6vBPfc66CL2fpKcD47

-- Dumped from database version 18.4 (Debian 18.4-1.pgdg13+1)
-- Dumped by pg_dump version 18.6

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET transaction_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

--
-- Name: compradores; Type: DATABASE; Schema: -; Owner: sub3
--

CREATE DATABASE compradores WITH TEMPLATE = template0 ENCODING = 'UTF8' LOCALE_PROVIDER = libc LOCALE = 'en_US.utf8';


ALTER DATABASE compradores OWNER TO sub3;

\unrestrict SCKgw04rljBJyudOuukm2cK7Ye3WyCHLoyPfIpgc2z4qF6vBPfc66CL2fpKcD47
\connect compradores
\restrict SCKgw04rljBJyudOuukm2cK7Ye3WyCHLoyPfIpgc2z4qF6vBPfc66CL2fpKcD47

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET transaction_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- Name: compradores; Type: TABLE; Schema: public; Owner: sub3
--

CREATE TABLE public.compradores (
    id bigint NOT NULL,
    cpf text NOT NULL,
    primeiro_nome text NOT NULL,
    ultimo_nome text NOT NULL,
    email text NOT NULL,
    telefone text NOT NULL,
    autorizado boolean DEFAULT false NOT NULL,
    CONSTRAINT compradores_cpf_check CHECK ((cpf ~ '^\d{3}\.\d{3}\.\d{3}-\d{2}|\d{11}$'::text))
);


ALTER TABLE public.compradores OWNER TO sub3;

--
-- Name: compradores_id_seq; Type: SEQUENCE; Schema: public; Owner: sub3
--

CREATE SEQUENCE public.compradores_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.compradores_id_seq OWNER TO sub3;

--
-- Name: compradores_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: sub3
--

ALTER SEQUENCE public.compradores_id_seq OWNED BY public.compradores.id;


--
-- Name: compradores id; Type: DEFAULT; Schema: public; Owner: sub3
--

ALTER TABLE ONLY public.compradores ALTER COLUMN id SET DEFAULT nextval('public.compradores_id_seq'::regclass);


--
-- Data for Name: compradores; Type: TABLE DATA; Schema: public; Owner: sub3
--

COPY public.compradores (id, cpf, primeiro_nome, ultimo_nome, email, telefone, autorizado) FROM stdin;
\.


--
-- Name: compradores_id_seq; Type: SEQUENCE SET; Schema: public; Owner: sub3
--

SELECT pg_catalog.setval('public.compradores_id_seq', 1, false);


--
-- PostgreSQL database dump complete
--

\unrestrict SCKgw04rljBJyudOuukm2cK7Ye3WyCHLoyPfIpgc2z4qF6vBPfc66CL2fpKcD47

--
-- Database "financeiro" dump
--

--
-- PostgreSQL database dump
--

\restrict TXzmFgsUnOEA0ZvZhPq0d0nc51RPw82otstjWCJK9yP3PwaekMdu4gXah3zQybV

-- Dumped from database version 18.4 (Debian 18.4-1.pgdg13+1)
-- Dumped by pg_dump version 18.6

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET transaction_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

--
-- Name: financeiro; Type: DATABASE; Schema: -; Owner: sub3
--

CREATE DATABASE financeiro WITH TEMPLATE = template0 ENCODING = 'UTF8' LOCALE_PROVIDER = libc LOCALE = 'en_US.utf8';


ALTER DATABASE financeiro OWNER TO sub3;

\unrestrict TXzmFgsUnOEA0ZvZhPq0d0nc51RPw82otstjWCJK9yP3PwaekMdu4gXah3zQybV
\connect financeiro
\restrict TXzmFgsUnOEA0ZvZhPq0d0nc51RPw82otstjWCJK9yP3PwaekMdu4gXah3zQybV

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET transaction_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- Name: pagamentos; Type: TABLE; Schema: public; Owner: sub3
--

CREATE TABLE public.pagamentos (
    id bigint NOT NULL,
    correlation_id uuid NOT NULL,
    cpf text NOT NULL,
    placa text NOT NULL,
    endereco text NOT NULL,
    cep text NOT NULL,
    pix_code text NOT NULL,
    quoted_amount numeric(12,2) NOT NULL,
    settled_amount numeric(12,2),
    status character varying(32) NOT NULL,
    created_at timestamp without time zone NOT NULL,
    expires_at timestamp without time zone NOT NULL,
    updated_at timestamp without time zone,
    version bigint DEFAULT 0 NOT NULL
);


ALTER TABLE public.pagamentos OWNER TO sub3;

--
-- Name: pagamentos_id_seq; Type: SEQUENCE; Schema: public; Owner: sub3
--

CREATE SEQUENCE public.pagamentos_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.pagamentos_id_seq OWNER TO sub3;

--
-- Name: pagamentos_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: sub3
--

ALTER SEQUENCE public.pagamentos_id_seq OWNED BY public.pagamentos.id;


--
-- Name: pagamentos id; Type: DEFAULT; Schema: public; Owner: sub3
--

ALTER TABLE ONLY public.pagamentos ALTER COLUMN id SET DEFAULT nextval('public.pagamentos_id_seq'::regclass);


--
-- Data for Name: pagamentos; Type: TABLE DATA; Schema: public; Owner: sub3
--

COPY public.pagamentos (id, correlation_id, cpf, placa, endereco, cep, pix_code, quoted_amount, settled_amount, status, created_at, expires_at, updated_at, version) FROM stdin;
\.


--
-- Name: pagamentos_id_seq; Type: SEQUENCE SET; Schema: public; Owner: sub3
--

SELECT pg_catalog.setval('public.pagamentos_id_seq', 1, false);


--
-- Name: pagamentos pagamentos_correlation_id_key; Type: CONSTRAINT; Schema: public; Owner: sub3
--

ALTER TABLE ONLY public.pagamentos
    ADD CONSTRAINT pagamentos_correlation_id_key UNIQUE (correlation_id);


--
-- Name: pagamentos pagamentos_pix_code_key; Type: CONSTRAINT; Schema: public; Owner: sub3
--

ALTER TABLE ONLY public.pagamentos
    ADD CONSTRAINT pagamentos_pix_code_key UNIQUE (pix_code);


--
-- Name: pagamentos pagamentos_pkey; Type: CONSTRAINT; Schema: public; Owner: sub3
--

ALTER TABLE ONLY public.pagamentos
    ADD CONSTRAINT pagamentos_pkey PRIMARY KEY (id);


--
-- PostgreSQL database dump complete
--

\unrestrict TXzmFgsUnOEA0ZvZhPq0d0nc51RPw82otstjWCJK9yP3PwaekMdu4gXah3zQybV

--
-- Database "postgres" dump
--

\connect postgres

--
-- PostgreSQL database dump
--

\restrict giri89nZh2J9vgGYo9NQ7ZdwXJ9nOa3ZrWBsNTsjqlYJcgyHGNw4uAXfP3RVbNW

-- Dumped from database version 18.4 (Debian 18.4-1.pgdg13+1)
-- Dumped by pg_dump version 18.6

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET transaction_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

--
-- PostgreSQL database dump complete
--

\unrestrict giri89nZh2J9vgGYo9NQ7ZdwXJ9nOa3ZrWBsNTsjqlYJcgyHGNw4uAXfP3RVbNW

--
-- Database "principal" dump
--

--
-- PostgreSQL database dump
--

\restrict 7eHocp9NvGBurYFf4fwQAIgdhAAt0ZBn2uh4SPpSVa2eFzFOzOpvp4P2qwBQtUH

-- Dumped from database version 18.4 (Debian 18.4-1.pgdg13+1)
-- Dumped by pg_dump version 18.6

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET transaction_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

--
-- Name: principal; Type: DATABASE; Schema: -; Owner: sub3
--

CREATE DATABASE principal WITH TEMPLATE = template0 ENCODING = 'UTF8' LOCALE_PROVIDER = libc LOCALE = 'en_US.utf8';


ALTER DATABASE principal OWNER TO sub3;

\unrestrict 7eHocp9NvGBurYFf4fwQAIgdhAAt0ZBn2uh4SPpSVa2eFzFOzOpvp4P2qwBQtUH
\connect principal
\restrict 7eHocp9NvGBurYFf4fwQAIgdhAAt0ZBn2uh4SPpSVa2eFzFOzOpvp4P2qwBQtUH

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET transaction_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- Name: veiculos; Type: TABLE; Schema: public; Owner: sub3
--

CREATE TABLE public.veiculos (
    id bigint NOT NULL,
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


ALTER TABLE public.veiculos OWNER TO sub3;

--
-- Name: veiculos_id_seq; Type: SEQUENCE; Schema: public; Owner: sub3
--

CREATE SEQUENCE public.veiculos_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.veiculos_id_seq OWNER TO sub3;

--
-- Name: veiculos_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: sub3
--

ALTER SEQUENCE public.veiculos_id_seq OWNED BY public.veiculos.id;


--
-- Name: veiculos id; Type: DEFAULT; Schema: public; Owner: sub3
--

ALTER TABLE ONLY public.veiculos ALTER COLUMN id SET DEFAULT nextval('public.veiculos_id_seq'::regclass);


--
-- Data for Name: veiculos; Type: TABLE DATA; Schema: public; Owner: sub3
--

COPY public.veiculos (id, marca, modelo, ano, placa, cor, valor, comprador_cpf, pagamento_id, vendido) FROM stdin;
\.


--
-- Name: veiculos_id_seq; Type: SEQUENCE SET; Schema: public; Owner: sub3
--

SELECT pg_catalog.setval('public.veiculos_id_seq', 1, false);


--
-- PostgreSQL database dump complete
--

\unrestrict 7eHocp9NvGBurYFf4fwQAIgdhAAt0ZBn2uh4SPpSVa2eFzFOzOpvp4P2qwBQtUH

--
-- PostgreSQL database cluster dump complete
--

