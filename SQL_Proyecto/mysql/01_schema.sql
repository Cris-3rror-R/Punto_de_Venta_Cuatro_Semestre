CREATE DATABASE IF NOT EXISTS Carniceria
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_spanish_ci;

USE Carniceria;

CREATE TABLE IF NOT EXISTS Tipos_Productos (
    Clav_TiP INT AUTO_INCREMENT PRIMARY KEY,
    Nom_TiP VARCHAR(35) NOT NULL UNIQUE,
    Des_TiP VARCHAR(100) NOT NULL
);

CREATE TABLE IF NOT EXISTS Proveedores (
    Clav_Pv INT AUTO_INCREMENT PRIMARY KEY,
    Nom_Pv VARCHAR(70) NOT NULL,
    Tipo_Pv VARCHAR(30) NOT NULL DEFAULT 'General',
    Tel_Pv VARCHAR(20) UNIQUE,
    Clav_TiP INT NULL,
    CONSTRAINT fk_proveedores_tipo
        FOREIGN KEY (Clav_TiP) REFERENCES Tipos_Productos(Clav_TiP)
);

CREATE TABLE IF NOT EXISTS Productos (
    Clav_Prd INT AUTO_INCREMENT PRIMARY KEY,
    Nom_Prd VARCHAR(70) NOT NULL,
    Cos_Prd DECIMAL(12,2) NOT NULL DEFAULT 0,
    Pre_Prd DECIMAL(12,2) NOT NULL,
    Inv_Prd DECIMAL(12,3) NOT NULL DEFAULT 0,
    Clav_TiP INT NOT NULL,
    Clav_Pv INT NULL,
    CONSTRAINT chk_productos_costo CHECK (Cos_Prd >= 0),
    CONSTRAINT chk_productos_precio CHECK (Pre_Prd >= 0),
    CONSTRAINT chk_productos_inventario CHECK (Inv_Prd >= 0),
    CONSTRAINT fk_productos_tipo
        FOREIGN KEY (Clav_TiP) REFERENCES Tipos_Productos(Clav_TiP),
    CONSTRAINT fk_productos_proveedor
        FOREIGN KEY (Clav_Pv) REFERENCES Proveedores(Clav_Pv)
);

CREATE TABLE IF NOT EXISTS Credito (
    Clav_Cr INT AUTO_INCREMENT PRIMARY KEY,
    Nom_Cr VARCHAR(35) NOT NULL,
    Ape_Cr VARCHAR(35) NOT NULL,
    Tel_Cr VARCHAR(20) NOT NULL UNIQUE,
    Cll_Cr VARCHAR(70) NOT NULL,
    Col_Cr VARCHAR(70) NOT NULL,
    NCl_Cr INT NOT NULL DEFAULT 0,
    Pob_Cr VARCHAR(70) NOT NULL
);

CREATE TABLE IF NOT EXISTS Ticket (
    Clav_Tk INT AUTO_INCREMENT PRIMARY KEY,
    Tot_Tk DECIMAL(12,2) NOT NULL,
    Descuento_Tk DECIMAL(12,2) NOT NULL DEFAULT 0,
    Pag_Tk VARCHAR(2) NOT NULL DEFAULT 'Si',
    Fec_Tk DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    Clav_Cr INT NULL,
    CONSTRAINT chk_ticket_total CHECK (Tot_Tk >= 0),
    CONSTRAINT chk_ticket_descuento CHECK (Descuento_Tk >= 0),
    CONSTRAINT chk_ticket_pagado CHECK (Pag_Tk IN ('Si', 'No')),
    CONSTRAINT fk_ticket_cliente
        FOREIGN KEY (Clav_Cr) REFERENCES Credito(Clav_Cr)
);

CREATE TABLE IF NOT EXISTS TicketDetalle (
    Clav_Tk INT NOT NULL,
    Clav_Prd INT NOT NULL,
    Can_Td DECIMAL(12,3) NOT NULL,
    Pre_Td DECIMAL(12,2) NOT NULL,
    PRIMARY KEY (Clav_Tk, Clav_Prd),
    CONSTRAINT chk_detalle_cantidad CHECK (Can_Td > 0),
    CONSTRAINT chk_detalle_precio CHECK (Pre_Td >= 0),
    CONSTRAINT fk_detalle_ticket
        FOREIGN KEY (Clav_Tk) REFERENCES Ticket(Clav_Tk),
    CONSTRAINT fk_detalle_producto
        FOREIGN KEY (Clav_Prd) REFERENCES Productos(Clav_Prd)
);

CREATE TABLE IF NOT EXISTS Gastos (
    Clav_Gt INT AUTO_INCREMENT PRIMARY KEY,
    Des_Gt VARCHAR(100) NOT NULL,
    Tot_Gt DECIMAL(12,2) NOT NULL,
    Fec_Gt DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT chk_gastos_total CHECK (Tot_Gt > 0)
);

CREATE TABLE IF NOT EXISTS Usuarios (
    Usuario VARCHAR(50) PRIMARY KEY,
    ContrasenaHash CHAR(64) NOT NULL
);
