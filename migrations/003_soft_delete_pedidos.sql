-- Migración 003: Borrado lógico para pedidos
-- El endpoint DELETE /api/admin/pedidos/{id} ahora desactiva en lugar de borrar físicamente.

ALTER TABLE pedidos
    ADD COLUMN activo BOOLEAN NOT NULL DEFAULT TRUE;

-- Verificar
SELECT COUNT(*) AS total, SUM(activo) AS activos FROM pedidos;
