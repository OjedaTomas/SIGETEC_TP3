USE sigetec;
-- Demostración SQL independiente: se revierte completa con ROLLBACK.
-- Se utiliza un cliente de prueba que no queda guardado luego de ejecutarlo.
START TRANSACTION;
INSERT INTO clientes (nombre, telefono, email)
VALUES ('Cliente de demostración','1100000000','demo@example.test');
SET @id_ejemplo = LAST_INSERT_ID();
SELECT * FROM clientes WHERE id_cliente = @id_ejemplo;
UPDATE clientes SET telefono='1199999999' WHERE id_cliente = @id_ejemplo;
SELECT * FROM clientes WHERE id_cliente = @id_ejemplo;
DELETE FROM clientes WHERE id_cliente = @id_ejemplo;
SELECT * FROM clientes WHERE id_cliente = @id_ejemplo;
ROLLBACK;
-- Para datos con tickets u órdenes asociadas se evita el borrado físico.
-- Las FK restringen el borrado para preservar el historial.
