/* Ejercicio 63
INSERT INTO Credito(Nom_Cr,Ape_Cr,Tel_Cr,Cll_Cr,Col_Cr,NCl_Cr,Pob_Cr,Clav_Tk) VALUES
('Raul Eugenio','Perez Gonzalez','0000000081','Galeana','Centro',374,'Ocotlan',9);
*/

/* Ejercicio 64
INSERT INTO Ticket (Des_Tk, Fec_Tk, Tot_Tk)
VALUES ('2.5kg Diesmillo, 1kg Longaniza', SYSDATETIME(), 600);
*/

/* Ejercicio 65
DECLARE @Descri NVARCHAR(100) = '3.5kg Diesmillo, 1.2kg Longaniza';
DECLARE @Fech DATETIME = SYSDATETIME();
DECLARE @Total INT = 970;

INSERT INTO Ticket (Des_Tk, Fec_Tk, Tot_Tk)
VALUES (@Descri, @Fech, @Total);
*/

/* Ejercicio 66
DECLARE @Descri NVARCHAR(255);
DECLARE @Fech DATETIME;
DECLARE @Total INT;

SET @Descri = '1.5kg Bola, 0.5kg Longaniza';
SET @Fech = SYSDATETIME();
SET @Total = 350;

INSERT INTO Ticket (Des_Tk, Fec_Tk, Tot_Tk)
VALUES (@Descri, @Fech, @Total);
*/

/* Ejercicio 67
INSERT INTO Productos (Nom_Prd, Cos_Prd, Pre_Prd, Inv_Prd, Clav_TiP)
SELECT P.Nom_Prd, P.Cos_Prd, P.Pre_Prd, P.Inv_Prd, P.Clav_TiP
FROM Productos P
JOIN Tipos_Productos T ON P.Clav_TiP = T.Clav_TiP
WHERE T.Nom_TiP = 'Puerco';
*/

/* Ejercicio 68
UPDATE Productos
SET Nom_Prd = 'Rellena', Cos_Prd =40 ,Pre_Prd =80, Inv_Prd =102.25 ,Clav_TiP =7
WHERE Clav_Prd = 40;
*/

/* Ejercicio 69
UPDATE Productos
SET Clav_TiP =8;
*/

/*
UPDATE Productos
SET Cos_Prd = P.Cos_Prd,
    Pre_Prd = P.Pre_Prd
FROM Productos
JOIN (
    SELECT Cos_Prd, Pre_Prd
    FROM Productos
    WHERE Clav_Prd = 21
) AS P ON Productos.Clav_Prd = 12;
*/

/* Ejercicio 71
UPDATE P
SET P.Clav_TiP =T.Clav_TiP
FROM Productos P
JOIN Tipos_Productos T ON 
    (P.Nom_Prd LIKE '%puerco%' AND T.Nom_TiP = 'Puerco');
*/

/* Ejercicio 72
DELETE FROM Productos
WHERE Clav_Prd = 33;
*/

/* Ejercicio 73
DELETE FROM Gastos;
*/
