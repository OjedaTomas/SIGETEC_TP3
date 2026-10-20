package ar.edu.siglo21.sigetec.modelo;

/**
 * La fábrica concentra la decisión de qué clase crear.
 * Así el menú no necesita conocer los constructores de cada subclase.
 */
public final class FabricaTickets {
    private FabricaTickets() { }
    public static Ticket crear(long id, long clienteId, TipoServicio tipo,
                               String descripcion, String detalle, Prioridad prioridad, String estado) {
        return switch (tipo) {
            case CCTV -> new TicketCctv(id, clienteId, descripcion, detalle, prioridad, estado);
            case SOPORTE_IT -> new TicketSoporteIt(id, clienteId, descripcion, detalle, prioridad, estado);
            default -> new TicketGeneral(id, clienteId, tipo, descripcion, detalle, prioridad, estado);
        };
    }
}
