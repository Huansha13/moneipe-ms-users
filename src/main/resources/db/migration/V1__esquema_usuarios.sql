CREATE TABLE usuarios (
    id          UUID PRIMARY KEY,
    username    VARCHAR(50)  NOT NULL UNIQUE,
    nombres     VARCHAR(100),
    apellidos   VARCHAR(100),
    email       VARCHAR(150),
    activo      BOOLEAN NOT NULL DEFAULT TRUE,
    created_at  TIMESTAMP,
    updated_at  TIMESTAMP,
    created_by  UUID,
    updated_by  UUID
);

CREATE INDEX idx_usuarios_username ON usuarios (username);
CREATE INDEX idx_usuarios_activo ON usuarios (activo);
