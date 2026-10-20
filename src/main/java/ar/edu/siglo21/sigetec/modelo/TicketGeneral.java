package ar.edu.siglo21.sigetec.modelo;

/** Para alarmas, cercos y redes. Completa el modelo sin duplicar clases. */
public class TicketGeneral extends Ticket {
    public TicketGeneral(long id, long clienteId, TipoServicio tipo, String descripcion,
                         String detalle, Prioridad prioridad, String estado) {
        super(id, clienteId, tipo, descripcion, detalle, prioridad, estado);
        if (tipo == TipoServicio.CCTV || tipo == TipoServicio.SOPORTE_IT)
            throw new IllegalArgumentException("CCTV y Soporte IT tienen clases especializadas.");
    }
    @Override public String instruccionTecnica() {
        return "Servicio " + getTipo() + ": verificar " + getDetalleEspecifico() + ".";
    }
}
