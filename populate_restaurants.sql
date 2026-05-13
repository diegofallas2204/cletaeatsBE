-- ========================================
-- SCRIPT PARA POBLAR RESTAURANTES Y COMBOS (CORREGIDO)
-- ========================================
USE cletaeats;

SET FOREIGN_KEY_CHECKS = 0;
TRUNCATE TABLE combos;
TRUNCATE TABLE restaurantes;
SET FOREIGN_KEY_CHECKS = 1;

-- 1. INSERTAR RESTAURANTES (Uno por categoría)
INSERT INTO restaurantes (id, nombre, cedula_juridica, direccion, tipo_comida) VALUES 
(1, 'Pizzeria La Nonna', '3-101-000001', 'Heredia, Centro', 'Pizza'),
(2, 'Burger Factory', '3-101-000002', 'San Jose, Escazu', 'Burger'),
(3, 'Pasta Italia', '3-101-000003', 'Alajuela, Centro', 'Pasta'),
(4, 'Green Garden', '3-101-000004', 'Cartago, Centro', 'Ensalada'),
(5, 'Sakura Sushi', '3-101-000005', 'Heredia, Belen', 'Sushi'),
(6, 'Aroma Café', '3-101-000006', 'San Jose, Curridabat', 'Café'),
(7, 'Sweet Delights', '3-101-000007', 'Alajuela, Guacima', 'Postres'),
(8, 'Taco Loco', '3-101-000008', 'Cartago, Paraiso', 'Tacos'),
(9, 'Crispy Chicken', '3-101-000009', 'Heredia, San Joaquin', 'Pollo'),
(10, 'Great Wall', '3-101-000010', 'San Jose, Barrio Chino', 'China'),
(11, 'Mariscos del Puerto', '3-101-000011', 'Puntarenas, Paseo de los Turistas', 'Mariscos'),
(12, 'Smoothie Bar', '3-101-000012', 'Guanacaste, Tamarindo', 'Bebidas');

-- 2. INSERTAR COMBOS (6 por restaurante)
-- Lógica de precio: (numero_combo * 1000) + 3000

-- Restaurante 1: Pizza
INSERT INTO combos (restaurante_id, numero_combo, nombre, precio) VALUES 
(1, 1, 'Margarita Personal', 4000), (1, 2, 'Pepperoni Mediana', 5000), (1, 3, 'Suprema Grande', 6000),
(1, 4, 'Hawaiana Familiar', 7000), (1, 5, 'Veggie Lover', 8000), (1, 6, 'Meat Lovers King', 9000);

-- Restaurante 2: Burger
INSERT INTO combos (restaurante_id, numero_combo, nombre, precio) VALUES 
(2, 1, 'Classic Burger', 4000), (2, 2, 'Double Cheese', 5000), (2, 3, 'Bacon Monster', 6000),
(2, 4, 'Crispy Chicken Sandwich', 7000), (2, 5, 'Fish Burger Meal', 8000), (2, 6, 'The Ultimate Factory', 9000);

-- Restaurante 3: Pasta
INSERT INTO combos (restaurante_id, numero_combo, nombre, precio) VALUES 
(3, 1, 'Spaghetti Pomodoro', 4000), (3, 2, 'Fettuccine Alfredo', 5000), (3, 3, 'Lasagna de Carne', 6000),
(3, 4, 'Penne Arrabiata', 7000), (3, 5, 'Ravioli de Queso', 8000), (3, 6, 'Gnocchi al Pesto', 9000);

-- Restaurante 4: Ensalada
INSERT INTO combos (restaurante_id, numero_combo, nombre, precio) VALUES 
(4, 1, 'Ensalada Cesar', 4000), (4, 2, 'Greek Salad', 5000), (4, 3, 'Quinoa Bowl', 6000),
(4, 4, 'Santa Fe Chicken', 7000), (4, 5, 'Tuna Poke Salad', 8000), (4, 6, 'Superfood Mix', 9000);

-- Restaurante 5: Sushi
INSERT INTO combos (restaurante_id, numero_combo, nombre, precio) VALUES 
(5, 1, 'California Roll', 4000), (5, 2, 'Philadelphia Roll', 5000), (5, 3, 'Spicy Tuna', 6000),
(5, 4, 'Tiger Roll', 7000), (5, 5, 'Dragon Roll Especial', 8000), (5, 6, 'Chef Platter 20pcs', 9000);

-- Restaurante 6: Café
INSERT INTO combos (restaurante_id, numero_combo, nombre, precio) VALUES 
(6, 1, 'Expresso + Muffin', 4000), (6, 2, 'Capuccino + Croissant', 5000), (6, 3, 'Latte + Tarta', 6000),
(6, 4, 'Mocha + Cheesecake', 7000), (6, 5, 'Frappe + Brownie', 8000), (6, 6, 'Coffee Lovers Pack', 9000);

-- Restaurante 7: Postres
INSERT INTO combos (restaurante_id, numero_combo, nombre, precio) VALUES 
(7, 1, 'Copa de Helado', 4000), (7, 2, 'Porción de Tiramisu', 5000), (7, 3, 'Tres Leches', 6000),
(7, 4, 'Pie de Limón', 7000), (7, 5, 'Volcán de Chocolate', 8000), (7, 6, 'Mega Dessert Platter', 9000);

-- Restaurante 8: Tacos
INSERT INTO combos (restaurante_id, numero_combo, nombre, precio) VALUES 
(8, 1, 'Taco al Pastor (3)', 4000), (8, 2, 'Gringa de Pollo', 5000), (8, 3, 'Quesabirria', 6000),
(8, 4, 'Burrito Supremo', 7000), (8, 5, 'Enchiladas Suizas', 8000), (8, 6, 'Taco Loco Family Pack', 9000);

-- Restaurante 9: Pollo
INSERT INTO combos (restaurante_id, numero_combo, nombre, precio) VALUES 
(9, 1, '2 Piezas + Papas', 4000), (9, 2, '3 Piezas + Acompaño', 5000), (9, 3, 'Burger de Pollo', 6000),
(9, 4, 'Bucket 8 Piezas', 7000), (9, 5, 'Bucket 12 Piezas', 8000), (9, 6, 'Bucket Familiar Completo', 9000);

-- Restaurante 10: China
INSERT INTO combos (restaurante_id, numero_combo, nombre, precio) VALUES 
(10, 1, 'Arroz Frito + Chop Suey', 4000), (10, 2, 'Pollo Agridulce', 5000), (10, 3, 'Cerdo al Jengibre', 6000),
(10, 4, 'Sopa de Wantán', 7000), (10, 5, 'Pato Pekín Porción', 8000), (10, 6, 'Gran Buffet Imperial', 9000);

-- Restaurante 11: Mariscos
INSERT INTO combos (restaurante_id, numero_combo, nombre, precio) VALUES 
(11, 1, 'Ceviche Mixto', 4000), (11, 2, 'Filete de Pargo', 5000), (11, 3, 'Arroz con Calamares', 6000),
(11, 4, 'Langosta a la Mantequilla', 7000), (11, 5, 'Sopa de Mariscos', 8000), (11, 6, 'Gran Banquete Marino', 9000);

-- Restaurante 12: Bebidas
INSERT INTO combos (restaurante_id, numero_combo, nombre, precio) VALUES 
(12, 1, 'Smoothie Tropical', 4000), (12, 2, 'Jugo Natural Grande', 5000), (12, 3, 'Batido de Fresa', 6000),
(12, 4, 'Limonada Imperial', 7000), (12, 5, 'Té Frío Artesanal', 8000), (12, 6, 'Pack 4 Bebidas Premium', 9000);
