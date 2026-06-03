-- -----------------------------------------------------
-- Base de Datos: TorneoTenis3FN
-- -----------------------------------------------------
CREATE DATABASE IF NOT EXISTS TorneoTenis;
USE TorneoTenis;

-- 1. TABLA ADMINISTRADORES
CREATE TABLE Administradores (
    id_admin INT AUTO_INCREMENT,
    nombre VARCHAR(50) NOT NULL,
    apellido1 VARCHAR(50) NOT NULL,
    apellido2 VARCHAR(50) NULL,
    nacionalidad VARCHAR(30) NULL,
    PRIMARY KEY (id_admin)
) ENGINE=InnoDB;

-- 2. TABLA TORNEOS
CREATE TABLE Torneos (
    id_torneo INT AUTO_INCREMENT,
    nombre VARCHAR(100) NOT NULL,
    ubicacion VARCHAR(100) NOT NULL,
    fecha_inicio DATE NOT NULL,
    fecha_fin DATE NOT NULL,
    id_admin INT NULL,
    PRIMARY KEY (id_torneo),
    FOREIGN KEY (id_admin) 
        REFERENCES Administradores(id_admin) 
        ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB;

-- 3. TABLA PISTAS
CREATE TABLE Pistas (
    id_pista INT AUTO_INCREMENT,
    nombre VARCHAR(50) NOT NULL,
    tipo_superficie VARCHAR(30) NOT NULL,
    capacidad INT NULL,
    id_torneo INT NOT NULL,
    PRIMARY KEY (id_pista),
    FOREIGN KEY (id_torneo) 
        REFERENCES Torneos(id_torneo) 
        ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT chk_superficie CHECK (tipo_superficie IN ('Arcilla', 'Césped', 'Dura', 'Interior'))
) ENGINE=InnoDB;

-- 4. TABLA JUGADORES
CREATE TABLE Jugadores (
    id_jugador INT AUTO_INCREMENT,
    nombre VARCHAR(50) NOT NULL,
    apellido1 VARCHAR(50) NOT NULL,
    apellido2 VARCHAR(50) NULL,
    nacionalidad VARCHAR(30) NULL,
    ranking INT NULL,
    edad INT NULL,
    PRIMARY KEY (id_jugador),
    UNIQUE INDEX idx_ranking_unique (ranking) -- El ranking debe ser único si está asignado
) ENGINE=InnoDB;

-- 5. TABLA PAREJAS (Modalidad Dobles Estricta)
CREATE TABLE Parejas (
    id_pareja INT AUTO_INCREMENT,
    id_jugador1 INT NOT NULL,
    id_jugador2 INT NOT NULL,
    PRIMARY KEY (id_pareja),
    FOREIGN KEY (id_jugador1) REFERENCES Jugadores(id_jugador) ON DELETE RESTRICT ON UPDATE CASCADE,
    FOREIGN KEY (id_jugador2) REFERENCES Jugadores(id_jugador) ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT chk_jugadores_distintos CHECK (id_jugador1 <> id_jugador2),
    -- Evita que exista la pareja (Jugador A, Jugador B) y también (Jugador B, Jugador A)
    CONSTRAINT uq_pareja UNIQUE (id_jugador1, id_jugador2) 
) ENGINE=InnoDB;

-- 6. TABLA ARBITROS
CREATE TABLE Arbitros (
    id_arbitro INT AUTO_INCREMENT,
    nombre VARCHAR(50) NOT NULL,
    apellido1 VARCHAR(50) NOT NULL,
    apellido2 VARCHAR(50) NULL,
    nacionalidad VARCHAR(30) NULL,
    PRIMARY KEY (id_arbitro)
) ENGINE=InnoDB;

-- 7. TABLA PARTIDOS (Se elimina la columna 'modalidad' porque ya sabemos que es dobles)
CREATE TABLE Partidos (
    id_partido INT AUTO_INCREMENT,
    id_torneo INT NOT NULL,
    id_pista INT NOT NULL,
    id_arbitro INT NOT NULL,
    fecha DATE NOT NULL,
    hora TIME NOT NULL,
    ronda VARCHAR(30) NOT NULL,
    PRIMARY KEY (id_partido),
    FOREIGN KEY (id_torneo) REFERENCES Torneos(id_torneo) ON DELETE RESTRICT ON UPDATE CASCADE,
    FOREIGN KEY (id_pista) REFERENCES Pistas(id_pista) ON DELETE RESTRICT ON UPDATE CASCADE,
    FOREIGN KEY (id_arbitro) REFERENCES Arbitros(id_arbitro) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB;

CONSTRAINT uq_solapamiento_pista UNIQUE (id_pista, fecha, hora)
) ENGINE=InnoDB;

-- 8. TABLA INTERMEDIA: PAREJAS POR PARTIDO
CREATE TABLE Participantes_Partido (
    id_partido INT NOT NULL,
    id_pareja INT NOT NULL,
    es_ganador BOOLEAN NOT NULL DEFAULT FALSE,
    PRIMARY KEY (id_partido, id_pareja), 
    FOREIGN KEY (id_partido) REFERENCES Partidos(id_partido) ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (id_pareja) REFERENCES Parejas(id_pareja) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB;

-- 9. TABLA RESULTADOS SETS (Rediseñada para 3FN Real)
-- Ahora cada fila registra los juegos que hizo UNA pareja específica en UN set de UN partido.
CREATE TABLE Resultados_Sets (
    id_partido INT NOT NULL,
    id_pareja INT NOT NULL,
    numero_set INT NOT NULL,
    juegos INT NOT NULL,
    PRIMARY KEY (id_partido, id_pareja, numero_set),
    -- Esta FK compuesta asegura que la pareja realmente jugó ese partido
    FOREIGN KEY (id_partido, id_pareja) 
        REFERENCES Participantes_Partido(id_partido, id_pareja) 
        ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT chk_num_set CHECK (numero_set BETWEEN 1 AND 5),
    CONSTRAINT chk_juegos CHECK (juegos >= 0)
) ENGINE=InnoDB;
