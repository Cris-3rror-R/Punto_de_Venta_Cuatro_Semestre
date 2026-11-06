/*Ejemplo 1*/
/*
CREATE PROCEDURE CalcularDescuentoTicket
    @Clav_Tk INT,                          
    @Descuento DECIMAL(5, 2),           
    @Precio_Final DECIMAL(10, 2) OUTPUT
AS
BEGIN
    DECLARE @Precio_Original DECIMAL(10, 2);
    SELECT @Precio_Original = Tot_Tk
    FROM Ticket
    WHERE Clav_Tk = @Clav_Tk;
    SET @Precio_Final = @Precio_Original * (1 - @Descuento);
END;
*/

/*Ejemplo 2*/
/*
CREATE PROCEDURE AddCreditoCliente
    @Nom_Cr VARCHAR(35),
    @Ape_Cr VARCHAR(35),       
    @Tel_Cr VARCHAR(10),        
    @Cll_Cr VARCHAR(35),      
    @Col_Cr VARCHAR(35),        
    @NCl_Cr INT,               
    @Pob_Cr VARCHAR(35),       
    @Clav_Tk INT 
AS
BEGIN
    IF EXISTS (SELECT 1 FROM Credito WHERE Tel_Cr = @Tel_Cr)
        THROW 50000, 'El teléfono ya existe en la tabla Credito.', 1;
    ELSE
        INSERT INTO Credito (Nom_Cr, Ape_Cr, Tel_Cr, Cll_Cr, Col_Cr, NCl_Cr, Pob_Cr, Clav_Tk)
        VALUES (@Nom_Cr, @Ape_Cr, @Tel_Cr, @Cll_Cr, @Col_Cr, @NCl_Cr, @Pob_Cr, @Clav_Tk);
END;
*/

/*Ejemplo 3*/
/*
CREATE PROCEDURE CalcularUtilidadDia
    @Fecha DATE,                       
    @Utilidad DECIMAL(10,2) OUTPUT
AS
BEGIN
    DECLARE @TotalVentas DECIMAL(10,2);
    DECLARE @TotalGastos DECIMAL(10,2);

    SELECT @TotalVentas = ISNULL(SUM(Tot_Tk),0)
    FROM Ticket
    WHERE CAST(Fec_Tk AS DATE) = @Fecha;

    SELECT @TotalGastos = ISNULL(SUM(Tot_Gt),0)
    FROM Gastos
    WHERE CAST(Fec_Gt AS DATE) = @Fecha;

    SET @Utilidad = @TotalVentas - @TotalGastos;
END;
*/

/*Ejemplo 4*/
/*
CREATE PROCEDURE ContarTicketsCreditoAntiguos
    @Clav_Cr INT,                 
    @CantidadTickets INT OUTPUT 
AS
BEGIN
    DECLARE @FechaLimite DATE;
    SET @FechaLimite = DATEADD(DAY, -15, GETDATE());
    SELECT @CantidadTickets = COUNT(*)
    FROM Ticket t
    INNER JOIN Credito c ON t.Clav_Tk = c.Clav_Tk
    WHERE c.Clav_Cr = @Clav_Cr
      AND CAST(t.Fec_Tk AS DATE) < @FechaLimite;
END;
*/

/*Ejemplo 5*/
CREATE PROCEDURE PromedioGastos
    @FechaInicio DATE,                 
    @FechaFin DATE,                    
    @Promedio DECIMAL(10,2) OUTPUT     
AS
BEGIN
    DECLARE @TotalGastos DECIMAL(10,2);
    DECLARE @Dias INT;

    SELECT @TotalGastos = ISNULL(SUM(Tot_Gt),0)
    FROM Gastos
    WHERE CAST(Fec_Gt AS DATE) BETWEEN @FechaInicio AND @FechaFin;

    SET @Dias = DATEDIFF(DAY, @FechaInicio, @FechaFin) + 1;

    SET @Promedio = CASE WHEN @Dias > 0 THEN @TotalGastos / @Dias ELSE 0 END;
END;

