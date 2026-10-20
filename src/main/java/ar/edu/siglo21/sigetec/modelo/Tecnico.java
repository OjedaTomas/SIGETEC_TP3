package ar.edu.siglo21.sigetec.modelo;

/** Técnico encargado de realizar tareas, por ejemplo CCTV o redes. */
public class Tecnico {
    private final long id;
    private final String nombre;
    private final String especialidad;

    public Tecnico(long id, String nombre, String especialidad) {
        if (id < 0) throw new IllegalArgumentException("El ID no puede ser negativo.");
        if (nombre == null || nombre.trim().length() < 2)
            throw new IllegalArgumentException("El nombre del técnico es obligatorio.");
        if (especialidad == null || especialidad.isBlank())
            throw new IllegalArgumentException("La especialidad es obligatoria.");
        this.id = id;
        this.nombre = nombre.trim();
        this.especialidad = especialidad.trim();
    }

    public long getId() { return id; }
    public String getNombre() { return nombre; }
    public String getEspecialidad() { return especialidad; }
    @Override public String toString() { return "Técnico #" + id + " - " + nombre + " / " + especialidad; }
}
