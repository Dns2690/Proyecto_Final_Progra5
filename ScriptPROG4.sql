-- ===================================================================
--  RESTAURANTE_DB  ·  Proyecto Final Programacion IV
--  Script unico: borra la base si existe, la vuelve a crear, arma las
--  17 tablas y carga los datos de demostracion.
--
--  COMO CORRERLO
--    Workbench : File > Open SQL Script, y darle al rayo de "Execute all"
--    Consola   : mysql -u root -p < ScriptPROG4.sql
--
--  No hay que crear la base a mano ni escoger el esquema antes: el
--  script lo hace todo. Se puede correr las veces que sea; siempre deja
--  la base igual, porque arranca borrandola.
--
--  MySQL 8 saca 5 avisos amarillos que dicen "Integer display width is
--  deprecated", uno por cada columna TINYINT(1). Son inofensivos: el
--  script termina bien igual. Se deja TINYINT(1) porque es asi como el
--  conector de Java entiende esas columnas como si/no.
--
--  Usuarios que quedan cargados
--    Administrador   admin    / admin123
--    Salonero        SAL001   / pass123     (Ana Solis)
--    Salonero        SAL002   / pass123     (Luis Mora)
--    Salonero        SAL003   / pass123     (Carmen Vega)
--    Salonero        SAL567   / nueva456    (Sofia Herrera)
--    Cocinero        COS001   / pass123     (Pedro Rojas)
--    Bartender       BAR001   / pass123     (Diego Campos)
--    Cajero          CAJ001   / pass123     (Maria Lopez)
--
--  Las contrasenas se guardan cifradas con MD5, igual que las cifra la
--  aplicacion en EncriptadorUtil antes de mandarlas a la base.
--
--  OJO con la rotacion del dia: las asignaciones que trae el script son
--  historicas. El dia de la demo hay que entrar como admin, pestana
--  Rotacion, y darle Guardar para generar la del dia; si no, el salonero
--  abre su pantalla sin mesas. No se dejan puestas con CURDATE() porque
--  MySQL puede ir en otra zona horaria que la aplicacion y quedar un dia
--  corrido.
-- ===================================================================

DROP DATABASE IF EXISTS restaurante_db;
CREATE DATABASE restaurante_db
    DEFAULT CHARACTER SET utf8mb4
    COLLATE utf8mb4_0900_ai_ci;
USE restaurante_db;


-- ===================================================================
--  TABLAS
--  Van en orden de dependencias: primero las que no apuntan a nadie, y
--  cada tabla se crea despues de aquellas a las que le hace referencia.
--  Asi ninguna llave foranea queda apuntando a una tabla que todavia no
--  existe y el script corre de un solo tiro.
-- ===================================================================

-- --- 1. Seguridad y personal -------------------------------------

CREATE TABLE administrador (
    id_admin      INT AUTO_INCREMENT PRIMARY KEY,
    usuario       VARCHAR(50)  NOT NULL UNIQUE,
    contrasena    VARCHAR(255) NOT NULL,   -- MD5
    nombre        VARCHAR(100) NOT NULL
);

CREATE TABLE tipo_usuario (
    id_tipo       INT AUTO_INCREMENT PRIMARY KEY,
    nombre        VARCHAR(30) NOT NULL UNIQUE,
    prefijo       CHAR(3)     NOT NULL UNIQUE
    -- Salonero/SAL, Cocinero/COS, Bartender/BAR, Cajero/CAJ
);

CREATE TABLE usuario (
    codigo        VARCHAR(6) PRIMARY KEY,  -- prefijo del rol + 3 digitos de la cedula: SAL001
    nombre        VARCHAR(100) NOT NULL,
    contrasena    VARCHAR(255) NOT NULL,   -- MD5
    id_tipo       INT          NOT NULL,
    activo        TINYINT(1)   DEFAULT 1,
    FOREIGN KEY (id_tipo) REFERENCES tipo_usuario(id_tipo)
);

-- --- 2. Salon ----------------------------------------------------

CREATE TABLE seccion_salon (
    id_seccion    INT AUTO_INCREMENT PRIMARY KEY,
    nombre        VARCHAR(60) NOT NULL
);

CREATE TABLE mesa (
    id_mesa       INT AUTO_INCREMENT PRIMARY KEY,
    numero_mesa   INT        NOT NULL,
    id_seccion    INT        NOT NULL,
    disponible    TINYINT(1) DEFAULT 1,
    FOREIGN KEY (id_seccion) REFERENCES seccion_salon(id_seccion)
);

CREATE TABLE asignacion_seccion (
    id_asignacion INT AUTO_INCREMENT PRIMARY KEY,
    codigo_sal    VARCHAR(6) NOT NULL,
    id_seccion    INT        NOT NULL,
    fecha         DATE       NOT NULL,
    UNIQUE (codigo_sal, fecha),          -- 1 salonero = 1 seccion por dia
    UNIQUE (id_seccion, fecha),          -- 1 seccion  = 1 salonero por dia
    FOREIGN KEY (codigo_sal) REFERENCES usuario(codigo),
    FOREIGN KEY (id_seccion) REFERENCES seccion_salon(id_seccion)
);

-- --- 3. Menu -----------------------------------------------------

CREATE TABLE categoria_comida (
    id_categoria  INT AUTO_INCREMENT PRIMARY KEY,
    nombre        VARCHAR(60) NOT NULL UNIQUE
);

CREATE TABLE comida (
    id_comida     INT AUTO_INCREMENT PRIMARY KEY,
    nombre        VARCHAR(100)  NOT NULL,
    id_categoria  INT           NOT NULL,
    descripcion   VARCHAR(255),
    precio        DECIMAL(10,2) NOT NULL,  -- sin impuesto
    activo        TINYINT(1)    DEFAULT 1,
    FOREIGN KEY (id_categoria) REFERENCES categoria_comida(id_categoria)
);

CREATE TABLE categoria_bebida (
    id_categoria  INT AUTO_INCREMENT PRIMARY KEY,
    nombre        VARCHAR(60) NOT NULL UNIQUE
);

CREATE TABLE bebida (
    id_bebida     INT AUTO_INCREMENT PRIMARY KEY,
    nombre        VARCHAR(100)  NOT NULL,
    id_categoria  INT           NOT NULL,
    descripcion   VARCHAR(255),
    precio        DECIMAL(10,2) NOT NULL,  -- sin impuesto
    activo        TINYINT(1)    DEFAULT 1,
    FOREIGN KEY (id_categoria) REFERENCES categoria_bebida(id_categoria)
);

-- --- 4. Operacion ------------------------------------------------

CREATE TABLE reserva (
    id_reserva      INT AUTO_INCREMENT PRIMARY KEY,
    nombre_cliente  VARCHAR(100) NOT NULL,
    telefono        VARCHAR(20),
    fecha_reserva   DATE         NOT NULL,
    hora_reserva    TIME         NOT NULL,
    cantidad_pers   INT          NOT NULL,
    incluye_ninos   TINYINT(1)   DEFAULT 0,
    id_mesa         INT,
    estado          ENUM('pendiente','confirmada','cancelada','atendida') DEFAULT 'pendiente',
    fecha_creacion  DATETIME     DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (id_mesa) REFERENCES mesa(id_mesa)
);

