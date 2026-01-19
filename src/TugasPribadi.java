import java.time.LocalDate;

public class TugasPribadi extends Task {

    public TugasPribadi(String judul, String deskripsi, LocalDate deadline) {
        super(judul, deskripsi, deadline);
    }

    public TugasPribadi(int id, String judul, String deskripsi, LocalDate deadline, boolean selesai) {
        super(id, judul, deskripsi, deadline, selesai);
    }

    @Override
    public String getJenis() {
        return "Pribadi";
    }

    @Override
    public String getIcon() {
        return "🏠";
    }
}