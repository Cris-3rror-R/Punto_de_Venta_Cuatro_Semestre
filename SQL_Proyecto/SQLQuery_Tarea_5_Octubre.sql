/* Ejercicio 18
SELECT Nom_Cr,Ape_Cr,Cll_Cr,NCl_Cr,Col_Cr 
FROM Credito
WHERE LOWER(Nom_Cr) = 'camila'
*/

/* Ejercicio 19
SELECT Nom_Prd, Inv_Prd, ROUND(Inv_Prd, 0) AS Inv_Redondeado
FROM Productos;
*/

/* Ejercicio 20
SELECT Nom_Prd,Pre_Prd, Inv_Prd, ROUND(Pre_Prd * Inv_Prd,1,1) AS Valor_Inventario_Trunc
FROM Productos;
*/

/* Ejercicio 23
SELECT FORMAT(Tot_Tk, 'C', 'es-MX') AS Total_MX
FROM Ticket
JOIN Credito ON Ticket.Clav_Tk = Credito.Clav_Tk
WHERE Nom_Cr = 'Waltter';
*/

/* Ejercicio 24
SELECT Nom_Prd,ISNULL(CAST(Inv_Prd AS VARCHAR), 'Sin registro') AS Inventario
FROM Productos;
*/

/* Ejercicio 25
SELECT Nom_Prd,Clav_TiP,
  CASE Clav_TiP
    WHEN 7 THEN 'Congelado'
    WHEN 9 THEN 'Cerdo'
    WHEN 14 THEN 'Res'
    WHEN 11 THEN 'Huevo'
    ELSE 'Otro'
  END AS Categoria
FROM Productos;
*/

/* Ejercicio 26
SELECT FORMAT(ISNULL(Tot_Tk, 0), 'C', 'es-MX') AS Total_Formateado
FROM Ticket
WHERE Tot_Tk <= 150;
*/

/* Ejercicio 30
SELECT Proveedores.Clav_Pv,Proveedores.Nom_Pv,Proveedores.Clav_TiP,Tipos_Productos.Clav_TiP,Tipos_Productos.Nom_TiP
FROM Proveedores,Tipos_Productos
WHERE Proveedores.Clav_TiP = Tipos_Productos.Clav_TiP;
*/

/* Ejercicio 31
SELECT P.Clav_Pv,P.Nom_Pv,P.Clav_TiP,T.Clav_TiP,T.Nom_TiP
FROM Proveedores P,Tipos_Productos T
WHERE P.Clav_TiP = T.Clav_TiP;
*/

/* Ejercicio 32
SELECT P.Clav_Pv,P.Nom_Pv,P.Clav_TiP,T.Clav_TiP,T.Nom_TiP,Pr.Nom_Prd,Pr.Clav_TiP
FROM Proveedores P,Tipos_Productos T,Productos Pr
WHERE P.Clav_TiP = T.Clav_TiP OR P.Clav_TiP = Pr.Clav_TiP;
*/

/* Ejercicio 33
SELECT Cr.Nom_Cr,Tk.Tot_Tk,Tk.Clav_Tk
FROM Credito Cr, Ticket Tk
WHERE Tk.Tot_Tk
BETWEEN  50 AND 350;
*/

/* Ejercicio 35
SELECT P.Nom_Pv, P.Clav_TiP
FROM Proveedores P
LEFT JOIN Tipos_Productos Tp ON P.Clav_TiP = Tp.Clav_TiP
ORDER BY P.Clav_TiP;
*/

/* Ejercicio 38
SELECT P1.Nom_Prd + ' comparte tipo con ' + P2.Nom_Prd AS Relacion
FROM Productos P1, Productos P2
WHERE P1.Clav_TiP = P2.Clav_TiP
AND P1.Nom_Prd > P2.Nom_Prd;
*/

/* Ejercicio 40
SELECT 
  AVG(Cos_Prd) AS Promedio_Costo,
  MAX(Pre_Prd) AS Precio_Maximo,
  MIN(Pre_Prd) AS Precio_Minimo,
  SUM(Inv_Prd) AS Inventario_Total
FROM Productos;
*/

/* Ejercicio 41
SELECT 
  MAX(t.Fec_Tk) AS Fecha_Reciente,
  MIN(t.Fec_Tk) AS Fecha_Antigua
FROM Ticket t;
*/

/* Ejercicio 42
SELECT COUNT(Pr.Clav_TiP) AS Productos_Cerdo
FROM Productos Pr
WHERE Pr.Clav_TiP = 9;
*/

/* Ejercicio 43
SELECT AVG(G.Tot_Gt) AS Promedio_Gasto
FROM Gastos G;
*/

/* Ejercicio 44
SELECT AVG(ISNULL(G.Tot_Gt,0)) AS Promedio_Gasto
FROM Gastos G;
*/

/* Ejercicio 45
SELECT Pr.Clav_TiP, AVG(Pr.Pre_Prd)
FROM Productos Pr
GROUP BY Pr.Clav_TiP;
*/

/*Ejercicio 46
SELECT Pr.Clav_TiP AS Tipo_Producto,
        Pr.Nom_Prd AS Producto,
  AVG(Pr.Pre_Prd) AS Precio_Promedio
FROM Productos Pr
GROUP BY Pr.Clav_TiP, Pr.Nom_Prd;
*/

/*Ejercicio 47
SELECT Pr.Clav_TiP, AVG(Pr.Pre_Prd) AS Precio_Promedio
FROM Productos Pr
GROUP BY Pr.Clav_TiP
HAVING AVG(Pr.Pre_Prd) > 100;
*/

/*Ejercicio 49
SELECT Tk.Des_Tk
FROM Ticket Tk
WHERE Tk.Tot_Tk > (
    SELECT MAX(Tot_Tk)
    FROM Ticket
    WHERE Tot_Tk = 300
);
*/

/*Ejercicio 50
SELECT Pr.Nom_Prd
FROM Productos Pr
WHERE Pr.Pre_Prd >
                (SELECT AVG(Pre_Prd)
                 FROM Productos)
AND Pr.Inv_Prd >
                (SELECT MIN(Inv_Prd)
                  FROM Productos
                  WHERE Clav_TiP = 9
                  );
*/

/*Ejercicio 51
SELECT Tk.Des_Tk,Tk.Fec_Tk
FROM Ticket Tk
WHERE Tk.Tot_Tk =
                (SELECT MAX(Tot_Tk)
                FROM Ticket);
*/

/*Ejercicio 52
SELECT Nom_Cr, NCl_Cr
FROM Credito
WHERE NCl_Cr > ANY (
    SELECT NCl_Cr
    FROM Credito
    WHERE Col_Cr = 'Mascotas'
);
*/

/*Ejercicio 53
SELECT Nom_Cr, NCl_Cr
FROM Credito
WHERE NCl_Cr > ALL (
    SELECT NCl_Cr
    FROM Credito
    WHERE Col_Cr = 'Mascotas'
);
*/
