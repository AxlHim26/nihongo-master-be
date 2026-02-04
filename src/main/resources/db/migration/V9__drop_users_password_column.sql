-- Remove legacy password column accidentally created by JPA ddl-auto update
ALTER TABLE users DROP COLUMN IF EXISTS password;
