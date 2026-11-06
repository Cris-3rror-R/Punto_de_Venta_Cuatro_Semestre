/*Ejemplo 1
CREATE TRIGGER EstatusCliente
ON Ticket
AFTER INSERT
AS
BEGIN
    IF EXISTS (
        SELECT 1
        FROM inserted i
        JOIN Ticket t ON t.Clav_Cr = i.Clav_Cr
        WHERE t.Pag_Tk = 'No'
        GROUP BY i.Clav_Cr
        HAVING COUNT(*) >= 3
    )
    BEGIN
        RAISERROR('El cliente ya tiene 3 tickets sin pagar. No puede añadir más.', 16, 1);
        ROLLBACK TRANSACTION;
    END
END;
*/

/*Ejemplo 2
CREATE TRIGGER InventarioNegativo
ON Ticket
AFTER INSERT
AS
BEGIN
    IF EXISTS (
        SELECT 1
        FROM inserted i
        JOIN Producto p ON i.Clav_Pr = p.Clav_Pr
        WHERE p.Stock <= 0
    )
    BEGIN
        RAISERROR('No se puede vender un producto con inventario negativo.', 16, 1);
        ROLLBACK TRANSACTION;
    END
END;
*/

/*Ejemplo 3
CREATE TRIGGER ActualizarInventario
ON TicketDetalle
AFTER INSERT
AS
BEGIN
    SET NOCOUNT ON;
    UPDATE p
    SET p.Inv_Prd = p.Inv_Prd - i.Can_Td
    FROM Productos p
    INNER JOIN inserted i ON p.Clav_Prd = i.Clav_Prd;
END;
*/

/*Ejemplo 4
CREATE TRIGGER ActualizarTotalTicket
ON TicketDetalle
AFTER INSERT
AS
BEGIN
    SET NOCOUNT ON;
UPDATE t
    SET t.Tot_Tk = (
        SELECT SUM(i.Can_Td * i.Pre_Td)
        FROM TicketDetalle i
        WHERE i.Clav_Tk = t.Clav_Tk
    )
    FROM Ticket t
    INNER JOIN inserted i ON t.Clav_Tk = i.Clav_Tk;
END;
*/

/*Ejemplo 5*/
CREATE TRIGGER EvitarEliminarProducto
ON Productos
INSTEAD OF DELETE
AS
BEGIN
    IF EXISTS (
        SELECT 1 FROM deleted WHERE Inv_Prd > 0
    )
    BEGIN
        RAISERROR('No se puede eliminar un producto con inventario disponible.', 16, 1);
        ROLLBACK TRANSACTION;
    END
    ELSE
    BEGIN
        DELETE FROM Productos WHERE Clav_Prd IN (SELECT Clav_Prd FROM deleted);
    END
END;




