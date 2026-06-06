package clasesCompartidas;
 
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
 
/**
 * Clase base compartida para el sistema de ranking con SQLite.
 * Cada juego debe extenderla y proporcionar el nombre de su tabla.
 *
 * Cada entrada contiene: nombre del jugador, nivel alcanzado, puntaje y fecha.
 */
public abstract class Ranking {
 
    // ── Entrada del ranking ───────────────────────────────────────────────────
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
 
    // ── Estado interno ────────────────────────────────────────────────────────
    protected Connection conn = null;
    private   final String rutaDB;
    private   final String nombreTabla;
    private   static final SimpleDateFormat SDF = new SimpleDateFormat("dd/MM/yyyy HH:mm");
 
    /**
     * @param rutaDB       Ruta al archivo .db dentro del classpath (ej: "db/ranking.db")
     * @param nombreTabla  Nombre de la tabla SQL a usar (ej: "ranking_space")
     */
    protected Ranking(String rutaDB, String nombreTabla) {
        this.rutaDB      = rutaDB;
        this.nombreTabla = nombreTabla;
        conectar();
        crearTabla();
    }
 
    // ── Conexión ──────────────────────────────────────────────────────────────
 
    private void conectar() {
        try {
            // FORZAR LA CARGA DEL DRIVER (Agregá esta línea para solucionar fallas en entornos de ejecución rápidos)
            Class.forName("org.sqlite.JDBC");

            // Intentamos abrir el archivo desde el classpath
            java.net.URL url = getClass().getClassLoader().getResource(rutaDB);
            String urlStr;
            if (url != null) {
                urlStr = "jdbc:sqlite:" + url;
            } else {
                // Si no existe en classpath, lo creamos en el directorio de trabajo
                urlStr = "jdbc:sqlite:" + rutaDB;
            }
            conn = DriverManager.getConnection(urlStr);
            System.out.println("Ranking: conectado a " + urlStr);
        } catch (ClassNotFoundException e) {
            System.out.println("Ranking: No se encontró la clase del Driver de SQLite — " + e.getMessage());
        } catch (SQLException e) {
            System.out.println("Ranking: error al conectar con la base de datos — " + e.getMessage());
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
 
    // ── API pública ───────────────────────────────────────────────────────────
 
    /**
     * Guarda una nueva entrada en el ranking.
     */
    public void guardar(String nombre, int nivel, int puntaje) {
        if (conn == null) return;
        String fecha = SDF.format(new Date());
        String sql   = "INSERT INTO " + nombreTabla + " (nombre, nivel, puntaje, fecha) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nombre.trim().isEmpty() ? "Anónimo" : nombre.trim());
            ps.setInt   (2, nivel);
            ps.setInt   (3, puntaje);
            ps.setString(4, fecha);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Ranking: error al guardar — " + e.getMessage());
        }
    }
 
    /**
     * Devuelve los 10 mejores puntajes, ordenados de mayor a menor.
     */
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
 
    /**
     * Cierra la conexión con la base de datos.
     * Llamar desde gameShutdown() del juego.
     */
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
 