CREATE TABLE comanda (
    id_comanda    INT AUTO_INCREMENT PRIMARY KEY,
    ORIGEN        ENUM('salon','bar') NOT NULL,
    codigo_emp    VARCHAR(6) NOT NULL,     -- salonero o bartender
    id_mesa       INT,                     -- NULL cuando la comanda es de barra
    hora_orden    DATETIME   NOT NULL,     -- de aqui sale el tope de 20 minutos
    hora_generada DATETIME,                -- cuando quedo lista
    estado        ENUM('abierta','en_proceso','lista','cerrada') DEFAULT 'abierta',
    FOREIGN KEY (codigo_emp) REFERENCES usuario(codigo),
    FOREIGN KEY (id_mesa)    REFERENCES mesa(id_mesa)
);

CREATE TABLE detalle_comanda (
    id_detalle    INT AUTO_INCREMENT PRIMARY KEY,
    id_comanda    INT NOT NULL,
    tipo_item     ENUM('comida','bebida') NOT NULL,
    id_item       INT NOT NULL,            -- apunta a comida o a bebida segun tipo_item
    cantidad      INT NOT NULL,
    precio_unit   DECIMAL(10,2) NOT NULL,
    FOREIGN KEY (id_comanda) REFERENCES comanda(id_comanda)
);

CREATE TABLE proceso_cocina (
    id_proceso    INT AUTO_INCREMENT PRIMARY KEY,
    id_comanda    INT      NOT NULL UNIQUE,
    hora_recibida DATETIME NOT NULL,
    hora_lista    DATETIME,
    codigo_cos    VARCHAR(6),
    FOREIGN KEY (id_comanda) REFERENCES comanda(id_comanda),
    FOREIGN KEY (codigo_cos) REFERENCES usuario(codigo)
);

CREATE TABLE proceso_bar (
    id_proceso    INT AUTO_INCREMENT PRIMARY KEY,
    id_comanda    INT      NOT NULL UNIQUE,
    hora_recibida DATETIME NOT NULL,
    hora_lista    DATETIME,
    codigo_bar    VARCHAR(6),
    FOREIGN KEY (id_comanda) REFERENCES comanda(id_comanda),
    FOREIGN KEY (codigo_bar) REFERENCES usuario(codigo)
);

-- --- 5. Facturacion ----------------------------------------------

CREATE TABLE factura (
    id_factura     INT AUTO_INCREMENT PRIMARY KEY,
    id_comanda     INT           NOT NULL,
    codigo_cajero  VARCHAR(6),
    fecha_emision  DATETIME      NOT NULL,
    subtotal       DECIMAL(10,2) NOT NULL,
    impuesto       DECIMAL(10,2) NOT NULL,  -- IVA 13% de Costa Rica
    total          DECIMAL(10,2) NOT NULL,
    tipo           ENUM('provisional','final') DEFAULT 'provisional',
    estado         ENUM('pendiente','pagada')  DEFAULT 'pendiente',
    FOREIGN KEY (id_comanda)    REFERENCES comanda(id_comanda),
    FOREIGN KEY (codigo_cajero) REFERENCES usuario(codigo)
);

CREATE TABLE detalle_factura (
    id_det_fac    INT AUTO_INCREMENT PRIMARY KEY,
    id_factura    INT NOT NULL,
    id_detalle    INT NOT NULL,            -- linea de detalle_comanda
    FOREIGN KEY (id_factura) REFERENCES factura(id_factura),
    FOREIGN KEY (id_detalle) REFERENCES detalle_comanda(id_detalle)
);
-- detalle_factura es lo que permite partir la cuenta de una mesa en
-- varias facturas: cada linea de la comanda se cobra en una sola de ellas.


-- ===================================================================
--  DATOS DE DEMOSTRACION
--  Van en el mismo orden en que se crearon las tablas, para que cada
--  llave foranea encuentre siempre su registro padre ya insertado.
-- ===================================================================

-- administrador (1 registros)
-- Administrador del sistema (usuario admin / clave admin123)
INSERT INTO administrador (id_admin, usuario, contrasena, nombre) VALUES (1,'admin','0192023a7bbd73250516f069df18b500','Administrador General');

-- tipo_usuario (4 registros)
-- Los 4 roles, con el prefijo que arma el codigo de empleado
INSERT INTO tipo_usuario (id_tipo, nombre, prefijo) VALUES (1,'Salonero','SAL');
INSERT INTO tipo_usuario (id_tipo, nombre, prefijo) VALUES (2,'Cocinero','COS');
INSERT INTO tipo_usuario (id_tipo, nombre, prefijo) VALUES (3,'Bartender','BAR');
INSERT INTO tipo_usuario (id_tipo, nombre, prefijo) VALUES (4,'Cajero','CAJ');

-- usuario (7 registros)
-- Empleados. La contrasena de todos es pass123, menos SAL567 que es nueva456
INSERT INTO usuario (codigo, nombre, contrasena, id_tipo, activo) VALUES ('BAR001','Diego Campos','32250170a0dca92d53ec9624f336ca24',3,1);
INSERT INTO usuario (codigo, nombre, contrasena, id_tipo, activo) VALUES ('CAJ001','María López','32250170a0dca92d53ec9624f336ca24',4,1);
INSERT INTO usuario (codigo, nombre, contrasena, id_tipo, activo) VALUES ('COS001','Pedro Rojas','32250170a0dca92d53ec9624f336ca24',2,1);
INSERT INTO usuario (codigo, nombre, contrasena, id_tipo, activo) VALUES ('SAL001','Ana Solís','32250170a0dca92d53ec9624f336ca24',1,1);
INSERT INTO usuario (codigo, nombre, contrasena, id_tipo, activo) VALUES ('SAL002','Luis Mora','32250170a0dca92d53ec9624f336ca24',1,1);
INSERT INTO usuario (codigo, nombre, contrasena, id_tipo, activo) VALUES ('SAL003','Carmen Vega','32250170a0dca92d53ec9624f336ca24',1,1);
INSERT INTO usuario (codigo, nombre, contrasena, id_tipo, activo) VALUES ('SAL567','Sofía Herrera','bb1dfaf399e9ac67760b674f47ef549d',1,1);

-- seccion_salon (4 registros)
-- Las 4 secciones del salon
INSERT INTO seccion_salon (id_seccion, nombre) VALUES (1,'Sección A');
INSERT INTO seccion_salon (id_seccion, nombre) VALUES (2,'Sección B');
INSERT INTO seccion_salon (id_seccion, nombre) VALUES (3,'Sección C');
INSERT INTO seccion_salon (id_seccion, nombre) VALUES (4,'Terraza');

