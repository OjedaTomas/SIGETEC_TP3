package ar.edu.siglo21.sigetec.modelo;

/**
 * ENCAPSULAMIENTO: los atributos son privados (private).
 * Desde otra clase no se modifica nombre directamente; se usa un método.
 */
public class Cliente {
    private final long id;
    private final String nombre;
    private final String telefono;
    private final String email;

    /** Constructor: inicializa un objeto Cliente con datos válidos. */
    public Cliente(long id, String nombre, String telefono, String email) {
        if (id < 0) throw new IllegalArgumentException("El ID no puede ser negativo.");
        if (nombre == null || nombre.trim().length() < 2)
            throw new IllegalArgumentException("El nombre debe tener al menos 2 caracteres.");
        if (telefono == null || telefono.trim().isEmpty())
            throw new IllegalArgumentException("El teléfono es obligatorio.");
        if (email != null && !email.isBlank() && (!email.contains("@") || email.contains(" ")))
            throw new IllegalArgumentException("El correo ingresado no es válido.");
        this.id = id;
        this.nombre = nombre.trim();
        this.telefono = telefono.trim();
        this.email = email == null ? "" : email.trim();
    }

    public long getId() { return id; }
    public String getNombre() { return nombre; }
    public String getTelefono() { return telefono; }
    public String getEmail() { return email; }
    @Override public String toString() { return "Cliente #" + id + " - " + nombre + " (" + telefono + ")"; }
}
