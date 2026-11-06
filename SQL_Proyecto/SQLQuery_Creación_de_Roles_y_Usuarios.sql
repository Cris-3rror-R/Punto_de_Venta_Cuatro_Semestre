-- Ejercicio historico para SQL Server; no ejecutar en MySQL.
-- Se omiten los comandos CREATE LOGIN y sus contrasenas de ejemplo.

CREATE ROLE DBA;
GRANT ALTER, CONTROL ON SCHEMA::dbo TO DBA;
DENY INSERT, UPDATE, DELETE ON SCHEMA::dbo TO DBA;

CREATE ROLE Dueno_Negocio;
GRANT SELECT ON SCHEMA::dbo TO Dueno_Negocio;
GRANT INSERT, UPDATE, DELETE ON Productos TO Dueno_Negocio;
GRANT INSERT, UPDATE, DELETE ON Proveedores TO Dueno_Negocio;
GRANT INSERT, UPDATE, DELETE ON Ticket TO Dueno_Negocio;
GRANT INSERT, UPDATE, DELETE ON Gastos TO Dueno_Negocio;
GRANT INSERT, UPDATE, DELETE ON Credito TO Dueno_Negocio;
DENY ALTER, CONTROL ON SCHEMA::dbo TO Dueno_Negocio;

CREATE ROLE Contador;
GRANT SELECT ON Ticket TO Contador;
GRANT SELECT ON Gastos TO Contador;

CREATE ROLE Encargado_Caja;
GRANT INSERT ON Ticket TO Encargado_Caja;
GRANT INSERT ON Credito TO Encargado_Caja;
GRANT INSERT ON Gastos TO Encargado_Caja;
GRANT SELECT ON Productos TO Encargado_Caja;
GRANT SELECT ON Proveedores TO Encargado_Caja;
GRANT SELECT ON Tipos_Productos TO Encargado_Caja;
GRANT SELECT ON Credito TO Encargado_Caja;
GRANT SELECT ON Ticket TO Encargado_Caja;
GRANT SELECT ON Gastos TO Encargado_Caja;

CREATE ROLE Trabajador;
GRANT SELECT ON Productos TO Trabajador;
