USE sigetec;
-- CONSULTA 1: comprobar que las tablas están creadas.
SHOW TABLES;

-- CONSULTA 2: clientes persistidos por Java.
SELECT id_cliente, nombre, telefono, email, activo FROM clientes ORDER BY id_cliente;

-- CONSULTA 3: tickets relacionados con cliente y servicio.
SELECT ti.id_ticket, cl.nombre AS cliente, ts.nombre AS servicio,
       ti.descripcion, ti.prioridad, ti.estado
FROM tickets AS ti
JOIN clientes AS cl ON cl.id_cliente = ti.id_cliente
JOIN tipos_servicio AS ts ON ts.codigo = ti.tipo_codigo
ORDER BY FIELD(ti.prioridad,'ALTA','MEDIA','BAJA'), ti.id_ticket;

-- CONSULTA 4: órdenes y técnicos asignados (una fila por técnico).
SELECT o.id_orden, ti.id_ticket, cl.nombre AS cliente, o.fecha_programada,
       o.estado, te.nombre AS tecnico
FROM ordenes_trabajo o
JOIN tickets ti ON ti.id_ticket = o.id_ticket
JOIN clientes cl ON cl.id_cliente = ti.id_cliente
LEFT JOIN asignaciones a ON a.id_orden = o.id_orden
LEFT JOIN tecnicos te ON te.id_tecnico = a.id_tecnico
ORDER BY o.id_orden, te.nombre;

-- CONSULTA 5: historial de una orden con intervenciones.
SELECT o.id_orden, i.fecha_registro, te.nombre AS tecnico,
       i.descripcion, i.resultado
FROM intervenciones i
JOIN ordenes_trabajo o ON o.id_orden = i.id_orden
JOIN tecnicos te ON te.id_tecnico = i.id_tecnico
ORDER BY i.fecha_registro DESC;

-- CONSULTA 6: cantidad de tickets por nivel de prioridad.
SELECT prioridad, COUNT(*) AS cantidad
FROM tickets GROUP BY prioridad ORDER BY cantidad DESC;
