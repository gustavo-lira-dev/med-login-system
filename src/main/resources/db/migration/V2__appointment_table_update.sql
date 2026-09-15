-- noinspection SqlAddNotNullColumnForFile
-- noinspection SqlResolveForFile

-- Working nominal, removing noinspection will show severe errors and warnings that do not happen; Flyway can't see the
-- datasource inside the Docker and causes such advices to pop up.

ALTER TABLE tb_appointments
    ADD COLUMN client_id BIGINT NOT NULL,
    ADD COLUMN medic_id BIGINT NOT NULL,
    ADD COLUMN made_date TIMESTAMP NOT NULL,
    ADD COLUMN scheduled_date TIMESTAMP NOT NULL;


ALTER TABLE tb_appointments
    ADD CONSTRAINT fk_appointments_client FOREIGN KEY (client_id) REFERENCES tb_users(id),
    ADD CONSTRAINT fk_appointments_medic FOREIGN KEY (medic_id) REFERENCES tb_users(id);