-- mesa (20 registros)
-- 20 mesas repartidas en las secciones
INSERT INTO mesa (id_mesa, numero_mesa, id_seccion, disponible) VALUES (1,1,1,1);
INSERT INTO mesa (id_mesa, numero_mesa, id_seccion, disponible) VALUES (2,2,1,1);
INSERT INTO mesa (id_mesa, numero_mesa, id_seccion, disponible) VALUES (3,3,1,0);
INSERT INTO mesa (id_mesa, numero_mesa, id_seccion, disponible) VALUES (4,4,1,1);
INSERT INTO mesa (id_mesa, numero_mesa, id_seccion, disponible) VALUES (5,5,1,1);
INSERT INTO mesa (id_mesa, numero_mesa, id_seccion, disponible) VALUES (6,6,1,0);
INSERT INTO mesa (id_mesa, numero_mesa, id_seccion, disponible) VALUES (7,7,2,1);
INSERT INTO mesa (id_mesa, numero_mesa, id_seccion, disponible) VALUES (8,8,2,0);
INSERT INTO mesa (id_mesa, numero_mesa, id_seccion, disponible) VALUES (9,9,2,0);
INSERT INTO mesa (id_mesa, numero_mesa, id_seccion, disponible) VALUES (10,10,2,0);
INSERT INTO mesa (id_mesa, numero_mesa, id_seccion, disponible) VALUES (11,11,2,1);
INSERT INTO mesa (id_mesa, numero_mesa, id_seccion, disponible) VALUES (12,12,2,1);
INSERT INTO mesa (id_mesa, numero_mesa, id_seccion, disponible) VALUES (13,13,3,1);
INSERT INTO mesa (id_mesa, numero_mesa, id_seccion, disponible) VALUES (14,14,3,1);
INSERT INTO mesa (id_mesa, numero_mesa, id_seccion, disponible) VALUES (15,15,3,0);
INSERT INTO mesa (id_mesa, numero_mesa, id_seccion, disponible) VALUES (16,16,3,1);
INSERT INTO mesa (id_mesa, numero_mesa, id_seccion, disponible) VALUES (17,17,3,1);
INSERT INTO mesa (id_mesa, numero_mesa, id_seccion, disponible) VALUES (18,18,3,1);
INSERT INTO mesa (id_mesa, numero_mesa, id_seccion, disponible) VALUES (20,20,4,1);
INSERT INTO mesa (id_mesa, numero_mesa, id_seccion, disponible) VALUES (21,19,4,1);

-- asignacion_seccion (14 registros)
-- Rotacion diaria: un salonero por seccion por dia
INSERT INTO asignacion_seccion (id_asignacion, codigo_sal, id_seccion, fecha) VALUES (1,'SAL001',1,'2026-06-12');
INSERT INTO asignacion_seccion (id_asignacion, codigo_sal, id_seccion, fecha) VALUES (2,'SAL002',2,'2026-06-12');
INSERT INTO asignacion_seccion (id_asignacion, codigo_sal, id_seccion, fecha) VALUES (3,'SAL003',3,'2026-06-12');
INSERT INTO asignacion_seccion (id_asignacion, codigo_sal, id_seccion, fecha) VALUES (11,'SAL001',2,'2026-07-28');
INSERT INTO asignacion_seccion (id_asignacion, codigo_sal, id_seccion, fecha) VALUES (12,'SAL002',3,'2026-07-28');
INSERT INTO asignacion_seccion (id_asignacion, codigo_sal, id_seccion, fecha) VALUES (13,'SAL003',1,'2026-07-28');
INSERT INTO asignacion_seccion (id_asignacion, codigo_sal, id_seccion, fecha) VALUES (14,'SAL001',3,'2026-07-29');
INSERT INTO asignacion_seccion (id_asignacion, codigo_sal, id_seccion, fecha) VALUES (15,'SAL002',4,'2026-07-29');
INSERT INTO asignacion_seccion (id_asignacion, codigo_sal, id_seccion, fecha) VALUES (16,'SAL003',2,'2026-07-29');
INSERT INTO asignacion_seccion (id_asignacion, codigo_sal, id_seccion, fecha) VALUES (17,'SAL567',1,'2026-07-29');
INSERT INTO asignacion_seccion (id_asignacion, codigo_sal, id_seccion, fecha) VALUES (19,'SAL001',4,'2026-07-30');
INSERT INTO asignacion_seccion (id_asignacion, codigo_sal, id_seccion, fecha) VALUES (20,'SAL002',1,'2026-07-30');
INSERT INTO asignacion_seccion (id_asignacion, codigo_sal, id_seccion, fecha) VALUES (21,'SAL003',3,'2026-07-30');
INSERT INTO asignacion_seccion (id_asignacion, codigo_sal, id_seccion, fecha) VALUES (22,'SAL567',2,'2026-07-30');

-- categoria_comida (5 registros)
INSERT INTO categoria_comida (id_categoria, nombre) VALUES (4,'Comida Rápida');
INSERT INTO categoria_comida (id_categoria, nombre) VALUES (1,'Ensalada');
INSERT INTO categoria_comida (id_categoria, nombre) VALUES (5,'Mariscos');
INSERT INTO categoria_comida (id_categoria, nombre) VALUES (2,'Plato Fuerte');
INSERT INTO categoria_comida (id_categoria, nombre) VALUES (3,'Postre');

-- comida (7 registros)
-- El menu de comidas. Todas activas
INSERT INTO comida (id_comida, nombre, id_categoria, descripcion, precio, activo) VALUES (1,'Ensalada César',1,'Lechuga, crutones, parmesano',4500.00,1);
INSERT INTO comida (id_comida, nombre, id_categoria, descripcion, precio, activo) VALUES (2,'Casado de Pollo',2,'Arroz, frijoles, pollo asado',7500.00,1);
INSERT INTO comida (id_comida, nombre, id_categoria, descripcion, precio, activo) VALUES (3,'Filete de Res',2,'Filete 250g con guarnición',12000.00,1);
INSERT INTO comida (id_comida, nombre, id_categoria, descripcion, precio, activo) VALUES (4,'Hamburguesa BBQ',4,'Carne, tocino, queso cheddar',8500.00,1);
INSERT INTO comida (id_comida, nombre, id_categoria, descripcion, precio, activo) VALUES (5,'Alitas Buffalo',4,'12 alitas, salsa buffalo',9000.00,1);
INSERT INTO comida (id_comida, nombre, id_categoria, descripcion, precio, activo) VALUES (6,'Flan Casero',3,'Flan de vainilla con caramelo',2500.00,1);
INSERT INTO comida (id_comida, nombre, id_categoria, descripcion, precio, activo) VALUES (7,'Ceviche de Corvina',5,'Corvina fresca con limon',7200.00,1);

-- categoria_bebida (4 registros)
INSERT INTO categoria_bebida (id_categoria, nombre) VALUES (2,'Bebida Caliente');
INSERT INTO categoria_bebida (id_categoria, nombre) VALUES (1,'Bebida Fría');
INSERT INTO categoria_bebida (id_categoria, nombre) VALUES (4,'Licor');
INSERT INTO categoria_bebida (id_categoria, nombre) VALUES (3,'Malteada');

