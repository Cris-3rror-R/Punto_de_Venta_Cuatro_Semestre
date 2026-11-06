/*
-- Crear un procedimiento para recuperar datos de un usuario específico por su UserID
CREATE PROCEDURE GetUserData 
    -- Parámetro de entrada: ID del usuario que se desea recuperar
    @UserID INT 
AS
BEGIN
    -- Selecciona todas las columnas de la tabla Users 
    -- donde el campo UserID coincida con el valor del parámetro de entrada
    SELECT * 
    FROM Users 
    WHERE UserID = @UserID;
END;
*/
/*
-- Crear un procedimiento para calcular el precio con descuento
CREATE PROCEDURE CalculateDiscount 
    @Price DECIMAL(10, 2),                 -- Precio original
    @DiscountRate DECIMAL(5, 2),           -- Tasa de descuento (ejemplo: 0.15 = 15%)
    @FinalPrice DECIMAL(10, 2) OUTPUT      -- Salida: precio final después del descuento
AS
BEGIN
    -- Calcular el precio final aplicando la tasa de descuento al precio original
    SET @FinalPrice = @Price * (1 - @DiscountRate);
END;
*/

/*
DELIMITER $
-- Crear un procedimiento para recuperar los detalles de un empleado específico por su EmployeeID
CREATE PROCEDURE GetEmployeeDetails(IN EmployeeID INT)  
BEGIN  
    -- Selecciona todas las columnas de la tabla Employees 
    -- donde el campo EmployeeID coincida con el parámetro de entrada
    SELECT * 
    FROM Employees 
    WHERE EmployeeID = EmployeeID;  
END$
DELIMITER ;
*/
/*
-- Crear un procedimiento para agregar un nuevo cliente, verificando que no exista un correo duplicado
CREATE PROCEDURE AddCustomer
    @CustomerName VARCHAR(50),       -- Nombre del cliente
    @CustomerEmail VARCHAR(50)       -- Correo electrónico del cliente
AS
BEGIN
    -- Verificar si el correo ya existe en la tabla Customers
    IF EXISTS (SELECT 1 FROM Customers WHERE Email = @CustomerEmail)
        -- Lanzar un error si el correo ya está en uso
        THROW 50000, 'El correo electrónico ya existe.', 1;
    ELSE
        -- Insertar los datos del nuevo cliente si el correo es único
        INSERT INTO Customers (Name, Email) VALUES (@CustomerName, @CustomerEmail);
END;
*/
/*
-- Crear un procedimiento para recuperar el saldo de una cuenta, con verificación de autorización
CREATE PROCEDURE GetAccountBalance
    @AccountID INT,   -- Identificador de la cuenta
    @UserID INT       -- Identificador del usuario que solicita la información
AS
BEGIN
    -- Verificar si la cuenta existe y pertenece al usuario especificado
    IF EXISTS (SELECT 1 FROM Accounts WHERE AccountID = @AccountID AND UserID = @UserID)
        -- Si está autorizado, seleccionar y devolver el saldo de la cuenta
        SELECT Balance FROM Accounts WHERE AccountID = @AccountID;
    ELSE
        -- Si no está autorizado, lanzar un error
        THROW 50000, 'Acceso no autorizado.', 1;
END;
*/

