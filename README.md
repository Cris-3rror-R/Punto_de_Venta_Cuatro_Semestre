# Punto de venta para carnicería

Aplicación de escritorio hecha con Java Swing para ventas, inventario, clientes con crédito y gastos. La interfaz se encuentra en `Proyecto_Final_Taller_de_Base_de_Datos/src`; los scripts necesarios para la aplicación están en `SQL_Proyecto/mysql`.

## Requisitos

- JDK 17 o posterior.
- NetBeans con soporte para proyectos Java/Ant.
- MySQL 8.0 o posterior.
- MySQL Connector/J 9.5.0 (incluido en `Proyecto_Final_Taller_de_Base_de_Datos/lib`).

## Preparar MySQL

1. Inicia el servidor MySQL.
2. Ejecuta `SQL_Proyecto/mysql/01_schema.sql` y después `SQL_Proyecto/mysql/02_procedures.sql` en Workbench o con el cliente `mysql`. El primer script crea la base `Carniceria`, sus tablas y la tabla de usuarios; el segundo instala los procedimientos que usa la aplicación.
3. Crea un usuario de acceso a la aplicación. La contraseña se guarda como SHA-256:

   ```sql
   USE Carniceria;
   INSERT INTO Usuarios (Usuario, ContrasenaHash)
   VALUES ('admin', SHA2('CAMBIA_ESTA_CONTRASENA', 256));
   ```

4. Agrega tipos de producto y productos antes de usarlos. Los archivos antiguos de `SQL_Proyecto` contienen ejercicios, algunos escritos para SQL Server, y no forman parte del proceso de instalación MySQL.

## Configurar la conexión

La aplicación lee primero propiedades de Java y, si no existen, variables de entorno:

| Propiedad de Java | Variable de entorno | Valor predeterminado |
|---|---|---|
| `carniceria.db.url` | `CARNICERIA_DB_URL` | `jdbc:mysql://localhost:3308/Carniceria` |
| `carniceria.db.user` | `CARNICERIA_DB_USER` | `root` |
| `carniceria.db.password` | `CARNICERIA_DB_PASSWORD` | Obligatorio, puede ser vacío si así está configurado MySQL |

En PowerShell, por ejemplo, antes de iniciar NetBeans desde esa misma sesión:

```powershell
$env:CARNICERIA_DB_URL = "jdbc:mysql://localhost:3306/Carniceria"
$env:CARNICERIA_DB_USER = "tu_usuario"
$env:CARNICERIA_DB_PASSWORD = "tu_contrasena"
```

También se pueden establecer las propiedades de Java en las opciones de la máquina virtual de NetBeans:

```text
-Dcarniceria.db.url=jdbc:mysql://localhost:3306/Carniceria
-Dcarniceria.db.user=tu_usuario
-Dcarniceria.db.password=tu_contrasena
```

No guardes contraseñas reales en el código ni en archivos que vayas a subir al repositorio.

## Ejecutar

Abre `Proyecto_Final_Taller_de_Base_de_Datos` en NetBeans, configura las credenciales de la base de datos y ejecuta el proyecto. El archivo `nbproject/project.properties` usa una ruta relativa para el conector MySQL.

## Estructura

- `Proyecto_Final_Taller_de_Base_de_Datos/src`: aplicación Java.
- `Proyecto_Final_Taller_de_Base_de_Datos/lib`: driver MySQL JDBC.
- `SQL_Proyecto/mysql`: esquema y procedimientos para la aplicación.
- `SQL_Proyecto`: ejercicios históricos de SQL; varios usan sintaxis específica de SQL Server y se conservan como material de clase.
- Los `.java` sueltos en la raíz son prototipos anteriores; la aplicación actual es la del proyecto NetBeans.
