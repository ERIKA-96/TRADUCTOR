```java
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.OutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class Servidor {

    // Carpeta donde Render tendrá los archivos de la página
    private static final Path WEB_DIR = Paths.get("/app/site");

    public static void main(String[] args) throws Exception {

        // Render proporciona el puerto mediante PORT
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

            enviarJSON(exchange, json.toString());

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

            enviarJSON(exchange, json.toString());

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

            enviarJSON(exchange, json);

        });


        // =====================================================
        // PAGINA WEB
        // =====================================================

        server.createContext("/", (HttpExchange exchange) -> {

            try {

                String ruta = exchange.getRequestURI().getPath();

                // Si entra directamente al dominio
                if (ruta.equals("/")) {
                    ruta = "/index.html";
                }

                // Evitar acceso fuera de la carpeta de la página
                Path archivo = WEB_DIR
                    .resolve(ruta.substring(1))
                    .normalize();

                if (!archivo.startsWith(WEB_DIR)) {

                    enviarTexto(
                        exchange,
                        403,
                        "Acceso no permitido"
                    );

                    return;
                }

                if (!Files.exists(archivo) ||
                    Files.isDirectory(archivo)) {

                    enviarTexto(
                        exchange,
                        404,
                        "Archivo no encontrado"
                    );

                    return;
                }

                byte[] contenido = Files.readAllBytes(archivo);

                String tipo = obtenerTipoContenido(
                    archivo.toString()
                );

                exchange.getResponseHeaders().set(
                    "Content-Type",
                    tipo
                );

                exchange.sendResponseHeaders(
                    200,
                    contenido.length
                );

                try (OutputStream os =
                         exchange.getResponseBody()) {

                    os.write(contenido);
                }

            } catch (Exception e) {

                e.printStackTrace();

                enviarTexto(
                    exchange,
                    500,
                    "Error interno del servidor"
                );
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
            "Página web disponible desde Render"
        );

        System.out.println(
            "Conectado a Supabase PostgreSQL"
        );
    }


    // =========================================================
    // ENVIAR JSON
    // =========================================================

    private static void enviarJSON(
        HttpExchange exchange,
        String contenido
    ) throws IOException {

        exchange.getResponseHeaders().set(
            "Access-Control-Allow-Origin",
            "*"
        );

        exchange.getResponseHeaders().set(
            "Content-Type",
            "application/json; charset=UTF-8"
        );

        byte[] respuesta =
            contenido.getBytes(StandardCharsets.UTF_8);

        exchange.sendResponseHeaders(
            200,
            respuesta.length
        );

        try (OutputStream os =
                 exchange.getResponseBody()) {

            os.write(respuesta);
        }
    }


    // =========================================================
    // ENVIAR TEXTO
    // =========================================================

    private static void enviarTexto(
        HttpExchange exchange,
        int codigo,
        String contenido
    ) throws IOException {

        byte[] respuesta =
            contenido.getBytes(StandardCharsets.UTF_8);

        exchange.getResponseHeaders().set(
            "Content-Type",
            "text/plain; charset=UTF-8"
        );

        exchange.sendResponseHeaders(
            codigo,
            respuesta.length
        );

        try (OutputStream os =
                 exchange.getResponseBody()) {

            os.write(respuesta);
        }
    }


    // =========================================================
    // TIPOS DE ARCHIVO
    // =========================================================

    private static String obtenerTipoContenido(
        String archivo
    ) {

        String nombre =
            archivo.toLowerCase();

        if (nombre.endsWith(".html")) {
            return "text/html; charset=UTF-8";
        }

        if (nombre.endsWith(".css")) {
            return "text/css; charset=UTF-8";
        }

        if (nombre.endsWith(".js")) {
            return "application/javascript; charset=UTF-8";
        }

        if (nombre.endsWith(".png")) {
            return "image/png";
        }

        if (nombre.endsWith(".jpg") ||
            nombre.endsWith(".jpeg")) {
            return "image/jpeg";
        }

        if (nombre.endsWith(".webp")) {
            return "image/webp";
        }

        if (nombre.endsWith(".gif")) {
            return "image/gif";
        }

        if (nombre.endsWith(".svg")) {
            return "image/svg+xml";
        }

        if (nombre.endsWith(".mp3")) {
            return "audio/mpeg";
        }

        if (nombre.endsWith(".wav")) {
            return "audio/wav";
        }

        if (nombre.endsWith(".ogg")) {
            return "audio/ogg";
        }

        if (nombre.endsWith(".mp4")) {
            return "video/mp4";
        }

        return "application/octet-stream";
    }
}
```
