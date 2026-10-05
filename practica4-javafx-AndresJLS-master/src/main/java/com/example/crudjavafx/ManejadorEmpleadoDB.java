package com.example.crudjavafx;

import java.sql.*;
import java.util.ArrayList;

public class ManejadorEmpleadoDB {

    private String url;
    private String user;
    private String password;

    public ManejadorEmpleadoDB(String url, String user, String password) {
        this.url = url;
        this.user = user;
        this.password = password;

    }

    //Metodos para realizar la conexion a laa base de datos
    public Connection abririConexion() {
       /* En las versiones mas nuevas de java no es necesario indicar el Driver
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        */
        try {
            DriverManager.setLoginTimeout(10); //Maximo 10 segundos para conectar

            return DriverManager.getConnection(url, user, password);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }

    }

    public void cerrarConexion(Connection conn) {

        try {
            if (conn != null) {//significa que hay una conexion
                conn.close();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean probarConexion() {
        Connection conn = abririConexion();
        if (conn != null) {
            cerrarConexion(conn);
            return true; // significa que si abrio conexion
        } else {
            return false;
        }
    }

    // MetodosCRUD

    // C - Create (INSERT INTO)
    public int insertar(Empleado empleado) {

        String query = "INSERT INTO empleados (nombre, puesto, salario) VALUES ('" + empleado.getNombre() + "', '" + empleado.getPuesto() + "', " + empleado.getSalario() + ")";

        try (Connection conn = abririConexion();
             Statement stmt = conn.createStatement()) {
            return stmt.executeUpdate(query); //devuelve el numero de registros agregados en la base de datos
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }

    }

    // C - Create (INSERT INTO) - PreparedStatment
    public int insertarPS(Empleado empleado) {

        String query = "INSERT INTO empleados (nombre, puesto, salario) VALUES (?, ?, ?)";

        try (Connection conn = abririConexion();
             // Statement stmt = conn.createStatement())
             PreparedStatement ps = conn.prepareStatement(query)) {
            // return stmt.executeUpdate(query); //devuelve el numero de registros agregados en la base de datos
            ps.setString(1, empleado.getNombre());
            ps.setString(2, empleado.getPuesto());
            ps.setDouble(3, empleado.getSalario());
            return ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }

    }

    // C - Create (INSERT INTO) - CallableStatement
    public int insertarCS(Empleado empleado) {

        String call = "{CALL insertar_empleado(?, ?, ?)}";

        try (Connection conn = abririConexion();
             // Statement stmt = conn.createStatement())
             CallableStatement cs = conn.prepareCall(call)) {
            // PreparedStatement ps = conn.prepareStatement(query)) {
            // return stmt.executeUpdate(query); //devuelve el numero de registros agregados en la base de datos
            cs.setString(1, empleado.getNombre());
            cs.setString(2, empleado.getPuesto());
            cs.setDouble(3, empleado.getSalario());


            return cs.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }

    }

    //R - Read (SELECT)

    public ArrayList<Empleado> getEmpleados() {
        ArrayList<Empleado> empleados = new ArrayList<>();
        String query = "SELECT * FROM empleados";
        try (Connection conn = abririConexion();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) { //recorrer cada linea del ResultSet
                int id = rs.getInt("id");
                String nombre = rs.getString("nombre");
                String puesto = rs.getString("puesto");
                double salario = rs.getDouble("salario");
                empleados.add(new Empleado(id, nombre, puesto, salario));

            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return empleados;
    }

    //R - Read (SELECT) - PreparedStatment
    public ArrayList<Empleado> getEmpleadosPS() {
        ArrayList<Empleado> empleados = new ArrayList<>();
        String query = "SELECT * FROM empleados";

        try (Connection conn = abririConexion();
             PreparedStatement ps = conn.prepareStatement(query);
             //Statement stmt = conn.createStatement();
             // ResultSet rs = stmt.executeQuery(query)
             ResultSet rs = ps.executeQuery();
        ) {

            while (rs.next()) {
                int id = rs.getInt("id");
                String nombre = rs.getString("nombre");
                String puesto = rs.getString("puesto");
                double salario = rs.getDouble("salario");
                empleados.add(new Empleado(id, nombre, puesto, salario));

            }


        } catch (SQLException e) {
            e.printStackTrace();
        }


        return empleados;
    }

    //R - Read (SELECT) - CallableStatment
    public ArrayList<Empleado> getEmpleadosCS() {
        ArrayList<Empleado> empleados = new ArrayList<>();
        String call = "{CALL obtener_empleados}";

        try (Connection conn = abririConexion();
             CallableStatement cs = conn.prepareCall(call);
             //PreparedStatement ps = conn.prepareStatement(query);
             //Statement stmt = conn.createStatement();
             //ResultSet rs = stmt.executeQuery(query)
             //ResultSet rs = ps.executeQuery();
             ResultSet rs = cs.executeQuery()

        ) {

            while (rs.next()) {
                int id = rs.getInt("id");
                String nombre = rs.getString("nombre");
                String puesto = rs.getString("puesto");
                double salario = rs.getDouble("salario");
                empleados.add(new Empleado(id, nombre, puesto, salario));

            }

        } catch (SQLException e) {
            e.printStackTrace();
        }


        return empleados;
    }


    // ManejadorEmpleadoDB

    // Despues de R- Read preparestatement
// R - Read (Select) - CallableStatement
    public ArrayList<Empleado> getEmpleadosPorFiltroCS(String nombreFiltro, String puestoFiltro, double salarioFiltro) {
        ArrayList<Empleado> empleados = new ArrayList<>();
        String call = "{CALL obtener_empleados_filtro(?, ?, ?)}";

        try (Connection conn = abririConexion();
             CallableStatement cs = conn.prepareCall(call)) {

            if (nombreFiltro != null)
                cs.setString(1, nombreFiltro);
            else
                cs.setNull(1, Types.VARCHAR);

            if (puestoFiltro != null)
                cs.setString(2, puestoFiltro);
            else
                cs.setNull(2, Types.VARCHAR);

            cs.setDouble(3, salarioFiltro);

            ResultSet rs = cs.executeQuery();

            while (rs.next()) {
                empleados.add(new Empleado(
                        rs.getInt("id"),
                        rs.getString("nombre"),
                        rs.getString("puesto"),
                        rs.getDouble("salario")
                ));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return empleados;
    }


    public ArrayList<Empleado> getEmpleadosPorFiltroPS(String nombreFiltro, String puestoFiltro, double salarioFiltro) {
        ArrayList<Empleado> empleados = new ArrayList<>();
        String query = "SELECT * FROM empleados WHERE 1=1";

        if (nombreFiltro != null) {
            query += " AND nombre = ?";
        }
        if (puestoFiltro != null) {
            query += " AND puesto = ?";
        }

        if (salarioFiltro != 0) {
            query += " AND salario = ?";
        }

        try (Connection conn = abririConexion();
             //Statement stmt = conn.createStatement();
             PreparedStatement ps = conn.prepareStatement(query)
             //ResultSet rs = stmt.executeQuery(query))
        ) {

            int index = 1;

            if (nombreFiltro != null) {
                ps.setString(index, nombreFiltro);
                index++;
            }
            if (puestoFiltro != null) {
                ps.setString(index, puestoFiltro);
                index++;
            }
            if (salarioFiltro != 0) {
                ps.setDouble(index, salarioFiltro);
                //index++;
            }

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                int id = rs.getInt("id");
                String nombre = rs.getString("nombre");
                String puesto = rs.getString("puesto");
                double salario = rs.getDouble("salario");
                empleados.add(new Empleado(id, nombre, puesto, salario));

            }


        } catch (SQLException e) {
            e.printStackTrace();
        }


        return empleados;
    }


    // U - Update (UPDATE)
    public int actualizar(Empleado empleado) {
        String query = "UPDATE empleados SET " +
                "nombre='" + empleado.getNombre() + "', " +
                "puesto=' " + empleado.getPuesto() + "', " +
                "salario= " + empleado.getSalario() +
                " WHERE id=" + empleado.getId();

        try (Connection conn = abririConexion();
             Statement stmt = conn.createStatement()) {
            return stmt.executeUpdate(query); //devuelve el numero de registros agregados en la base de datos
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }

    }

    // U - Update (UPDATE) - PreparedStatment
    public int actualizarPS(Empleado empleado) {
        String query = "UPDATE empleados SET " +
                "nombre=?," +
                "puesto=?," +
                "salario=? " +
                " WHERE id=?";

        try (Connection conn = abririConexion();
             //Statement stmt = conn.createStatement())
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, empleado.getNombre());
            ps.setString(2, empleado.getPuesto());
            ps.setDouble(3, empleado.getSalario());
            ps.setInt(4, empleado.getId());
            //return stmt.executeUpdate(query); //devuelve el numero de registros agregados en la base de datos
            return ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }

    }

    // U - Update (UPDATE) - CallableStatment
    public int actualizarCS(Empleado empleado) {
        String call = "{CALL actualizarEmpleado(?, ?, ?, ?)}";

        try (Connection conn = abririConexion();
             //Statement stmt = conn.createStatement())
             CallableStatement cs = conn.prepareCall(call)) {
            //PreparedStatement ps = conn.prepareStatement(query)) {
            cs.setString(1, empleado.getNombre());
            cs.setString(2, empleado.getPuesto());
            cs.setDouble(3, empleado.getSalario());
            cs.setInt(4, empleado.getId());
            //return stmt.executeUpdate(query); //devuelve el numero de registros agregados en la base de datos
            return cs.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }

    }

    // D - Delete (DELETE)
    public int eliminar(int idEliminar) {
        String query = "DELETE FROM EMPLEADOS WHERE id='" + idEliminar + "';";
        try (Connection conn = abririConexion();
             Statement stmt = conn.createStatement()) {
            return stmt.executeUpdate(query); //devuelve el numero de registros agregados en la base de datos
        } catch (SQLException e) {
            e.printStackTrace();

            return 0;
        }

    }

    // D - Delete (DELETE) - PreparedStatement
    public int eliminarPS(int idEliminar) {
        String query = "DELETE FROM empleados WHERE id=?";
        try (Connection conn = abririConexion();
             //Statement stmt = conn.createStatement())
             PreparedStatement ps = conn.prepareStatement(query)) {
            //return stmt.executeUpdate(query); //devuelve el numero de registros agregados en la base de datos
            ps.setInt(1, idEliminar);
            return ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();

            return 0;
        }
    }

    // D - Delete (DELETE) - CallableStatement
    public int eliminarCS(int idEliminar) {
        String call = "{CALL eliminarEmpleado(?)}";
        try (Connection conn = abririConexion();
             //Statement stmt = conn.createStatement())
             CallableStatement cs = conn.prepareCall(call)) {
            //PreparedStatement ps = conn.prepareStatement(call)) {
            //return stmt.executeUpdate(query); //devuelve el numero de registros agregados en la base de datos
            cs.setInt(1, idEliminar);
            return cs.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();

            return 0;
        }
    }
    // metodo para llamar el Procedure IN y OUT
    public int contarEmpleadosPorPuestoCS(String puesto) {
        String call = "{CALL contar_empleados_por_puesto(?, ?)}";

        try (Connection conn = abririConexion();
             CallableStatement cs = conn.prepareCall(call)) {

            cs.setString(1, puesto);               // Parámetro de entrada
            cs.registerOutParameter(2, Types.INTEGER); // Parámetro de salida

            cs.execute(); // Ejecuta el procedimiento

            int total = cs.getInt(2); // Recupera el valor del parámetro OUT
            return total;

        } catch (SQLException e) {
            e.printStackTrace();
            return -1; // Devuelve -1 si ocurre un error
        }
    }


    // metodo para llamar un Procedure IN-OUT
    public String aumentarPuestoCS(String puesto, String sufijo) {
        String call = "{CALL aumentar_puesto(?, ?)}";

        try (Connection conn = abririConexion();
             CallableStatement cs = conn.prepareCall(call)) {

            // Parámetro INOUT
            cs.setString(1, puesto);
            cs.registerOutParameter(1, Types.VARCHAR);

            // Parámetro IN
            cs.setString(2, sufijo);

            cs.execute();

            // Recupera el valor actualizado del parámetro INOUT
            String nuevoPuesto = cs.getString(1);
            return nuevoPuesto;

        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    // para cargar todos los puestos en la base de datos, util para ponerlos en un combobox
    public ArrayList<String> obtenerPuestos() {
        ArrayList<String> puestos = new ArrayList<>();
        String query = "SELECT DISTINCT puesto FROM empleados ORDER BY puesto ASC";

        try (Connection conn = abririConexion();
             PreparedStatement ps = conn.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                puestos.add(rs.getString("puesto"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return puestos;
    }
}