-- Migración 002: Borrado lógico — agregar columna activo a restaurantes y combos
-- Ejecutar en Aiven MySQL (defaultdb)
-- Las tablas usuarios, clientes y repartidores ya tienen activo o estado equivalente

ALTER TABLE restaurantes
    ADD COLUMN activo BOOLEAN NOT NULL DEFAULT TRUE;

ALTER TABLE combos
    ADD COLUMN activo BOOLEAN NOT NULL DEFAULT TRUE;

-- Verificar
SELECT 'restaurantes' AS tabla, COUNT(*) AS total, SUM(activo) AS activos FROM restaurantes
UNION ALL
SELECT 'combos',       COUNT(*),                   SUM(activo)              FROM combos;
