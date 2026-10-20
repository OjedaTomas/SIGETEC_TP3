# Guía de ejecución paso a paso (Windows)

### Parte A. Instalar herramientas

1. Verificá JDK en CMD: `java -version`. Debe informar 17 o mayor.
2. Verificá que tengas NetBeans con Maven.
3. Abrí MySQL Workbench y probá la conexión al servidor MySQL local. MySQL Workbench por sí solo NO es el servidor: asegurate de que MySQL Server esté instalado y activo.

### Parte B. Crear MySQL de cero

4. En Workbench, File > Open SQL Script > `sql/01_crear_base.sql`. Ejecutá todo el script (rayo). En el panel Schemas, actualizá y buscá `sigetec`.
5. Abrí `sql/02_datos_referencia.sql` y ejecutalo. En `tipos_servicio` deberían figurar cinco categorías.
6. Si elegís usuario dedicado: abrí `sql/05_usuario_opcional.sql`, reemplazá la contraseña de ejemplo y ejecutalo como administrador. Si ya creaste ese usuario, este script NO cambia su contraseña: administrala desde Workbench.
7. Copiá `config/sigetec.properties.ejemplo` a `config/sigetec.properties`. Revisá el puerto 3306 (si tu MySQL usa otro, corregilo), tu usuario y contraseña.

### Parte C. Abrir y probar Java

8. File > Open Project en NetBeans. Seleccioná la carpeta `SIGETEC_TP3_COMPLETO` con el `pom.xml`.
9. Esperá la descarga de `mysql-connector-j` desde Maven Central.
10. Ejecutá Clean and Build. Si aparecen errores, revisá que el proyecto use JDK >=17.
11. Ejecutá Run Project y buscá `Conexión MySQL: correcta`.
12. Si no aparece un campo para escribir en NetBeans, probá abrir una terminal en la carpeta del proyecto y ejecutar `mvn exec:java` (si Maven está instalado en PATH).

### Parte D. Recorrido de prueba para las capturas

13. Opción 1: `Empresa Prueba`, teléfono `1122334455`, correo `prueba@example.test`. Guardá el ID que aparece, por ejemplo 1.
14. Opción 2: `Técnico Demostración`, especialidad `CCTV`. Guardá su ID, por ejemplo 1.
15. Opción 3: elegí ID de cliente; tipo 1 CCTV; descripción `Cámara IP sin imagen`; ubicación `Ingreso principal`; prioridad 3 ALTA. Anotá ID ticket.
16. Opción 4: comprobá que el ticket exista.
17. Opción 5: usá el ID del ticket para generar una orden. Anotá ID orden.
18. Opción 6: elegí ID de orden, fecha de hoy o futura en formato `AAAA-MM-DD`, ID del técnico creado.
19. Opción 7: ingresá ID orden e ID técnico, tarea `Se revisó el PoE y el conector RJ45`; resultado `Cámara en línea`.
20. Opción 8: usá ID de orden; debería indicar que la orden quedó finalizada y el ticket cerrado.
21. Opción 11: mostrará tickets ordenados ALTA a BAJA si tenés varios registros.
22. Opción 10: buscá un ticket conocido y después uno inexistente; la segunda búsqueda NO debe cerrar el programa.
23. Opción 0: cerrá el programa. Volvé a ejecutarlo y usá 4, 9 y 12 para demostrar que los datos siguieron guardados.
24. Workbench: ejecutá `sql/03_consultas.sql`. Sacá una captura de la consulta que relaciona tickets y clientes y otra de órdenes con técnicos.
25. Para verificar INSERT/UPDATE/DELETE sin alterar el prototipo, ejecutá `sql/04_ejemplo_insert_update_delete.sql` en Workbench y revisá sus resultados.

### Parte E. Preparar entrega y GitHub

26. Guardá en `evidencias/` capturas de la consola, NetBeans y Workbench SIN contraseñas ni datos verdaderos.
27. Abrí `docs/INFORME_TP3_SIGETEC.docx`, agregá capturas donde se indican, marcá los resultados que verificaste realmente y agregá la URL de GitHub una vez publicado.
28. Exportá en PDF: formato A4, Calibri 11, espaciado simple; nombre recomendado `OJEDA-TOMAS-AP3.pdf` (verificá el formato exacto que te exija la plataforma).
29. En GitHub creá un repositorio, por ejemplo `SIGETEC-TP3`, subí `src/`, `sql/`, `diagramas/`, `docs/`, `pom.xml`, `README.md`, `.gitignore` y `config/sigetec.properties.ejemplo`. Nunca `config/sigetec.properties` ni `target/`.
30. Abrí el enlace publicado desde otro navegador para comprobar que se ve el código fuente y los scripts.

### Nota sobre la verificación

El código fue compilado con Java 21 en modo compatible Java 17; las 16 pruebas del modelo pasaron. No se pudo verificar aquí la conexión a un servidor MySQL real, por lo cual NO se incluyen capturas de resultados inventados. Esa evidencia debe generarse con tus propios datos ficticios en tu máquina.
