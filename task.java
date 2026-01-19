import java.time.LocalDate;

public abstract class Task implements Manageable {
    protected int id;
    protected String judul;
    protected String deskripsi;
    protected LocalDate deadline;
    protected boolean selesai;

    public Task(String judul, String deskripsi, LocalDate deadline) {
        this.judul = judul;
        this.deskripsi = deskripsi;
        this.deadline = deadline;
        this.selesai = false;
    }

    public Task(int id, String judul, String deskripsi, LocalDate deadline, boolean selesai) {
        this.id = id;
        this.judul = judul;
        this.deskripsi = deskripsi;
        this.deadline = deadline;
        this.selesai = selesai;
    }

    // Abstract method untuk polimorfisme
    public abstract String getJenis();
    public abstract String getIcon();

    // ===== IMPLEMENTASI INTERFACE MANAGEABLE =====
    @Override
    public void tandaiSelesai() {
        this.selesai = true;
    }

    @Override
    public void tandaiBelumSelesai() {
        this.selesai = false;
    }

    @Override
    public boolean isOverdue() {
        return !selesai && deadline.isBefore(LocalDate.now());
    }

    @Override
    public String getInfoRingkas() {
        String status = selesai ? "SELESAI" : (isOverdue() ? "TERLAMBAT" : "PENDING");
        return String.format("[%s] %s - %s (%s)", getJenis(), judul, deadline, status);
    }
    // ===== END IMPLEMENTASI INTERFACE =====

    // Getters dan Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getJudul() { return judul; }
    public void setJudul(String judul) { this.judul = judul; }
    public String getDeskripsi() { return deskripsi; }
    public void setDeskripsi(String deskripsi) { this.deskripsi = deskripsi; }
    public LocalDate getDeadline() { return deadline; }
    public void setDeadline(LocalDate deadline) { this.deadline = deadline; }
    public boolean isSelesai() { return selesai; }
    public void setSelesai(boolean selesai) { this.selesai = selesai; }

    public String getStatus() {
        return selesai ? "Selesai" : "Belum Selesai";
    }

    @Override
    public String toString() {
        return getIcon() + " " + getInfoRingkas();
    }
}
