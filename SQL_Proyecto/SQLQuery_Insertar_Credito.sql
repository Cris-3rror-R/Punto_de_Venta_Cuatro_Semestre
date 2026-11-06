/*INSERT INTO Credito(Nom_Cr,Ape_Cr,Tel_Cr,Cll_Cr,Col_Cr,NCl_Cr,Pob_Cr,Clav_Tk) VALUES
('Martin Jose','Lopez Lopez','0000000000','Reforma','Centro',124,'Ocotlan',1),
('Maria Patricia','Gonzalez Carrillo','0000000001','Galeana','Centro',141,'Ocotlan',1),
('Luis','De la Torre Fierros','0000000002','Morelos','Centro',74,'Ocotlan',1),
('Alberto Yunuel','Martinez Lopez','0000000003','Alvaro Obregon','Centro',151,'Ocotlan',1),
('Waltter','Gondinez Amezcua','000000004','1910','Mascotas',251,'Ocotlan',1),
('Kevin Brayan','Zaragoza Velazquez','0000000005','12 de Octubre','San Juan',712,'Ocotlan',1),
('Jose Maria','Ramirez Lopez','0000000006','Ficus','Habitacional San Andres',781,'Ocotlan',1)
;
*/

BEGIN TRAN ActualizacionClavTk;

-- Martin Jose
UPDATE Credito
SET Clav_Tk = 2
WHERE Nom_Cr = 'Martin Jose' AND Ape_Cr = 'Lopez Lopez';

-- Maria Patricia
UPDATE Credito
SET Clav_Tk = 3
WHERE Nom_Cr = 'Maria Patricia' AND Ape_Cr = 'Gonzalez Carrillo';

-- Luis
UPDATE Credito
SET Clav_Tk = 4
WHERE Nom_Cr = 'Luis' AND Ape_Cr = 'De la Torre Fierros';

-- Alberto Yunuel
UPDATE Credito
SET Clav_Tk = 5
WHERE Nom_Cr = 'Alberto Yunuel' AND Ape_Cr = 'Martinez Lopez';

-- Waltter
UPDATE Credito
SET Clav_Tk = 6
WHERE Nom_Cr = 'Waltter' AND Ape_Cr = 'Gondinez Amezcua';

-- Kevin Brayan
UPDATE Credito
SET Clav_Tk = 7
WHERE Nom_Cr = 'Kevin Brayan' AND Ape_Cr = 'Zaragoza Velazquez';

-- Jose Maria
UPDATE Credito
SET Clav_Tk = 8
WHERE Nom_Cr = 'Jose Maria' AND Ape_Cr = 'Ramirez Lopez';

COMMIT TRAN ActualizacionClavTk;

-- Mostrar resultado
SELECT Clav_Cr, Nom_Cr, Ape_Cr, Tel_Cr, Cll_Cr, Col_Cr, NCl_Cr, Pob_Cr, Clav_Tk
FROM Credito;




SELECT Clav_Cr,Nom_Cr,Ape_Cr,Tel_Cr,Cll_Cr,NCl_Cr,Col_Cr,Pob_Cr,Clav_Tk FROM Credito WHERE Nom_Cr = 'Raul Eugenio';

SELECT * FROM Ticket

SELECT * FROM Productos;

SELECT * FROM Tipos_Productos;

SELECT * FROM Gastos;