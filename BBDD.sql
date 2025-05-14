
-- Crear la base de datos
CREATE Schema tienda_pianos;

set search_path to tienda_pianos;


-- Tabla para pianos
CREATE TABLE pianos (
  id SERIAL PRIMARY KEY,
  nombre VARCHAR(100) NOT NULL,
  modelo VARCHAR(100) NOT NULL,
  precio DECIMAL(10, 2) NOT NULL,
  opcion_alquiler DECIMAL(10, 2),
  imagen VARCHAR(255) NOT NULL,
  descripcion TEXT,
  fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  estado VARCHAR(20) DEFAULT 'activo'
);

-- Tablas para especificaciones
CREATE TABLE tipos_especificacion (
  id SERIAL PRIMARY KEY,
  nombre VARCHAR(100) UNIQUE NOT NULL  -- 'Altura', 'Ancho', 'Peso', etc.
);

CREATE TABLE valores_especificacion (
  id SERIAL PRIMARY KEY,
  tipo_id INTEGER REFERENCES tipos_especificacion(id),
  valor VARCHAR(100) NOT NULL,
  UNIQUE(tipo_id, valor)  -- Evita duplicados para el mismo tipo
);

-- Tabla de unión piano-especificación
CREATE TABLE piano_especificaciones (
  piano_id INTEGER REFERENCES pianos(id) ON DELETE CASCADE,
  valor_especificacion_id INTEGER REFERENCES valores_especificacion(id),
  PRIMARY KEY (piano_id, valor_especificacion_id)
);

-- Tablas para características
CREATE TABLE caracteristicas (
  id SERIAL PRIMARY KEY,
  descripcion TEXT UNIQUE NOT NULL  -- 'Sistema de resonancia avanzado', etc.
);

-- Tabla de unión piano-característica
CREATE TABLE piano_caracteristicas (
  piano_id INTEGER REFERENCES pianos(id) ON DELETE CASCADE,
  caracteristica_id INTEGER REFERENCES caracteristicas(id),
  PRIMARY KEY (piano_id, caracteristica_id)
);

-- Tabla para usuarios
CREATE TABLE usuarios (
  id SERIAL PRIMARY KEY,
  nombre VARCHAR(100) NOT NULL,
  email VARCHAR(100) UNIQUE NOT NULL,
  contrasena_hash VARCHAR(255) NOT NULL,
  rol VARCHAR(20) NOT NULL DEFAULT 'user',
  fecha_registro TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  ultimo_login TIMESTAMP
);

-- Tabla para pedidos
CREATE TABLE pedidos (
  id SERIAL PRIMARY KEY,
  usuario_id INTEGER REFERENCES usuarios(id),
  fecha_pedido TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  estado VARCHAR(50) DEFAULT 'pendiente',
  total DECIMAL(10, 2) NOT NULL,
  direccion_envio TEXT,
  metodo_pago VARCHAR(50)
);

-- Tabla para items de pedido
CREATE TABLE items_pedido (
  id SERIAL PRIMARY KEY,
  pedido_id INTEGER REFERENCES pedidos(id) ON DELETE CASCADE,
  piano_id INTEGER REFERENCES pianos(id),
  cantidad INTEGER NOT NULL DEFAULT 1,
  precio_unitario DECIMAL(10, 2) NOT NULL,
  subtotal DECIMAL(10, 2) NOT NULL
);

-- Tabla para carrito
CREATE TABLE carrito (
  id SERIAL PRIMARY KEY,
  usuario_id INTEGER REFERENCES usuarios(id) ON DELETE CASCADE,
  piano_id INTEGER REFERENCES pianos(id),
  cantidad INTEGER NOT NULL DEFAULT 1,
  fecha_agregado TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);




-- Script para poblar la base de datos de ADAGGIO - Piano Store
-- Este script crea datos de ejemplo para todas las tablas

