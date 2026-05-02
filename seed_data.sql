-- ========================================
-- SCRIPT DE DATOS DE PRUEBA (SEED)
-- ========================================
USE cletaeats;

-- Desactivar llaves foráneas temporalmente para poder limpiar las tablas sin errores
SET FOREIGN_KEY_CHECKS = 0;

TRUNCATE TABLE quejas;
TRUNCATE TABLE pedido_detalle;
TRUNCATE TABLE pedidos;
TRUNCATE TABLE combos;
TRUNCATE TABLE restaurantes;
TRUNCATE TABLE admins;
TRUNCATE TABLE repartidores;
TRUNCATE TABLE clientes;
TRUNCATE TABLE usuarios;

SET FOREIGN_KEY_CHECKS = 1;

-- ========================================
-- 1. INSERTAR USUARIOS
-- Nota: El hash $2a$10$xyz... es representativo. 
-- Para hacer pruebas reales de login, te sugiero registrar los usuarios desde Postman
-- y luego copiar los hashes que MySQL genere en esta plantilla.
-- ========================================
INSERT INTO usuarios (id, username, password, rol, activo) VALUES 
(1, 'admin_juan', '$2a$10$EixZaYVK1fsbw1ZfbX3OXePaWxn96p36WQoeG6Lruj3vjQsGVu/Uu', 'admin', 1),
(2, 'cliente_maria', '$2a$10$EixZaYVK1fsbw1ZfbX3OXePaWxn96p36WQoeG6Lruj3vjQsGVu/Uu', 'cliente', 1),
(3, 'repa_carlos', '$2a$10$EixZaYVK1fsbw1ZfbX3OXePaWxn96p36WQoeG6Lruj3vjQsGVu/Uu', 'repartidor', 1);

-- ========================================
-- 2. INSERTAR PERFILES
-- ========================================
INSERT INTO admins (usuario_id, nombre) VALUES (1, 'Juan Perez (Admin Principal)');

INSERT INTO clientes (usuario_id, cedula, nombre, direccion, telefono, email, tarjeta, estado) VALUES 
(2, '1-1111-1111', 'Maria Gonzalez', 'San Jose, Centro', '8888-8888', 'maria@email.com', '4000-1234-5678-9010', 'activo');

INSERT INTO repartidores (usuario_id, cedula, nombre, email, direccion, telefono, tarjeta, estado, km_recorridos, amonestaciones) VALUES 
(3, '2-2222-2222', 'Carlos Jimenez', 'carlos@repartidor.com', 'Alajuela', '7777-7777', '4000-1111-2222-3333', 'disponible', 0, 0);

-- ========================================
-- 3. INSERTAR RESTAURANTES Y COMBOS
-- ========================================
INSERT INTO restaurantes (id, nombre, cedula_juridica, direccion, tipo_comida) VALUES 
(1, 'Cleta Burgers', '3-101-000001', 'Heredia', 'Comida Rápida');

INSERT INTO combos (restaurante_id, numero_combo, nombre, precio) VALUES 
(1, 1, 'Combo 1: Hamburguesa Clasica', 4000),
(1, 2, 'Combo 2: Hamburguesa Doble', 5000),
(1, 3, 'Combo 3: Hamburguesa Triple', 6000);

-- ========================================
-- 4. INSERTAR PEDIDOS
-- ========================================
INSERT INTO pedidos (id, cliente_id, restaurante_id, repartidor_id, estado, distancia_km, subtotal, costo_envio, iva, total) VALUES 
(1, 1, 1, 1, 'entregado', 5.5, 9000, 1500, 1170, 11670),
(2, 1, 1, NULL, 'preparacion', 3.0, 4000, 1000, 520, 5520);

-- ========================================
-- 5. INSERTAR DETALLES DE PEDIDO
-- ========================================
-- Detalles del pedido 1
INSERT INTO pedido_detalle (pedido_id, combo_id, cantidad, precio_unitario, agrandado, notas) VALUES 
(1, 1, 1, 4000, FALSE, 'Sin cebolla'),
(1, 2, 1, 5000, TRUE, 'Con extra queso');

-- Detalles del pedido 2
INSERT INTO pedido_detalle (pedido_id, combo_id, cantidad, precio_unitario, agrandado, notas) VALUES 
(2, 1, 1, 4000, FALSE, 'Normal');