-- bebida (6 registros)
-- El menu de bebidas. La Malteada Vainilla va inactiva a proposito
INSERT INTO bebida (id_bebida, nombre, id_categoria, descripcion, precio, activo) VALUES (1,'Agua Natural 500ml',1,'Agua purificada',1000.00,1);
INSERT INTO bebida (id_bebida, nombre, id_categoria, descripcion, precio, activo) VALUES (2,'Refresco Natural',1,'Limón, tamarindo o cas',1500.00,1);
INSERT INTO bebida (id_bebida, nombre, id_categoria, descripcion, precio, activo) VALUES (3,'Café Americano',2,'Café negro, grano local',1800.00,1);
INSERT INTO bebida (id_bebida, nombre, id_categoria, descripcion, precio, activo) VALUES (4,'Malteada Vainilla',3,'Helado, leche, vainilla',3500.00,0);
INSERT INTO bebida (id_bebida, nombre, id_categoria, descripcion, precio, activo) VALUES (5,'Cerveza Nacional',4,'Botella 355ml fría',2500.00,1);
INSERT INTO bebida (id_bebida, nombre, id_categoria, descripcion, precio, activo) VALUES (6,'Chocolate Caliente',2,'Con leche y canela',2200.00,1);

-- reserva (11 registros)
-- Reservas de ejemplo, con su mesa asignada
INSERT INTO reserva (id_reserva, nombre_cliente, telefono, fecha_reserva, hora_reserva, cantidad_pers, incluye_ninos, id_mesa, estado, fecha_creacion) VALUES (1,'Juan Pérez','88001234','2026-06-12','12:00:00',4,0,1,'confirmada','2026-06-12 23:48:46');
INSERT INTO reserva (id_reserva, nombre_cliente, telefono, fecha_reserva, hora_reserva, cantidad_pers, incluye_ninos, id_mesa, estado, fecha_creacion) VALUES (2,'Laura Jiménez','87552211','2026-07-10','19:00:00',2,0,5,'confirmada','2026-07-09 04:58:19');
INSERT INTO reserva (id_reserva, nombre_cliente, telefono, fecha_reserva, hora_reserva, cantidad_pers, incluye_ninos, id_mesa, estado, fecha_creacion) VALUES (3,'Carlos Brenes','86114455','2026-07-11','12:30:00',6,1,10,'pendiente','2026-07-09 04:58:19');
INSERT INTO reserva (id_reserva, nombre_cliente, telefono, fecha_reserva, hora_reserva, cantidad_pers, incluye_ninos, id_mesa, estado, fecha_creacion) VALUES (4,'Rosa Quirós','89663322','2026-07-09','18:00:00',3,1,NULL,'cancelada','2026-07-09 04:58:19');
INSERT INTO reserva (id_reserva, nombre_cliente, telefono, fecha_reserva, hora_reserva, cantidad_pers, incluye_ninos, id_mesa, estado, fecha_creacion) VALUES (5,'Familia Vargas','8888-1122','2026-07-29','19:00:00',6,1,NULL,'confirmada','2026-07-29 02:06:49');
INSERT INTO reserva (id_reserva, nombre_cliente, telefono, fecha_reserva, hora_reserva, cantidad_pers, incluye_ninos, id_mesa, estado, fecha_creacion) VALUES (6,'Ana Cordero','8712-4590','2026-07-30','20:30:00',2,0,NULL,'pendiente','2026-07-29 02:06:49');
INSERT INTO reserva (id_reserva, nombre_cliente, telefono, fecha_reserva, hora_reserva, cantidad_pers, incluye_ninos, id_mesa, estado, fecha_creacion) VALUES (7,'Empresa Solutions','2222-3344','2026-08-02','12:30:00',12,0,NULL,'confirmada','2026-07-29 02:06:49');
INSERT INTO reserva (id_reserva, nombre_cliente, telefono, fecha_reserva, hora_reserva, cantidad_pers, incluye_ninos, id_mesa, estado, fecha_creacion) VALUES (8,'Marco Ureña','6045-7788','2026-08-04','18:00:00',4,1,NULL,'pendiente','2026-07-29 02:06:49');
INSERT INTO reserva (id_reserva, nombre_cliente, telefono, fecha_reserva, hora_reserva, cantidad_pers, incluye_ninos, id_mesa, estado, fecha_creacion) VALUES (9,'Grupo Chaves','8811-2200','2026-07-28','13:00:00',4,0,5,'atendida','2026-07-29 02:29:06');
INSERT INTO reserva (id_reserva, nombre_cliente, telefono, fecha_reserva, hora_reserva, cantidad_pers, incluye_ninos, id_mesa, estado, fecha_creacion) VALUES (10,'Mesa de Karla','8700-3311','2026-07-28','17:30:00',3,0,6,'atendida','2026-07-29 02:29:06');
INSERT INTO reserva (id_reserva, nombre_cliente, telefono, fecha_reserva, hora_reserva, cantidad_pers, incluye_ninos, id_mesa, estado, fecha_creacion) VALUES (11,'Pareja Ramírez','8955-1010','2026-07-28','19:00:00',2,0,9,'atendida','2026-07-29 02:29:06');

