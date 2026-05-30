-- Migración 001: Extender ENUM de estados y agregar tabla valoraciones
-- Ejecutar en Railway MySQL

-- 1. Ampliar el ENUM de estados para incluir todos los que usa el backend
ALTER TABLE pedidos MODIFY COLUMN estado
    ENUM('preparacion','aceptado','camino','entregado','suspendido')
    DEFAULT 'preparacion';

-- 2. Tabla de valoraciones (1 por pedido, solo cuando está entregado)
CREATE TABLE IF NOT EXISTS valoraciones (
    id            INT AUTO_INCREMENT PRIMARY KEY,
    pedido_id     INT NOT NULL UNIQUE,
    cliente_id    INT NOT NULL,
    rating        TINYINT NOT NULL CHECK (rating BETWEEN 1 AND 5),
    comentario    TEXT,
    fecha         TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (pedido_id)   REFERENCES pedidos(id) ON DELETE CASCADE,
    FOREIGN KEY (cliente_id)  REFERENCES clientes(id) ON DELETE CASCADE
) ENGINE=InnoDB;
