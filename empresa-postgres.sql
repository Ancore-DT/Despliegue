-- ============================================
-- Script PostgreSQL para Sistema de Gestión Empresarial
-- ============================================
-- Uso:
-- psql -U postgres -d empresa -f empresa-postgres.sql
-- O desde pgAdmin: copiar y ejecutar todo el script

-- Crear base de datos (ejecutar como superusuario)
-- DROP DATABASE IF EXISTS empresa;
-- CREATE DATABASE empresa
--     WITH 
--     ENCODING = 'UTF8'
--     LC_COLLATE = 'en_US.UTF-8'
--     LC_CTYPE = 'en_US.UTF-8';

-- Conectar a la base de datos
-- \c empresa;

-- ============================================
-- ELIMINAR TABLAS EXISTENTES (opcional)
-- ============================================
DROP TABLE IF EXISTS roles CASCADE;
DROP TABLE IF EXISTS empleados CASCADE;
DROP TABLE IF EXISTS usuarios CASCADE;

-- ============================================
-- CREAR TABLAS
-- ============================================

-- Tabla de usuarios
CREATE TABLE usuarios (
    id BIGSERIAL PRIMARY KEY,
    activo BOOLEAN NOT NULL DEFAULT true,
    password VARCHAR(255),
    username VARCHAR(255) NOT NULL UNIQUE,
    reset_token VARCHAR(255),
    reset_token_expiry TIMESTAMP,
    email VARCHAR(255) NOT NULL UNIQUE,
    fecha_ultimo_acceso TIMESTAMP,
    rol VARCHAR(20) NOT NULL CHECK (rol IN ('ADMIN', 'MANAGER', 'USER'))
);

-- Tabla de empleados
CREATE TABLE empleados (
    id BIGSERIAL PRIMARY KEY,
    nombre VARCHAR(255),
    apellido VARCHAR(255),
    cargo VARCHAR(255),
    salario DOUBLE PRECISION,
    email VARCHAR(255) UNIQUE,
    fecha_creacion TIMESTAMP,
    fecha_actualizacion TIMESTAMP,
    usuario VARCHAR(50),
    usuario_rol VARCHAR(20) CHECK (usuario_rol IN ('ADMIN', 'MANAGER', 'USER')),
    usuario_id BIGINT UNIQUE,
    CONSTRAINT fk_empleados_usuario FOREIGN KEY (usuario_id) 
        REFERENCES usuarios(id) ON DELETE SET NULL
);

-- Tabla de roles
CREATE TABLE roles (
    usuario_id BIGINT NOT NULL,
    rol VARCHAR(255),
    CONSTRAINT fk_roles_usuario FOREIGN KEY (usuario_id) 
        REFERENCES usuarios(id) ON DELETE CASCADE
);

-- ============================================
-- CREAR ÍNDICES
-- ============================================
CREATE INDEX idx_empleados_email ON empleados(email);
CREATE INDEX idx_empleados_cargo ON empleados(cargo);
CREATE INDEX idx_usuarios_username ON usuarios(username);
CREATE INDEX idx_usuarios_email ON usuarios(email);
CREATE INDEX idx_roles_usuario_id ON roles(usuario_id);

-- ============================================
-- INSERTAR DATOS INICIALES
-- ============================================

-- Usuarios (contraseñas encriptadas con BCrypt)
-- admin: admin123
-- user: user123
-- ConejoDC: password123
INSERT INTO usuarios (activo, password, username, email, rol) VALUES
(true, '$2y$10$nB9QX8PTKk.2TlgCciLzbe3/AzLI/gSBtPFnrMY7/QIbrZFwO8dBG', 'admin', 'admin@empresa.com', 'ADMIN'),
(true, '$2a$10$leBOrZU4dJg/oOZAu0ikTuvBiqvlmEnoKPwgiNNa8qMrNo23Y6gli', 'user', 'user@empresa.com', 'USER'),
(true, '$2a$10$KUDQQQ4/pwg5Jj5ZjctjB.3n7yc9o3f2UhN0b1vh.dqHmOZ4uzB2u', 'ConejoDC', 'conejoDC@empresa.com', 'ADMIN');

-- Roles de usuarios
INSERT INTO roles (usuario_id, rol) VALUES
(1, 'ADMIN'),
(1, 'USER'),
(2, 'USER'),
(3, 'ADMIN');

-- Empleados de ejemplo
INSERT INTO empleados (nombre, apellido, cargo, salario, email, fecha_creacion, fecha_actualizacion) VALUES
('Carlos', 'Martínez', 'Desarrollador', 50000, 'carlos@empresa.com', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Ana', 'García', 'Desarrolladora', 50000, 'ana@empresa.com', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Luis', 'Rodríguez', 'Gerente', 75000, 'luis@empresa.com', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('María', 'López', 'Diseñadora', 45000, 'maria@empresa.com', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- ============================================
-- VERIFICAR DATOS
-- ============================================
SELECT 'Usuarios creados:' AS mensaje;
SELECT id, username, email, rol, activo FROM usuarios;

SELECT 'Roles asignados:' AS mensaje;
SELECT u.username, r.rol 
FROM usuarios u 
JOIN roles r ON u.id = r.usuario_id 
ORDER BY u.username, r.rol;

SELECT 'Empleados creados:' AS mensaje;
SELECT id, nombre, apellido, cargo, salario, email FROM empleados;

-- ============================================
-- INFORMACIÓN ADICIONAL
-- ============================================
-- Credenciales de acceso:
-- Usuario: admin    | Contraseña: admin123    | Rol: ADMIN
-- Usuario: user     | Contraseña: user123     | Rol: USER
-- Usuario: ConejoDC | Contraseña: password123 | Rol: ADMIN

-- Para conectar desde Spring Boot, configurar en application-postgres.properties:
-- spring.datasource.url=jdbc:postgresql://localhost:5432/empresa
-- spring.datasource.username=postgres
-- spring.datasource.password=tu_password

-- ============================================
-- FIN DEL SCRIPT
-- ============================================
