import java.sql.Connection;
import java.sql.DriverManager;

public class Conexion {

    public static Connection conectar() {

        Connection con = null;

        try {

            // Datos de conexión proporcionados mediante
            // variables de entorno.
            String url = System.getenv("SUPABASE_DB_URL");
            String usuario = System.getenv("SUPABASE_DB_USER");
            String password = System.getenv("SUPABASE_DB_PASSWORD");

            // Comprobar que las variables existan
            if (url == null || usuario == null || password == null) {

                throw new Exception(
                    "Faltan las variables de entorno de Supabase."
                );
            }

            con = DriverManager.getConnection(
                url,
                usuario,
                password
            );

            System.out.println(
                "Conexión exitosa a Supabase"
            );

        } catch (Exception e) {

            System.out.println(
                "Error de conexión a Supabase: "
                + e.getMessage()
            );
        }

        return con;
    }
}
