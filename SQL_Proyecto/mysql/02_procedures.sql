USE Carniceria;

DROP PROCEDURE IF EXISTS ValidarLogin;
DROP PROCEDURE IF EXISTS ObtenerProductos;
DROP PROCEDURE IF EXISTS ObtenerDatosProducto;
DROP PROCEDURE IF EXISTS ActualizarProducto;
DROP PROCEDURE IF EXISTS ObtenerClientes;
DROP PROCEDURE IF EXISTS ObtenerDatosCliente;
DROP PROCEDURE IF EXISTS ObtenerDeudaCliente;
DROP PROCEDURE IF EXISTS ActualizarCliente;
DROP PROCEDURE IF EXISTS ObtenerGastos;
DROP PROCEDURE IF EXISTS InsertarGasto;
DROP PROCEDURE IF EXISTS EliminarGasto;
DROP PROCEDURE IF EXISTS InsertarTicket;
DROP PROCEDURE IF EXISTS InsertarTicketDetalle;
DROP PROCEDURE IF EXISTS ActualizarInventarioVenta;
DROP PROCEDURE IF EXISTS ObtenerCorteDia;

DELIMITER $$

CREATE PROCEDURE ValidarLogin(
    IN p_usuario VARCHAR(50),
    IN p_contrasena VARCHAR(255),
    OUT p_valido INT
)
BEGIN
    SELECT EXISTS (
        SELECT 1
        FROM Usuarios
        WHERE Usuario = p_usuario
          AND ContrasenaHash = SHA2(p_contrasena, 256)
    ) INTO p_valido;
END$$

CREATE PROCEDURE ObtenerProductos(IN p_filtro VARCHAR(100))
BEGIN
    SELECT Clav_Prd, Nom_Prd, Pre_Prd, Inv_Prd
    FROM Productos
    WHERE p_filtro IS NULL
       OR Nom_Prd LIKE CONCAT('%', p_filtro, '%')
       OR CAST(Clav_Prd AS CHAR) = p_filtro
    ORDER BY Nom_Prd;
END$$

CREATE PROCEDURE ObtenerDatosProducto(IN p_clave INT)
BEGIN
    SELECT p.Clav_Prd, p.Nom_Prd, p.Cos_Prd, p.Pre_Prd, p.Inv_Prd,
           tp.Nom_TiP
    FROM Productos p
    JOIN Tipos_Productos tp ON tp.Clav_TiP = p.Clav_TiP
    WHERE p.Clav_Prd = p_clave;
END$$

CREATE PROCEDURE ActualizarProducto(
    IN p_clave INT,
    IN p_inventario DECIMAL(12,3),
    IN p_costo DECIMAL(12,2),
    IN p_precio DECIMAL(12,2)
)
BEGIN
    UPDATE Productos
    SET Inv_Prd = p_inventario,
        Cos_Prd = p_costo,
        Pre_Prd = p_precio
    WHERE Clav_Prd = p_clave
      AND p_inventario >= 0
      AND p_costo >= 0
      AND p_precio >= 0;
END$$

CREATE PROCEDURE ObtenerClientes(IN p_filtro VARCHAR(100))
BEGIN
    SELECT c.Clav_Cr, c.Nom_Cr, c.Ape_Cr, c.Tel_Cr,
           (
               SELECT COUNT(*)
               FROM Ticket t
               WHERE t.Clav_Cr = c.Clav_Cr
                 AND t.Pag_Tk = 'No'
           ) AS Tickets_Pendientes
    FROM Credito c
    WHERE p_filtro IS NULL
       OR c.Nom_Cr LIKE CONCAT('%', p_filtro, '%')
       OR c.Ape_Cr LIKE CONCAT('%', p_filtro, '%')
       OR c.Tel_Cr LIKE CONCAT('%', p_filtro, '%')
       OR CAST(c.Clav_Cr AS CHAR) = p_filtro
    ORDER BY c.Nom_Cr, c.Ape_Cr;
END$$

CREATE PROCEDURE ObtenerDatosCliente(IN p_clave INT)
BEGIN
    SELECT Clav_Cr, Nom_Cr, Ape_Cr, Tel_Cr, Cll_Cr, Col_Cr, NCl_Cr, Pob_Cr
    FROM Credito
    WHERE Clav_Cr = p_clave;
END$$

CREATE PROCEDURE ObtenerDeudaCliente(
    IN p_clave INT,
    OUT p_deuda DECIMAL(12,2)
)
BEGIN
    SELECT COALESCE(SUM(Tot_Tk), 0)
    INTO p_deuda
    FROM Ticket
    WHERE Clav_Cr = p_clave
      AND Pag_Tk = 'No';
