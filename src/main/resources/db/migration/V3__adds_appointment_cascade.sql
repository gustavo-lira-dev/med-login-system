-- noinspection SqlAddNotNullColumnForFile
-- noinspection SqlResolveForFile

-- Working nominal, removing noinspection will show severe errors and warnings that do not happen; Flyway can't see the
-- datasource inside the Docker and causes such advices to pop up.

-- 1. Remove the old foreign keys that were blocking the deletion
ALTER TABLE tb_appointments DROP CONSTRAINT fk_appointments_client;
ALTER TABLE tb_appointments DROP CONSTRAINT fk_appointments_medic;

-- 2. Re-create the foreign keys with the automatic ON DELETE CASCADE behavior
ALTER TABLE tb_appointments
    ADD CONSTRAINT fk_appointments_client FOREIGN KEY (client_id) REFERENCES tb_users(id) ON DELETE CASCADE;

ALTER TABLE tb_appointments
    ADD CONSTRAINT fk_appointments_medic FOREIGN KEY (medic_id) REFERENCES tb_users(id) ON DELETE CASCADE;