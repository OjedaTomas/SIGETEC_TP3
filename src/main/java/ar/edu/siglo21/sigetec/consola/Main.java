package ar.edu.siglo21.sigetec.consola;

import ar.edu.siglo21.sigetec.datos.BaseDeDatos;
import ar.edu.siglo21.sigetec.datos.Repositorio;
import ar.edu.siglo21.sigetec.modelo.*;
import ar.edu.siglo21.sigetec.servicio.ServicioSigetec;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;

/**
 * PUNTO DE ENTRADA. Esta es la clase que NetBeans ejecuta primero.
 * No hay que saber programar una ventana gráfica: la consola nos permite
 * comprobar la lógica, los objetos y la persistencia en MySQL.
 */
public class Main {
    private final Scanner entrada = new Scanner(System.in);
    private final ServicioSigetec servicio;

    public Main(ServicioSigetec servicio) { this.servicio = servicio; }

    public static void main(String[] args) {
        System.out.println("SIGETEC - Sistema Integral de Gestión de Servicios Técnicos");
        try {
            BaseDeDatos bd = new BaseDeDatos();
            // Prueba REAL de conexión antes de mostrar el menú.
            try (var conexion = bd.conectar()) {
                System.out.println("Conexión MySQL: correcta. Base de datos: " + conexion.getCatalog());
            }
            new Main(new ServicioSigetec(new Repositorio(bd))).ejecutar();
        } catch (IOException e) {
            System.out.println("CONFIGURACIÓN: " + e.getMessage());
        } catch (SQLException e) {
            System.out.println("NO SE PUDO CONECTAR A MYSQL: " + explicarSql(e));
            System.out.println("Revisá que MySQL esté iniciado, que ejecutaste los scripts SQL y las credenciales.");
        }
    }

    private void ejecutar() {
        // REPETICIÓN (while): el menú sigue visible hasta elegir 0.
        boolean continuar = true;
        while (continuar) {
            mostrarMenu();
            int opcion = leerNumero("Seleccioná una opción: ", 0, 12);
            try {
                // CONDICIONAL (switch): cada opción ejecuta un método diferente.
                switch (opcion) {
                    case 1 -> registrarCliente();
                    case 2 -> registrarTecnico();
                    case 3 -> registrarTicket();
                    case 4 -> mostrarTickets();
                    case 5 -> generarOrden();
                    case 6 -> planificarYAsignar();
                    case 7 -> registrarIntervencion();
                    case 8 -> finalizarOrden();
                    case 9 -> mostrarOrdenes();
                    case 10 -> buscarTicket();
                    case 11 -> ordenarTickets();
                    case 12 -> historialCliente();
                    case 0 -> { continuar = false; System.out.println("Cerrando SIGETEC. ¡Hasta pronto!"); }
                    default -> System.out.println("Opción no disponible.");
                }
            } catch (SQLException ex) {
                System.out.println("ERROR EN LA BASE: " + explicarSql(ex));
            } catch (IllegalArgumentException ex) {
                System.out.println("DATOS INVÁLIDOS: " + ex.getMessage());
            } catch (RuntimeException ex) {
                // Manejo de excepciones: controlamos el error sin cerrar todo el programa.
                System.out.println("OPERACIÓN NO REALIZADA: " + ex.getMessage());
            }
            System.out.println();
        }
    }

    private void mostrarMenu() {
        System.out.println("\n========== MENÚ SIGETEC ==========");
        System.out.println(" 1. Registrar cliente");
        System.out.println(" 2. Registrar técnico");
        System.out.println(" 3. Registrar ticket de servicio");
        System.out.println(" 4. Consultar todos los tickets");
        System.out.println(" 5. Generar orden de trabajo");
        System.out.println(" 6. Programar orden y asignar técnico");
        System.out.println(" 7. Registrar intervención técnica");
        System.out.println(" 8. Finalizar orden de trabajo");
        System.out.println(" 9. Consultar órdenes de trabajo");
        System.out.println("10. Buscar ticket por ID");
        System.out.println("11. Ordenar tickets por prioridad");
        System.out.println("12. Consultar historial de cliente");
        System.out.println(" 0. Salir");
    }

    private void registrarCliente() throws SQLException {
        String nombre = leerTexto("Nombre del cliente: ");
        String telefono = leerTexto("Teléfono: ");
        String email = leerOpcional("Email (opcional): ");
        long id = servicio.registrarCliente(nombre, telefono, email);
        System.out.println("Cliente guardado en MySQL con ID #" + id);
    }

    private void registrarTecnico() throws SQLException {
        String nombre = leerTexto("Nombre del técnico: ");
        String especialidad = leerTexto("Especialidad (CCTV, Redes, Soporte IT...): ");
        System.out.println("Técnico registrado con ID #" + servicio.registrarTecnico(nombre, especialidad));
    }

    private void registrarTicket() throws SQLException {
        mostrarLista(servicio.clientes(), "clientes");
        long id = leerId("ID del cliente: ");
        System.out.println("1=CCTV, 2=Soporte IT, 3=Alarma, 4=Cerco eléctrico, 5=Redes");
        int tipoNumero = leerNumero("Tipo de servicio: ", 1, 5);
        TipoServicio tipo = switch (tipoNumero) {
            case 1 -> TipoServicio.CCTV;
            case 2 -> TipoServicio.SOPORTE_IT;
            case 3 -> TipoServicio.ALARMA;
            case 4 -> TipoServicio.CERCO;
            default -> TipoServicio.REDES;
        };
        String descripcion = leerTexto("Descripción del problema: ");
        String detalle = leerTexto("Ubicación de cámara / equipo / sitio o detalle técnico: ");
        System.out.println("1=Baja, 2=Media, 3=Alta");
        Prioridad prioridad = switch (leerNumero("Prioridad: ", 1, 3)) {
            case 1 -> Prioridad.BAJA;
            case 2 -> Prioridad.MEDIA;
            default -> Prioridad.ALTA;
        };
        long ticket = servicio.abrirTicket(id, tipo, descripcion, detalle, prioridad);
        System.out.println("Ticket creado en MySQL con ID #" + ticket);
    }