-- Limpieza de tablas (opcional, usar con precaución en producción)
TRUNCATE TABLE carrito CASCADE;
TRUNCATE TABLE items_pedido CASCADE;
TRUNCATE TABLE pedidos CASCADE;
TRUNCATE TABLE piano_caracteristicas CASCADE;
TRUNCATE TABLE piano_especificaciones CASCADE;
TRUNCATE TABLE pianos CASCADE;
TRUNCATE TABLE caracteristicas CASCADE;
TRUNCATE TABLE valores_especificacion CASCADE;
TRUNCATE TABLE tipos_especificacion CASCADE;
TRUNCATE TABLE usuarios CASCADE;

-- Insertar tipos de especificaciones
INSERT INTO tipos_especificacion (nombre) VALUES
('Altura'),
('Ancho'),
('Profundidad'),
('Peso'),
('Acabado'),
('Pedales'),
('Tipo'),
('Longitud'),
('Sistema'),
('Conectividad'),
('Modelo'),
('Edición'),
('Mecanismo');

-- Insertar valores de especificaciones
INSERT INTO valores_especificacion (tipo_id, valor) VALUES
-- Alturas (tipo_id = 1)
(1, '132 cm'),
(1, '145 cm'),
(1, '102 cm'),
-- Anchos (tipo_id = 2)
(2, '155 cm'),
(2, '152 cm'),
(2, '148 cm'),
(2, '156 cm'),
-- Profundidades (tipo_id = 3)
(3, '67 cm'),
-- Pesos (tipo_id = 4)
(4, '280 kg'),
(4, '345 kg'),
-- Acabados (tipo_id = 5)
(5, 'Cromo Blanco'),
(5, 'Negro Pulido'),
(5, 'Macassar'),
-- Pedales (tipo_id = 6)
(6, '3'),
-- Tipos (tipo_id = 7)
(7, 'Piano Vertical'),
(7, 'Piano de Cola'),
-- Longitudes (tipo_id = 8)
(8, '193 cm'),
(8, '211 cm'),
(8, '227 cm'),
-- Sistema (tipo_id = 9)
(9, 'Spirio'),
(9, 'Estándar'),
-- Conectividad (tipo_id = 10)
(10, 'Bluetooth y Wi-Fi'),
(10, 'Ninguna'),
-- Modelo (tipo_id = 11)
(11, 'B-211'),
(11, 'K-132'),
-- Edición (tipo_id = 12)
(12, '8x8 Masterpiece'),
(12, 'Estándar'),
-- Mecanismo (tipo_id = 13)
(13, 'Estándar Steinway'),
(13, 'Sistema de regulación de precisión');

-- Insertar características
INSERT INTO caracteristicas (descripcion) VALUES
('Mueble diseñado por aclamados diseñadores'),
('Teclas de marfil premium'),
('Sistema de resonancia avanzado'),
('Cuerdas importadas de Alemania'),
('Garantía de 10 años'),
('Diseño de cola tradicional'),
('Tapa superior con soporte ajustable'),
('Teclas de marfil sintético de alta calidad'),
('Sistema de regulación de precisión'),
('Mecanismo de alta sensibilidad'),
('Garantía de fabricante de 12 años'),
('Sistema Spirio de reproducción de alta resolución'),
('Biblioteca de música integrada'),
('Aplicación móvil para control remoto'),
('Actualizaciones de repertorio periódicas'),
('Grabación de interpretaciones en tiempo real'),
('Artesanía Steinway tradicional'),
('Diseño exclusivo de edición limitada'),
('Madera de ébano de Macassar de alta calidad'),
('Construcción artesanal de precisión'),
('Sonido característico Steinway'),
('Certificado de autenticidad incluido'),
('Garantía extendida de 15 años');

-- Insertar pianos
INSERT INTO pianos (nombre, modelo, precio, opcion_alquiler, imagen, descripcion, estado) VALUES
('STEINWAY & SONS', 'K-132 CHROME BLANCO', '39325.00', '464.00', 'assets/K-132_CHROME_BLANCO.png',
 'Piano vertical profesional de la prestigiosa marca Steinway & Sons, acabado en cromo blanco. Este elegante instrumento ofrece un sonido excepcional y una presencia visual imponente en cualquier espacio.',
 'activo'),