-- comanda (24 registros)
-- Comandas de ejemplo, de salon y de bar
INSERT INTO comanda (id_comanda, ORIGEN, codigo_emp, id_mesa, hora_orden, hora_generada, estado) VALUES (1,'salon','SAL001',1,'2026-06-12 23:49:09','2026-06-12 23:49:09','en_proceso');
INSERT INTO comanda (id_comanda, ORIGEN, codigo_emp, id_mesa, hora_orden, hora_generada, estado) VALUES (2,'salon','SAL002',8,'2026-07-08 04:56:28','2026-07-08 04:56:28','cerrada');
INSERT INTO comanda (id_comanda, ORIGEN, codigo_emp, id_mesa, hora_orden, hora_generada, estado) VALUES (3,'salon','SAL003',14,'2026-07-08 04:56:28','2026-07-08 04:56:28','cerrada');
INSERT INTO comanda (id_comanda, ORIGEN, codigo_emp, id_mesa, hora_orden, hora_generada, estado) VALUES (4,'salon','SAL001',2,'2026-07-07 04:56:28','2026-07-07 04:56:28','cerrada');
INSERT INTO comanda (id_comanda, ORIGEN, codigo_emp, id_mesa, hora_orden, hora_generada, estado) VALUES (5,'bar','BAR001',NULL,'2026-07-08 04:56:28','2026-07-08 04:56:28','cerrada');
INSERT INTO comanda (id_comanda, ORIGEN, codigo_emp, id_mesa, hora_orden, hora_generada, estado) VALUES (6,'bar','BAR001',NULL,'2026-07-09 04:56:28','2026-07-09 04:56:28','en_proceso');
INSERT INTO comanda (id_comanda, ORIGEN, codigo_emp, id_mesa, hora_orden, hora_generada, estado) VALUES (7,'bar','BAR001',NULL,'2026-07-07 04:56:28','2026-07-07 04:56:28','cerrada');
INSERT INTO comanda (id_comanda, ORIGEN, codigo_emp, id_mesa, hora_orden, hora_generada, estado) VALUES (10,'salon','SAL001',7,'2026-07-28 19:31:49',NULL,'cerrada');
INSERT INTO comanda (id_comanda, ORIGEN, codigo_emp, id_mesa, hora_orden, hora_generada, estado) VALUES (11,'salon','SAL001',8,'2026-07-28 19:58:49',NULL,'abierta');
INSERT INTO comanda (id_comanda, ORIGEN, codigo_emp, id_mesa, hora_orden, hora_generada, estado) VALUES (12,'bar','BAR001',NULL,'2026-07-28 19:54:49',NULL,'abierta');
INSERT INTO comanda (id_comanda, ORIGEN, codigo_emp, id_mesa, hora_orden, hora_generada, estado) VALUES (13,'salon','SAL001',9,'2026-07-28 19:16:49',NULL,'cerrada');
INSERT INTO comanda (id_comanda, ORIGEN, codigo_emp, id_mesa, hora_orden, hora_generada, estado) VALUES (14,'salon','SAL002',13,'2026-07-28 19:21:49',NULL,'cerrada');
INSERT INTO comanda (id_comanda, ORIGEN, codigo_emp, id_mesa, hora_orden, hora_generada, estado) VALUES (15,'salon','SAL003',14,'2026-07-28 19:29:29',NULL,'cerrada');
INSERT INTO comanda (id_comanda, ORIGEN, codigo_emp, id_mesa, hora_orden, hora_generada, estado) VALUES (16,'salon','SAL002',15,'2026-07-28 19:47:16',NULL,'abierta');
INSERT INTO comanda (id_comanda, ORIGEN, codigo_emp, id_mesa, hora_orden, hora_generada, estado) VALUES (17,'salon','SAL003',3,'2026-07-28 20:06:16',NULL,'abierta');
INSERT INTO comanda (id_comanda, ORIGEN, codigo_emp, id_mesa, hora_orden, hora_generada, estado) VALUES (18,'bar','BAR001',NULL,'2026-07-28 20:09:16',NULL,'abierta');
INSERT INTO comanda (id_comanda, ORIGEN, codigo_emp, id_mesa, hora_orden, hora_generada, estado) VALUES (19,'salon','SAL001',10,'2026-07-28 19:42:16',NULL,'lista');
INSERT INTO comanda (id_comanda, ORIGEN, codigo_emp, id_mesa, hora_orden, hora_generada, estado) VALUES (20,'bar','BAR001',6,'2026-07-28 20:09:06',NULL,'lista');
INSERT INTO comanda (id_comanda, ORIGEN, codigo_emp, id_mesa, hora_orden, hora_generada, estado) VALUES (21,'salon','SAL001',5,'2026-07-28 20:14:06',NULL,'cerrada');
INSERT INTO comanda (id_comanda, ORIGEN, codigo_emp, id_mesa, hora_orden, hora_generada, estado) VALUES (22,'salon','SAL001',9,'2026-07-28 20:38:43',NULL,'abierta');
INSERT INTO comanda (id_comanda, ORIGEN, codigo_emp, id_mesa, hora_orden, hora_generada, estado) VALUES (23,'salon','SAL001',11,'2026-07-28 20:42:15',NULL,'cerrada');
INSERT INTO comanda (id_comanda, ORIGEN, codigo_emp, id_mesa, hora_orden, hora_generada, estado) VALUES (24,'bar','BAR001',NULL,'2026-07-29 18:02:39',NULL,'abierta');
INSERT INTO comanda (id_comanda, ORIGEN, codigo_emp, id_mesa, hora_orden, hora_generada, estado) VALUES (25,'salon','SAL001',13,'2026-07-29 18:05:02',NULL,'cerrada');
INSERT INTO comanda (id_comanda, ORIGEN, codigo_emp, id_mesa, hora_orden, hora_generada, estado) VALUES (26,'salon','SAL001',20,'2026-07-30 17:03:39',NULL,'cerrada');

