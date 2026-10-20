package ar.edu.siglo21.sigetec.modelo;

/** Registro de lo que el técnico realmente hizo en una orden de trabajo. */
public class Intervencion {
    private final long ordenId;
    private final long tecnicoId;
    private final String descripcion;
    private final String resultado;

    public Intervencion(long ordenId, long tecnicoId, String descripcion, String resultado) {
        if (ordenId <= 0 || tecnicoId <= 0) throw new IllegalArgumentException("IDs inválidos.");
        if (descripcion == null || descripcion.isBlank()) throw new IllegalArgumentException("La tarea realizada es obligatoria.");
        if (resultado == null || resultado.isBlank()) throw new IllegalArgumentException("El resultado es obligatorio.");
        this.ordenId = ordenId;
        this.tecnicoId = tecnicoId;
        this.descripcion = descripcion.trim();
        this.resultado = resultado.trim();
    }
    public long getOrdenId() { return ordenId; }
    public long getTecnicoId() { return tecnicoId; }
    public String getDescripcion() { return descripcion; }
    public String getResultado() { return resultado; }
}
