package ar.edu.siglo21.sigetec.datos;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
 /**JDBC permite que Java envíe instrucciones SQL a MySQL.
 * Esta clase tiene UNA responsabilidad: abrir conexiones.
 * Las credenciales se cargan desde un archivo LOCAL ignorado por Git./*
 * 
 * @author VAMOS
 */
public class BaseDeDatos {
    private final String url;
    private final String usuario;
    private final String clave;

    public BaseDeDatos() throws IOException {
        Properties propiedades = new Properties();
        Path ruta = Path.of("config", "sigetec.properties");
        if (Files.exists(ruta)) {
            try (InputStream entrada = Files.newInputStream(ruta)) {
                propiedades.load(entrada);
            }
        }
        this.url = usarEntornoOArchivo("SIGETEC_DB_URL", propiedades, "db.url");
        this.usuario = usarEntornoOArchivo("SIGETEC_DB_USER", propiedades, "db.usuario");
        this.clave = usarEntornoOArchivo("SIGETEC_DB_PASSWORD", propiedades, "db.clave");
        if (url.isBlank() || usuario.isBlank() || clave.isBlank() || clave.equals("COLOCA_AQUI_TU_CLAVE")) {
            throw new IOException("Falta configurar MySQL. Copiá config/sigetec.properties.ejemplo "
                    + "a config/sigetec.properties y completá usuario, contraseña y URL.");
        }
        if (!url.startsWith("jdbc:mysql://")) {
            throw new IOException("La URL JDBC debe comenzar con jdbc:mysql://");
        }
    }
    private static String usarEntornoOArchivo(String variable, Properties p, String campo) {
        String valor = System.getenv(variable);
        return valor != null && !valor.isBlank() ? valor.trim() : p.getProperty(campo, "").trim();
    }
    public Connection conectar() throws SQLException {
        // DriverManager busca el controlador Connector/J agregado por Maven.
        return DriverManager.getConnection(url, usuario, clave);
    }
}