-- detalle_comanda (47 registros)
-- Las lineas de cada comanda (tipo_item dice si es comida o bebida)
INSERT INTO detalle_comanda (id_detalle, id_comanda, tipo_item, id_item, cantidad, precio_unit) VALUES (1,1,'comida',2,1,7500.00);
INSERT INTO detalle_comanda (id_detalle, id_comanda, tipo_item, id_item, cantidad, precio_unit) VALUES (2,1,'bebida',2,2,1500.00);
INSERT INTO detalle_comanda (id_detalle, id_comanda, tipo_item, id_item, cantidad, precio_unit) VALUES (3,2,'comida',3,2,12000.00);
INSERT INTO detalle_comanda (id_detalle, id_comanda, tipo_item, id_item, cantidad, precio_unit) VALUES (4,2,'bebida',5,2,2500.00);
INSERT INTO detalle_comanda (id_detalle, id_comanda, tipo_item, id_item, cantidad, precio_unit) VALUES (5,3,'comida',4,1,8500.00);
INSERT INTO detalle_comanda (id_detalle, id_comanda, tipo_item, id_item, cantidad, precio_unit) VALUES (6,3,'bebida',1,1,1000.00);
INSERT INTO detalle_comanda (id_detalle, id_comanda, tipo_item, id_item, cantidad, precio_unit) VALUES (7,4,'comida',1,1,4500.00);
INSERT INTO detalle_comanda (id_detalle, id_comanda, tipo_item, id_item, cantidad, precio_unit) VALUES (8,5,'bebida',5,3,2500.00);
INSERT INTO detalle_comanda (id_detalle, id_comanda, tipo_item, id_item, cantidad, precio_unit) VALUES (9,6,'bebida',4,1,3500.00);
INSERT INTO detalle_comanda (id_detalle, id_comanda, tipo_item, id_item, cantidad, precio_unit) VALUES (10,6,'comida',5,1,9000.00);
INSERT INTO detalle_comanda (id_detalle, id_comanda, tipo_item, id_item, cantidad, precio_unit) VALUES (11,7,'bebida',5,2,2500.00);
INSERT INTO detalle_comanda (id_detalle, id_comanda, tipo_item, id_item, cantidad, precio_unit) VALUES (15,10,'comida',2,2,7500.00);
INSERT INTO detalle_comanda (id_detalle, id_comanda, tipo_item, id_item, cantidad, precio_unit) VALUES (16,10,'bebida',5,2,2500.00);
INSERT INTO detalle_comanda (id_detalle, id_comanda, tipo_item, id_item, cantidad, precio_unit) VALUES (17,11,'comida',3,1,12000.00);
INSERT INTO detalle_comanda (id_detalle, id_comanda, tipo_item, id_item, cantidad, precio_unit) VALUES (18,11,'comida',1,1,4500.00);
INSERT INTO detalle_comanda (id_detalle, id_comanda, tipo_item, id_item, cantidad, precio_unit) VALUES (19,12,'bebida',3,2,1800.00);
INSERT INTO detalle_comanda (id_detalle, id_comanda, tipo_item, id_item, cantidad, precio_unit) VALUES (20,12,'bebida',4,1,3500.00);
INSERT INTO detalle_comanda (id_detalle, id_comanda, tipo_item, id_item, cantidad, precio_unit) VALUES (21,13,'comida',4,1,8500.00);
INSERT INTO detalle_comanda (id_detalle, id_comanda, tipo_item, id_item, cantidad, precio_unit) VALUES (22,13,'bebida',1,2,1000.00);
INSERT INTO detalle_comanda (id_detalle, id_comanda, tipo_item, id_item, cantidad, precio_unit) VALUES (23,14,'comida',2,1,7500.00);
INSERT INTO detalle_comanda (id_detalle, id_comanda, tipo_item, id_item, cantidad, precio_unit) VALUES (24,14,'comida',5,1,9000.00);
INSERT INTO detalle_comanda (id_detalle, id_comanda, tipo_item, id_item, cantidad, precio_unit) VALUES (25,14,'bebida',5,2,2500.00);
INSERT INTO detalle_comanda (id_detalle, id_comanda, tipo_item, id_item, cantidad, precio_unit) VALUES (26,14,'bebida',2,1,1500.00);
INSERT INTO detalle_comanda (id_detalle, id_comanda, tipo_item, id_item, cantidad, precio_unit) VALUES (27,15,'comida',1,2,4500.00);
INSERT INTO detalle_comanda (id_detalle, id_comanda, tipo_item, id_item, cantidad, precio_unit) VALUES (28,15,'comida',3,1,12000.00);
INSERT INTO detalle_comanda (id_detalle, id_comanda, tipo_item, id_item, cantidad, precio_unit) VALUES (29,15,'bebida',2,3,1500.00);
INSERT INTO detalle_comanda (id_detalle, id_comanda, tipo_item, id_item, cantidad, precio_unit) VALUES (30,15,'bebida',5,1,2500.00);
INSERT INTO detalle_comanda (id_detalle, id_comanda, tipo_item, id_item, cantidad, precio_unit) VALUES (31,16,'comida',5,2,9000.00);
INSERT INTO detalle_comanda (id_detalle, id_comanda, tipo_item, id_item, cantidad, precio_unit) VALUES (32,16,'bebida',4,1,3500.00);
INSERT INTO detalle_comanda (id_detalle, id_comanda, tipo_item, id_item, cantidad, precio_unit) VALUES (33,17,'comida',1,1,4500.00);
INSERT INTO detalle_comanda (id_detalle, id_comanda, tipo_item, id_item, cantidad, precio_unit) VALUES (34,18,'bebida',5,3,2500.00);
INSERT INTO detalle_comanda (id_detalle, id_comanda, tipo_item, id_item, cantidad, precio_unit) VALUES (35,19,'comida',3,1,12000.00);
INSERT INTO detalle_comanda (id_detalle, id_comanda, tipo_item, id_item, cantidad, precio_unit) VALUES (36,19,'bebida',2,2,1500.00);
INSERT INTO detalle_comanda (id_detalle, id_comanda, tipo_item, id_item, cantidad, precio_unit) VALUES (37,20,'bebida',5,2,2500.00);
INSERT INTO detalle_comanda (id_detalle, id_comanda, tipo_item, id_item, cantidad, precio_unit) VALUES (38,20,'bebida',3,2,1800.00);
INSERT INTO detalle_comanda (id_detalle, id_comanda, tipo_item, id_item, cantidad, precio_unit) VALUES (39,21,'comida',1,2,4500.00);
INSERT INTO detalle_comanda (id_detalle, id_comanda, tipo_item, id_item, cantidad, precio_unit) VALUES (40,21,'bebida',2,2,1500.00);
INSERT INTO detalle_comanda (id_detalle, id_comanda, tipo_item, id_item, cantidad, precio_unit) VALUES (41,22,'comida',3,2,12000.00);
INSERT INTO detalle_comanda (id_detalle, id_comanda, tipo_item, id_item, cantidad, precio_unit) VALUES (42,22,'bebida',5,3,2500.00);
INSERT INTO detalle_comanda (id_detalle, id_comanda, tipo_item, id_item, cantidad, precio_unit) VALUES (43,23,'comida',3,2,12000.00);
INSERT INTO detalle_comanda (id_detalle, id_comanda, tipo_item, id_item, cantidad, precio_unit) VALUES (44,23,'bebida',5,3,2500.00);
INSERT INTO detalle_comanda (id_detalle, id_comanda, tipo_item, id_item, cantidad, precio_unit) VALUES (45,24,'comida',2,1,7500.00);
INSERT INTO detalle_comanda (id_detalle, id_comanda, tipo_item, id_item, cantidad, precio_unit) VALUES (46,24,'bebida',5,2,2500.00);
INSERT INTO detalle_comanda (id_detalle, id_comanda, tipo_item, id_item, cantidad, precio_unit) VALUES (47,25,'comida',3,2,12000.00);
INSERT INTO detalle_comanda (id_detalle, id_comanda, tipo_item, id_item, cantidad, precio_unit) VALUES (48,25,'bebida',5,3,2500.00);
INSERT INTO detalle_comanda (id_detalle, id_comanda, tipo_item, id_item, cantidad, precio_unit) VALUES (49,26,'comida',3,2,12000.00);
INSERT INTO detalle_comanda (id_detalle, id_comanda, tipo_item, id_item, cantidad, precio_unit) VALUES (50,26,'bebida',5,3,2500.00);

-- proceso_cocina (19 registros)
-- Despacho de cocina por comanda
INSERT INTO proceso_cocina (id_proceso, id_comanda, hora_recibida, hora_lista, codigo_cos) VALUES (1,1,'2026-06-12 23:50:00','2026-06-13 00:05:00','COS001');
INSERT INTO proceso_cocina (id_proceso, id_comanda, hora_recibida, hora_lista, codigo_cos) VALUES (2,2,'2026-07-08 04:57:06','2026-07-08 04:57:06','COS001');
INSERT INTO proceso_cocina (id_proceso, id_comanda, hora_recibida, hora_lista, codigo_cos) VALUES (3,3,'2026-07-08 04:57:06','2026-07-08 04:57:06','COS001');
INSERT INTO proceso_cocina (id_proceso, id_comanda, hora_recibida, hora_lista, codigo_cos) VALUES (4,4,'2026-07-07 04:57:06','2026-07-07 04:57:06','COS001');
INSERT INTO proceso_cocina (id_proceso, id_comanda, hora_recibida, hora_lista, codigo_cos) VALUES (5,6,'2026-07-08 04:57:06','2026-07-08 04:57:06','COS001');
INSERT INTO proceso_cocina (id_proceso, id_comanda, hora_recibida, hora_lista, codigo_cos) VALUES (8,10,'2026-07-28 19:31:49','2026-07-28 20:07:41','COS001');
INSERT INTO proceso_cocina (id_proceso, id_comanda, hora_recibida, hora_lista, codigo_cos) VALUES (9,11,'2026-07-28 19:58:49',NULL,NULL);
INSERT INTO proceso_cocina (id_proceso, id_comanda, hora_recibida, hora_lista, codigo_cos) VALUES (10,13,'2026-07-28 19:16:49','2026-07-28 19:28:49','COS001');
INSERT INTO proceso_cocina (id_proceso, id_comanda, hora_recibida, hora_lista, codigo_cos) VALUES (11,14,'2026-07-28 19:21:49','2026-07-28 19:33:49','COS001');
INSERT INTO proceso_cocina (id_proceso, id_comanda, hora_recibida, hora_lista, codigo_cos) VALUES (12,15,'2026-07-28 19:29:29','2026-07-28 19:40:29','COS001');
INSERT INTO proceso_cocina (id_proceso, id_comanda, hora_recibida, hora_lista, codigo_cos) VALUES (13,16,'2026-07-28 19:47:16',NULL,NULL);
INSERT INTO proceso_cocina (id_proceso, id_comanda, hora_recibida, hora_lista, codigo_cos) VALUES (14,17,'2026-07-28 20:06:16',NULL,NULL);
INSERT INTO proceso_cocina (id_proceso, id_comanda, hora_recibida, hora_lista, codigo_cos) VALUES (15,19,'2026-07-28 19:42:16','2026-07-28 19:54:16','COS001');
INSERT INTO proceso_cocina (id_proceso, id_comanda, hora_recibida, hora_lista, codigo_cos) VALUES (16,21,'2026-07-28 20:14:06','2026-07-28 20:26:06','COS001');
INSERT INTO proceso_cocina (id_proceso, id_comanda, hora_recibida, hora_lista, codigo_cos) VALUES (17,22,'2026-07-28 20:38:43',NULL,NULL);
INSERT INTO proceso_cocina (id_proceso, id_comanda, hora_recibida, hora_lista, codigo_cos) VALUES (18,23,'2026-07-28 20:42:15','2026-07-28 20:42:16','COS001');
INSERT INTO proceso_cocina (id_proceso, id_comanda, hora_recibida, hora_lista, codigo_cos) VALUES (19,24,'2026-07-29 18:02:39',NULL,NULL);
INSERT INTO proceso_cocina (id_proceso, id_comanda, hora_recibida, hora_lista, codigo_cos) VALUES (20,25,'2026-07-29 18:05:02','2026-07-29 18:05:04','COS001');
INSERT INTO proceso_cocina (id_proceso, id_comanda, hora_recibida, hora_lista, codigo_cos) VALUES (21,26,'2026-07-30 17:03:40','2026-07-30 17:03:40','COS001');

