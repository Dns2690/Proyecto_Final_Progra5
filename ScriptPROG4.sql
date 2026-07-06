CREATE TABLE administrador (
    id_admin      INT AUTO_INCREMENT PRIMARY KEY,
    usuario       VARCHAR(50)  NOT NULL UNIQUE,
    contrasena    VARCHAR(255) NOT NULL,  -- encriptada
    nombre        VARCHAR(100) NOT NULL
);


CREATE TABLE categoria_comida (
	id_categoria INT AUTO_INCREMENT PRIMARY KEY,
	nombre VARCHAR(60) NOT NULL UNIQUE
);


CREATE TABLE comida (
    id_comida     INT AUTO_INCREMENT PRIMARY KEY,
    nombre        VARCHAR(100) NOT NULL,
    id_categoria  INT          NOT NULL,
    descripcion   VARCHAR(255),
    precio        DECIMAL(10,2) NOT NULL,  -- sin impuesto
    activo        TINYINT(1)   DEFAULT 1,
    FOREIGN KEY (id_categoria) REFERENCES categoria_comida(id_categoria)
);


CREATE TABLE categoria_bebida (
	id_categoria INT AUTO_INCREMENT PRIMARY KEY,
	nombre VARCHAR(60) NOT NULL UNIQUE
);

CREATE TABLE bebida (
	id_bebida INT AUTO_INCREMENT PRIMARY KEY,
	nombre VARCHAR(100) NOT NULL,
	id_categoria INT NOT NULL,
	descripcion VARCHAR(255),
	precio DECIMAL(10,2) NOT NULL,
	activo TINYINT(1) DEFAULT 1,
	FOREIGN KEY (id_categoria) REFERENCES categoria_bebida(id_categoria)
);

CREATE TABLE seccion_salon (
    id_seccion    INT AUTO_INCREMENT PRIMARY KEY,
    nombre        VARCHAR(60) NOT NULL
);

CREATE TABLE mesa (
    id_mesa       INT AUTO_INCREMENT PRIMARY KEY,
    numero_mesa   INT          NOT NULL,
    id_seccion    INT          NOT NULL,
    disponible    TINYINT(1)   DEFAULT 1,
    FOREIGN KEY (id_seccion) REFERENCES seccion_salon(id_seccion)
);

CREATE TABLE tipo_usuario (
	id_tipo INT AUTO_INCREMENT PRIMARY KEY,
	nombre VARCHAR(30) NOT NULL UNIQUE,
	prefijo CHAR(3) NOT NULL UNIQUE
	-- Salonero/SAL, Cocinero/COS, Bartender/BAR, Cajero/CAJ
);

CREATE TABLE usuario (
	codigo VARCHAR(6) PRIMARY KEY, -- SAL001
	nombre VARCHAR(100) NOT NULL,
	contrasena VARCHAR(255) NOT NULL,
	id_tipo INT NOT NULL,
	activo TINYINT(1) DEFAULT 1,
	FOREIGN KEY (id_tipo) REFERENCES tipo_usuario(id_tipo)
);

CREATE TABLE asignacion_seccion (
    id_asignacion INT AUTO_INCREMENT PRIMARY KEY,
    codigo_sal    VARCHAR(6)  NOT NULL,
    id_seccion    INT         NOT NULL,
    fecha         DATE        NOT NULL,
    UNIQUE(codigo_sal, fecha),       -- 1 salonero = 1 sección/día
    UNIQUE(id_seccion, fecha),       -- 1 sección = 1 salonero/día
    FOREIGN KEY (codigo_sal) REFERENCES usuario(codigo),
    FOREIGN KEY (id_seccion) REFERENCES seccion_salon(id_seccion)
);

CREATE TABLE reserva (
    id_reserva      INT AUTO_INCREMENT PRIMARY KEY,
    nombre_cliente  VARCHAR(100) NOT NULL,
    telefono        VARCHAR(20),
    fecha_reserva   DATE         NOT NULL,
    hora_reserva    TIME         NOT NULL,
    cantidad_pers   INT          NOT NULL,
    incluye_ninos   TINYINT(1)   DEFAULT 0,
    id_mesa         INT,
    estado          ENUM('pendiente','confirmada','cancelada','atendida')
                    DEFAULT 'pendiente',
    fecha_creacion  DATETIME     DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (id_mesa) REFERENCES mesa(id_mesa)
);

