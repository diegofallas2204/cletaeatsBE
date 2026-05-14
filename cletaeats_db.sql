-- ========================================
-- BASE DE DATOS CLETAEATS
-- ========================================
DROP DATABASE IF EXISTS cletaeats;
CREATE DATABASE cletaeats CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE cletaeats;

-- ========================================
-- 1. USUARIOS (LOGIN)
-- ========================================
CREATE TABLE usuarios (
                          id INT AUTO_INCREMENT PRIMARY KEY,
                          username VARCHAR(50) UNIQUE NOT NULL,
                          password VARCHAR(255) NOT NULL, -- Recomendado para hashes
                          rol ENUM('cliente','repartidor','admin') NOT NULL,
                          activo BOOLEAN DEFAULT TRUE
) ENGINE=InnoDB;

-- ========================================
-- 2. CLIENTES
-- ========================================
CREATE TABLE clientes (
                          id INT AUTO_INCREMENT PRIMARY KEY,
                          usuario_id INT UNIQUE NOT NULL,
                          cedula VARCHAR(20) UNIQUE NOT NULL,
                          nombre VARCHAR(100) NOT NULL,
                          direccion TEXT NOT NULL,
                          telefono VARCHAR(20),
                          email VARCHAR(100),
                          tarjeta VARCHAR(50),
                          estado ENUM('activo','suspendido') DEFAULT 'activo',
                          FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- ========================================
-- 3. REPARTIDORES
-- ========================================
CREATE TABLE repartidores (
                              id INT AUTO_INCREMENT PRIMARY KEY,
                              usuario_id INT UNIQUE NOT NULL,
                              cedula VARCHAR(20) UNIQUE NOT NULL,
                              nombre VARCHAR(100) NOT NULL,
                              email VARCHAR(100),
                              direccion TEXT,
                              telefono VARCHAR(20),
                              tarjeta VARCHAR(50),
                              estado ENUM('disponible','ocupado') DEFAULT 'disponible',
                              km_recorridos FLOAT DEFAULT 0,
                              amonestaciones INT DEFAULT 0,
                              FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- ========================================
-- 4. ADMINS
-- ========================================
CREATE TABLE admins (
                        id INT AUTO_INCREMENT PRIMARY KEY,
                        usuario_id INT UNIQUE NOT NULL,
                        nombre VARCHAR(100),
                        FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- ========================================
-- 5. RESTAURANTES
-- ========================================
CREATE TABLE restaurantes (
                              id INT AUTO_INCREMENT PRIMARY KEY,
                              nombre VARCHAR(100) NOT NULL,
                              cedula_juridica VARCHAR(50) UNIQUE NOT NULL,
                              direccion TEXT,
                              tipo_comida VARCHAR(50)
) ENGINE=InnoDB;

-- ========================================
-- 6. COMBOS
-- ========================================
CREATE TABLE combos (
                        id INT AUTO_INCREMENT PRIMARY KEY,
                        restaurante_id INT NOT NULL,
                        numero_combo INT NOT NULL CHECK (numero_combo BETWEEN 1 AND 9),
                        nombre VARCHAR(100) NOT NULL,
                        precio FLOAT NOT NULL, -- Calculado en Java: (numero_combo * 1000) + 3000
                        FOREIGN KEY (restaurante_id) REFERENCES restaurantes(id) ON DELETE CASCADE,
                        UNIQUE(restaurante_id, numero_combo)
) ENGINE=InnoDB;

-- ========================================
-- 7. PEDIDOS
-- ========================================
CREATE TABLE pedidos (
                         id INT AUTO_INCREMENT PRIMARY KEY,
                         cliente_id INT NOT NULL,
                         restaurante_id INT NOT NULL,
                         repartidor_id INT,
                         estado ENUM('preparacion','camino','entregado','suspendido') DEFAULT 'preparacion',
                         distancia_km FLOAT DEFAULT 0, -- Necesario para calcular costo_envio
                         subtotal FLOAT DEFAULT 0,
                         costo_envio FLOAT DEFAULT 0,
                         iva FLOAT DEFAULT 0,
                         total FLOAT DEFAULT 0,
                         fecha_pedido DATETIME DEFAULT CURRENT_TIMESTAMP,
                         fecha_entrega DATETIME NULL,
                         FOREIGN KEY (cliente_id) REFERENCES clientes(id),
                         FOREIGN KEY (restaurante_id) REFERENCES restaurantes(id),
                         FOREIGN KEY (repartidor_id) REFERENCES repartidores(id)
) ENGINE=InnoDB;

-- ========================================
-- 8. DETALLE PEDIDO
-- ========================================
CREATE TABLE pedido_detalle (
                                id INT AUTO_INCREMENT PRIMARY KEY,
                                pedido_id INT NOT NULL,
                                combo_id INT NOT NULL,
                                cantidad INT DEFAULT 1,
                                precio_unitario FLOAT,
                                agrandado BOOLEAN DEFAULT FALSE,
                                notas TEXT,
                                FOREIGN KEY (pedido_id) REFERENCES pedidos(id) ON DELETE CASCADE,
                                FOREIGN KEY (combo_id) REFERENCES combos(id)
) ENGINE=InnoDB;

-- ========================================
-- 9. QUEJAS
-- ========================================
CREATE TABLE quejas (
                        id INT AUTO_INCREMENT PRIMARY KEY,
                        pedido_id INT NOT NULL,
                        repartidor_id INT NOT NULL, -- Referencia directa para reportes rápidos
                        descripcion TEXT NOT NULL,
                        fecha TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                        FOREIGN KEY (pedido_id) REFERENCES pedidos(id),
                        FOREIGN KEY (repartidor_id) REFERENCES repartidores(id)
) ENGINE=InnoDB;

-- ========================================
-- 10. TARJETAS CLIENTE
-- ========================================
CREATE TABLE tarjetas_cliente (
                                  id INT AUTO_INCREMENT PRIMARY KEY,
                                  cliente_id INT NOT NULL,
                                  numero_tarjeta VARCHAR(20) NOT NULL,
                                  fecha_vencimiento VARCHAR(5) NOT NULL,
                                  cvv VARCHAR(4) NOT NULL,
                                  FOREIGN KEY (cliente_id) REFERENCES clientes(id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- ========================================
-- DATOS DE PRUEBA (MOCK DATA)
-- ========================================
-- Usuarios
INSERT INTO usuarios (username, password, rol) VALUES ('cliente1', 'pass123', 'cliente');
INSERT INTO usuarios (username, password, rol) VALUES ('repartidor1', 'pass123', 'repartidor');
INSERT INTO usuarios (username, password, rol) VALUES ('admin1', 'pass123', 'admin');

-- Clientes y Repartidores
INSERT INTO clientes (usuario_id, cedula, nombre, direccion, telefono, email) VALUES (1, '1-1111-1111', 'Juan Perez', 'San Jose Centro', '88888888', 'juan@test.com');
INSERT INTO repartidores (usuario_id, cedula, nombre, direccion, telefono, email) VALUES (2, '2-2222-2222', 'Carlos Gomez', 'Alajuela', '77777777', 'carlos@test.com');
INSERT INTO admins (usuario_id, nombre) VALUES (3, 'Admin Cleta');

-- Tarjetas
INSERT INTO tarjetas_cliente (cliente_id, numero_tarjeta, fecha_vencimiento, cvv) VALUES (1, '**** **** **** 1234', '12/25', '123');

-- Restaurantes
INSERT INTO restaurantes (nombre, cedula_juridica, direccion, tipo_comida) VALUES ('Pizza Hutt', '3-101-123456', 'San Pedro', 'Pizza');
INSERT INTO restaurantes (nombre, cedula_juridica, direccion, tipo_comida) VALUES ('Burger King', '3-101-654321', 'Escazu', 'Burger');

-- Combos
INSERT INTO combos (restaurante_id, numero_combo, nombre, precio) VALUES (1, 1, 'Combo Pizza Personal', 4000);
INSERT INTO combos (restaurante_id, numero_combo, nombre, precio) VALUES (1, 2, 'Combo Pizza Grande', 5000);
INSERT INTO combos (restaurante_id, numero_combo, nombre, precio) VALUES (2, 1, 'Combo Whopper', 4000);