-- proceso_bar (20 registros)
-- Despacho de bar por comanda
INSERT INTO proceso_bar (id_proceso, id_comanda, hora_recibida, hora_lista, codigo_bar) VALUES (1,1,'2026-06-12 23:50:17','2026-06-12 23:55:17','BAR001');
INSERT INTO proceso_bar (id_proceso, id_comanda, hora_recibida, hora_lista, codigo_bar) VALUES (2,2,'2026-07-08 04:57:22','2026-07-08 04:57:22','BAR001');
INSERT INTO proceso_bar (id_proceso, id_comanda, hora_recibida, hora_lista, codigo_bar) VALUES (3,5,'2026-07-08 04:57:22','2026-07-08 04:57:22','BAR001');
INSERT INTO proceso_bar (id_proceso, id_comanda, hora_recibida, hora_lista, codigo_bar) VALUES (4,6,'2026-07-08 04:57:22','2026-07-08 04:57:22','BAR001');
INSERT INTO proceso_bar (id_proceso, id_comanda, hora_recibida, hora_lista, codigo_bar) VALUES (5,7,'2026-07-07 04:57:22','2026-07-07 04:57:22','BAR001');
INSERT INTO proceso_bar (id_proceso, id_comanda, hora_recibida, hora_lista, codigo_bar) VALUES (8,10,'2026-07-28 19:31:49','2026-07-28 20:08:08','BAR001');
INSERT INTO proceso_bar (id_proceso, id_comanda, hora_recibida, hora_lista, codigo_bar) VALUES (9,12,'2026-07-28 19:54:49',NULL,NULL);
INSERT INTO proceso_bar (id_proceso, id_comanda, hora_recibida, hora_lista, codigo_bar) VALUES (10,13,'2026-07-28 19:16:49','2026-07-28 19:22:49','BAR001');
INSERT INTO proceso_bar (id_proceso, id_comanda, hora_recibida, hora_lista, codigo_bar) VALUES (11,14,'2026-07-28 19:21:49','2026-07-28 19:27:49','BAR001');
INSERT INTO proceso_bar (id_proceso, id_comanda, hora_recibida, hora_lista, codigo_bar) VALUES (12,15,'2026-07-28 19:29:29','2026-07-28 19:34:29','BAR001');
INSERT INTO proceso_bar (id_proceso, id_comanda, hora_recibida, hora_lista, codigo_bar) VALUES (13,16,'2026-07-28 19:47:16',NULL,NULL);
INSERT INTO proceso_bar (id_proceso, id_comanda, hora_recibida, hora_lista, codigo_bar) VALUES (14,18,'2026-07-28 20:09:16',NULL,NULL);
INSERT INTO proceso_bar (id_proceso, id_comanda, hora_recibida, hora_lista, codigo_bar) VALUES (15,19,'2026-07-28 19:42:16','2026-07-28 19:48:16','BAR001');
INSERT INTO proceso_bar (id_proceso, id_comanda, hora_recibida, hora_lista, codigo_bar) VALUES (16,20,'2026-07-28 20:09:06','2026-07-28 20:15:06','BAR001');
INSERT INTO proceso_bar (id_proceso, id_comanda, hora_recibida, hora_lista, codigo_bar) VALUES (17,21,'2026-07-28 20:14:06','2026-07-28 20:20:06','BAR001');
INSERT INTO proceso_bar (id_proceso, id_comanda, hora_recibida, hora_lista, codigo_bar) VALUES (18,22,'2026-07-28 20:38:43',NULL,NULL);
INSERT INTO proceso_bar (id_proceso, id_comanda, hora_recibida, hora_lista, codigo_bar) VALUES (19,23,'2026-07-28 20:42:15','2026-07-28 20:42:17','BAR001');
INSERT INTO proceso_bar (id_proceso, id_comanda, hora_recibida, hora_lista, codigo_bar) VALUES (20,24,'2026-07-29 18:02:39',NULL,NULL);
INSERT INTO proceso_bar (id_proceso, id_comanda, hora_recibida, hora_lista, codigo_bar) VALUES (21,25,'2026-07-29 18:05:02','2026-07-29 18:05:05','BAR001');
INSERT INTO proceso_bar (id_proceso, id_comanda, hora_recibida, hora_lista, codigo_bar) VALUES (22,26,'2026-07-30 17:03:40','2026-07-30 17:03:41','BAR001');