CREATE TABLE comanda(
	id_comanda INT AUTO_INCREMENT PRIMARY KEY,
	ORIGEN ENUM('salon','bar') NOT NULL,
	codigo_emp VARCHAR(6) NOT NULL, -- SALONERO O BARTENDER
	id_mesa INT, -- NULL SI ESTA LIBRE
	hora_orden DATETIME NOT NULL,
	hora_generada DATETIME, -- CUANDO ESTA LISTA
	estado ENUM('abierta','en_proceso','lista','cerrada') DEFAULT('abierta'),
    FOREIGN KEY (codigo_emp) REFERENCES usuario(codigo),
    FOREIGN KEY (id_mesa)    REFERENCES mesa(id_mesa)	
	
);

CREATE TABLE detalle_comanda (
	id_detalle INT AUTO_INCREMENT PRIMARY KEY,
	id_comanda INT NOT NULL,
	tipo_item ENUM('comida', 'bebida') NOT NULL,
	id_item INT NOT NULL,
	cantidad INT NOT NULL,
	precio_unit DECIMAL(10,2) NOT NULL,
	FOREIGN KEY (id_comanda) REFERENCES comanda(id_comanda)
);

CREATE TABLE proceso_cocina (
	id_proceso INT AUTO_INCREMENT PRIMARY KEY,
	id_comanda INT NOT NULL UNIQUE,
	hora_recibida DATETIME NOT NULL,
	hora_lista DATETIME,
	codigo_cos VARCHAR(6),
	FOREIGN KEY (id_comanda) REFERENCES comanda(id_comanda),
	FOREIGN KEY (codigo_cos) REFERENCES usuario(codigo)
);

CREATE TABLE proceso_bar (
    id_proceso    INT AUTO_INCREMENT PRIMARY KEY,
    id_comanda    INT       NOT NULL UNIQUE,
    hora_recibida DATETIME  NOT NULL,
    hora_lista    DATETIME,
    codigo_bar    VARCHAR(6),
    FOREIGN KEY (id_comanda) REFERENCES comanda(id_comanda),
    FOREIGN KEY (codigo_bar) REFERENCES usuario(codigo)
);

CREATE TABLE factura (
    id_factura      INT AUTO_INCREMENT PRIMARY KEY,
    id_comanda      INT          NOT NULL,
    codigo_cajero   VARCHAR(6),
    fecha_emision   DATETIME     NOT NULL,
    subtotal        DECIMAL(10,2) NOT NULL,
    impuesto        DECIMAL(10,2) NOT NULL,  -- 13% IVA CR
    total           DECIMAL(10,2) NOT NULL,
    tipo            ENUM('provisional','final') DEFAULT 'provisional',
    estado          ENUM('pendiente','pagada')  DEFAULT 'pendiente',
    FOREIGN KEY (id_comanda)    REFERENCES comanda(id_comanda),
    FOREIGN KEY (codigo_cajero) REFERENCES usuario(codigo)
);

CREATE TABLE detalle_factura (
    id_det_fac    INT AUTO_INCREMENT PRIMARY KEY,
    id_factura    INT          NOT NULL,
    id_detalle    INT          NOT NULL,   -- de detalle_comanda
    FOREIGN KEY (id_factura) REFERENCES factura(id_factura),
    FOREIGN KEY (id_detalle) REFERENCES detalle_comanda(id_detalle)
); -- permite facturas separadas por cliente

-- Admin: usuario=admin / pass=admin123 (en app se encripta)
INSERT INTO administrador (usuario, contrasena, nombre) VALUES
    ('admin', MD5('admin123'), 'Administrador General');

INSERT INTO tipo_usuario (nombre, prefijo) VALUES
    ('Salonero',   'SAL'),
    ('Cocinero',   'COS'),
    ('Bartender',  'BAR'),
    ('Cajero',     'CAJ');

INSERT INTO categoria_comida (nombre) VALUES
    ('Ensalada'), ('Plato Fuerte'), ('Postre'), ('Comida Rápida');

INSERT INTO categoria_bebida (nombre) VALUES
    ('Bebida Fría'), ('Bebida Caliente'), ('Malteada'), ('Licor');



INSERT INTO comida (nombre, id_categoria, descripcion, precio) VALUES
    ('Ensalada César',   1, 'Lechuga, crutones, parmesano',   4500.00),
    ('Casado de Pollo',  2, 'Arroz, frijoles, pollo asado',    7500.00),
    ('Filete de Res',    2, 'Filete 250g con guarnición',       12000.00),
    ('Hamburguesa BBQ',  4, 'Carne, tocino, queso cheddar',    8500.00),
    ('Alitas Buffalo',   4, '12 alitas, salsa buffalo',         9000.00),
    ('Flan Casero',      3, 'Flan de vainilla con caramelo',   2500.00);

