CREATE TABLE Proveedores (
    Clav_Pv INT IDENTITY(1,1),
    Nom_Pv VARCHAR(35) NOT NULL,
    Tipo_Pv VARCHAR(20) NOT NULL,
    Tel_Pv VARCHAR(10) UNIQUE NOT NULL,
    PRIMARY KEY (Clav_Pv)
);

CREATE TABLE Productos (
    Clav_Prd INT IDENTITY(1,1),
    Nom_Prd VARCHAR(35) NOT NULL,
    Cos_Prd FLOAT NOT NULL,
    Pre_Prd FLOAT NOT NULL,
    Inv_Prd FLOAT NOT NULL,
    Tip_Prd VARCHAR(30) NOT NULL,
    Clav_Pv INT NOT NULL,
    PRIMARY KEY (Clav_Prd),
    FOREIGN KEY (Clav_Pv) REFERENCES Proveedores(Clav_Pv)
);

CREATE TABLE Ticket (
    Clav_Tk INT IDENTITY(1,1),
    Des_Tk VARCHAR(60) NOT NULL,
    Tot_Tk INT NOT NULL,
    Fec_Tk DATETIME NOT NULL,
    PRIMARY KEY (Clav_Tk)
);

CREATE TABLE Credito (
    Clav_Cr INT IDENTITY(1,1),
    Nom_Cr VARCHAR(35) NOT NULL,
    Tel_Cr VARCHAR(10) UNIQUE NOT NULL,
    Dom_Cr VARCHAR(30),
    Clav_Tk INT,
    PRIMARY KEY (Clav_Cr),
    FOREIGN KEY (Clav_Tk) REFERENCES Ticket(Clav_Tk)
);

CREATE TABLE Gastos (
    Clav_Gt INT IDENTITY(1,1),
    Des_Gt VARCHAR(35) NOT NULL,
    Tot_Gt FLOAT NOT NULL,
    Fec_Gt DATETIME NOT NULL,
    PRIMARY KEY (Clav_Gt)
);
