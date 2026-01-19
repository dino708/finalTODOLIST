import java.time.LocalDate;

public class TugasKuliah extends Task {

    public TugasKuliah(String judul, String deskripsi, LocalDate deadline) {
        super(judul, deskripsi, deadline);
    }

    public TugasKuliah(int id, String judul, String deskripsi, LocalDate deadline, boolean selesai) {
        super(id, judul, deskripsi, deadline, selesai);
    }

    @Override
    public String getJenis() {
        return "Kuliah";
    }

    @Override
    public String getIcon() {
        return "📚";
    }
}