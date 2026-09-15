-- ============================================================================
-- Description: Creates the user sequence and the core users table.
-- Version: V1
-- ============================================================================

-- Create sequence for user identifiers
CREATE SEQUENCE seq_user_id
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

-- Create sequence for appointments identifiers
CREATE SEQUENCE seq_appointment_id
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

-- Create core users table
CREATE TABLE tb_users (
                        id BIGINT DEFAULT nextval('seq_user_id'),
                        email VARCHAR(100) NOT NULL,
                        password VARCHAR(255) NOT NULL,
                        role VARCHAR(15),

                        CONSTRAINT chk_role CHECK (role IN ('CLIENT', 'MEDIC', 'ADMIN')),
                        CONSTRAINT pk_users PRIMARY KEY (id),
                        CONSTRAINT uk_users_email UNIQUE (email)
);
-- Create core appointments table
CREATE TABLE tb_appointments (
                        id BIGINT DEFAULT nextval('seq_appointment_id'),
                        status VARCHAR(20),

                        CONSTRAINT pk_appointments PRIMARY KEY (id),
                        CONSTRAINT chk_status CHECK (status IN ('ON_ANALYSIS', 'ADMITTED', 'CANCELLED'))
);