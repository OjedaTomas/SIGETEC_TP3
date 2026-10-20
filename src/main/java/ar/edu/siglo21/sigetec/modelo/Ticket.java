package ar.edu.siglo21.sigetec.modelo;

/**
 * ABSTRACCIÓN: Ticket reúne lo que comparten todas las solicitudes.
 * Es abstract porque una solicitud concreta necesita un tipo de trabajo.
 * HERENCIA: TicketCctv y TicketSoporteIt extienden esta clase.
 */
public abstract class Ticket {
    private final long id;
    private final long clienteId;
    private final TipoServicio tipo;
    private final String descripcion;
    private final String detalleEspecifico;
    private final Prioridad prioridad;
    private final String estado;

    protected Ticket(long id, long clienteId, TipoServicio tipo, String descripcion,
                     String detalleEspecifico, Prioridad prioridad, String estado) {
        if (id < 0 || clienteId <= 0) throw new IllegalArgumentException("Identificadores inválidos.");
        if (tipo == null || prioridad == null) throw new IllegalArgumentException("Tipo y prioridad son obligatorios.");
        if (descripcion == null || descripcion.trim().isEmpty())
            throw new IllegalArgumentException("La descripción no puede estar vacía.");
        if (detalleEspecifico == null || detalleEspecifico.isBlank())
            throw new IllegalArgumentException("El dato específico es obligatorio.");
        this.id = id;
        this.clienteId = clienteId;
        this.tipo = tipo;
        this.descripcion = descripcion.trim();
        this.detalleEspecifico = detalleEspecifico.trim();
        this.prioridad = prioridad;
        this.estado = estado == null ? "ABIERTO" : estado;
    }

    /** POLIMORFISMO: el resultado cambia según el tipo concreto de ticket. */
    public abstract String instruccionTecnica();

    public String resumen() {
        return "Ticket #" + id + " | " + tipo + " | " + prioridad + " | " + estado
                + " | Cliente #" + clienteId + "\n  " + descripcion
                + "\n  " + instruccionTecnica();
    }

    public long getId() { return id; }
    public long getClienteId() { return clienteId; }
    public TipoServicio getTipo() { return tipo; }
    public String getDescripcion() { return descripcion; }
    public String getDetalleEspecifico() { return detalleEspecifico; }
    public Prioridad getPrioridad() { return prioridad; }
    public String getEstado() { return estado; }
}
