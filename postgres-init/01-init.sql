-- CREATE USER postgres WITH PASSWORD 'postgres' SUPERUSER;
    CREATE DATABASE mood_tracking;
-- CREATE DATABASE keycloak;
GRANT ALL PRIVILEGES ON DATABASE mood_tracking TO postgres;
-- GRANT ALL PRIVILEGES ON DATABASE keycloak TO postgres;