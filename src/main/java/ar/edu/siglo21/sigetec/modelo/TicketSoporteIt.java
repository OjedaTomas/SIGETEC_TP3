package ar.edu.siglo21.sigetec.modelo;

/** Otro tipo concreto de Ticket: reparación o diagnóstico informático. */
public class TicketSoporteIt extends Ticket {
    public TicketSoporteIt(long id, long clienteId, String descripcion, String equipo,
                           Prioridad prioridad, String estado) {
        super(id, clienteId, TipoServicio.SOPORTE_IT, descripcion, equipo, prioridad, estado);
    }
    @Override public String instruccionTecnica() {
        return "Soporte IT: diagnosticar equipo " + getDetalleEspecifico() + " y registrar pruebas.";
    }
}
