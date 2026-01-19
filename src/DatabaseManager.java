import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class DatabaseManager {
    private static final String DB_URL = "jdbc:sqlite:tasks.db";
    private Connection connection;

    public DatabaseManager() {
        connect();
        createTable();
    }

    private void connect() {
        try {
            connection = DriverManager.getConnection(DB_URL);
            System.out.println("Koneksi ke database berhasil!");
        } catch (SQLException e) {
            System.out.println("Error koneksi: " + e.getMessage());
        }
    }

    private void createTable() {
        String sql = """
            CREATE TABLE IF NOT EXISTS tasks (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                judul TEXT NOT NULL,
                deskripsi TEXT,
                deadline TEXT,
                jenis TEXT,
                selesai INTEGER DEFAULT 0
            )
        """;

        try (Statement stmt = connection.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            System.out.println("Error membuat tabel: " + e.getMessage());
        }
    }

    // CREATE
    public void tambahTask(Task task) {
        String sql = "INSERT INTO tasks(judul, deskripsi, deadline, jenis, selesai) VALUES(?,?,?,?,?)";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, task.getJudul());
            pstmt.setString(2, task.getDeskripsi());
            pstmt.setString(3, task.getDeadline().toString());
            pstmt.setString(4, task.getJenis());
            pstmt.setInt(5, task.isSelesai() ? 1 : 0);
            pstmt.executeUpdate();
            System.out.println("Task berhasil ditambahkan!");
        } catch (SQLException e) {
            System.out.println("Error menambah task: " + e.getMessage());
        }
    }

    // READ - Semua Task
    public List<Task> getAllTasks() {
        List<Task> tasks = new ArrayList<>();
        String sql = "SELECT * FROM tasks ORDER BY deadline";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Task task = createTaskFromResultSet(rs);
                tasks.add(task);
            }
        } catch (SQLException e) {
            System.out.println("Error membaca tasks: " + e.getMessage());
        }
        return tasks;
    }

    // READ - Filter by Status
    public List<Task> getTasksByStatus(boolean selesai) {
        List<Task> tasks = new ArrayList<>();
        String sql = "SELECT * FROM tasks WHERE selesai = ? ORDER BY deadline";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, selesai ? 1 : 0);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Task task = createTaskFromResultSet(rs);
                tasks.add(task);
            }
        } catch (SQLException e) {
            System.out.println("Error filter tasks: " + e.getMessage());
        }
        return tasks;
    }

    // READ - Filter by Jenis
    public List<Task> getTasksByJenis(String jenis) {
        List<Task> tasks = new ArrayList<>();
        String sql = "SELECT * FROM tasks WHERE jenis = ? ORDER BY deadline";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, jenis);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Task task = createTaskFromResultSet(rs);
                tasks.add(task);
            }
        } catch (SQLException e) {
            System.out.println("Error filter tasks: " + e.getMessage());
        }
        return tasks;
    }

    // UPDATE
    public void updateTask(Task task) {
        String sql = "UPDATE tasks SET judul = ?, deskripsi = ?, deadline = ?, jenis = ?, selesai = ? WHERE id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, task.getJudul());
            pstmt.setString(2, task.getDeskripsi());
            pstmt.setString(3, task.getDeadline().toString());
            pstmt.setString(4, task.getJenis());
            pstmt.setInt(5, task.isSelesai() ? 1 : 0);
            pstmt.setInt(6, task.getId());
            pstmt.executeUpdate();
            System.out.println("Task berhasil diupdate!");
        } catch (SQLException e) {
            System.out.println("Error update task: " + e.getMessage());
        }
    }

    // UPDATE Status
    public void updateStatus(int id, boolean selesai) {
        String sql = "UPDATE tasks SET selesai = ? WHERE id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, selesai ? 1 : 0);
            pstmt.setInt(2, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error update status: " + e.getMessage());
        }
    }

    // DELETE
    public void hapusTask(int id) {
        String sql = "DELETE FROM tasks WHERE id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
            System.out.println("Task berhasil dihapus!");
        } catch (SQLException e) {

            System.out.println("Error hapus task: " + e.getMessage());
        }
    }

    private Task createTaskFromResultSet(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        String judul = rs.getString("judul");
        String deskripsi = rs.getString("deskripsi");
        LocalDate deadline = LocalDate.parse(rs.getString("deadline"));
        String jenis = rs.getString("jenis");
        boolean selesai = rs.getInt("selesai") == 1;

        return switch (jenis) {
            case "Pekerjaan" -> new TugasPekerjaan(id, judul, deskripsi, deadline, selesai);
            case "Kuliah" -> new TugasKuliah(id, judul, deskripsi, deadline, selesai);
            case "Pribadi" -> new TugasPribadi(id, judul, deskripsi, deadline, selesai);
            default -> new TugasPribadi(id, judul, deskripsi, deadline, selesai);
        };
    }

    public void closeConnection() {
        try {
            if (connection != null) {
                connection.close();
            }
        } catch (SQLException e) {
            System.out.println("Error menutup koneksi: " + e.getMessage());
        }
    }
}