('BOSTON', 'GP-193 PE II', '39325.00', '464.00', 'assets/GP-193_PE II.jpg',
 'Piano de cola elegante de la marca Boston, con un sonido excepcional y acabado premium. Ideal para salas de concierto y espacios amplios donde se aprecie la calidad del sonido.',
 'activo'),

('STEINWAY & SONS', 'Spiro', '390325.00', '4604.00', 'assets/Spiro.png',
 'El revolucionario Steinway Spirio es el piano de alta resolución que reproduce con precisión las interpretaciones de los pianistas más destacados del mundo. Una combinación perfecta de artesanía tradicional y tecnología innovadora.',
 'activo'),

('STEINWAY & SONS', 'B-211 8x8', '39325.00', '464.00', 'assets/steinway-sons-b211-spirio-r-masterpiece-8x8-macassar-3.jpg',
 'El Steinway B-211 8x8 es una pieza excepcional con un diseño único de chapa de ébano de Macassar. Este piano de cola combina la tradición acústica de Steinway con un diseño contemporáneo de edición limitada.',
 'activo');

-- Asignar especificaciones a los pianos
-- Piano 1: STEINWAY & SONS K-132 CHROME BLANCO
INSERT INTO piano_especificaciones (piano_id, valor_especificacion_id) VALUES
(1, 1),  -- Altura: 132 cm
(1, 4),  -- Ancho: 155 cm
(1, 7),  -- Profundidad: 67 cm
(1, 8),  -- Peso: 280 kg
(1, 10), -- Acabado: Cromo Blanco
(1, 13), -- Pedales: 3
(1, 14), -- Tipo: Piano Vertical
(1, 24); -- Modelo: K-132

-- Piano 2: BOSTON GP-193 PE II
INSERT INTO piano_especificaciones (piano_id, valor_especificacion_id) VALUES
(2, 16), -- Longitud: 193 cm
(2, 5),  -- Ancho: 152 cm
(2, 3),  -- Altura: 102 cm
(2, 9),  -- Peso: 345 kg
(2, 11), -- Acabado: Negro Pulido
(2, 13), -- Pedales: 3
(2, 15), -- Tipo: Piano de Cola
(2, 28); -- Mecanismo: Sistema de regulación de precisión

-- Piano 3: STEINWAY & SONS Spiro
INSERT INTO piano_especificaciones (piano_id, valor_especificacion_id) VALUES
(3, 15), -- Tipo: Piano de Cola
(3, 18), -- Longitud: 227 cm
(3, 6),  -- Ancho: 156 cm
(3, 19), -- Sistema: Spirio
(3, 11), -- Acabado: Negro Pulido
(3, 20); -- Conectividad: Bluetooth y Wi-Fi

-- Piano 4: STEINWAY & SONS B-211 8x8
INSERT INTO piano_especificaciones (piano_id, valor_especificacion_id) VALUES
(4, 17), -- Longitud: 211 cm
(4, 5),  -- Ancho: 152 cm
(4, 23), -- Modelo: B-211
(4, 25), -- Edición: 8x8 Masterpiece
(4, 12), -- Acabado: Macassar
(4, 27); -- Mecanismo: Estándar Steinway

-- Asignar características a los pianos
-- Piano 1: STEINWAY & SONS K-132 CHROME BLANCO
INSERT INTO piano_caracteristicas (piano_id, caracteristica_id) VALUES
(1, 1), -- Mueble diseñado por aclamados diseñadores
(1, 2), -- Teclas de marfil premium
(1, 3), -- Sistema de resonancia avanzado
(1, 4), -- Cuerdas importadas de Alemania
(1, 5); -- Garantía de 10 años

-- Piano 2: BOSTON GP-193 PE II
INSERT INTO piano_caracteristicas (piano_id, caracteristica_id) VALUES
(2, 6),  -- Diseño de cola tradicional
(2, 7),  -- Tapa superior con soporte ajustable
(2, 8),  -- Teclas de marfil sintético de alta calidad
(2, 9),  -- Sistema de regulación de precisión
(2, 10), -- Mecanismo de alta sensibilidad
(2, 11); -- Garantía de fabricante de 12 años

