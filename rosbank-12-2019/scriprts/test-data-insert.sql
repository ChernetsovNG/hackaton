
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";


INSERT INTO clients (id, first_name) VALUES (uuid_generate_v1(), 'Ivan', 'Ivanov');

