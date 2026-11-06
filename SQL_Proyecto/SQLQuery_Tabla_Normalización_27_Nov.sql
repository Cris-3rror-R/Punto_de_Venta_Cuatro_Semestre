/*
CREATE TABLE TicketDetalle (
    Clav_Tk INT NOT NULL,       -- Relación con el ticket
    Clav_Prd INT NOT NULL,      -- Producto vendido
    Cantidad INT NOT NULL,      -- Cantidad vendida
    Precio FLOAT NOT NULL,      -- Precio unitario
    PRIMARY KEY (Clav_Tk, Clav_Prd),
    FOREIGN KEY (Clav_Tk) REFERENCES Ticket(Clav_Tk),
    FOREIGN KEY (Clav_Prd) REFERENCES Productos(Clav_Prd)
);
*/
-- Cambiar columna Cantidad a Can_Td
EXEC sp_rename 'TicketDetalle.Cantidad', 'Can_Td', 'COLUMN';

-- Cambiar columna Precio a Pre_Td
EXEC sp_rename 'TicketDetalle.Precio', 'Pre_Td', 'COLUMN';
