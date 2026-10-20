# SIGETEC - Trabajo Práctico 3
## Sistema Integral de Gestión de Servicios Técnicos

**Autor:** Tomás Ojeda | **Carrera:** Licenciatura en Informática | **Asignatura:** Seminario de Práctica de Informática  
**Tecnologías:** Java 17+, Maven, MySQL 8.0+, JDBC (MySQL Connector/J 9.6.0), NetBeans.

## ¿Qué hace?

Prototipo real de consola para la empresa ficticia Nexo Seguridad y Tecnología. Permite registrar clientes, técnicos, tickets de CCTV/Soporte IT/otros, órdenes, fechas de visita, asignación de técnicos, intervenciones y cierre. Los datos **se guardan en MySQL** y siguen allí al cerrar Java. No es la aplicación web final del TP1: representa un módulo operacional dentro de su desarrollo incremental.

## Archivos más importantes

- `src/main/java/`: **todo el código Java**, con explicaciones para principiantes.
- `src/test/java/`: pruebas automáticas de lógica Java (sin MySQL).
- `sql/01_crear_base.sql`: creación de la base de datos y sus siete tablas.
- `sql/02_datos_referencia.sql`: tipos de servicio predefinidos.
- `sql/03_consultas.sql`: consultas de verificación, relaciones y reportes.
- `sql/04_ejemplo_insert_update_delete.sql`: INSERT/SELECT/UPDATE/DELETE de práctica (reversibles).
- `sql/05_usuario_opcional.sql`: ejemplo para crear un usuario con permisos mínimos.
- `config/sigetec.properties.ejemplo`: plantilla de configuración sin contraseña real.
- `diagramas/`: arquitectura, clases y DER con archivos `.dot` editables.
- `docs/GUIA_PASO_A_PASO.md`: instrucciones completas para Windows.
- `docs/INFORME_TP3_SIGETEC.docx` y `.pdf`: memoria académica editable y para entrega.

## Instrucciones resumidas en Windows + MySQL Workbench + NetBeans

**1.** Instalá JDK 17 o superior, MySQL Server 8 o superior, MySQL Workbench y NetBeans con soporte Maven.

**2.** Abrí MySQL Workbench, conectate como administrador y ejecutá **en este orden** los scripts `sql/01_crear_base.sql`, `sql/02_datos_referencia.sql`. Cada script se ejecuta usando el ícono del rayo.

**3.** Configurá el usuario MySQL:
- Opción recomendada: abrí `sql/05_usuario_opcional.sql`, reemplazá `REEMPLAZAR_CLAVE` por tu propia contraseña y ejecutá el script **solo una vez** usando una cuenta con permisos para crear usuarios.
- Si no tenés permisos para crear otro usuario, podés usar tu usuario MySQL existente, sin modificar el script SQL de la aplicación.

**4.** Copiá el archivo `config/sigetec.properties.ejemplo` como `config/sigetec.properties` (con ese nombre EXACTO y en la misma carpeta). Editá `db.usuario` y `db.clave` según tu configuración. Ejemplo:

```properties
db.url=jdbc:mysql://localhost:3306/sigetec?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=America/Argentina/Buenos_Aires
db.usuario=sigetec_app
db.clave=MI_CLAVE_LOCAL
```

No publiques tu contraseña. Este archivo está incluido en `.gitignore`.

**5.** Descomprimí el ZIP del proyecto. Abrí **NetBeans > File > Open Project** y seleccioná la carpeta `SIGETEC_TP3_COMPLETO` que contiene `pom.xml`. Esperá a que descargue las dependencias Maven (necesita Internet la primera vez).

**6.** Hacé clic derecho sobre el proyecto y elegí **Clean and Build**. Después **Run Project** (F6). Clase principal: `ar.edu.siglo21.sigetec.consola.Main`.

**7.** La consola debería mostrar `Conexión MySQL: correcta`. Si no podés escribir en la ventana de Output, usá la terminal integrada de NetBeans o la terminal de Windows en la carpeta del proyecto con `mvn exec:java` (si Maven está agregado al PATH). Otra alternativa es **Tools > Open in Terminal** (según versión de NetBeans) y ejecutar Maven desde allí.

