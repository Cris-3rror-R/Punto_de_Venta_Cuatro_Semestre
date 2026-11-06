import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import javax.swing.JOptionPane;

public class ConectarBD {
    public Connection Conectar;

    private static final String URL_PREDETERMINADA =
            "jdbc:mysql://localhost:3308/Carniceria";

    public Connection Conexion() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            String url = obtenerConfiguracion("carniceria.db.url", "CARNICERIA_DB_URL", URL_PREDETERMINADA);
            String usuario = obtenerConfiguracion("carniceria.db.user", "CARNICERIA_DB_USER", "root");
            String contrasena = obtenerConfiguracion("carniceria.db.password", "CARNICERIA_DB_PASSWORD", null);

            if (contrasena == null) {
                throw new SQLException("Configure carniceria.db.password o CARNICERIA_DB_PASSWORD.");
            }

            Conectar = DriverManager.getConnection(url, usuario, contrasena);
        }catch (Exception e) {
            JOptionPane.showMessageDialog(null, "No se pudo conectar a MySQL: " + e.getMessage());
        }
        return Conectar;
    }

    private String obtenerConfiguracion(String propiedad, String variable, String valorPredeterminado) {
        String valor = System.getProperty(propiedad);
        if (valor == null) {
            valor = System.getenv(variable);
        }
        return valor != null ? valor : valorPredeterminado;
    }

    public Connection ObtenerConexion() {
        return Conectar;
    }
}