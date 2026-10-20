package ar.edu.siglo21.sigetec;

import ar.edu.siglo21.sigetec.modelo.*;
import java.util.ArrayList;
import java.util.Comparator;

/** Pruebas que no dependen de MySQL; se ejecutan usando Java puro. */
public class PruebasModelo {
    private static int aprobadas = 0;
    private static void comprobar(boolean condicion, String mensaje) {
        if (!condicion) throw new AssertionError("FALLÓ: " + mensaje);
        aprobadas++;
        System.out.println("OK - " + mensaje);
    }
    private static void debeFallar(Runnable codigo, String mensaje) {
        try { codigo.run(); throw new AssertionError("No falló: " + mensaje); }
        catch (IllegalArgumentException ex) { comprobar(true, mensaje); }
    }
    public static void main(String[] args) {
        Cliente c = new Cliente(1, "Cliente Prueba", "1100000", "prueba@ejemplo.test");
        comprobar(c.getId() == 1, "Constructor y atributos encapsulados de Cliente");
        debeFallar(() -> new Cliente(0, "", "11", ""), "No acepta clientes sin nombre");
        debeFallar(() -> new Cliente(0, "Correcto", "", ""), "No acepta clientes sin teléfono");
        Tecnico tec = new Tecnico(2, "Técnico Prueba", "CCTV");
        comprobar(tec.getEspecialidad().equals("CCTV"), "Constructor de Técnico");
        Ticket a = FabricaTickets.crear(1, 1, TipoServicio.CCTV, "Cámara offline", "Portería", Prioridad.ALTA, "ABIERTO");
        Ticket b = FabricaTickets.crear(2, 1, TipoServicio.SOPORTE_IT, "PC sin video", "PC Dell", Prioridad.MEDIA, "ABIERTO");
        Ticket d = FabricaTickets.crear(3, 1, TipoServicio.ALARMA, "Sensor con falla", "Acceso norte", Prioridad.BAJA, "ABIERTO");
        comprobar(a instanceof TicketCctv, "Herencia TicketCctv");
        comprobar(b instanceof TicketSoporteIt, "Herencia TicketSoporteIt");
        comprobar(d instanceof TicketGeneral, "Herencia TicketGeneral");
        comprobar(a.instruccionTecnica().contains("PoE"), "Polimorfismo CCTV");
        comprobar(b.instruccionTecnica().contains("diagnosticar"), "Polimorfismo Soporte IT");
        comprobar(d.instruccionTecnica().contains("ALARMA"), "Polimorfismo para alarmas");
        debeFallar(() -> FabricaTickets.crear(0, 1, TipoServicio.CCTV, "", "Portería", Prioridad.ALTA, "ABIERTO"),
                "No acepta tickets sin descripción");
        debeFallar(() -> new Intervencion(1, 1, "", "Listo"), "No acepta intervención sin tarea");
        Intervencion i = new Intervencion(5, 2, "Revisar conexión", "Solucionado");
        comprobar(i.getOrdenId() == 5, "Constructor de Intervención");
        ArrayList<Ticket> tickets = new ArrayList<>();
        tickets.add(b); tickets.add(d); tickets.add(a);
        tickets.sort(Comparator.comparingInt((Ticket t) -> t.getPrioridad().getPuntaje()).reversed());
        comprobar(tickets.get(0).getPrioridad() == Prioridad.ALTA, "Algoritmo de ordenación por prioridad");
        Ticket encontrado = null;
        for (Ticket t : tickets) if (t.getId() == 2) encontrado = t;
        comprobar(encontrado == b, "Algoritmo de búsqueda por ID");
        comprobar(a.resumen().contains("Cámara offline"), "Resumen del ticket muestra datos correctos");
        System.out.println("\nPRUEBAS APROBADAS: " + aprobadas);
    }
}
