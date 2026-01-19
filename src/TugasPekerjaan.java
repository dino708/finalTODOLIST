import java.time.LocalDate;

public class TugasPekerjaan extends Task {

    public TugasPekerjaan(String judul, String deskripsi, LocalDate deadline) {
        super(judul, deskripsi, deadline);
    }

    public TugasPekerjaan(int id, String judul, String deskripsi, LocalDate deadline, boolean selesai) {
        super(id, judul, deskripsi, deadline, selesai);
    }

    @Override
    public String getJenis() {
        return "Pekerjaan";
    }

    @Override
    public String getIcon() {
        return "💼";
    }
}