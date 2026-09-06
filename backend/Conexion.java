import java.sql.Connection;
import java.sql.DriverManager;

public class Conexion {

    public static Connection conectar() {
        Connection con = null;

        try {
            String url = "jdbc:mysql://localhost:3306/TRADUCTOR";
            String usuario = "root";
            String password = "ErikaGBL09";

            con = DriverManager.getConnection(url, usuario, password);

            System.out.println("Conexión exitosa");

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }

        return con;
    }
}