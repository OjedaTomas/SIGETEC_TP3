USE sigetec;
-- Datos fijos de ejemplo, no relacionados con clientes reales.
INSERT INTO tipos_servicio (codigo, nombre) VALUES
 ('CCTV','Cámaras y videovigilancia'),
 ('SOPORTE_IT','Reparación y soporte informático'),
 ('ALARMA','Alarmas de seguridad'),
 ('CERCO','Cerco eléctrico'),
 ('REDES','Redes y conectividad')
ON DUPLICATE KEY UPDATE nombre = VALUES(nombre);