-- factura (13 registros)
-- Facturas emitidas, con el IVA del 13% ya calculado
INSERT INTO factura (id_factura, id_comanda, codigo_cajero, fecha_emision, subtotal, impuesto, total, tipo, estado) VALUES (1,1,'CAJ001','2026-06-12 23:50:45',10500.00,1365.00,11865.00,'final','pagada');
INSERT INTO factura (id_factura, id_comanda, codigo_cajero, fecha_emision, subtotal, impuesto, total, tipo, estado) VALUES (2,2,'CAJ001','2026-07-08 04:57:37',29000.00,3770.00,32770.00,'final','pagada');
INSERT INTO factura (id_factura, id_comanda, codigo_cajero, fecha_emision, subtotal, impuesto, total, tipo, estado) VALUES (3,3,'CAJ001','2026-07-08 04:57:37',9500.00,1235.00,10735.00,'final','pagada');
INSERT INTO factura (id_factura, id_comanda, codigo_cajero, fecha_emision, subtotal, impuesto, total, tipo, estado) VALUES (4,5,'CAJ001','2026-07-08 04:57:37',7500.00,975.00,8475.00,'final','pagada');
INSERT INTO factura (id_factura, id_comanda, codigo_cajero, fecha_emision, subtotal, impuesto, total, tipo, estado) VALUES (5,13,'CAJ001','2026-07-28 20:08:38',10500.00,1365.00,11865.00,'final','pagada');
INSERT INTO factura (id_factura, id_comanda, codigo_cajero, fecha_emision, subtotal, impuesto, total, tipo, estado) VALUES (6,14,'CAJ001','2026-07-28 20:08:39',23000.00,2990.00,25990.00,'final','pagada');
INSERT INTO factura (id_factura, id_comanda, codigo_cajero, fecha_emision, subtotal, impuesto, total, tipo, estado) VALUES (7,15,'CAJ001','2026-07-28 20:09:47',21000.00,2730.00,23730.00,'final','pagada');
INSERT INTO factura (id_factura, id_comanda, codigo_cajero, fecha_emision, subtotal, impuesto, total, tipo, estado) VALUES (8,15,'CAJ001','2026-07-28 20:11:29',7000.00,910.00,7910.00,'final','pagada');
INSERT INTO factura (id_factura, id_comanda, codigo_cajero, fecha_emision, subtotal, impuesto, total, tipo, estado) VALUES (9,23,'CAJ001','2026-07-28 20:42:19',31500.00,4095.00,35595.00,'final','pagada');
INSERT INTO factura (id_factura, id_comanda, codigo_cajero, fecha_emision, subtotal, impuesto, total, tipo, estado) VALUES (10,10,'CAJ001','2026-07-29 18:04:01',20000.00,2600.00,22600.00,'final','pagada');
INSERT INTO factura (id_factura, id_comanda, codigo_cajero, fecha_emision, subtotal, impuesto, total, tipo, estado) VALUES (11,25,'CAJ001','2026-07-29 18:05:07',31500.00,4095.00,35595.00,'final','pagada');
INSERT INTO factura (id_factura, id_comanda, codigo_cajero, fecha_emision, subtotal, impuesto, total, tipo, estado) VALUES (12,21,'SAL001','2026-07-29 18:09:45',12000.00,1560.00,13560.00,'provisional','pendiente');
INSERT INTO factura (id_factura, id_comanda, codigo_cajero, fecha_emision, subtotal, impuesto, total, tipo, estado) VALUES (13,26,'CAJ001','2026-07-30 17:03:42',31500.00,4095.00,35595.00,'final','pagada');

-- detalle_factura (25 registros)
-- Que linea de cual comanda entro en cual factura (cuentas separadas)
INSERT INTO detalle_factura (id_det_fac, id_factura, id_detalle) VALUES (1,1,1);
INSERT INTO detalle_factura (id_det_fac, id_factura, id_detalle) VALUES (2,1,2);
INSERT INTO detalle_factura (id_det_fac, id_factura, id_detalle) VALUES (3,2,3);
INSERT INTO detalle_factura (id_det_fac, id_factura, id_detalle) VALUES (4,2,4);
INSERT INTO detalle_factura (id_det_fac, id_factura, id_detalle) VALUES (5,3,5);
INSERT INTO detalle_factura (id_det_fac, id_factura, id_detalle) VALUES (6,3,6);
INSERT INTO detalle_factura (id_det_fac, id_factura, id_detalle) VALUES (7,4,8);
INSERT INTO detalle_factura (id_det_fac, id_factura, id_detalle) VALUES (8,5,21);
INSERT INTO detalle_factura (id_det_fac, id_factura, id_detalle) VALUES (9,5,22);
INSERT INTO detalle_factura (id_det_fac, id_factura, id_detalle) VALUES (10,6,23);
INSERT INTO detalle_factura (id_det_fac, id_factura, id_detalle) VALUES (11,6,24);
INSERT INTO detalle_factura (id_det_fac, id_factura, id_detalle) VALUES (12,6,25);
INSERT INTO detalle_factura (id_det_fac, id_factura, id_detalle) VALUES (13,6,26);
INSERT INTO detalle_factura (id_det_fac, id_factura, id_detalle) VALUES (14,7,27);
INSERT INTO detalle_factura (id_det_fac, id_factura, id_detalle) VALUES (15,7,28);
INSERT INTO detalle_factura (id_det_fac, id_factura, id_detalle) VALUES (16,8,29);
INSERT INTO detalle_factura (id_det_fac, id_factura, id_detalle) VALUES (17,8,30);
INSERT INTO detalle_factura (id_det_fac, id_factura, id_detalle) VALUES (18,9,43);
INSERT INTO detalle_factura (id_det_fac, id_factura, id_detalle) VALUES (19,9,44);
INSERT INTO detalle_factura (id_det_fac, id_factura, id_detalle) VALUES (20,10,15);
INSERT INTO detalle_factura (id_det_fac, id_factura, id_detalle) VALUES (21,10,16);
INSERT INTO detalle_factura (id_det_fac, id_factura, id_detalle) VALUES (22,11,47);
INSERT INTO detalle_factura (id_det_fac, id_factura, id_detalle) VALUES (23,11,48);
INSERT INTO detalle_factura (id_det_fac, id_factura, id_detalle) VALUES (24,13,49);
INSERT INTO detalle_factura (id_det_fac, id_factura, id_detalle) VALUES (25,13,50);

-- ===================================================================
--  VERIFICACION
--  Si algo fallo, estos conteos no cuadran y se ve de una vez.
-- ===================================================================

SELECT 'administrador' AS tabla, COUNT(*) AS registros FROM administrador
UNION ALL SELECT 'tipo_usuario',       COUNT(*) FROM tipo_usuario
UNION ALL SELECT 'usuario',            COUNT(*) FROM usuario
UNION ALL SELECT 'seccion_salon',      COUNT(*) FROM seccion_salon
UNION ALL SELECT 'mesa',               COUNT(*) FROM mesa
UNION ALL SELECT 'asignacion_seccion', COUNT(*) FROM asignacion_seccion
UNION ALL SELECT 'categoria_comida',   COUNT(*) FROM categoria_comida
UNION ALL SELECT 'comida',             COUNT(*) FROM comida
UNION ALL SELECT 'categoria_bebida',   COUNT(*) FROM categoria_bebida
UNION ALL SELECT 'bebida',             COUNT(*) FROM bebida
UNION ALL SELECT 'reserva',            COUNT(*) FROM reserva
UNION ALL SELECT 'comanda',            COUNT(*) FROM comanda
UNION ALL SELECT 'detalle_comanda',    COUNT(*) FROM detalle_comanda
UNION ALL SELECT 'proceso_cocina',     COUNT(*) FROM proceso_cocina
UNION ALL SELECT 'proceso_bar',        COUNT(*) FROM proceso_bar
UNION ALL SELECT 'factura',            COUNT(*) FROM factura
UNION ALL SELECT 'detalle_factura',    COUNT(*) FROM detalle_factura;
