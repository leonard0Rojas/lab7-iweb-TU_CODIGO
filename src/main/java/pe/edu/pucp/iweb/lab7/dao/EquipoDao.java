package pe.edu.pucp.iweb.lab7.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import pe.edu.pucp.iweb.lab7.beans.EquipoRed;
import pe.edu.pucp.iweb.lab7.beans.EquipoSwitch;
import pe.edu.pucp.iweb.lab7.beans.EquipoAP;
import pe.edu.pucp.iweb.lab7.beans.Sede;

public class EquipoDao extends BaseDao{ // TODO 2a: debe heredar de BaseDao

    /**
     * Ejercicio 2c: retorna los equipos de la sede indicada, ordenados por sede y modelo.
     * Si idSede es 0, retorna todos los equipos.
     *
     * Según la columna tipo, cree un objeto EquipoSwitch ('SWITCH') o EquipoAP ('AP').
     * Use PreparedStatement.
     */
    public ArrayList<EquipoRed> listarPorSede(int idSede) {
        ArrayList<EquipoRed> lista = new ArrayList<>();
        // TODO 2c
        String sql =
                "SELECT e.idequipo, e.modelo, e.tipo, " +
                        "e.precio, e.cantidad, s.idsede, s.nombre " +
                        "FROM equipo e " +
                        "INNER JOIN sede s ON e.idsede = s.idsede " +
                        "WHERE (? = 0 OR e.idsede = ?) " +
                        "ORDER BY s.nombre, e.modelo";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            // Colocamos el valor de idSede en ambos parámetros.
            ps.setInt(1, idSede);
            ps.setInt(2, idSede);

            try (ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {

                    String tipo = rs.getString("tipo");
                    EquipoRed equipo;

                    // Creamos el objeto correspondiente.
                    if ("SWITCH".equals(tipo)) {
                        equipo = new EquipoSwitch();

                    } else if ("AP".equals(tipo)) {
                        equipo = new EquipoAP();

                    } else {
                        throw new SQLException(
                                "Tipo no reconocido: " + tipo);
                    }

                    // Asignamos los datos del equipo.
                    equipo.setIdEquipo(rs.getInt("idequipo"));
                    equipo.setModelo(rs.getString("modelo"));
                    equipo.setTipo(tipo);
                    equipo.setPrecio(rs.getDouble("precio"));
                    equipo.setCantidad(rs.getInt("cantidad"));

                    // Asociamos el equipo a su sede.
                    Sede sede = new Sede(
                            rs.getInt("idsede"),
                            rs.getString("nombre")
                    );

                    equipo.setSede(sede);

                    // Guardamos el equipo en la lista.
                    lista.add(equipo);
                }
            }
        } catch (SQLException e) {
                throw new RuntimeException(
                        "Error al listar los equipos", e);
        }
        return lista;
    }
}
