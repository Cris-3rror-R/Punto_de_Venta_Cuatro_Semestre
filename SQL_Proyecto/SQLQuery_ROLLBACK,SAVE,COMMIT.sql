/* COMMIT */
BEGIN TRANSACTION
DELETE FROM PROVEEDORES WHERE Clav_TiP = 9
INSERT INTO Proveedores (Nom_Pv,Tel_Pv,Clav_TiP) VALUES 
('Malcedo Meat','0000000781',9);
COMMIT;

/*ROLLBACK*/
BEGIN TRANSACTION
DELETE FROM Gastos
WHERE Fec_Gt >= '2025-09-27 00:00:00' AND Fec_Gt <= '2025-09-27 23:59:59'
DELETE FROM Ticket
WHERE Fec_Tk >= '2025-09-27 00:00:00' AND Fec_Tk <= '2025-09-27 23:59:59'
ROLLBACK;

/*SAVE POINT*/
BEGIN TRANSACTION;
SAVE TRANSACTION BANDERA;
UPDATE Credito
SET Tel_Cr = '3312345678',
    Cll_Cr = 'Calle Hidalgo',
    NCl_Cr = 712,
    Col_Cr = 'Centro',
    Pob_Cr = 'Ocotlán',
    Ape_Cr = 'Zaragoza Velázquez'
WHERE Clav_Cr = 1;
UPDATE Credito
SET Tel_Cr = '3922345678',
    Cll_Cr = 'Av. Universidad',
    NCl_Cr = 781,
    Col_Cr = 'La Primavera',
    Pob_Cr = 'Ocotlán',
    Ape_Cr = 'Ramírez López'
WHERE Clav_Cr = 2;
ROLLBACK TRANSACTION BANDERA;
COMMIT;


