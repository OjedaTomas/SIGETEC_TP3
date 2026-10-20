package ar.edu.siglo21.sigetec.modelo;

/** Un tipo concreto de Ticket: trabajos de cámaras y videovigilancia. */
public class TicketCctv extends Ticket {
    public TicketCctv(long id, long clienteId, String descripcion, String ubicacionCamara,
                      Prioridad prioridad, String estado) {
        super(id, clienteId, TipoServicio.CCTV, descripcion, ubicacionCamara, prioridad, estado);
    }
    @Override public String instruccionTecnica() {
        return "CCTV: revisar cámara en " + getDetalleEspecifico() + ", PoE y conexión de red.";
    }
}