-- Piano 3: STEINWAY & SONS Spiro
INSERT INTO piano_caracteristicas (piano_id, caracteristica_id) VALUES
(3, 12), -- Sistema Spirio de reproducción de alta resolución
(3, 13), -- Biblioteca de música integrada
(3, 14), -- Aplicación móvil para control remoto
(3, 15), -- Actualizaciones de repertorio periódicas
(3, 16), -- Grabación de interpretaciones en tiempo real
(3, 17); -- Artesanía Steinway tradicional

-- Piano 4: STEINWAY & SONS B-211 8x8
INSERT INTO piano_caracteristicas (piano_id, caracteristica_id) VALUES
(4, 18), -- Diseño exclusivo de edición limitada
(4, 19), -- Madera de ébano de Macassar de alta calidad
(4, 20), -- Construcción artesanal de precisión
(4, 21), -- Sonido característico Steinway
(4, 22), -- Certificado de autenticidad incluido
(4, 23); -- Garantía extendida de 15 años

-- Insertar usuarios (contraseñas: se mostrará 'password' en texto plano, pero en realidad serían hashes)
-- En un sistema real, estas contraseñas estarían hasheadas con bcrypt o similar
INSERT INTO usuarios (nombre, email, contrasena_hash, rol, fecha_registro, ultimo_login) VALUES
('Administrador', 'admin@adaggio.com', '$2a$10$X7VYJfXZtQ5L4Db5xJ9i8O6LQQw5zcSUemJJm8iR/pv9vf77Tyi1.', 'admin', NOW(), NOW()),
('Cliente Ejemplo', 'user@example.com', '$2a$10$YFKj8LDzaXz1QNh3mGFCp.XZ6U5KHQ1PT.ugqr.8T5ey4Xh3MZ9B.', 'user', NOW(), NOW()),
('Juan Pérez', 'juan@example.com', '$2a$10$RyPUZlcQPQsZF2qOXY8aZO5fT1iV9dZ2Q0hY6g3PZ8iK3.q.WMWOq', 'user', NOW() - INTERVAL '10 days', NOW() - INTERVAL '2 days');

-- Insertar pedidos
INSERT INTO pedidos (usuario_id, fecha_pedido, estado, total, direccion_envio, metodo_pago) VALUES
(2, NOW() - INTERVAL '5 days', 'completado', 39325.00, 'Calle Principal 123, Madrid, España', 'tarjeta'),
(3, NOW() - INTERVAL '8 days', 'completado', 39325.00, 'Av. Central 456, Barcelona, España', 'transferencia'),
(3, NOW() - INTERVAL '1 day', 'pendiente', 390325.00, 'Av. Central 456, Barcelona, España', 'financiación');

-- Insertar items de pedido
INSERT INTO items_pedido (pedido_id, piano_id, cantidad, precio_unitario, subtotal) VALUES
(1, 1, 1, 39325.00, 39325.00),
(2, 2, 1, 39325.00, 39325.00),
(3, 3, 1, 390325.00, 390325.00);

-- Insertar items en el carrito
INSERT INTO carrito (usuario_id, piano_id, cantidad, fecha_agregado) VALUES
(2, 4, 1, NOW()),
(3, 1, 1, NOW() - INTERVAL '2 days');

-- Confirmación final
SELECT 'Base de datos poblada con éxito. Creados:' AS mensaje;
SELECT COUNT(*) AS "Pianos" FROM pianos;
SELECT COUNT(*) AS "Especificaciones" FROM valores_especificacion;
SELECT COUNT(*) AS "Características" FROM caracteristicas;
SELECT COUNT(*) AS "Usuarios" FROM usuarios;
SELECT COUNT(*) AS "Pedidos" FROM pedidos;
SELECT COUNT(*) AS "Items de carrito" FROM carrito;