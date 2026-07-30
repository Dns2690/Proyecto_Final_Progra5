-- =====================================================================
-- restaurante_db - respaldo completo (estructura + datos)
-- Proyecto Final Programacion IV - UISIL
-- Generado el 2026-07-29 desde MySQL 8.0.46
--
-- Trae la base con datos de demostracion listos para presentar:
--   17 tablas
--   7 empleados y 4 tipos de usuario
--   20 mesas repartidas en 4 secciones
--   7 comidas y 6 bebidas (una bebida inactiva a proposito)
--   23 comandas en todos los estados: abiertas, en proceso, listas y cerradas
--   5 comandas esperando en cocina y 5 en el bar (varias pasadas de los 20 minutos)
--   12 facturas, 227130.00 colones ya cobrados en facturas finales
--   1 factura provisional pendiente de cancelar en caja (comanda 21, mesa 5)
--   una cuenta separada repartida en dos facturas (la 7 y la 8, de la comanda 15)
--   11 reservas (6 con mesa asignada) entre atendidas, confirmadas,
--     pendientes y canceladas
--   rotacion de secciones de varias fechas
--
-- COMO RESTAURARLO (desde la terminal, en la carpeta del proyecto):
--   mysql -u root -p < restaurante_db_dump.sql
--
-- OJO: el archivo hace DROP DATABASE primero, o sea que borra la base
-- restaurante_db que tenga en su maquina y la vuelve a crear desde cero.
--
-- Usuarios para entrar al sistema:
--   admin  / admin123   (administrador)
--   SAL001 / pass123    (salonero)     SAL002, SAL003 igual
--   COS001 / pass123    (cocinero)
--   BAR001 / pass123    (bartender)
--   CAJ001 / pass123    (cajero)
--   SAL567 / nueva456   (salonero creado desde la pantalla de admin)
-- Las contrasenas se guardan en MD5, aqui van en texto solo de referencia.
-- =====================================================================

-- MariaDB dump 10.19  Distrib 10.4.28-MariaDB, for osx10.10 (x86_64)
--
-- Host: 127.0.0.1    Database: restaurante_db
-- ------------------------------------------------------
-- Server version	8.0.46

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Current Database: `restaurante_db`
--

/*!40000 DROP DATABASE IF EXISTS `restaurante_db`*/;

CREATE DATABASE /*!32312 IF NOT EXISTS*/ `restaurante_db` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;

USE `restaurante_db`;

--
-- Table structure for table `administrador`
--

