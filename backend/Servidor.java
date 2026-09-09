import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class Servidor {

    public static void main(String[] args) throws Exception {

        // Render proporciona el puerto mediante la variable PORT.
        // Si ejecutas el proyecto localmente, utilizará el puerto 8000.
        int puerto = Integer.parseInt(
            System.getenv().getOrDefault("PORT", "8000")
        );

        HttpServer server = HttpServer.create(
            new InetSocketAddress("0.0.0.0", puerto), 0
        );


        // =====================================================
        // CATEGORIAS
        // =====================================================

        server.createContext("/categorias", (HttpExchange exchange) -> {

            StringBuilder json = new StringBuilder("[");

            try {

                Connection con = Conexion.conectar();
                Statement st = con.createStatement();

                ResultSet rs = st.executeQuery(
                    "SELECT * FROM \"CATEGORIA\""
                );

                boolean primero = true;

                while (rs.next()) {

                    if (!primero) {
                        json.append(",");
                    }

                    json.append("{")
                        .append("\"id\":")
                        .append(rs.getInt("idCateg"))
                        .append(",")
                        .append("\"nombre\":\"")
                        .append(rs.getString("nomCateg"))
                        .append("\"")
                        .append("}");

                    primero = false;
                }

                json.append("]");

                rs.close();
                st.close();
                con.close();

            } catch (Exception e) {

                e.printStackTrace();
            }

            exchange.getResponseHeaders().add(
                "Access-Control-Allow-Origin", "*"
            );

            exchange.getResponseHeaders().add(
                "Content-Type",
                "application/json; charset=UTF-8"
            );

            byte[] respuesta =
                json.toString().getBytes(StandardCharsets.UTF_8);

            exchange.sendResponseHeaders(
                200,
                respuesta.length
            );

            try (OutputStream os = exchange.getResponseBody()) {
                os.write(respuesta);
            }

        });


        // =====================================================
        // PALABRAS
        // =====================================================

        server.createContext("/palabras", (HttpExchange exchange) -> {

            String query = exchange.getRequestURI().getQuery();

            String idCategoria = query.split("=")[1];

            StringBuilder json = new StringBuilder("[");

            try {

                Connection con = Conexion.conectar();
                Statement st = con.createStatement();

                ResultSet rs = st.executeQuery(
                    "SELECT * FROM \"PALABRA\" WHERE \"idCateg\" = "
                    + idCategoria
                );

                boolean primero = true;

                while (rs.next()) {

                    if (!primero) {
                        json.append(",");
                    }

                    json.append("{")
                        .append("\"id\":")
                        .append(rs.getInt("idPala"))
                        .append(",")
                        .append("\"palabra\":\"")
                        .append(rs.getString("palabraEspañol"))
                        .append("\"")
                        .append("}");

                    primero = false;
                }

                json.append("]");

                rs.close();
                st.close();
                con.close();

            } catch (Exception e) {

                e.printStackTrace();
            }

            exchange.getResponseHeaders().add(
                "Access-Control-Allow-Origin", "*"
            );

            exchange.getResponseHeaders().add(
                "Content-Type",
                "application/json; charset=UTF-8"
            );

            byte[] respuesta =
                json.toString().getBytes(StandardCharsets.UTF_8);

            exchange.sendResponseHeaders(
                200,
                respuesta.length
            );

            try (OutputStream os = exchange.getResponseBody()) {
                os.write(respuesta);
            }

        });


        // =====================================================
        // TRADUCCION
        // =====================================================

        server.createContext("/traducir", (HttpExchange exchange) -> {

            String query = exchange.getRequestURI().getQuery();

            String idPalabra = query.split("=")[1];

            String json = "{}";

            try {

                Connection con = Conexion.conectar();
                Statement st = con.createStatement();

                ResultSet rs = st.executeQuery(
                    "SELECT * FROM \"TRADUCCION\" WHERE \"idPala\" = "
                    + idPalabra
                );

                if (rs.next()) {

                    String traduccion =
                        rs.getString("palabraTraducida");

                    String audio =
                        rs.getString("audio");

                    String imagen =
                        rs.getString("imagen");

                    json = "{"
                        + "\"traduccion\":\""
                        + traduccion
                        + "\","
                        + "\"audio\":\""
                        + audio
                        + "\","
                        + "\"imagen\":\""
                        + imagen
                        + "\""
                        + "}";
                }

                rs.close();
                st.close();
                con.close();

            } catch (Exception e) {

                e.printStackTrace();
            }

            exchange.getResponseHeaders().add(
                "Access-Control-Allow-Origin", "*"
            );

            exchange.getResponseHeaders().add(
                "Content-Type",
                "application/json; charset=UTF-8"
            );

            byte[] respuesta =
                json.getBytes(StandardCharsets.UTF_8);

            exchange.sendResponseHeaders(
                200,
                respuesta.length
            );

            try (OutputStream os = exchange.getResponseBody()) {
                os.write(respuesta);
            }

        });


        // =====================================================
        // INICIAR SERVIDOR
        // =====================================================

        server.start();

        System.out.println(
            "Servidor iniciado en el puerto " + puerto
        );

        System.out.println(
            "Conectado a Supabase PostgreSQL"
        );
    }
}
