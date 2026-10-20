package ar.edu.siglo21.sigetec.datos;

import ar.edu.siglo21.sigetec.modelo.*;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * El repositorio contiene el SQL del programa (patrón DAO simplificado).
 * Regla de oro: NO concatenamos lo que escribe el usuario a una consulta.
 * Usamos PreparedStatement y signos ?, evitando inyección SQL.
 */
public class Repositorio {
    private final BaseDeDatos bd;

    public Repositorio(BaseDeDatos bd) { this.bd = bd; }

    /** Devuelve el ID generado por la base de datos después de un INSERT. */
    private long idCreado(PreparedStatement sentencia) throws SQLException {
        try (ResultSet ids = sentencia.getGeneratedKeys()) {
            if (ids.next()) return ids.getLong(1);
            throw new SQLException("MySQL no devolvió el identificador generado.");
        }
    }

    public long registrarCliente(Cliente cliente) throws SQLException {
        String sql = "INSERT INTO clientes (nombre, telefono, email) VALUES (?, ?, ?)";
        try (Connection con = bd.conectar();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, cliente.getNombre());
            ps.setString(2, cliente.getTelefono());
            ps.setString(3, cliente.getEmail().isBlank() ? null : cliente.getEmail());
            ps.executeUpdate();
            return idCreado(ps);
        }
    }

    public List<Cliente> listarClientes() throws SQLException {
        List<Cliente> clientes = new ArrayList<>();
        try (Connection con = bd.conectar();
             PreparedStatement ps = con.prepareStatement(
                     "SELECT id_cliente, nombre, telefono, email FROM clientes WHERE activo = TRUE ORDER BY id_cliente");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                clientes.add(new Cliente(rs.getLong("id_cliente"), rs.getString("nombre"),
                        rs.getString("telefono"), rs.getString("email")));
            }
        }
        return clientes;
    }

    public long registrarTecnico(Tecnico tecnico) throws SQLException {
        String sql = "INSERT INTO tecnicos (nombre, especialidad) VALUES (?, ?)";
        try (Connection con = bd.conectar();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, tecnico.getNombre());
            ps.setString(2, tecnico.getEspecialidad());
            ps.executeUpdate();
            return idCreado(ps);
        }
    }

    public List<Tecnico> listarTecnicos() throws SQLException {
        List<Tecnico> tecnicos = new ArrayList<>();
        try (Connection con = bd.conectar();
             PreparedStatement ps = con.prepareStatement(
                     "SELECT id_tecnico, nombre, especialidad FROM tecnicos WHERE activo = TRUE ORDER BY id_tecnico");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                tecnicos.add(new Tecnico(rs.getLong("id_tecnico"), rs.getString("nombre"),
                        rs.getString("especialidad")));
            }
        }
        return tecnicos;
    }

    public long registrarTicket(Ticket ticket) throws SQLException {
        String sql = "INSERT INTO tickets (id_cliente, tipo_codigo, descripcion, detalle_especifico, prioridad) "
                + "VALUES (?, ?, ?, ?, ?)";
        try (Connection con = bd.conectar();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setLong(1, ticket.getClienteId());
            ps.setString(2, ticket.getTipo().name());
            ps.setString(3, ticket.getDescripcion());
            ps.setString(4, ticket.getDetalleEspecifico());
            ps.setString(5, ticket.getPrioridad().name());
            ps.executeUpdate();
            return idCreado(ps);
        }
    }

    /** Los datos de MySQL se convierten en el subtipo Java apropiado. */
    private Ticket construirTicket(ResultSet rs) throws SQLException {
        return FabricaTickets.crear(rs.getLong("id_ticket"), rs.getLong("id_cliente"),
                TipoServicio.valueOf(rs.getString("tipo_codigo")),
                rs.getString("descripcion"), rs.getString("detalle_especifico"),
                Prioridad.valueOf(rs.getString("prioridad")), rs.getString("estado"));
    }

    public List<Ticket> listarTickets() throws SQLException {
        List<Ticket> tickets = new ArrayList<>();
        String sql = "SELECT id_ticket, id_cliente, tipo_codigo, descripcion, detalle_especifico, prioridad, estado "
                + "FROM tickets ORDER BY id_ticket";
        try (Connection con = bd.conectar();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) tickets.add(construirTicket(rs));
        }
        return tickets;
    }

    /** BÚSQUEDA por clave primaria: el índice SQL evita recorrer todas las filas. */
    public Ticket buscarTicket(long id) throws SQLException {
        String sql = "SELECT id_ticket, id_cliente, tipo_codigo, descripcion, detalle_especifico, prioridad, estado "
                + "FROM tickets WHERE id_ticket = ?";
        try (Connection con = bd.conectar(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? construirTicket(rs) : null;
            }
        }
    }

    public long crearOrden(long ticketId) throws SQLException {
        // Las dos operaciones pertenecen a UNA transacción: orden y cambio de ticket.
        try (Connection con = bd.conectar()) {
            con.setAutoCommit(false);
            try {
                long id;
                try (PreparedStatement ps = con.prepareStatement(
                        "INSERT INTO ordenes_trabajo (id_ticket) VALUES (?)", Statement.RETURN_GENERATED_KEYS)) {
                    ps.setLong(1, ticketId);
                    ps.executeUpdate();
                    id = idCreado(ps);
                }
                try (PreparedStatement ps = con.prepareStatement(
                        "UPDATE tickets SET estado = 'EN_ATENCION' WHERE id_ticket = ? AND estado = 'ABIERTO'")) {
                    ps.setLong(1, ticketId);
                    if (ps.executeUpdate() != 1)
                        throw new IllegalArgumentException("El ticket no existe o no está abierto.");
                }
                con.commit();
                return id;
            } catch (SQLException | RuntimeException ex) {
                con.rollback();
                throw ex;
            }
        }
    }

    /**
     * Programa y asigna al técnico en una sola transacción. Si el técnico no
     * existe o está inactivo, también deshace la fecha y el cambio de estado.
     */
    public void programarYAsignar(long ordenId, LocalDate fecha, long tecnicoId) throws SQLException {
        try (Connection con = bd.conectar()) {
            con.setAutoCommit(false);
            try {
                try (PreparedStatement ps = con.prepareStatement(
                        "UPDATE ordenes_trabajo SET fecha_programada = ?, estado = 'PROGRAMADA' "
                        + "WHERE id_orden = ? AND estado IN ('PENDIENTE','PROGRAMADA')")) {
                    ps.setDate(1, Date.valueOf(fecha));
                    ps.setLong(2, ordenId);
                    if (ps.executeUpdate() != 1)
                        throw new IllegalArgumentException("La orden no existe o no permite programación.");
                }
                try (PreparedStatement ps = con.prepareStatement(
                        "INSERT IGNORE INTO asignaciones (id_orden,id_tecnico) "
                        + "SELECT o.id_orden,t.id_tecnico FROM ordenes_trabajo o "
                        + "JOIN tecnicos t ON t.id_tecnico = ? AND t.activo = TRUE "
                        + "WHERE o.id_orden = ?")) {
                    ps.setLong(1, tecnicoId);
                    ps.setLong(2, ordenId);
                    // Si ya estaba asignado (INSERT IGNORE), comprobamos su existencia.
                    if (ps.executeUpdate() == 0) {
                        try (PreparedStatement check = con.prepareStatement(
                                "SELECT 1 FROM asignaciones WHERE id_orden=? AND id_tecnico=?")) {
                            check.setLong(1, ordenId);
                            check.setLong(2, tecnicoId);
                            try (ResultSet rs = check.executeQuery()) {
                                if (!rs.next()) throw new IllegalArgumentException("El técnico no existe o está inactivo.");
                            }
                        }
                    }
                }
                con.commit();
            } catch (SQLException | RuntimeException ex) {
                con.rollback();
                throw ex;
            }
        }
    }

    public void programarOrden(long ordenId, LocalDate fecha) throws SQLException {
        String sql = "UPDATE ordenes_trabajo SET fecha_programada = ?, estado = 'PROGRAMADA' "
                + "WHERE id_orden = ? AND estado IN ('PENDIENTE','PROGRAMADA')";
        try (Connection con = bd.conectar(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(fecha));
            ps.setLong(2, ordenId);
            if (ps.executeUpdate() == 0) {
                throw new IllegalArgumentException("La orden no existe o su estado no permite programarla.");
            }
        }
    }

    public void asignarTecnico(long ordenId, long tecnicoId) throws SQLException {
        String sql = "INSERT INTO asignaciones (id_orden,id_tecnico) "
                + "SELECT o.id_orden, t.id_tecnico FROM ordenes_trabajo o "
                + "JOIN tecnicos t ON t.id_tecnico = ? AND t.activo = TRUE "
                + "WHERE o.id_orden = ? AND o.estado = 'PROGRAMADA'";
        try (Connection con = bd.conectar(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, tecnicoId);
            ps.setLong(2, ordenId);
            if (ps.executeUpdate() == 0) {
                throw new IllegalArgumentException("Para asignar un técnico, la orden debe estar PROGRAMADA y el técnico activo.");
            }
        }
    }

    public long registrarIntervencion(Intervencion i) throws SQLException {
        // La FK de la base asegura que el técnico esté asignado a la orden.
        String sql = "INSERT INTO intervenciones (id_orden,id_tecnico,descripcion,resultado) "
                + "VALUES (?,?,?,?)";
        try (Connection con = bd.conectar()) {
            con.setAutoCommit(false);
            try {
                // Primero bloqueamos la orden para evitar actualizarla si ya finalizó.
                try (PreparedStatement estado = con.prepareStatement(
                        "SELECT estado FROM ordenes_trabajo WHERE id_orden = ? FOR UPDATE")) {
                    estado.setLong(1, i.getOrdenId());
                    try (ResultSet rs = estado.executeQuery()) {
                        if (!rs.next()) throw new IllegalArgumentException("La orden no existe.");
                        if (!("PROGRAMADA".equals(rs.getString(1)) || "EN_EJECUCION".equals(rs.getString(1)))) {
                            throw new IllegalArgumentException("La orden debe estar programada o en ejecución.");
                        }
                    }
                }
                long id;
                try (PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                    ps.setLong(1, i.getOrdenId());
                    ps.setLong(2, i.getTecnicoId());
                    ps.setString(3, i.getDescripcion());
                    ps.setString(4, i.getResultado());
                    ps.executeUpdate();
                    id = idCreado(ps);
                }
                try (PreparedStatement ps = con.prepareStatement(
                        "UPDATE ordenes_trabajo SET estado = 'EN_EJECUCION' WHERE id_orden = ?")) {
                    ps.setLong(1, i.getOrdenId());
                    ps.executeUpdate();
                }
                con.commit();
                return id;
            } catch (SQLException | RuntimeException ex) {
                con.rollback();
                throw ex;
            }
        }
    }

    /** TRANSACCIÓN: cerramos orden y ticket juntos, o no cerramos ninguno. */
    public void finalizarOrden(long ordenId) throws SQLException {
        try (Connection con = bd.conectar()) {
            con.setAutoCommit(false);
            try {
                long ticketId;
                try (PreparedStatement ps = con.prepareStatement(
                        "SELECT id_ticket, estado FROM ordenes_trabajo WHERE id_orden = ? FOR UPDATE")) {
                    ps.setLong(1, ordenId);
                    try (ResultSet rs = ps.executeQuery()) {
                        if (!rs.next()) throw new IllegalArgumentException("La orden no existe.");
                        if (!"EN_EJECUCION".equals(rs.getString("estado")))
                            throw new IllegalArgumentException("Primero debés registrar una intervención.");
                        ticketId = rs.getLong("id_ticket");
                    }
                }
                try (PreparedStatement ps = con.prepareStatement(
                        "UPDATE ordenes_trabajo SET estado = 'FINALIZADA' WHERE id_orden = ?")) {
                    ps.setLong(1, ordenId);
                    ps.executeUpdate();
                }
                try (PreparedStatement ps = con.prepareStatement(
                        "UPDATE tickets SET estado = 'CERRADO' WHERE id_ticket = ?")) {
                    ps.setLong(1, ticketId);
                    ps.executeUpdate();
                }
                con.commit();
            } catch (SQLException | RuntimeException ex) {
                con.rollback();
                throw ex;
            }
        }
    }

    public List<OrdenTrabajo> listarOrdenes() throws SQLException {
        List<OrdenTrabajo> ordenes = new ArrayList<>();
        String sql = "SELECT o.id_orden,o.id_ticket,o.fecha_programada,o.estado,c.nombre, "
                + "COALESCE(GROUP_CONCAT(te.nombre ORDER BY te.nombre SEPARATOR ', '),'sin asignar') AS tecnicos "
                + "FROM ordenes_trabajo o JOIN tickets ti ON ti.id_ticket=o.id_ticket "
                + "JOIN clientes c ON c.id_cliente=ti.id_cliente "
                + "LEFT JOIN asignaciones a ON a.id_orden=o.id_orden "
                + "LEFT JOIN tecnicos te ON te.id_tecnico=a.id_tecnico "
                + "GROUP BY o.id_orden,o.id_ticket,o.fecha_programada,o.estado,c.nombre "
                + "ORDER BY o.id_orden";
        try (Connection con = bd.conectar();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Date fecha = rs.getDate("fecha_programada");
                ordenes.add(new OrdenTrabajo(rs.getLong("id_orden"), rs.getLong("id_ticket"),
                        fecha == null ? null : fecha.toLocalDate(), rs.getString("estado"),
                        rs.getString("nombre"), rs.getString("tecnicos")));
            }
        }
        return ordenes;
    }

    public List<String> historialCliente(long clienteId) throws SQLException {
        List<String> historial = new ArrayList<>();
        String sql = "SELECT ti.id_ticket, o.id_orden, o.estado, i.descripcion, i.resultado, "
                + "te.nombre AS tecnico FROM tickets ti "
                + "LEFT JOIN ordenes_trabajo o ON o.id_ticket=ti.id_ticket "
                + "LEFT JOIN intervenciones i ON i.id_orden=o.id_orden "
                + "LEFT JOIN tecnicos te ON te.id_tecnico=i.id_tecnico "
                + "WHERE ti.id_cliente=? ORDER BY ti.id_ticket, i.fecha_registro";
        try (Connection con = bd.conectar(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, clienteId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    long orden = rs.getLong("id_orden");
                    String linea = "Ticket #" + rs.getLong("id_ticket")
                            + (rs.wasNull() ? "" : " / Orden #" + orden)
                            + " / Estado: " + (rs.getString("estado") == null ? "sin orden" : rs.getString("estado"));
                    if (rs.getString("descripcion") != null) {
                        linea += "\n  " + rs.getString("tecnico") + ": " + rs.getString("descripcion")
                                + " => " + rs.getString("resultado");
                    }
                    historial.add(linea);
                }
            }
        }
        return historial;
    }
}
