CREATE DATABASE IF NOT EXISTS gestion_alojamientos_ROOMLY_;
USE gestion_alojamientos_ROOMLY_;

-- 1. TABLA: ANFITRIONES
CREATE TABLE anfitriones (
    id_anfitrion INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(50) NOT NULL,
    apellidos VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    telefono VARCHAR(15) NOT NULL,
    usuario VARCHAR(50) UNIQUE NOT NULL,      
    pass VARCHAR(60) NOT NULL       
) ENGINE=InnoDB;

-- 2. TABLA: ALOJAMIENTOS
CREATE TABLE alojamientos (
    id_alojamiento INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    direccion VARCHAR(255) NOT NULL,
    precio_dia DECIMAL(10, 2) NOT NULL,
    id_anfitrion INT NOT NULL,
    FOREIGN KEY (id_anfitrion) REFERENCES anfitriones(id_anfitrion) 
        ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB;

-- 3. TABLA: HUÉSPEDES
CREATE TABLE huespedes (
    id_huesped INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(50) NOT NULL,
    apellidos VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    telefono VARCHAR(15) NOT NULL,
    usuario VARCHAR(50) UNIQUE NOT NULL,      
    pass VARCHAR(60) NOT NULL       
) ENGINE=InnoDB;

-- 4. TABLA: RESERVAS
CREATE TABLE reservas (
    id_reserva INT AUTO_INCREMENT PRIMARY KEY,
    id_alojamiento INT NOT NULL,
    id_huesped INT NOT NULL,
    fecha_entrada DATE NOT NULL,
    fecha_salida DATE NOT NULL,
    precio_total DECIMAL(10, 2) NOT NULL,
    FOREIGN KEY (id_alojamiento) REFERENCES alojamientos(id_alojamiento)
        ON DELETE RESTRICT ON UPDATE CASCADE,
    FOREIGN KEY (id_huesped) REFERENCES huespedes(id_huesped)
        ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT chk_fechas CHECK (fecha_salida > fecha_entrada)
) ENGINE=InnoDB;

-- 5. TABLA NUEVA: OPERARIOS DE MANTENIMIENTO
CREATE TABLE operarios_mantenimiento (
    id_operario INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    usuario VARCHAR(50) UNIQUE NOT NULL,       
    pass VARCHAR(60) NOT NULL
) ENGINE=InnoDB;

-- 6. TABLA MODIFICADA: MANTENIMIENTOS
CREATE TABLE mantenimientos (
    id_mantenimiento INT AUTO_INCREMENT PRIMARY KEY,
    id_alojamiento INT NOT NULL,
    id_operario INT NOT NULL, -- Ahora apunta a la tabla de operarios
    fecha_inicio DATE NOT NULL,
    descripcion VARCHAR(255) NOT NULL,
    estado ENUM('Pendiente', 'En progreso', 'Completado') DEFAULT 'Pendiente',     
    FOREIGN KEY (id_alojamiento) REFERENCES alojamientos(id_alojamiento)
        ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (id_operario) REFERENCES operarios_mantenimiento(id_operario)
        ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB;

-- 7. TABLA: RESEÑAS
CREATE TABLE resenas (
    id_resena INT AUTO_INCREMENT PRIMARY KEY, 
    id_reserva INT UNIQUE NOT NULL,          
    puntuacion INT NOT NULL CHECK (puntuacion BETWEEN 1 AND 5),
    comentario TEXT,
    fecha DATE NOT NULL,
    FOREIGN KEY (id_reserva) REFERENCES reservas(id_reserva)
        ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB;


-- ==========================================
-- INSERCIÓN DE DATOS DE PRUEBA CORREGIDOS
-- ==========================================

-- ANFITRIONES 
INSERT INTO anfitriones (nombre, apellidos, email, telefono, usuario, pass) VALUES
('Carlos', 'García Pérez', 'carlos.garcia@email.com', '600111222', 'carlos_anfi', '$2a$12$e0MbgS/a8U7N9fA3f5.bkuU81E7sIscMvQhks2mIqGZfWbN5Z7pQy'),
('María', 'López Martínez', 'maria.lopez@email.com', '600333444', 'maria_anfi', '$2a$12$e0MbgS/a8U7N9fA3f5.bkuU81E7sIscMvQhks2mIqGZfWbN5Z7pQy'),
('Juan', 'Rodríguez Gómez', 'juan.rod@email.com', '600555666', 'juan_anfi', '$2a$12$e0MbgS/a8U7N9fA3f5.bkuU81E7sIscMvQhks2mIqGZfWbN5Z7pQy');

-- ALOJAMIENTOS 
INSERT INTO alojamientos (nombre, direccion, precio_dia, id_anfitrion) VALUES
('Apartamento Centro Sol', 'Calle Mayor 14, Madrid', 85.00, 1),
('Villa Marítima', 'Av. del Mar 5, Valencia', 150.00, 1),
('Estudio Malasaña', 'Calle Pez 3, Madrid', 60.00, 2),
('Ático Vistas Alhambra', 'Calle Real 22, Granada', 120.00, 3);

-- HUÉSPEDES 
INSERT INTO huespedes (nombre, apellidos, email, telefono, usuario, pass) VALUES
('Ana', 'Sánchez Ruiz', 'ana.sanchez@email.com', '677111111', 'ana_huesped', '$2a$12$e0MbgS/a8U7N9fA3f5.bkuU81E7sIscMvQhks2mIqGZfWbN5Z7pQy'),
('David', 'Fernández Vega', 'david.fer@email.com', '677222222', 'david_huesped', '$2a$12$e0MbgS/a8U7N9fA3f5.bkuU81E7sIscMvQhks2mIqGZfWbN5Z7pQy'),
('Elena', 'Navarro Torres', 'elena.nav@email.com', '677333333', 'elena_huesped', '$2a$12$e0MbgS/a8U7N9fA3f5.bkuU81E7sIscMvQhks2mIqGZfWbN5Z7pQy');

-- RESERVAS 
INSERT INTO reservas (id_alojamiento, id_huesped, fecha_entrada, fecha_salida, precio_total) VALUES
(1, 1, '2026-06-10', '2026-06-15', 425.00), 
(2, 2, '2026-07-01', '2026-07-08', 1050.00), 
(3, 3, '2026-06-20', '2026-06-22', 120.00),  
(4, 1, '2026-08-12', '2026-08-19', 840.00);  

-- OPERARIOS
INSERT INTO operarios_mantenimiento (nombre, usuario, pass) VALUES
('Técnico Sol', 'tecnico_sol', '$2a$12$e0MbgS/a8U7N9fA3f5.bkuU81E7sIscMvQhks2mIqGZfWbN5Z7pQy'),
('Técnico Malasaña', 'tecnico_malasanya', '$2a$12$e0MbgS/a8U7N9fA3f5.bkuU81E7sIscMvQhks2mIqGZfWbN5Z7pQy');

-- MANTENIMIENTOS 
INSERT INTO mantenimientos (id_alojamiento, id_operario, fecha_inicio, descripcion, estado) VALUES
(1, 1, '2026-06-01', 'Arreglo de la cisterna del baño principal', 'Completado'),
(3, 2, '2026-06-04', 'Pintar pared del salón por humedades', 'Pendiente');

-- RESEÑAS
INSERT INTO resenas (id_reserva, puntuacion, comentario, fecha) VALUES
(1, 5, 'Excelente ubicación y todo muy limpio.', '2026-06-16'), 
(2, 4, 'Villa espectacular con piscina, muy recomendada.', '2026-07-09'), 
(3, 3, 'Pequeño pero acogedor. Un poco de ruido.', '2026-06-23'), 
(4, 5, 'Vistas inmejorables de la Alhambra.', '2026-08-20');

--  CONSULTAS PRINCIPALES --

-- Conultar noches reservadas, dinero facturado mes a mes
SELECT 
    DATE_FORMAT(fecha_entrada, '%Y-%m') AS mes,
    COUNT(id_reserva) AS total_reservas,
    SUM(DATEDIFF(fecha_salida, fecha_entrada)) AS total_noches_reservadas,
    SUM(precio_total) AS ingresos_mensuales
FROM reservas
GROUP BY DATE_FORMAT(fecha_entrada, '%Y-%m')
ORDER BY mes DESC;



-- Buscar TOP clientes que mas han gastado
SELECT 
    h.id_huesped,
    CONCAT(h.nombre, ' ', h.apellidos) AS huesped,
    h.email,
    COUNT(r.id_reserva) AS veces_hospedado,
    SUM(r.precio_total) AS gasto_total
FROM huespedes h
JOIN reservas r ON h.id_huesped = r.id_huesped
GROUP BY h.id_huesped
ORDER BY gasto_total DESC
LIMIT 3;


-- Estado de mantenimiento
SELECT 
    a.nombre AS alojamiento,
    m.descripcion AS problema_mantenimiento,
    m.estado AS estado_mantenimiento,
    r.fecha_entrada AS proxima_reserva,
    CONCAT(h.nombre, ' ', h.apellidos) AS huesped
FROM alojamientos a
JOIN mantenimientos m ON a.id_alojamiento = m.id_alojamiento
JOIN reservas r ON a.id_alojamiento = r.id_alojamiento
JOIN huespedes h ON r.id_huesped = h.id_huesped
WHERE m.estado IN ('Pendiente', 'En progreso')
  AND r.fecha_entrada >= m.fecha_inicio
ORDER BY r.fecha_entrada ASC;



