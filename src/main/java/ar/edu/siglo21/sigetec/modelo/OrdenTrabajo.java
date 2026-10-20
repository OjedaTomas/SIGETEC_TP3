package ar.edu.siglo21.sigetec.modelo;

import java.time.LocalDate;

/** Una orden transforma un ticket en un trabajo planificable. */
public class OrdenTrabajo {
    private final long id;
    private final long ticketId;
    private final LocalDate fechaProgramada;
    private final String estado;
    private final String cliente;
    private final String tecnicos;

    public OrdenTrabajo(long id, long ticketId, LocalDate fechaProgramada,
                        String estado, String cliente, String tecnicos) {
        this.id = id;
        this.ticketId = ticketId;
        this.fechaProgramada = fechaProgramada;
        this.estado = estado;
        this.cliente = cliente;
        this.tecnicos = tecnicos;
    }

    public long getId() { return id; }
    public long getTicketId() { return ticketId; }
    public LocalDate getFechaProgramada() { return fechaProgramada; }
    public String getEstado() { return estado; }
    @Override public String toString() {
        return "Orden #" + id + " | Ticket #" + ticketId + " | " + estado
                + " | Fecha: " + (fechaProgramada == null ? "sin programar" : fechaProgramada)
                + " | Cliente: " + cliente + " | Técnicos: " + tecnicos;
    }
}
