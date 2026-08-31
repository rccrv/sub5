FROM postgres:18.4

COPY docker/ddl.sql /docker-entrypoint-initdb.d/01-ddl.sql
COPY misc/aws/postgres/set-app-password.sh /docker-entrypoint-initdb.d/02-set-app-password.sh