INSERT INTO bebida (nombre, id_categoria, descripcion, precio) VALUES
    ('Agua Natural 500ml', 1, 'Agua purificada',            1000.00),
    ('Refresco Natural',   1, 'Limón, tamarindo o cas',     1500.00),
    ('Café Americano',     2, 'Café negro, grano local',    1800.00),
    ('Malteada Vainilla',  3, 'Helado, leche, vainilla',    3500.00),
    ('Cerveza Nacional',   4, 'Botella 355ml fría',         2500.00);

INSERT INTO seccion_salon (nombre) VALUES
    ('Sección A'), ('Sección B'), ('Sección C');

INSERT INTO mesa (numero_mesa, id_seccion) VALUES
    (1,1),(2,1),(3,1),(4,1),(5,1),(6,1),
-- Sección B = mesas 7-12
    (7,2),(8,2),(9,2),(10,2),(11,2),(12,2),
-- Sección C = mesas 13-18
    (13,3),(14,3),(15,3),(16,3),(17,3),(18,3);


INSERT INTO usuario (codigo, nombre, contrasena, id_tipo) VALUES
    ('SAL001', 'Ana Solís',      MD5('pass123'), 1),
    ('SAL002', 'Luis Mora',      MD5('pass123'), 1),
    ('SAL003', 'Carmen Vega',   MD5('pass123'), 1),
    ('COS001', 'Pedro Rojas',   MD5('pass123'), 2),
    ('BAR001', 'Diego Campos',  MD5('pass123'), 3),
    ('CAJ001', 'María López',   MD5('pass123'), 4);


INSERT INTO asignacion_seccion (codigo_sal, id_seccion, fecha) VALUES
    ('SAL001', 1, CURDATE()),
    ('SAL002', 2, CURDATE()),
    ('SAL003', 3, CURDATE());


INSERT INTO reserva
    (nombre_cliente, telefono, fecha_reserva, hora_reserva,
     cantidad_pers, incluye_ninos, id_mesa, estado)
VALUES
    ('Juan Pérez', '88001234', CURDATE(), '12:00:00',
     4, 0, 1, 'confirmada');

-- 1. Abrir comanda
INSERT INTO comanda
    (origen, codigo_emp, id_mesa, hora_orden, hora_generada, estado)
VALUES
    ('salon', 'SAL001', 1, NOW(), NOW(), 'en_proceso');

INSERT INTO detalle_comanda
    (id_comanda, tipo_item, id_item, cantidad, precio_unit)
VALUES
    (1, 'comida',  2, 1, 7500.00),
    (1, 'bebida', 2, 2, 1500.00);

INSERT INTO proceso_cocina
    (id_comanda, hora_recibida, hora_lista, codigo_cos)
VALUES (1, NOW(), DATE_ADD(NOW(), INTERVAL 15 MINUTE), 'COS001');

INSERT INTO proceso_bar
    (id_comanda, hora_recibida, hora_lista, codigo_bar)
VALUES (1, NOW(), DATE_ADD(NOW(), INTERVAL 5 MINUTE), 'BAR001');

INSERT INTO factura
    (id_comanda, codigo_cajero, fecha_emision,
     subtotal, impuesto, total, tipo, estado)
VALUES
    (1, 'CAJ001', NOW(),
     10500.00, 1365.00, 11865.00, 'final', 'pagada');

INSERT INTO detalle_factura (id_factura, id_detalle) VALUES
    (1, 1), (1, 2);


SHOW TABLES;

-- Verificar conteos clave
SELECT 'comidas'        AS tabla, COUNT(*) AS total FROM comida
UNION ALL
SELECT 'bebidas',       COUNT(*) FROM bebida
UNION ALL
SELECT 'mesas',         COUNT(*) FROM mesa
UNION ALL
SELECT 'usuarios',      COUNT(*) FROM usuario
UNION ALL
SELECT 'comandas',      COUNT(*) FROM comanda
UNION ALL
SELECT 'facturas',      COUNT(*) FROM factura;

-- Reporte: comandas por origen (para los gráficos)
SELECT
    origen,
    COUNT(*) AS total_comandas,
    SUM(CASE WHEN estado = 'cerrada' THEN 1 ELSE 0 END) AS atendidas
FROM comanda
GROUP BY origen;


