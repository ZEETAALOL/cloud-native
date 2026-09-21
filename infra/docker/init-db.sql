-- Script de inicialización de base de datos PostgreSQL
-- Pedidos360 - Cloud Native Architecture

-- Base de datos ya está creada por POSTGRES_DB
-- Solo necesitamos crear esquemas si es necesario

-- Crear esquema para Audit si no existe
CREATE SCHEMA IF NOT EXISTS audit;

-- Crear esquema para Orders si no existe
CREATE SCHEMA IF NOT EXISTS orders;

-- Crear esquema para Report si no existe
CREATE SCHEMA IF NOT EXISTS report;

-- Mensaje de confirmación
DO $$
BEGIN
    RAISE NOTICE 'Base de datos Pedidos360 inicializada correctamente';
END $$;