**8.** Probá este recorrido: `1` registrar cliente, `2` registrar técnico, `3` crear ticket usando ID de cliente, `5` generar orden usando ID de ticket, `6` programarla y asignar el técnico, `7` registrar intervención, `8` cerrar orden, `9` revisar órdenes. Cerrá con `0`, volvé a entrar y verificá con `4`/`9` que los registros persisten.

**9.** En MySQL Workbench ejecutá `sql/03_consultas.sql` y obtené capturas del contenido registrado. Las capturas deberán ir al informe antes de entregar.

**10.** Para mostrar UPDATE y DELETE por separado, usá `sql/04_ejemplo_insert_update_delete.sql`: los cambios de demostración se revierten al final, no toca registros reales.

**11.** Subí el proyecto completo a un repositorio de GitHub. Debe incluir código, SQL, documentación y diagramas, pero **nunca** `config/sigetec.properties` ni `target/`. Agregá en el informe el enlace real al repo cuando exista. El ZIP no constituye un enlace de GitHub.

## Pilares de POO: dónde encontrarlos

| Concepto | Código | Explicación sencilla |
|---|---|---|
| Encapsulamiento | `modelo/Cliente.java` y `Tecnico.java` | `private` evita cambios descontrolados. |
| Herencia | `TicketCctv extends Ticket` | Una clase hija reutiliza los atributos y métodos de la clase padre. |
| Abstracción | `abstract class Ticket` | Se describe lo que todos los tickets tienen en común sin crear un ticket genérico incompleto. |
| Polimorfismo | `instruccionTecnica()` | Una llamada a un método genera una respuesta específica según el subtipo. |
| Constructores | `new Cliente(...)`, `new Tecnico(...)` | Inicializan objetos con valores válidos. |
| Condicionales | `switch` en `Main`, `if` en validaciones | Controlan decisiones. |
| Repeticiones | `while` en el menú y `for` al listar | Permiten varias operaciones sin reiniciar el programa. |
| Excepciones | `try/catch`, validaciones y transacciones JDBC | Evitan que errores de usuario o de SQL detengan la aplicación sin explicación. |
| Búsqueda | `buscarTicket` | Consulta por ID con `WHERE id_ticket = ?`. |
| Ordenación | `ticketsPorPrioridad()` | Ordena de ALTA a BAJA usando `Comparator`. |

## Compilar y probar SIN MySQL (solo parte de POO)

En una terminal con Java 17+, con la carpeta del proyecto como directorio actual:

**Windows, PowerShell** (el proyecto NO depende de MySQL para estas pruebas de clases):

```powershell
New-Item -ItemType Directory -Force -Path prueba-clases | Out-Null
$archivos = Get-ChildItem src/main/java,src/test/java -Filter *.java -Recurse | ForEach-Object FullName
javac -encoding UTF-8 --release 17 -d prueba-clases $archivos
java -cp prueba-clases ar.edu.siglo21.sigetec.PruebasModelo
```

En la validación previa del proyecto se ejecutaron **16 comprobaciones del modelo**, todas satisfactorias. Esto NO equivale a probar el servidor MySQL local; esa prueba final debe hacerse en tu equipo.

## Problemas frecuentes

- `No se pudo conectar`: revisá que MySQL Server esté iniciado, que exista la base `sigetec` y que el usuario y contraseña sean correctos.
- `Unknown database 'sigetec'`: faltó ejecutar `01_crear_base.sql`.
- `Table ... doesn't exist`: ejecutá el script SQL de creación.
- `Cannot add or update a child row`: usaste el ID de un cliente inexistente o intentaste registrar una intervención con técnico no asignado.
- `Duplicate entry`: ya existe ese registro, por ejemplo una segunda orden del mismo ticket o una asignación repetida.
- Maven no descarga Connector/J: verificá Internet, proxy/red de tu computadora o la configuración Maven de NetBeans.
- `InputMismatchException` o valores incorrectos: el menú usa `Scanner.nextLine()` y valida las conversiones numéricas.

## Alcance y límites

Este TP implementa **un prototipo Java de consola conectado a MySQL**, NO una aplicación web ni funciones de login/roles. Las funciones de gestión de guardias, reparación detallada de PCs, archivos adjuntos, facturación o comunicaciones externas quedan fuera de este incremento.

Este trabajo debe revisarse, ejecutarse y documentarse con capturas reales antes de presentarlo. Los nombres del negocio y registros de prueba son ficticios.
