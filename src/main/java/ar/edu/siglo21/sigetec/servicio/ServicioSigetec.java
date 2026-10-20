package ar.edu.siglo21.sigetec.servicio;

import ar.edu.siglo21.sigetec.datos.Repositorio;
import ar.edu.siglo21.sigetec.modelo.*;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;

/**
 * La capa de NEGOCIO decide qué operaciones tienen sentido.
 * El menú recoge datos, este servicio aplica reglas y el repositorio habla con MySQL.
 */
public class ServicioSigetec {
    private final Repositorio repositorio;

    public ServicioSigetec(Repositorio repositorio) { this.repositorio = repositorio; }

    public long registrarCliente(String nombre, String telefono, String email) throws SQLException {
        return repositorio.registrarCliente(new Cliente(0, nombre, telefono, email));
    }
    public List<Cliente> clientes() throws SQLException { return repositorio.listarClientes(); }

    public long registrarTecnico(String nombre, String especialidad) throws SQLException {
        return repositorio.registrarTecnico(new Tecnico(0, nombre, especialidad));
    }
    public List<Tecnico> tecnicos() throws SQLException { return repositorio.listarTecnicos(); }

    public long abrirTicket(long cliente, TipoServicio tipo, String descripcion,
                            String detalle, Prioridad prioridad) throws SQLException {
        Ticket t = FabricaTickets.crear(0, cliente, tipo, descripcion, detalle, prioridad, "ABIERTO");
        return repositorio.registrarTicket(t);
    }
    public List<Ticket> tickets() throws SQLException { return repositorio.listarTickets(); }
    public Ticket buscarTicket(long id) throws SQLException { return repositorio.buscarTicket(id); }

    public List<Ticket> ticketsPorPrioridad() throws SQLException {
        List<Ticket> copia = repositorio.listarTickets();
        // ORDENACIÓN: Collections.sort / List.sort usan un algoritmo eficiente de la biblioteca Java.
        // Primero van los más urgentes; si hay empate, primero el número de ticket más chico.
        copia.sort(Comparator.comparingInt((Ticket t) -> t.getPrioridad().getPuntaje())
                .reversed().thenComparingLong(Ticket::getId));
        return copia;
    }

    public long generarOrden(long ticketId) throws SQLException {
        Ticket t = repositorio.buscarTicket(ticketId);
        if (t == null) throw new IllegalArgumentException("No existe el ticket #" + ticketId);
        if (!"ABIERTO".equals(t.getEstado()))
            throw new IllegalArgumentException("Solo se genera una orden para tickets abiertos.");
        return repositorio.crearOrden(ticketId);
    }

    public void programarYAsignar(long ordenId, LocalDate fecha, long tecnicoId) throws SQLException {
        if (fecha.isBefore(LocalDate.now()))
            throw new IllegalArgumentException("La fecha no puede ser anterior al día de hoy.");
        repositorio.programarYAsignar(ordenId, fecha, tecnicoId);
    }

    public void programar(long ordenId, LocalDate fecha) throws SQLException {
        if (fecha.isBefore(LocalDate.now()))
            throw new IllegalArgumentException("La fecha no puede ser anterior al día de hoy.");
        repositorio.programarOrden(ordenId, fecha);
    }
    public void asignar(long ordenId, long tecnicoId) throws SQLException {
        repositorio.asignarTecnico(ordenId, tecnicoId);
    }
    public long intervenir(long ordenId, long tecnicoId, String descripcion, String resultado) throws SQLException {
        return repositorio.registrarIntervencion(new Intervencion(ordenId, tecnicoId, descripcion, resultado));
    }
    public void cerrarOrden(long ordenId) throws SQLException { repositorio.finalizarOrden(ordenId); }
    public List<OrdenTrabajo> ordenes() throws SQLException { return repositorio.listarOrdenes(); }
    public List<String> historial(long clienteId) throws SQLException { return repositorio.historialCliente(clienteId); }
}
