package clasesCompartidas;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.io.File;

public abstract class Ranking {

    public static class Entrada {
        public final String nombre;
        public final int    nivel;
        public final int    puntaje;
        public final String fecha;

        public Entrada(String nombre, int nivel, int puntaje, String fecha) {
            this.nombre  = nombre;
            this.nivel   = nivel;
            this.puntaje = puntaje;
            this.fecha   = fecha;
        }

        @Override
        public String toString() {
            return String.format("%-20s  Niv.%-4d  %6d pts  %s", nombre, nivel, puntaje, fecha);
        }
    }

    protected Connection conn = null;
    private final String nombreTabla;
    private static final SimpleDateFormat SDF = new SimpleDateFormat("dd/MM/yyyy HH:mm");

    protected Ranking(String nombreArchivoDB, String nombreTabla) {
        this.nombreTabla = nombreTabla;
        conectar(nombreArchivoDB);
        crearTabla();
    }

    private void conectar(String nombreArchivoDB) {
        try {
            Class.forName("org.sqlite.JDBC");

            // Buscar primero en el classpath (recursos empaquetados)
            java.net.URL url = getClass().getClassLoader().getResource(nombreArchivoDB);
            String urlStr;

            if (url != null) {
                // Existe como recurso empaquetado
                urlStr = "jdbc:sqlite:" + url;
            } else {
                // No existe en classpath: lo creamos en el directorio raíz del proyecto.
                // System.getProperty("user.dir") devuelve el directorio desde donde se lanzó la JVM,
                // que en Gradle/VS Code es la raíz del proyecto.
                String dirRaiz = System.getProperty("user.dir");
                File carpetaDB = new File(dirRaiz, "db");

                // Crear la carpeta db si no existe
                if (!carpetaDB.exists()) {
                    boolean creada = carpetaDB.mkdirs();
                    System.out.println("Ranking: carpeta db " + (creada ? "creada en " : "ya existe en ") + carpetaDB.getAbsolutePath());
                }

                File archivoDB = new File(carpetaDB, new File(nombreArchivoDB).getName());
                urlStr = "jdbc:sqlite:" + archivoDB.getAbsolutePath();
            }

            conn = DriverManager.getConnection(urlStr);
            System.out.println("Ranking: conectado a " + urlStr);

        } catch (ClassNotFoundException e) {
            System.out.println("Ranking: driver SQLite no encontrado — " + e.getMessage());
        } catch (SQLException e) {
            System.out.println("Ranking: error al conectar — " + e.getMessage());
        }
    }

    private void crearTabla() {
        if (conn == null) return;
        String sql = "CREATE TABLE IF NOT EXISTS " + nombreTabla + " ("
                   + "id      INTEGER PRIMARY KEY AUTOINCREMENT, "
                   + "nombre  TEXT    NOT NULL, "
                   + "nivel   INTEGER NOT NULL, "
                   + "puntaje INTEGER NOT NULL, "
                   + "fecha   TEXT    NOT NULL"
                   + ");";
        try (Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            System.out.println("Ranking: error al crear tabla — " + e.getMessage());
        }
    }

    public void guardar(String nombre, int nivel, int puntaje) {
        if (conn == null) return;
        if (nombre == null || nombre.trim().isEmpty()) nombre = "Anónimo";
        String fecha = SDF.format(new Date());
        String sql   = "INSERT INTO " + nombreTabla + " (nombre, nivel, puntaje, fecha) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nombre.trim());
            ps.setInt   (2, nivel);
            ps.setInt   (3, puntaje);
            ps.setString(4, fecha);
            ps.executeUpdate();
            System.out.println("Ranking: guardado — " + nombre + " | nivel " + nivel + " | " + puntaje + " pts");
        } catch (SQLException e) {
            System.out.println("Ranking: error al guardar — " + e.getMessage());
        }
    }

    public ArrayList<Entrada> obtenerTop10() {
        ArrayList<Entrada> lista = new ArrayList<>();
        if (conn == null) return lista;
        String sql = "SELECT nombre, nivel, puntaje, fecha FROM " + nombreTabla
                   + " ORDER BY puntaje DESC LIMIT 10";
        try (Statement stmt = conn.createStatement();
             ResultSet rs   = stmt.executeQuery(sql)) {
            while (rs.next()) {
                lista.add(new Entrada(
                    rs.getString("nombre"),
                    rs.getInt   ("nivel"),
                    rs.getInt   ("puntaje"),
                    rs.getString("fecha")
                ));
            }
        } catch (SQLException e) {
            System.out.println("Ranking: error al obtener top 10 — " + e.getMessage());
        }
        return lista;
    }

    public void cerrar() {
        try {
            if (conn != null && !conn.isClosed()) {
                conn.close();
                System.out.println("Ranking: conexión cerrada.");
            }
        } catch (SQLException e) {
            System.out.println("Ranking: error al cerrar — " + e.getMessage());
        }
    }
}