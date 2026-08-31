FROM quay.io/keycloak/keycloak:26.6

COPY docker/sub3-realm.json /opt/keycloak/data/import/sub3-realm-template.json
COPY misc/aws/keycloak/entrypoint.sh /opt/keycloak/bin/cars-galore-entrypoint.sh
RUN chmod 0555 /opt/keycloak/bin/cars-galore-entrypoint.sh

ENTRYPOINT ["/opt/keycloak/bin/cars-galore-entrypoint.sh"]
CMD ["start-dev", "--import-realm"]
