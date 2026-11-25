-- Tabla sede
CREATE TABLE sede (
    id BIGSERIAL PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    direccion VARCHAR(200) NOT NULL,
    umbral_confianza NUMERIC(5,2) NOT NULL DEFAULT 85.00
        CHECK (umbral_confianza BETWEEN 0 AND 100),
    activa BOOLEAN NOT NULL DEFAULT TRUE
);

-- Tabla usuario
CREATE TABLE usuario (
    id BIGSERIAL PRIMARY KEY,
    sede_id BIGINT REFERENCES sede(id),
    nombre VARCHAR(100) NOT NULL,
    correo VARCHAR(150) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    rol VARCHAR(30) NOT NULL
        CHECK (rol IN ('ADMIN', 'OPERADOR')),
    activo BOOLEAN NOT NULL DEFAULT TRUE
);

-- Tabla tarifa
CREATE TABLE tarifa (
    id BIGSERIAL PRIMARY KEY,
    sede_id BIGINT NOT NULL REFERENCES sede(id),
    nombre VARCHAR(100) NOT NULL,
    tipo_vehiculo VARCHAR(10) NOT NULL
        CHECK (tipo_vehiculo IN ('CARRO', 'MOTO')),
    franja VARCHAR(10) NOT NULL
        CHECK (franja IN ('DIURNA', 'NOCTURNA')),
    hora_inicio TIME NOT NULL,
    hora_fin TIME NOT NULL,
    valor NUMERIC(12,2) NOT NULL CHECK (valor >= 0),
    activa BOOLEAN NOT NULL DEFAULT TRUE,
    CHECK (hora_inicio <> hora_fin)
);

-- Tabla cupo
CREATE TABLE cupo (
    id BIGSERIAL PRIMARY KEY,
    sede_id BIGINT NOT NULL REFERENCES sede(id),
    codigo VARCHAR(30) NOT NULL,
    tipo_vehiculo VARCHAR(10) NOT NULL
        CHECK (tipo_vehiculo IN ('CARRO', 'MOTO')),
    estado VARCHAR(20) NOT NULL DEFAULT 'DISPONIBLE'
        CHECK (estado IN ('DISPONIBLE', 'OCUPADO', 'INACTIVO')),
    UNIQUE (sede_id, codigo)
);

-- Tabla evento_acceso
CREATE TABLE evento_acceso (
    id BIGSERIAL PRIMARY KEY,
    sede_id BIGINT NOT NULL REFERENCES sede(id),
    cupo_id BIGINT REFERENCES cupo(id),
    usuario_id BIGINT REFERENCES usuario(id),
    placa VARCHAR(15) NOT NULL,
    tipo_vehiculo VARCHAR(10)
        CHECK (tipo_vehiculo IN ('CARRO', 'MOTO')),
    tipo VARCHAR(10) NOT NULL
        CHECK (tipo IN ('INGRESO', 'SALIDA')),
    confianza NUMERIC(5,2)
        CHECK (confianza BETWEEN 0 AND 100),
    lectura_manual BOOLEAN NOT NULL DEFAULT FALSE,
    acceso_autorizado BOOLEAN NOT NULL DEFAULT FALSE,
    motivo VARCHAR(255),
    fecha_hora TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Tabla cobro
CREATE TABLE cobro (
    id BIGSERIAL PRIMARY KEY,
    placa VARCHAR(15) NOT NULL,
    sede_id BIGINT NOT NULL REFERENCES sede(id),
    tarifa_id BIGINT NOT NULL REFERENCES tarifa(id),
    dia_operativo DATE NOT NULL,
    valor NUMERIC(12,2) NOT NULL CHECK (valor >= 0),
    estado VARCHAR(20) NOT NULL DEFAULT 'PENDIENTE'
        CHECK (estado IN (
            'PENDIENTE',
            'PAGADO',
            'VENCIDO',
            'ANULADO'
        )),
    fecha_creacion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT uq_cobro_placa_sede_dia
        UNIQUE (placa, sede_id, dia_operativo)
);

-- Tabla pago
CREATE TABLE pago (
    id BIGSERIAL PRIMARY KEY,
    cobro_id BIGINT NOT NULL UNIQUE REFERENCES cobro(id),
    usuario_id BIGINT REFERENCES usuario(id),
    valor NUMERIC(12,2) NOT NULL CHECK (valor > 0),
    metodo_pago VARCHAR(30) NOT NULL,
    clave_idempotencia VARCHAR(100) NOT NULL UNIQUE,
    fecha_pago TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    pase_valido_hasta TIMESTAMP NOT NULL
);

-- Indices para consultas
CREATE INDEX idx_evento_placa
    ON evento_acceso (placa);

CREATE INDEX idx_evento_fecha
    ON evento_acceso (fecha_hora);

CREATE INDEX idx_cobro_estado
    ON cobro (estado);

CREATE INDEX idx_cupo_disponibilidad
    ON cupo (sede_id, tipo_vehiculo, estado);