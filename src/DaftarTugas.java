import java.util.List;

public class DaftarTugas {
    private DatabaseManager db;

    public DaftarTugas() {
        db = new DatabaseManager();
    }

    public void tambah(Task task) {
        db.tambahTask(task);
    }

    public List<Task> getSemuaTugas() {
        return db.getAllTasks();
    }

    public List<Task> filterByStatus(boolean selesai) {
        return db.getTasksByStatus(selesai);
    }

    public List<Task> filterByJenis(String jenis) {
        return db.getTasksByJenis(jenis);
    }

    public void update(Task task) {
        db.updateTask(task);
    }

    public void updateStatus(int id, boolean selesai) {
        db.updateStatus(id, selesai);
    }

    public void hapus(int id) {
        db.hapusTask(id);
    }

    public void closeDatabase() {
        db.closeConnection();
    }
}