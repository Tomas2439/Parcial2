package src;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MySQLDA {
            // Recuerde que usuariosdb es la base de datos de MySQL.
            // Modifíquela de acuerdo a la base de datos utilizada
            //"jdbc:mysql://localhost:3306/usuariosdb";
    private String URL;  
    private String user;
    private String password;
    
    MySQLDA(String usuario, String clave, String url) {
        this.user = usuario;
        this.password = clave;
        this.URL = url;

    } // constructor

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, user, password);
    } // getConnection()

    public List<String[]> convertirResultSet(ResultSet resultSet) {
        List<String[]> resultado = new ArrayList<>();
        try {
            ResultSetMetaData metaData = resultSet.getMetaData();
            int nColumnas = metaData.getColumnCount();
            // nColumnas  2 
            String[] encabezados = new String[nColumnas];
            for (int i = 0; i < nColumnas; i++) {
                encabezados[i] = metaData.getColumnName(i+1);
            }
            // encabezados ["nombre", "email"]
            resultado.add(encabezados);
            // Recorre sobre las filas de la tabla
            // para cargar cada registro en la lista
            while (resultSet.next()) {
                String[] fila = new String[nColumnas];
        
                for (int c = 0; c < nColumnas; c++) {
                    fila[c] = resultSet.getString(encabezados[c]);
                }
                resultado.add(fila);
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        return resultado;
    }

    public List<String[]> getQueryList(String consulta, List<Object> parametros) {
        List<String[]> resultado = null;

        try (Connection conn = this.getConnection();
            PreparedStatement sentencia = conn.prepareStatement(consulta)) {
                for (int i = 0; i < parametros.size(); i++) {
                    sentencia.setObject(i+1, parametros.get(i));
                }

                try (ResultSet resultSet = sentencia.executeQuery()) {
                    resultado = convertirResultSet(resultSet);
                }

            } catch (SQLException e) {
                System.err.println(e.getMessage());
            }
        return resultado;
    } // getQueryResult()

    public int ejecutarSentencia(String consulta, List<Object> parametros) {
        int filasAfectadas = 0;
        try (Connection conn = this.getConnection();
            PreparedStatement sentencia = conn.prepareStatement(consulta)) {

            for (int i = 0; i < parametros.size(); i++) {
                sentencia.setObject(i + 1, parametros.get(i));
            }

            filasAfectadas = sentencia.executeUpdate(); // Para INSERT, UPDATE, DELETE, etc.
        } catch (SQLException e) {
            System.err.println("Error al ejecutar sentencia: " + e.getMessage());
        }
        return filasAfectadas;
    }

    public String getURL() {
        return URL;
    }

    public void setURL(String uRL) {
        URL = uRL;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
    
    
}
