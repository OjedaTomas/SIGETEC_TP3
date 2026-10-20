-- SIGETEC - Esquema relacional MySQL 8.0+
-- Ejecutar una vez en MySQL Workbench con un usuario autorizado.
-- No contiene datos personales reales de clientes ni de la empresa.

CREATE DATABASE IF NOT EXISTS sigetec
  CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE sigetec;

-- Una tabla no debe repetir en cada fila datos que pertenecen a otra entidad.
CREATE TABLE IF NOT EXISTS clientes (
  id_cliente BIGINT AUTO_INCREMENT PRIMARY KEY,
  nombre VARCHAR(100) NOT NULL,
  telefono VARCHAR(30) NOT NULL,
  email VARCHAR(150) NULL,
  activo BOOLEAN NOT NULL DEFAULT TRUE,
  CONSTRAINT chk_cliente_nombre CHECK (CHAR_LENGTH(TRIM(nombre)) >= 2)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS tecnicos (
  id_tecnico BIGINT AUTO_INCREMENT PRIMARY KEY,
  nombre VARCHAR(100) NOT NULL,
  especialidad VARCHAR(80) NOT NULL,
  activo BOOLEAN NOT NULL DEFAULT TRUE
) ENGINE=InnoDB;

-- Catálogo centralizado: nombres de servicios no repetidos manualmente.
CREATE TABLE IF NOT EXISTS tipos_servicio (
  codigo VARCHAR(20) PRIMARY KEY,
  nombre VARCHAR(80) NOT NULL UNIQUE
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS tickets (
  id_ticket BIGINT AUTO_INCREMENT PRIMARY KEY,
  id_cliente BIGINT NOT NULL,
  tipo_codigo VARCHAR(20) NOT NULL,
  descripcion VARCHAR(500) NOT NULL,
  detalle_especifico VARCHAR(250) NOT NULL,
  prioridad VARCHAR(10) NOT NULL,
  estado VARCHAR(20) NOT NULL DEFAULT 'ABIERTO',
  fecha_creacion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_ticket_cliente FOREIGN KEY (id_cliente)
    REFERENCES clientes(id_cliente) ON DELETE RESTRICT,
  CONSTRAINT fk_ticket_tipo FOREIGN KEY (tipo_codigo)
    REFERENCES tipos_servicio(codigo) ON DELETE RESTRICT,
  CONSTRAINT chk_ticket_prioridad CHECK (prioridad IN ('BAJA','MEDIA','ALTA')),
  CONSTRAINT chk_ticket_estado CHECK (estado IN ('ABIERTO','EN_ATENCION','CERRADO')),
  INDEX ix_tickets_cliente (id_cliente),
  INDEX ix_tickets_prioridad (prioridad, fecha_creacion)
) ENGINE=InnoDB;

-- En este prototipo se permite una sola orden por ticket.
CREATE TABLE IF NOT EXISTS ordenes_trabajo (
  id_orden BIGINT AUTO_INCREMENT PRIMARY KEY,
  id_ticket BIGINT NOT NULL UNIQUE,
  fecha_programada DATE NULL,
  estado VARCHAR(25) NOT NULL DEFAULT 'PENDIENTE',
  observaciones VARCHAR(500) NULL,
  fecha_creacion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_orden_ticket FOREIGN KEY (id_ticket)
    REFERENCES tickets(id_ticket) ON DELETE RESTRICT,
  CONSTRAINT chk_orden_estado CHECK (estado IN ('PENDIENTE','PROGRAMADA','EN_EJECUCION','FINALIZADA')),
  INDEX ix_orden_fecha (fecha_programada, estado)
) ENGINE=InnoDB;

-- Resuelve una relación N a N entre órdenes y técnicos (normalización).
CREATE TABLE IF NOT EXISTS asignaciones (
  id_orden BIGINT NOT NULL,
  id_tecnico BIGINT NOT NULL,
  fecha_asignacion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id_orden, id_tecnico),
  CONSTRAINT fk_asignacion_orden FOREIGN KEY (id_orden)
    REFERENCES ordenes_trabajo(id_orden) ON DELETE RESTRICT,
  CONSTRAINT fk_asignacion_tecnico FOREIGN KEY (id_tecnico)
    REFERENCES tecnicos(id_tecnico) ON DELETE RESTRICT,
  INDEX ix_asignacion_tecnico (id_tecnico)
) ENGINE=InnoDB;

-- La clave compuesta obliga a que la intervención sea de un técnico asignado.
CREATE TABLE IF NOT EXISTS intervenciones (
  id_intervencion BIGINT AUTO_INCREMENT PRIMARY KEY,
  id_orden BIGINT NOT NULL,
  id_tecnico BIGINT NOT NULL,
  descripcion VARCHAR(800) NOT NULL,
  resultado VARCHAR(500) NOT NULL,
  fecha_registro TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_intervencion_asignacion FOREIGN KEY (id_orden, id_tecnico)
    REFERENCES asignaciones(id_orden, id_tecnico) ON DELETE RESTRICT,
  INDEX ix_intervencion_orden (id_orden, fecha_registro)
) ENGINE=InnoDB;