    private void mostrarTickets() throws SQLException {
        // POLIMORFISMO: resumen() llama instruccionTecnica() del subtipo correcto.
        List<Ticket> tickets = servicio.tickets();
        if (tickets.isEmpty()) System.out.println("Todavía no hay tickets.");
        for (Ticket ticket : tickets) System.out.println(ticket.resumen());
    }

    private void generarOrden() throws SQLException {
        long ticketId = leerId("ID del ticket: ");
        System.out.println("Orden creada con ID #" + servicio.generarOrden(ticketId));
    }

    private void planificarYAsignar() throws SQLException {
        mostrarOrdenes();
        long idOrden = leerId("ID de la orden: ");
        LocalDate fecha = leerFecha("Fecha programada (AAAA-MM-DD): ");
        mostrarLista(servicio.tecnicos(), "técnicos");
        long idTecnico = leerId("ID del técnico que vas a asignar: ");
        servicio.programarYAsignar(idOrden, fecha, idTecnico);
        System.out.println("Orden programada y técnico asignado.");
        System.out.println("Para agregar otro técnico repetí esta opción con la misma fecha.");
    }

    private void registrarIntervencion() throws SQLException {
        long orden = leerId("ID de la orden: ");
        long tecnico = leerId("ID del técnico asignado: ");
        String trabajo = leerTexto("Trabajo realizado: ");
        String resultado = leerTexto("Resultado: ");
        System.out.println("Intervención #" + servicio.intervenir(orden, tecnico, trabajo, resultado)
                + " registrada. Orden ahora EN_EJECUCION.");
    }

    private void finalizarOrden() throws SQLException {
        long id = leerId("Orden a finalizar: ");
        servicio.cerrarOrden(id);
        System.out.println("Orden FINALIZADA y ticket CERRADO en la misma transacción.");
    }

    private void mostrarOrdenes() throws SQLException {
        mostrarLista(servicio.ordenes(), "órdenes");
    }

    private void buscarTicket() throws SQLException {
        long id = leerId("Número de ticket a buscar: ");
        Ticket t = servicio.buscarTicket(id);
        System.out.println(t == null ? "Ticket no encontrado." : t.resumen());
    }

    private void ordenarTickets() throws SQLException {
        System.out.println("Tickets ordenados ALTA > MEDIA > BAJA:");
        for (Ticket t : servicio.ticketsPorPrioridad()) System.out.println(t.resumen());
    }

    private void historialCliente() throws SQLException {
        long id = leerId("ID del cliente: ");
        mostrarLista(servicio.historial(id), "registros para este cliente");
    }

    /** Lee una opción y vuelve a preguntar si escriben letras o un número fuera de rango. */
    private int leerNumero(String pregunta, int minimo, int maximo) {
        while (true) {
            System.out.print(pregunta);
            String texto = entrada.nextLine().trim();
            try {
                int numero = Integer.parseInt(texto);
                if (numero >= minimo && numero <= maximo) return numero;
            } catch (NumberFormatException ex) {
                // No hacemos nada: mostramos la ayuda y repetimos la pregunta.
            }
            System.out.println("Ingresá un número entre " + minimo + " y " + maximo + ".");
        }
    }

    private long leerId(String pregunta) {
        while (true) {
            System.out.print(pregunta);
            try {
                long numero = Long.parseLong(entrada.nextLine().trim());
                if (numero > 0) return numero;
            } catch (NumberFormatException ex) { /* se repite la pregunta */ }
            System.out.println("El ID debe ser un número entero positivo.");
        }
    }

    private LocalDate leerFecha(String pregunta) {
        while (true) {
            System.out.print(pregunta);
            try { return LocalDate.parse(entrada.nextLine().trim()); }
            catch (DateTimeParseException ex) { System.out.println("Formato inválido; ejemplo: 2026-10-18."); }
        }
    }

    private String leerTexto(String pregunta) {
        while (true) {
            String texto = leerOpcional(pregunta);
            if (!texto.isEmpty()) return texto;
            System.out.println("Este dato es obligatorio.");
        }
    }

    private String leerOpcional(String pregunta) {
        System.out.print(pregunta);
        return entrada.nextLine().trim();
    }

    private void mostrarLista(List<?> valores, String descripcion) {
        if (valores.isEmpty()) System.out.println("No se encontraron " + descripcion + ".");
        for (Object valor : valores) System.out.println(" - " + valor);
    }

    private static String explicarSql(SQLException ex) {
        return switch (ex.getErrorCode()) {
            case 1062 -> "Ese registro ya existe (duplicado).";
            case 1452 -> "Un cliente, técnico o dato relacionado no existe, o el técnico no está asignado.";
            case 1451 -> "No se puede borrar: existen registros relacionados.";
            case 1045 -> "Acceso denegado: usuario o contraseña de MySQL incorrectos.";
            case 1049 -> "La base 'sigetec' todavía no existe. Ejecutá 01_crear_base.sql.";
            case 1146 -> "Falta una tabla. Revisá los scripts SQL 01 y 02.";
            default -> ex.getMessage();
        };
    }
}
