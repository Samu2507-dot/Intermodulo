
CREATE DATABASE IF NOT EXISTS gestion_alojamientos;
USE gestion_alojamientos;

-- 1. TABLA: ANFITRIONES
CREATE TABLE anfitriones (
    id_anfitrion INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(50) NOT NULL,
    apellidos VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    telefono VARCHAR(15) NOT NULL
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
    telefono VARCHAR(15) NOT NULL
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

-- 5. TABLA: MANTENIMIENTOS
CREATE TABLE mantenimientos (
    id_mantenimiento INT AUTO_INCREMENT PRIMARY KEY,
    id_alojamiento INT NOT NULL,
    fecha_inicio DATE NOT NULL,
    descripcion VARCHAR(255) NOT NULL,
    estado ENUM('Pendiente', 'En progreso', 'Completado') DEFAULT 'Pendiente',
    FOREIGN KEY (id_alojamiento) REFERENCES alojamientos(id_alojamiento)
        ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB;

-- 6. TABLA: RESEÑAS
CREATE TABLE resenas (
    id_resena INT AUTO_INCREMENT PRIMARY KEY, 
    id_reserva INT UNIQUE NOT NULL,          
    puntuacion INT NOT NULL CHECK (puntuacion BETWEEN 1 AND 5),
    comentario TEXT,
    fecha DATE NOT NULL,
    FOREIGN KEY (id_reserva) REFERENCES reservas(id_reserva)
        ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB;

-- ANFITRIONES 
INSERT INTO anfitriones (nombre, apellidos, email, telefono) VALUES
('Carlos', 'García Pérez', 'carlos.garcia@email.com', '600111222'),
('María', 'López Martínez', 'maria.lopez@email.com', '600333444'),
('Juan', 'Rodríguez Gómez', 'juan.rod@email.com', '600555666');

-- ALOJAMIENTOS 
INSERT INTO alojamientos (nombre, direccion, precio_dia, id_anfitrion) VALUES
('Apartamento Centro Sol', 'Calle Mayor 14, Madrid', 85.00, 1),
('Villa Marítima', 'Av. del Mar 5, Valencia', 150.00, 1),
('Estudio Malasaña', 'Calle Pez 3, Madrid', 60.00, 2),
('Ático Vistas Alhambra', 'Calle Real 22, Granada', 120.00, 3);

-- HUÉSPEDES 
INSERT INTO huespedes (nombre, apellidos, email, telefono) VALUES
('Ana', 'Sánchez Ruiz', 'ana.sanchez@email.com', '677111111'),
('David', 'Fernández Vega', 'david.fer@email.com', '677222222'),
('Elena', 'Navarro Torres', 'elena.nav@email.com', '677333333');

-- RESERVAS 
INSERT INTO reservas (id_alojamiento, id_huesped, fecha_entrada, fecha_salida, precio_total) VALUES
(1, 1, '2026-06-10', '2026-06-15', 425.00), 
(2, 2, '2026-07-01', '2026-07-08', 1050.00), 
(3, 3, '2026-06-20', '2026-06-22', 120.00),  
(4, 1, '2026-08-12', '2026-08-19', 840.00);  

-- MANTENIMIENTOS 
INSERT INTO mantenimientos (id_alojamiento, fecha_inicio, descripcion, estado) VALUES
(1, '2026-06-01', 'Arreglo de la cisterna del baño principal', 'Completado'),
(3, '2026-06-04', 'Pintar pared del salón por humedades', 'Pendiente');

-- RESEÑAS
INSERT INTO resenas (id_reserva, puntuacion, comentario, fecha) VALUES
(1, 5, 'Excelente ubicación y todo muy limpio.', '2026-06-16'), 
(2, 4, 'Villa espectacular con piscina, muy recomendada.', '2026-07-09'), 
(3, 3, 'Pequeño pero acogedor. Un poco de ruido.', '2026-06-23'), 
(4, 5, 'Vistas inmejorables de la Alhambra.', '2026-08-20'); 