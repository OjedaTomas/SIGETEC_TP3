-- OPCIONAL. Solo si tenés permiso de crear usuarios MySQL.
-- Reemplazá REEMPLAZAR_CLAVE por una contraseña propia y fuerte.
CREATE USER IF NOT EXISTS 'sigetec_app'@'localhost' IDENTIFIED BY 'REEMPLAZAR_CLAVE';
GRANT SELECT, INSERT, UPDATE, DELETE ON sigetec.* TO 'sigetec_app'@'localhost';
-- Si el usuario ya existe con otra clave, gestionarla desde MySQL Workbench.
