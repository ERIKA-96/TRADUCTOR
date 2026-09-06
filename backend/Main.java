import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class Main {

    public static void main(String[] args) {

        try {

            Connection con = Conexion.conectar();

            Statement st = con.createStatement();

            ResultSet rs = st.executeQuery(
                "SELECT * FROM CATEGORIA"
            );

            while(rs.next()) {
                System.out.println(
                    rs.getInt("idCateg")
                    + " - "
                    + rs.getString("nomCateg")
                );
            }

            con.close();

        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}