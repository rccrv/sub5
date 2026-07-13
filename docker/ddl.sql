--
-- PostgreSQL database cluster dump
--

\restrict 8adnqPrRfvdXCJuyMbAN9Wa1cBTGy7TpD1W7SLJO3wQCRoiaINvs8xkSpfEsdgM

SET default_transaction_read_only = off;

SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;

--
-- Roles
--

--CREATE ROLE postgres;
--ALTER ROLE postgres WITH SUPERUSER INHERIT CREATEROLE CREATEDB LOGIN REPLICATION BYPASSRLS PASSWORD 'SCRAM-SHA-256$4096:l3mToePvG4nUMyZSTKjFUA==$zqTQ70L24ZaVB6buasWtmij+kQKaEy+nbTOT6g8936c=:i6FmK8zNjhdQRNfB3x6qkxrwvk4OTkT5zCFEF0d+Nds=';
CREATE ROLE sub3;
ALTER ROLE sub3 WITH NOSUPERUSER INHERIT NOCREATEROLE NOCREATEDB LOGIN NOREPLICATION NOBYPASSRLS PASSWORD 'SCRAM-SHA-256$4096:ifvbVRWOHz2wtiFYkUl4Dg==$ENLJiKc40DfPMQz+lp7hp2gIOt+XcLGLH+zZe6nFaIo=:VvbauyjJZ1SgONeupmDoxNDb46bdXqwKSolQfN5VFyI=';

--
-- User Configurations
--








\unrestrict 8adnqPrRfvdXCJuyMbAN9Wa1cBTGy7TpD1W7SLJO3wQCRoiaINvs8xkSpfEsdgM

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

\restrict yuWFmhcgqpbHb0ksYfCP5XF8EmSPnoeKRPngzSDAEQidb697QBwmSKhw8HxTCfX

-- Dumped from database version 18.4 (Debian 18.4-1.pgdg13+1)
-- Dumped by pg_dump version 18.4 (Debian 18.4-1.pgdg13+1)

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

\unrestrict yuWFmhcgqpbHb0ksYfCP5XF8EmSPnoeKRPngzSDAEQidb697QBwmSKhw8HxTCfX

--
-- Database "compradores" dump
--

--
-- PostgreSQL database dump
--

\restrict jiEnkpVCbm2aI4WEehjv4qIiDlenRbAMcUcld91SirjuKXZjh2B7nyMKzdPYtBD

-- Dumped from database version 18.4 (Debian 18.4-1.pgdg13+1)
-- Dumped by pg_dump version 18.4 (Debian 18.4-1.pgdg13+1)

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

\unrestrict jiEnkpVCbm2aI4WEehjv4qIiDlenRbAMcUcld91SirjuKXZjh2B7nyMKzdPYtBD
\connect compradores
\restrict jiEnkpVCbm2aI4WEehjv4qIiDlenRbAMcUcld91SirjuKXZjh2B7nyMKzdPYtBD

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

SELECT pg_catalog.setval('public.compradores_id_seq', 1, true);


--
-- Name: compradores compradores_cpf_key; Type: CONSTRAINT; Schema: public; Owner: sub3
--

ALTER TABLE ONLY public.compradores
    ADD CONSTRAINT compradores_cpf_key UNIQUE (cpf);


--
-- Name: compradores compradores_email_key; Type: CONSTRAINT; Schema: public; Owner: sub3
--

ALTER TABLE ONLY public.compradores
    ADD CONSTRAINT compradores_email_key UNIQUE (email);


--
-- Name: compradores compradores_pkey; Type: CONSTRAINT; Schema: public; Owner: sub3
--

ALTER TABLE ONLY public.compradores
    ADD CONSTRAINT compradores_pkey PRIMARY KEY (id);


--
-- Name: compradores compradores_telefone_key; Type: CONSTRAINT; Schema: public; Owner: sub3
--

ALTER TABLE ONLY public.compradores
    ADD CONSTRAINT compradores_telefone_key UNIQUE (telefone);


--
-- PostgreSQL database dump complete
--

\unrestrict jiEnkpVCbm2aI4WEehjv4qIiDlenRbAMcUcld91SirjuKXZjh2B7nyMKzdPYtBD

--
-- Database "postgres" dump
--

\connect postgres

--
-- PostgreSQL database dump
--

\restrict mtFOcfSho8LJ2jWmKV4RR2sbcjXSl3nVUzKY7VyxaDLnW7zN4jMscnckG8DH146

-- Dumped from database version 18.4 (Debian 18.4-1.pgdg13+1)
-- Dumped by pg_dump version 18.4 (Debian 18.4-1.pgdg13+1)

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

\unrestrict mtFOcfSho8LJ2jWmKV4RR2sbcjXSl3nVUzKY7VyxaDLnW7zN4jMscnckG8DH146

--
-- Database "principal" dump
--

--
-- PostgreSQL database dump
--

\restrict 5G1cw4GdtQBskbUqwtvdrJQaegqGCXZSTsiRaBhl9L59HikqZNX2a8aKaaB21Tq

-- Dumped from database version 18.4 (Debian 18.4-1.pgdg13+1)
-- Dumped by pg_dump version 18.4 (Debian 18.4-1.pgdg13+1)

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

\unrestrict 5G1cw4GdtQBskbUqwtvdrJQaegqGCXZSTsiRaBhl9L59HikqZNX2a8aKaaB21Tq
\connect principal
\restrict 5G1cw4GdtQBskbUqwtvdrJQaegqGCXZSTsiRaBhl9L59HikqZNX2a8aKaaB21Tq

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

COPY public.veiculos (id, marca, modelo, ano, placa, cor, valor, comprador_cpf, vendido) FROM stdin;
\.


--
-- Name: veiculos_id_seq; Type: SEQUENCE SET; Schema: public; Owner: sub3
--

SELECT pg_catalog.setval('public.veiculos_id_seq', 3, true);


--
-- Name: veiculos veiculos_pkey; Type: CONSTRAINT; Schema: public; Owner: sub3
--

ALTER TABLE ONLY public.veiculos
    ADD CONSTRAINT veiculos_pkey PRIMARY KEY (id);


--
-- Name: veiculos veiculos_placa_key; Type: CONSTRAINT; Schema: public; Owner: sub3
--

ALTER TABLE ONLY public.veiculos
    ADD CONSTRAINT veiculos_placa_key UNIQUE (placa);


--
-- PostgreSQL database dump complete
--

\unrestrict 5G1cw4GdtQBskbUqwtvdrJQaegqGCXZSTsiRaBhl9L59HikqZNX2a8aKaaB21Tq

--
-- PostgreSQL database cluster dump complete
--

