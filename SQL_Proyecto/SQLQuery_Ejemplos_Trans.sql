/*Ejemplo 1*/
BEGIN TRAN ActualizacionCredito;
UPDATE Credito
SET Tel_Cr = '3315555555',
    Cll_Cr = 'Morelos',
    Col_Cr = 'Centro',
    Pob_Cr = 'Ocotlán'
WHERE Clav_Cr = 5;
COMMIT TRAN ActualizacionCredito;

SELECT * FROM Credito WHERE Clav_Cr = 5;

/*Ejemplo 2*/
BEGIN TRAN ActualizacionMartin;
UPDATE Credito
SET Tel_Cr = '3319999999'
WHERE Nom_Cr = 'Martin Jose' AND Ape_Cr = 'Lopez Lopez';
COMMIT TRAN ActualizacionMartin;

SELECT * FROM Credito WHERE Nom_Cr = 'Martin Jose';

/*Ejemplo 3*/
BEGIN TRAN CambioMaria;
UPDATE Credito
SET Clav_Tk = 10
WHERE Nom_Cr = 'Maria Patricia' AND Ape_Cr = 'Gonzalez Carrillo';
ROLLBACK TRAN CambioMaria;

/*SELECT * FROM Credito WHERE Nom_Cr = 'Maria Patricia';*/

/*Ejemplo 4*/
BEGIN TRAN EliminarLuis;
DELETE FROM Credito WHERE Nom_Cr = 'Luis' AND Ape_Cr = 'De la Torre Fierros';
ROLLBACK TRAN EliminarLuis;

/*SELECT * FROM Credito WHERE Nom_Cr = 'Luis';*/

/*Ejemplo 5*/
BEGIN TRAN CambiosParciales;

UPDATE Credito
SET Col_Cr = 'Centro Histórico'
WHERE Nom_Cr = 'Kevin Brayan';

SAVE TRANSACTION PuntoMedio;

UPDATE Credito
SET Col_Cr = 'Habitacional San Andres'
WHERE Nom_Cr = 'Jose Maria';

ROLLBACK TRANSACTION PuntoMedio;
COMMIT TRAN CambiosParciales;

SELECT Nom_Cr, Ape_Cr, Col_Cr FROM Credito WHERE Nom_Cr IN ('Kevin Brayan','Jose Maria');
