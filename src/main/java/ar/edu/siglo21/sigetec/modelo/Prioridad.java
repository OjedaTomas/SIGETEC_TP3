package ar.edu.siglo21.sigetec.modelo;

/** A mayor puntaje, mayor urgencia. Se usa para ordenar los tickets. */
public enum Prioridad {
    BAJA(1), MEDIA(2), ALTA(3);
    private final int puntaje;
    Prioridad(int puntaje) { this.puntaje = puntaje; }
    public int getPuntaje() { return puntaje; }
}
