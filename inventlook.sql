-- MySQL dump 10.13  Distrib 8.0.46, for Win64 (x86_64)
--
-- Host: localhost    Database: inventlook
-- ------------------------------------------------------
-- Server version	8.0.46

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `categorias`
--

DROP TABLE IF EXISTS `categorias`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `categorias` (
  `id_categoria` int NOT NULL AUTO_INCREMENT,
  `nombre_categoria` varchar(50) NOT NULL,
  PRIMARY KEY (`id_categoria`),
  UNIQUE KEY `nombre_categoria` (`nombre_categoria`)
) ENGINE=InnoDB AUTO_INCREMENT=42 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `categorias`
--

LOCK TABLES `categorias` WRITE;
/*!40000 ALTER TABLE `categorias` DISABLE KEYS */;
INSERT INTO `categorias` VALUES (41,'Aceites'),(27,'Aguas'),(9,'carnes'),(21,'Cerdo'),(32,'Cocina'),(17,'Condimentos'),(35,'Cuidado Capilar'),(30,'Desechables'),(22,'Embutidos'),(16,'Endulzantes'),(19,'Enlatados'),(11,'Frutas'),(29,'Gaseosas'),(1,'Granos'),(15,'Harinas'),(34,'Higiene'),(31,'Hogar'),(25,'Huevos'),(28,'Jugos'),(24,'Lacteos'),(33,'Limpieza'),(36,'Mascotas'),(38,'Mecatos'),(13,'Pastas'),(23,'Pescados'),(20,'Pollo'),(18,'Salsas'),(26,'Verduras');
/*!40000 ALTER TABLE `categorias` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `codigo_recuperacion`
--

DROP TABLE IF EXISTS `codigo_recuperacion`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `codigo_recuperacion` (
  `id` int NOT NULL AUTO_INCREMENT,
  `correo` varchar(150) COLLATE utf8mb4_spanish_ci NOT NULL,
  `codigo` varchar(10) COLLATE utf8mb4_spanish_ci NOT NULL,
  `fecha_expiracion` datetime NOT NULL,
  `usado` tinyint(1) DEFAULT '0',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uq_correo_codigo` (`correo`,`codigo`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_spanish_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `codigo_recuperacion`
--

LOCK TABLES `codigo_recuperacion` WRITE;
/*!40000 ALTER TABLE `codigo_recuperacion` DISABLE KEYS */;
INSERT INTO `codigo_recuperacion` VALUES (1,'noireliteofficial@gmail.com','088442','2026-09-02 16:15:49',1),(2,'noireliteofficial@gmail.com','427383','2026-09-14 15:25:01',1),(3,'noireliteofficial@gmail.com','333954','2026-09-28 18:16:53',0);
/*!40000 ALTER TABLE `codigo_recuperacion` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `movimientos`
--

DROP TABLE IF EXISTS `movimientos`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `movimientos` (
  `id_movimiento` int NOT NULL AUTO_INCREMENT,
  `tipo_movimiento` varchar(20) COLLATE utf8mb4_spanish_ci NOT NULL,
  `cantidad` int NOT NULL,
  `fecha_hora` datetime NOT NULL,
  `observacion` varchar(255) COLLATE utf8mb4_spanish_ci DEFAULT NULL,
  `id_producto` int DEFAULT NULL,
  `id_usuario` int NOT NULL,
  `nombre_producto` varchar(100) COLLATE utf8mb4_spanish_ci NOT NULL,
  PRIMARY KEY (`id_movimiento`),
  KEY `idx_tipo` (`tipo_movimiento`),
  KEY `idx_fecha` (`fecha_hora`),
  KEY `idx_producto` (`id_producto`),
  KEY `idx_usuario` (`id_usuario`),
  CONSTRAINT `fk_movimientos_producto` FOREIGN KEY (`id_producto`) REFERENCES `producto` (`id_producto`) ON DELETE SET NULL ON UPDATE CASCADE,
  CONSTRAINT `movimientos_ibfk_2` FOREIGN KEY (`id_usuario`) REFERENCES `usuario` (`id_usuario`) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=77 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_spanish_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `movimientos`
--

LOCK TABLES `movimientos` WRITE;
/*!40000 ALTER TABLE `movimientos` DISABLE KEYS */;
INSERT INTO `movimientos` VALUES (69,'AJUSTE',24,'2026-09-24 06:41:41','Se disminuyo el stock en 24 unidades',20,3,'Arroz diana 500g'),(70,'AJUSTE',13,'2026-09-24 06:42:29','Se aumento el stock en 13 unidades',20,3,'Arroz diana 500g'),(71,'AJUSTE',13,'2026-09-24 10:26:13','Se disminuyo el stock en 13 unidades',20,3,'Arroz diana 500g'),(72,'ENTRADA',80,'2026-09-28 18:07:59','Producto agregado al inventario',32,3,'Bolsa de agua  6l'),(73,'ENTRADA',134,'2026-09-28 18:08:33','Producto agregado al inventario',33,3,'Tomate de arbol'),(74,'ENTRADA',68,'2026-10-01 15:52:21','Producto agregado al inventario',34,3,'Aguila - 15l'),(75,'ENTRADA',0,'2026-10-01 15:52:44','Producto agregado al inventario',NULL,3,'poker, 500g'),(76,'SALIDA',0,'2026-10-01 15:52:51','Producto eliminado del inventario',NULL,3,'poker, 500g');
/*!40000 ALTER TABLE `movimientos` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `producto`
--

DROP TABLE IF EXISTS `producto`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `producto` (
  `id_producto` int NOT NULL AUTO_INCREMENT,
  `codigo` varchar(10) NOT NULL,
  `nombre` varchar(100) NOT NULL,
  `id_categoria` int DEFAULT NULL,
  `id_usuario` int DEFAULT NULL,
  `estado` varchar(20) NOT NULL DEFAULT 'Óptimo',
  `proveedor` varchar(100) DEFAULT NULL,
  `actual` int NOT NULL DEFAULT '0',
  `bodega` int NOT NULL DEFAULT '0',
  PRIMARY KEY (`id_producto`),
  UNIQUE KEY `codigo` (`codigo`),
  KEY `id_usuario` (`id_usuario`),
  KEY `fk_nueva_sin_conflicto` (`id_categoria`),
  CONSTRAINT `fk_nueva_sin_conflicto` FOREIGN KEY (`id_categoria`) REFERENCES `categorias` (`id_categoria`) ON DELETE SET NULL,
  CONSTRAINT `producto_ibfk_2` FOREIGN KEY (`id_usuario`) REFERENCES `usuario` (`id_usuario`)
) ENGINE=InnoDB AUTO_INCREMENT=36 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `producto`
--

LOCK TABLES `producto` WRITE;
/*!40000 ALTER TABLE `producto` DISABLE KEYS */;
INSERT INTO `producto` VALUES (20,'001','Arroz diana 500g',1,3,'Stock bajo','Molinos Diana',10,23),(21,'002','Coca-cola 3L',29,3,'Optimo','Coca-cola Colombia',56,24),(25,'003','frijol - 500g',1,3,'Optimo','granos yopal',24,145),(31,'004','bigcola 1L',29,3,'Optimo','surtivicola',123,45),(32,'005','Bolsa de agua  6l',27,3,'Stock bajo','hielo yopal',13,67),(33,'006','Tomate de arbol',26,3,'Optimo','',45,89),(34,'007','Aguila - 1.5L',28,3,'Stock bajo','AGUILA COLOMBIA',12,56);
/*!40000 ALTER TABLE `producto` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `usuario`
--

DROP TABLE IF EXISTS `usuario`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `usuario` (
  `id_usuario` int NOT NULL AUTO_INCREMENT,
  `nombre` varchar(100) NOT NULL,
  `correo` varchar(150) NOT NULL,
  `contrasena` varchar(255) NOT NULL,
  `telefono` varchar(20) DEFAULT NULL,
  `fecha_registro` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `rol` varchar(20) NOT NULL DEFAULT 'EMPLEADO',
  `activo` tinyint(1) NOT NULL DEFAULT '1',
  PRIMARY KEY (`id_usuario`),
  UNIQUE KEY `correo` (`correo`),
  UNIQUE KEY `uk_telefono` (`telefono`)
) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `usuario`
--

LOCK TABLES `usuario` WRITE;
/*!40000 ALTER TABLE `usuario` DISABLE KEYS */;
INSERT INTO `usuario` VALUES (3,'Santos Achagua','noireliteofficial@gmail.com','10296644910','3108320965','2026-09-02 19:07:14','ADMINISTRADOR',1),(9,'Daniel Puin','daniel@gmail.com','1234567890','3108320987','2026-09-17 23:09:56','EMPLEADO',1);
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

-- Dump completed on 2026-10-05 12:53:44