END$$

CREATE PROCEDURE ActualizarCliente(
    IN p_clave INT,
    IN p_nombre VARCHAR(35),
    IN p_apellido VARCHAR(35),
    IN p_telefono VARCHAR(20),
    IN p_calle VARCHAR(70),
    IN p_colonia VARCHAR(70),
    IN p_numero INT,
    IN p_poblacion VARCHAR(70)
)
BEGIN
    UPDATE Credito
    SET Nom_Cr = p_nombre,
        Ape_Cr = p_apellido,
        Tel_Cr = p_telefono,
        Cll_Cr = p_calle,
        Col_Cr = p_colonia,
        NCl_Cr = p_numero,
        Pob_Cr = p_poblacion
    WHERE Clav_Cr = p_clave;
END$$

CREATE PROCEDURE ObtenerGastos(IN p_filtro VARCHAR(100))
BEGIN
    SELECT Clav_Gt, Des_Gt, Tot_Gt, Fec_Gt
    FROM Gastos
    WHERE p_filtro IS NULL
       OR Des_Gt LIKE CONCAT('%', p_filtro, '%')
       OR CAST(Clav_Gt AS CHAR) = p_filtro
    ORDER BY Fec_Gt DESC, Clav_Gt DESC;
END$$

CREATE PROCEDURE InsertarGasto(
    IN p_descripcion VARCHAR(100),
    IN p_total DECIMAL(12,2)
)
BEGIN
    INSERT INTO Gastos (Des_Gt, Tot_Gt)
    VALUES (p_descripcion, p_total);
END$$

CREATE PROCEDURE EliminarGasto(IN p_clave INT)
BEGIN
    DELETE FROM Gastos WHERE Clav_Gt = p_clave;
END$$

CREATE PROCEDURE InsertarTicket(
    OUT p_clave_ticket INT,
    IN p_total DECIMAL(12,2),
    IN p_descuento DECIMAL(12,2),
    IN p_pagado VARCHAR(2),
    IN p_clave_cliente INT
)
BEGIN
    INSERT INTO Ticket (Tot_Tk, Descuento_Tk, Pag_Tk, Clav_Cr)
    VALUES (p_total, p_descuento, p_pagado, NULLIF(p_clave_cliente, 0));

    SET p_clave_ticket = LAST_INSERT_ID();
END$$

CREATE PROCEDURE InsertarTicketDetalle(
    IN p_clave_ticket INT,
    IN p_clave_producto INT,
    IN p_cantidad DECIMAL(12,3),
    IN p_precio DECIMAL(12,2)
)
BEGIN
    INSERT INTO TicketDetalle (Clav_Tk, Clav_Prd, Can_Td, Pre_Td)
    VALUES (p_clave_ticket, p_clave_producto, p_cantidad, p_precio);
END$$

CREATE PROCEDURE ActualizarInventarioVenta(
    IN p_clave_producto INT,
    IN p_cantidad DECIMAL(12,3)
)
BEGIN
    UPDATE Productos
    SET Inv_Prd = Inv_Prd - p_cantidad
    WHERE Clav_Prd = p_clave_producto
      AND p_cantidad > 0
      AND Inv_Prd >= p_cantidad;

    IF ROW_COUNT() <> 1 THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Producto inexistente o inventario insuficiente.';
    END IF;
END$$

CREATE PROCEDURE ObtenerCorteDia(
    OUT p_ventas DECIMAL(12,2),
    OUT p_gastos DECIMAL(12,2),
    OUT p_utilidad DECIMAL(12,2),
    OUT p_total_caja DECIMAL(12,2)
)
BEGIN
    SELECT COALESCE(SUM(Tot_Tk), 0)
    INTO p_ventas
    FROM Ticket
    WHERE Pag_Tk = 'Si'
      AND Fec_Tk >= CURRENT_DATE()
      AND Fec_Tk < CURRENT_DATE() + INTERVAL 1 DAY;

    SELECT COALESCE(SUM(Tot_Gt), 0)
    INTO p_gastos
    FROM Gastos
    WHERE Fec_Gt >= CURRENT_DATE()
      AND Fec_Gt < CURRENT_DATE() + INTERVAL 1 DAY;

    SET p_utilidad = p_ventas - p_gastos;
    SET p_total_caja = 3000 + p_utilidad;
END$$

DELIMITER ;