DROP TABLE IF EXISTS `administrador`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `administrador` (
  `id_admin` int NOT NULL AUTO_INCREMENT,
  `usuario` varchar(50) NOT NULL,
  `contrasena` varchar(255) NOT NULL,
  `nombre` varchar(100) NOT NULL,
  PRIMARY KEY (`id_admin`),
  UNIQUE KEY `usuario` (`usuario`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `administrador`
--

LOCK TABLES `administrador` WRITE;
/*!40000 ALTER TABLE `administrador` DISABLE KEYS */;
INSERT INTO `administrador` VALUES (1,'admin','0192023a7bbd73250516f069df18b500','Administrador General');
/*!40000 ALTER TABLE `administrador` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `asignacion_seccion`
--

DROP TABLE IF EXISTS `asignacion_seccion`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `asignacion_seccion` (
  `id_asignacion` int NOT NULL AUTO_INCREMENT,
  `codigo_sal` varchar(6) NOT NULL,
  `id_seccion` int NOT NULL,
  `fecha` date NOT NULL,
  PRIMARY KEY (`id_asignacion`),
  UNIQUE KEY `codigo_sal` (`codigo_sal`,`fecha`),
  UNIQUE KEY `id_seccion` (`id_seccion`,`fecha`),
  CONSTRAINT `asignacion_seccion_ibfk_1` FOREIGN KEY (`codigo_sal`) REFERENCES `usuario` (`codigo`),
  CONSTRAINT `asignacion_seccion_ibfk_2` FOREIGN KEY (`id_seccion`) REFERENCES `seccion_salon` (`id_seccion`)
) ENGINE=InnoDB AUTO_INCREMENT=23 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `asignacion_seccion`
--

LOCK TABLES `asignacion_seccion` WRITE;
/*!40000 ALTER TABLE `asignacion_seccion` DISABLE KEYS */;
INSERT INTO `asignacion_seccion` VALUES (1,'SAL001',1,'2026-06-12'),(2,'SAL002',2,'2026-06-12'),(3,'SAL003',3,'2026-06-12'),(11,'SAL001',2,'2026-07-28'),(12,'SAL002',3,'2026-07-28'),(13,'SAL003',1,'2026-07-28'),(14,'SAL001',3,'2026-07-29'),(15,'SAL002',4,'2026-07-29'),(16,'SAL003',2,'2026-07-29'),(17,'SAL567',1,'2026-07-29'),(19,'SAL001',4,'2026-07-30'),(20,'SAL002',1,'2026-07-30'),(21,'SAL003',3,'2026-07-30'),(22,'SAL567',2,'2026-07-30');
/*!40000 ALTER TABLE `asignacion_seccion` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `bebida`
--

DROP TABLE IF EXISTS `bebida`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `bebida` (
  `id_bebida` int NOT NULL AUTO_INCREMENT,
  `nombre` varchar(100) NOT NULL,
  `id_categoria` int NOT NULL,
  `descripcion` varchar(255) DEFAULT NULL,
  `precio` decimal(10,2) NOT NULL,
  `activo` tinyint(1) DEFAULT '1',
  PRIMARY KEY (`id_bebida`),
  KEY `id_categoria` (`id_categoria`),
  CONSTRAINT `bebida_ibfk_1` FOREIGN KEY (`id_categoria`) REFERENCES `categoria_bebida` (`id_categoria`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `bebida`
--

LOCK TABLES `bebida` WRITE;
/*!40000 ALTER TABLE `bebida` DISABLE KEYS */;
INSERT INTO `bebida` VALUES (1,'Agua Natural 500ml',1,'Agua purificada',1000.00,1),(2,'Refresco Natural',1,'Limón, tamarindo o cas',1500.00,1),(3,'Café Americano',2,'Café negro, grano local',1800.00,1),(4,'Malteada Vainilla',3,'Helado, leche, vainilla',3500.00,0),(5,'Cerveza Nacional',4,'Botella 355ml fría',2500.00,1),(6,'Chocolate Caliente',2,'Con leche y canela',2200.00,1);
/*!40000 ALTER TABLE `bebida` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `categoria_bebida`
--

DROP TABLE IF EXISTS `categoria_bebida`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `categoria_bebida` (
  `id_categoria` int NOT NULL AUTO_INCREMENT,
  `nombre` varchar(60) NOT NULL,
  PRIMARY KEY (`id_categoria`),
  UNIQUE KEY `nombre` (`nombre`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `categoria_bebida`
--

LOCK TABLES `categoria_bebida` WRITE;
/*!40000 ALTER TABLE `categoria_bebida` DISABLE KEYS */;
INSERT INTO `categoria_bebida` VALUES (2,'Bebida Caliente'),(1,'Bebida Fría'),(4,'Licor'),(3,'Malteada');
/*!40000 ALTER TABLE `categoria_bebida` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `categoria_comida`
--

DROP TABLE IF EXISTS `categoria_comida`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `categoria_comida` (
  `id_categoria` int NOT NULL AUTO_INCREMENT,
  `nombre` varchar(60) NOT NULL,
  PRIMARY KEY (`id_categoria`),
  UNIQUE KEY `nombre` (`nombre`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `categoria_comida`
--

LOCK TABLES `categoria_comida` WRITE;
/*!40000 ALTER TABLE `categoria_comida` DISABLE KEYS */;
INSERT INTO `categoria_comida` VALUES (4,'Comida Rápida'),(1,'Ensalada'),(5,'Mariscos'),(2,'Plato Fuerte'),(3,'Postre');
/*!40000 ALTER TABLE `categoria_comida` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `comanda`
--

DROP TABLE IF EXISTS `comanda`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `comanda` (
  `id_comanda` int NOT NULL AUTO_INCREMENT,
  `ORIGEN` enum('salon','bar') NOT NULL,
  `codigo_emp` varchar(6) NOT NULL,
  `id_mesa` int DEFAULT NULL,
  `hora_orden` datetime NOT NULL,
  `hora_generada` datetime DEFAULT NULL,
  `estado` enum('abierta','en_proceso','lista','cerrada') DEFAULT (_utf8mb4'abierta'),
  PRIMARY KEY (`id_comanda`),
  KEY `codigo_emp` (`codigo_emp`),
  KEY `id_mesa` (`id_mesa`),
  CONSTRAINT `comanda_ibfk_1` FOREIGN KEY (`codigo_emp`) REFERENCES `usuario` (`codigo`),
  CONSTRAINT `comanda_ibfk_2` FOREIGN KEY (`id_mesa`) REFERENCES `mesa` (`id_mesa`)
) ENGINE=InnoDB AUTO_INCREMENT=26 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `comanda`
--

LOCK TABLES `comanda` WRITE;
/*!40000 ALTER TABLE `comanda` DISABLE KEYS */;
INSERT INTO `comanda` VALUES (1,'salon','SAL001',1,'2026-06-12 23:49:09','2026-06-12 23:49:09','en_proceso'),(2,'salon','SAL002',8,'2026-07-08 04:56:28','2026-07-08 04:56:28','cerrada'),(3,'salon','SAL003',14,'2026-07-08 04:56:28','2026-07-08 04:56:28','cerrada'),(4,'salon','SAL001',2,'2026-07-07 04:56:28','2026-07-07 04:56:28','cerrada'),(5,'bar','BAR001',NULL,'2026-07-08 04:56:28','2026-07-08 04:56:28','cerrada'),(6,'bar','BAR001',NULL,'2026-07-09 04:56:28','2026-07-09 04:56:28','en_proceso'),(7,'bar','BAR001',NULL,'2026-07-07 04:56:28','2026-07-07 04:56:28','cerrada'),(10,'salon','SAL001',7,'2026-07-28 19:31:49',NULL,'cerrada'),(11,'salon','SAL001',8,'2026-07-28 19:58:49',NULL,'abierta'),(12,'bar','BAR001',NULL,'2026-07-28 19:54:49',NULL,'abierta'),(13,'salon','SAL001',9,'2026-07-28 19:16:49',NULL,'cerrada'),(14,'salon','SAL002',13,'2026-07-28 19:21:49',NULL,'cerrada'),(15,'salon','SAL003',14,'2026-07-28 19:29:29',NULL,'cerrada'),(16,'salon','SAL002',15,'2026-07-28 19:47:16',NULL,'abierta'),(17,'salon','SAL003',3,'2026-07-28 20:06:16',NULL,'abierta'),(18,'bar','BAR001',NULL,'2026-07-28 20:09:16',NULL,'abierta'),(19,'salon','SAL001',10,'2026-07-28 19:42:16',NULL,'lista'),(20,'bar','BAR001',6,'2026-07-28 20:09:06',NULL,'lista'),(21,'salon','SAL001',5,'2026-07-28 20:14:06',NULL,'cerrada'),(22,'salon','SAL001',9,'2026-07-28 20:38:43',NULL,'abierta'),(23,'salon','SAL001',11,'2026-07-28 20:42:15',NULL,'cerrada'),(24,'bar','BAR001',NULL,'2026-07-29 18:02:39',NULL,'abierta'),(25,'salon','SAL001',13,'2026-07-29 18:05:02',NULL,'cerrada');
/*!40000 ALTER TABLE `comanda` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `comida`
--

DROP TABLE IF EXISTS `comida`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `comida` (
  `id_comida` int NOT NULL AUTO_INCREMENT,
  `nombre` varchar(100) NOT NULL,
  `id_categoria` int NOT NULL,
  `descripcion` varchar(255) DEFAULT NULL,
  `precio` decimal(10,2) NOT NULL,
  `activo` tinyint(1) DEFAULT '1',
  PRIMARY KEY (`id_comida`),
  KEY `id_categoria` (`id_categoria`),
  CONSTRAINT `comida_ibfk_1` FOREIGN KEY (`id_categoria`) REFERENCES `categoria_comida` (`id_categoria`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `comida`
--

LOCK TABLES `comida` WRITE;
/*!40000 ALTER TABLE `comida` DISABLE KEYS */;
INSERT INTO `comida` VALUES (1,'Ensalada César',1,'Lechuga, crutones, parmesano',4500.00,1),(2,'Casado de Pollo',2,'Arroz, frijoles, pollo asado',7500.00,1),(3,'Filete de Res',2,'Filete 250g con guarnición',12000.00,1),(4,'Hamburguesa BBQ',4,'Carne, tocino, queso cheddar',8500.00,1),(5,'Alitas Buffalo',4,'12 alitas, salsa buffalo',9000.00,1),(6,'Flan Casero',3,'Flan de vainilla con caramelo',2500.00,1),(7,'Ceviche de Corvina',5,'Corvina fresca con limon',7200.00,1);
/*!40000 ALTER TABLE `comida` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `detalle_comanda`
--

DROP TABLE IF EXISTS `detalle_comanda`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `detalle_comanda` (
  `id_detalle` int NOT NULL AUTO_INCREMENT,
  `id_comanda` int NOT NULL,
  `tipo_item` enum('comida','bebida') NOT NULL,
  `id_item` int NOT NULL,
  `cantidad` int NOT NULL,
  `precio_unit` decimal(10,2) NOT NULL,
  PRIMARY KEY (`id_detalle`),
  KEY `id_comanda` (`id_comanda`),
  CONSTRAINT `detalle_comanda_ibfk_1` FOREIGN KEY (`id_comanda`) REFERENCES `comanda` (`id_comanda`)
) ENGINE=InnoDB AUTO_INCREMENT=49 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `detalle_comanda`
--

LOCK TABLES `detalle_comanda` WRITE;
/*!40000 ALTER TABLE `detalle_comanda` DISABLE KEYS */;
INSERT INTO `detalle_comanda` VALUES (1,1,'comida',2,1,7500.00),(2,1,'bebida',2,2,1500.00),(3,2,'comida',3,2,12000.00),(4,2,'bebida',5,2,2500.00),(5,3,'comida',4,1,8500.00),(6,3,'bebida',1,1,1000.00),(7,4,'comida',1,1,4500.00),(8,5,'bebida',5,3,2500.00),(9,6,'bebida',4,1,3500.00),(10,6,'comida',5,1,9000.00),(11,7,'bebida',5,2,2500.00),(15,10,'comida',2,2,7500.00),(16,10,'bebida',5,2,2500.00),(17,11,'comida',3,1,12000.00),(18,11,'comida',1,1,4500.00),(19,12,'bebida',3,2,1800.00),(20,12,'bebida',4,1,3500.00),(21,13,'comida',4,1,8500.00),(22,13,'bebida',1,2,1000.00),(23,14,'comida',2,1,7500.00),(24,14,'comida',5,1,9000.00),(25,14,'bebida',5,2,2500.00),(26,14,'bebida',2,1,1500.00),(27,15,'comida',1,2,4500.00),(28,15,'comida',3,1,12000.00),(29,15,'bebida',2,3,1500.00),(30,15,'bebida',5,1,2500.00),(31,16,'comida',5,2,9000.00),(32,16,'bebida',4,1,3500.00),(33,17,'comida',1,1,4500.00),(34,18,'bebida',5,3,2500.00),(35,19,'comida',3,1,12000.00),(36,19,'bebida',2,2,1500.00),(37,20,'bebida',5,2,2500.00),(38,20,'bebida',3,2,1800.00),(39,21,'comida',1,2,4500.00),(40,21,'bebida',2,2,1500.00),(41,22,'comida',3,2,12000.00),(42,22,'bebida',5,3,2500.00),(43,23,'comida',3,2,12000.00),(44,23,'bebida',5,3,2500.00),(45,24,'comida',2,1,7500.00),(46,24,'bebida',5,2,2500.00),(47,25,'comida',3,2,12000.00),(48,25,'bebida',5,3,2500.00);
/*!40000 ALTER TABLE `detalle_comanda` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `detalle_factura`
--

DROP TABLE IF EXISTS `detalle_factura`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `detalle_factura` (
  `id_det_fac` int NOT NULL AUTO_INCREMENT,
  `id_factura` int NOT NULL,
  `id_detalle` int NOT NULL,
  PRIMARY KEY (`id_det_fac`),
  KEY `id_factura` (`id_factura`),
  KEY `id_detalle` (`id_detalle`),
  CONSTRAINT `detalle_factura_ibfk_1` FOREIGN KEY (`id_factura`) REFERENCES `factura` (`id_factura`),
  CONSTRAINT `detalle_factura_ibfk_2` FOREIGN KEY (`id_detalle`) REFERENCES `detalle_comanda` (`id_detalle`)
) ENGINE=InnoDB AUTO_INCREMENT=24 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `detalle_factura`
--

LOCK TABLES `detalle_factura` WRITE;
/*!40000 ALTER TABLE `detalle_factura` DISABLE KEYS */;
INSERT INTO `detalle_factura` VALUES (1,1,1),(2,1,2),(3,2,3),(4,2,4),(5,3,5),(6,3,6),(7,4,8),(8,5,21),(9,5,22),(10,6,23),(11,6,24),(12,6,25),(13,6,26),(14,7,27),(15,7,28),(16,8,29),(17,8,30),(18,9,43),(19,9,44),(20,10,15),(21,10,16),(22,11,47),(23,11,48);
/*!40000 ALTER TABLE `detalle_factura` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `factura`
--

DROP TABLE IF EXISTS `factura`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `factura` (
  `id_factura` int NOT NULL AUTO_INCREMENT,
  `id_comanda` int NOT NULL,
  `codigo_cajero` varchar(6) DEFAULT NULL,
  `fecha_emision` datetime NOT NULL,
  `subtotal` decimal(10,2) NOT NULL,
  `impuesto` decimal(10,2) NOT NULL,
  `total` decimal(10,2) NOT NULL,
  `tipo` enum('provisional','final') DEFAULT 'provisional',
  `estado` enum('pendiente','pagada') DEFAULT 'pendiente',
  PRIMARY KEY (`id_factura`),
  KEY `id_comanda` (`id_comanda`),
  KEY `codigo_cajero` (`codigo_cajero`),
  CONSTRAINT `factura_ibfk_1` FOREIGN KEY (`id_comanda`) REFERENCES `comanda` (`id_comanda`),
  CONSTRAINT `factura_ibfk_2` FOREIGN KEY (`codigo_cajero`) REFERENCES `usuario` (`codigo`)
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `factura`
--

LOCK TABLES `factura` WRITE;
/*!40000 ALTER TABLE `factura` DISABLE KEYS */;
INSERT INTO `factura` VALUES (1,1,'CAJ001','2026-06-12 23:50:45',10500.00,1365.00,11865.00,'final','pagada'),(2,2,'CAJ001','2026-07-08 04:57:37',29000.00,3770.00,32770.00,'final','pagada'),(3,3,'CAJ001','2026-07-08 04:57:37',9500.00,1235.00,10735.00,'final','pagada'),(4,5,'CAJ001','2026-07-08 04:57:37',7500.00,975.00,8475.00,'final','pagada'),(5,13,'CAJ001','2026-07-28 20:08:38',10500.00,1365.00,11865.00,'final','pagada'),(6,14,'CAJ001','2026-07-28 20:08:39',23000.00,2990.00,25990.00,'final','pagada'),(7,15,'CAJ001','2026-07-28 20:09:47',21000.00,2730.00,23730.00,'final','pagada'),(8,15,'CAJ001','2026-07-28 20:11:29',7000.00,910.00,7910.00,'final','pagada'),(9,23,'CAJ001','2026-07-28 20:42:19',31500.00,4095.00,35595.00,'final','pagada'),(10,10,'CAJ001','2026-07-29 18:04:01',20000.00,2600.00,22600.00,'final','pagada'),(11,25,'CAJ001','2026-07-29 18:05:07',31500.00,4095.00,35595.00,'final','pagada'),(12,21,'SAL001','2026-07-29 18:09:45',12000.00,1560.00,13560.00,'provisional','pendiente');
/*!40000 ALTER TABLE `factura` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `mesa`
--

DROP TABLE IF EXISTS `mesa`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `mesa` (
  `id_mesa` int NOT NULL AUTO_INCREMENT,
  `numero_mesa` int NOT NULL,
  `id_seccion` int NOT NULL,
  `disponible` tinyint(1) DEFAULT '1',
  PRIMARY KEY (`id_mesa`),
  KEY `id_seccion` (`id_seccion`),
  CONSTRAINT `mesa_ibfk_1` FOREIGN KEY (`id_seccion`) REFERENCES `seccion_salon` (`id_seccion`)
) ENGINE=InnoDB AUTO_INCREMENT=22 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `mesa`
--

LOCK TABLES `mesa` WRITE;
/*!40000 ALTER TABLE `mesa` DISABLE KEYS */;
INSERT INTO `mesa` VALUES (1,1,1,1),(2,2,1,1),(3,3,1,0),(4,4,1,1),(5,5,1,1),(6,6,1,0),(7,7,2,1),(8,8,2,0),(9,9,2,0),(10,10,2,0),(11,11,2,1),(12,12,2,1),(13,13,3,1),(14,14,3,1),(15,15,3,0),(16,16,3,1),(17,17,3,1),(18,18,3,1),(20,20,4,1),(21,19,4,1);
/*!40000 ALTER TABLE `mesa` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `proceso_bar`
--

DROP TABLE IF EXISTS `proceso_bar`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `proceso_bar` (
  `id_proceso` int NOT NULL AUTO_INCREMENT,
  `id_comanda` int NOT NULL,
  `hora_recibida` datetime NOT NULL,
  `hora_lista` datetime DEFAULT NULL,
  `codigo_bar` varchar(6) DEFAULT NULL,
  PRIMARY KEY (`id_proceso`),
  UNIQUE KEY `id_comanda` (`id_comanda`),
  KEY `codigo_bar` (`codigo_bar`),
  CONSTRAINT `proceso_bar_ibfk_1` FOREIGN KEY (`id_comanda`) REFERENCES `comanda` (`id_comanda`),
  CONSTRAINT `proceso_bar_ibfk_2` FOREIGN KEY (`codigo_bar`) REFERENCES `usuario` (`codigo`)
) ENGINE=InnoDB AUTO_INCREMENT=22 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `proceso_bar`
--

LOCK TABLES `proceso_bar` WRITE;
/*!40000 ALTER TABLE `proceso_bar` DISABLE KEYS */;
INSERT INTO `proceso_bar` VALUES (1,1,'2026-06-12 23:50:17','2026-06-12 23:55:17','BAR001'),(2,2,'2026-07-08 04:57:22','2026-07-08 04:57:22','BAR001'),(3,5,'2026-07-08 04:57:22','2026-07-08 04:57:22','BAR001'),(4,6,'2026-07-08 04:57:22','2026-07-08 04:57:22','BAR001'),(5,7,'2026-07-07 04:57:22','2026-07-07 04:57:22','BAR001'),(8,10,'2026-07-28 19:31:49','2026-07-28 20:08:08','BAR001'),(9,12,'2026-07-28 19:54:49',NULL,NULL),(10,13,'2026-07-28 19:16:49','2026-07-28 19:22:49','BAR001'),(11,14,'2026-07-28 19:21:49','2026-07-28 19:27:49','BAR001'),(12,15,'2026-07-28 19:29:29','2026-07-28 19:34:29','BAR001'),(13,16,'2026-07-28 19:47:16',NULL,NULL),(14,18,'2026-07-28 20:09:16',NULL,NULL),(15,19,'2026-07-28 19:42:16','2026-07-28 19:48:16','BAR001'),(16,20,'2026-07-28 20:09:06','2026-07-28 20:15:06','BAR001'),(17,21,'2026-07-28 20:14:06','2026-07-28 20:20:06','BAR001'),(18,22,'2026-07-28 20:38:43',NULL,NULL),(19,23,'2026-07-28 20:42:15','2026-07-28 20:42:17','BAR001'),(20,24,'2026-07-29 18:02:39',NULL,NULL),(21,25,'2026-07-29 18:05:02','2026-07-29 18:05:05','BAR001');
/*!40000 ALTER TABLE `proceso_bar` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `proceso_cocina`
--

DROP TABLE IF EXISTS `proceso_cocina`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `proceso_cocina` (
  `id_proceso` int NOT NULL AUTO_INCREMENT,
  `id_comanda` int NOT NULL,
  `hora_recibida` datetime NOT NULL,
  `hora_lista` datetime DEFAULT NULL,
  `codigo_cos` varchar(6) DEFAULT NULL,
  PRIMARY KEY (`id_proceso`),
  UNIQUE KEY `id_comanda` (`id_comanda`),
  KEY `codigo_cos` (`codigo_cos`),
  CONSTRAINT `proceso_cocina_ibfk_1` FOREIGN KEY (`id_comanda`) REFERENCES `comanda` (`id_comanda`),
  CONSTRAINT `proceso_cocina_ibfk_2` FOREIGN KEY (`codigo_cos`) REFERENCES `usuario` (`codigo`)
) ENGINE=InnoDB AUTO_INCREMENT=21 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `proceso_cocina`
--

LOCK TABLES `proceso_cocina` WRITE;
/*!40000 ALTER TABLE `proceso_cocina` DISABLE KEYS */;
INSERT INTO `proceso_cocina` VALUES (1,1,'2026-06-12 23:50:00','2026-06-13 00:05:00','COS001'),(2,2,'2026-07-08 04:57:06','2026-07-08 04:57:06','COS001'),(3,3,'2026-07-08 04:57:06','2026-07-08 04:57:06','COS001'),(4,4,'2026-07-07 04:57:06','2026-07-07 04:57:06','COS001'),(5,6,'2026-07-08 04:57:06','2026-07-08 04:57:06','COS001'),(8,10,'2026-07-28 19:31:49','2026-07-28 20:07:41','COS001'),(9,11,'2026-07-28 19:58:49',NULL,NULL),(10,13,'2026-07-28 19:16:49','2026-07-28 19:28:49','COS001'),(11,14,'2026-07-28 19:21:49','2026-07-28 19:33:49','COS001'),(12,15,'2026-07-28 19:29:29','2026-07-28 19:40:29','COS001'),(13,16,'2026-07-28 19:47:16',NULL,NULL),(14,17,'2026-07-28 20:06:16',NULL,NULL),(15,19,'2026-07-28 19:42:16','2026-07-28 19:54:16','COS001'),(16,21,'2026-07-28 20:14:06','2026-07-28 20:26:06','COS001'),(17,22,'2026-07-28 20:38:43',NULL,NULL),(18,23,'2026-07-28 20:42:15','2026-07-28 20:42:16','COS001'),(19,24,'2026-07-29 18:02:39',NULL,NULL),(20,25,'2026-07-29 18:05:02','2026-07-29 18:05:04','COS001');
/*!40000 ALTER TABLE `proceso_cocina` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `reserva`
--

DROP TABLE IF EXISTS `reserva`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `reserva` (
  `id_reserva` int NOT NULL AUTO_INCREMENT,
  `nombre_cliente` varchar(100) NOT NULL,
  `telefono` varchar(20) DEFAULT NULL,
  `fecha_reserva` date NOT NULL,
  `hora_reserva` time NOT NULL,
  `cantidad_pers` int NOT NULL,
  `incluye_ninos` tinyint(1) DEFAULT '0',
  `id_mesa` int DEFAULT NULL,
  `estado` enum('pendiente','confirmada','cancelada','atendida') DEFAULT 'pendiente',
  `fecha_creacion` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id_reserva`),
  KEY `id_mesa` (`id_mesa`),
  CONSTRAINT `reserva_ibfk_1` FOREIGN KEY (`id_mesa`) REFERENCES `mesa` (`id_mesa`)
) ENGINE=InnoDB AUTO_INCREMENT=31 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `reserva`
--

LOCK TABLES `reserva` WRITE;
/*!40000 ALTER TABLE `reserva` DISABLE KEYS */;
INSERT INTO `reserva` VALUES (1,'Juan Pérez','88001234','2026-06-12','12:00:00',4,0,1,'confirmada','2026-06-12 23:48:46'),(2,'Laura Jiménez','87552211','2026-07-10','19:00:00',2,0,5,'confirmada','2026-07-09 04:58:19'),(3,'Carlos Brenes','86114455','2026-07-11','12:30:00',6,1,10,'pendiente','2026-07-09 04:58:19'),(4,'Rosa Quirós','89663322','2026-07-09','18:00:00',3,1,NULL,'cancelada','2026-07-09 04:58:19'),(5,'Familia Vargas','8888-1122','2026-07-29','19:00:00',6,1,NULL,'confirmada','2026-07-29 02:06:49'),(6,'Ana Cordero','8712-4590','2026-07-30','20:30:00',2,0,NULL,'pendiente','2026-07-29 02:06:49'),(7,'Empresa Solutions','2222-3344','2026-08-02','12:30:00',12,0,NULL,'confirmada','2026-07-29 02:06:49'),(8,'Marco Ureña','6045-7788','2026-08-04','18:00:00',4,1,NULL,'pendiente','2026-07-29 02:06:49'),(9,'Grupo Chaves','8811-2200','2026-07-28','13:00:00',4,0,5,'atendida','2026-07-29 02:29:06'),(10,'Mesa de Karla','8700-3311','2026-07-28','17:30:00',3,0,6,'atendida','2026-07-29 02:29:06'),(11,'Pareja Ramírez','8955-1010','2026-07-28','19:00:00',2,0,9,'atendida','2026-07-29 02:29:06');
/*!40000 ALTER TABLE `reserva` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `seccion_salon`
--

DROP TABLE IF EXISTS `seccion_salon`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `seccion_salon` (
  `id_seccion` int NOT NULL AUTO_INCREMENT,
  `nombre` varchar(60) NOT NULL,
  PRIMARY KEY (`id_seccion`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `seccion_salon`
--

LOCK TABLES `seccion_salon` WRITE;
/*!40000 ALTER TABLE `seccion_salon` DISABLE KEYS */;
INSERT INTO `seccion_salon` VALUES (1,'Sección A'),(2,'Sección B'),(3,'Sección C'),(4,'Terraza');
/*!40000 ALTER TABLE `seccion_salon` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `tipo_usuario`
--

DROP TABLE IF EXISTS `tipo_usuario`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tipo_usuario` (
  `id_tipo` int NOT NULL AUTO_INCREMENT,
  `nombre` varchar(30) NOT NULL,
  `prefijo` char(3) NOT NULL,
  PRIMARY KEY (`id_tipo`),
  UNIQUE KEY `nombre` (`nombre`),
  UNIQUE KEY `prefijo` (`prefijo`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tipo_usuario`
--

LOCK TABLES `tipo_usuario` WRITE;
/*!40000 ALTER TABLE `tipo_usuario` DISABLE KEYS */;
INSERT INTO `tipo_usuario` VALUES (1,'Salonero','SAL'),(2,'Cocinero','COS'),(3,'Bartender','BAR'),(4,'Cajero','CAJ');
/*!40000 ALTER TABLE `tipo_usuario` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `usuario`
--

DROP TABLE IF EXISTS `usuario`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `usuario` (
  `codigo` varchar(6) NOT NULL,
  `nombre` varchar(100) NOT NULL,
  `contrasena` varchar(255) NOT NULL,
  `id_tipo` int NOT NULL,
  `activo` tinyint(1) DEFAULT '1',
  PRIMARY KEY (`codigo`),
  KEY `id_tipo` (`id_tipo`),
  CONSTRAINT `usuario_ibfk_1` FOREIGN KEY (`id_tipo`) REFERENCES `tipo_usuario` (`id_tipo`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `usuario`
--

LOCK TABLES `usuario` WRITE;
/*!40000 ALTER TABLE `usuario` DISABLE KEYS */;
INSERT INTO `usuario` VALUES ('BAR001','Diego Campos','32250170a0dca92d53ec9624f336ca24',3,1),('CAJ001','María López','32250170a0dca92d53ec9624f336ca24',4,1),('COS001','Pedro Rojas','32250170a0dca92d53ec9624f336ca24',2,1),('SAL001','Ana Solís','32250170a0dca92d53ec9624f336ca24',1,1),('SAL002','Luis Mora','32250170a0dca92d53ec9624f336ca24',1,1),('SAL003','Carmen Vega','32250170a0dca92d53ec9624f336ca24',1,1),('SAL567','Sofía Herrera','bb1dfaf399e9ac67760b674f47ef549d',1,1);
/*!40000 ALTER TABLE `usuario` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-07-29 18:10:39
