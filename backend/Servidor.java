import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpExchange;

import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class Servidor {

    public static void main(String[] args) throws Exception {

        HttpServer server = HttpServer.create(
            new InetSocketAddress(8000), 0
        );

        // CATEGORIAS
        server.createContext("/categorias", (HttpExchange exchange) -> {

            StringBuilder json = new StringBuilder("[");

            try {

                Connection con = Conexion.conectar();
                Statement st = con.createStatement();

                ResultSet rs = st.executeQuery(
                    "SELECT * FROM CATEGORIA"
                );

                boolean primero = true;

                while(rs.next()) {

                    if(!primero){
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

                con.close();

            } catch(Exception e){
                e.printStackTrace();
            }

            exchange.getResponseHeaders().add(
                "Access-Control-Allow-Origin", "*"
            );

            exchange.getResponseHeaders().add(
                "Content-Type", "application/json"
            );

            byte[] respuesta = json.toString().getBytes();

            exchange.sendResponseHeaders(
                200, respuesta.length
            );

            OutputStream os = exchange.getResponseBody();
            os.write(respuesta);
            os.close();

        });

        // PALABRAS
        server.createContext("/palabras", (HttpExchange exchange) -> {

            String query = exchange.getRequestURI().getQuery();
            String idCategoria = query.split("=")[1];

            StringBuilder json = new StringBuilder("[");

            try {

                Connection con = Conexion.conectar();
                Statement st = con.createStatement();

                ResultSet rs = st.executeQuery(
                    "SELECT * FROM PALABRA WHERE idCateg = " + idCategoria
                );

                boolean primero = true;

                while(rs.next()) {

                    if(!primero){
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

                con.close();

            } catch(Exception e){
                e.printStackTrace();
            }

            exchange.getResponseHeaders().add(
                "Access-Control-Allow-Origin", "*"
            );

            exchange.getResponseHeaders().add(
                "Content-Type", "application/json"
            );

            byte[] respuesta = json.toString().getBytes();

            exchange.sendResponseHeaders(
                200, respuesta.length
            );

            OutputStream os = exchange.getResponseBody();
            os.write(respuesta);
            os.close();

        });

        // TRADUCCION
        server.createContext("/traducir", (HttpExchange exchange) -> {

            String query = exchange.getRequestURI().getQuery();
            String idPalabra = query.split("=")[1];

            String json = "{}";

            try {

                Connection con = Conexion.conectar();
                Statement st = con.createStatement();

                ResultSet rs = st.executeQuery(
                    "SELECT * FROM TRADUCCION WHERE idPala = " + idPalabra
                );

                if(rs.next()) {

                    json = "{"
                    + "\"traduccion\":\""
                    + rs.getString("palabraTraducida")
                    + "\","
                    + "\"audio\":\""
                    + rs.getString("audio")
                    + "\","
                    + "\"imagen\":\""
                    + rs.getStrin   g("imagen")
                    + "\""
                    + "}";
                }

                con.close();

            } catch(Exception e){
                e.printStackTrace();
            }

            exchange.getResponseHeaders().add(
                "Access-Control-Allow-Origin", "*"
            );

            exchange.getResponseHeaders().add(
                "Content-Type", "application/json"
            );

            byte[] respuesta = json.getBytes();

            exchange.sendResponseHeaders(
                200, respuesta.length
            );

            OutputStream os = exchange.getResponseBody();
            os.write(respuesta);
            os.close();

        });

        server.start();

        System.out.println(
            "Servidor iniciado en http://localhost:8000"
        );
    }
}