import java.util.List;

public class Laporan {
    private DaftarTugas daftarTugas;

    public Laporan(DaftarTugas daftarTugas) {
        this.daftarTugas = daftarTugas;
    }

    public String generateLaporan() {
        List<Task> semuaTugas = daftarTugas.getSemuaTugas();
        List<Task> tugasSelesai = daftarTugas.filterByStatus(true);
        List<Task> tugasBelumSelesai = daftarTugas.filterByStatus(false);

        StringBuilder sb = new StringBuilder();
        sb.append("═══════════════════════════════════════\n");
        sb.append("           LAPORAN TUGAS\n");
        sb.append("═══════════════════════════════════════\n\n");
        sb.append("Total Tugas: ").append(semuaTugas.size()).append("\n");
        sb.append("Tugas Selesai: ").append(tugasSelesai.size()).append("\n");
        sb.append("Tugas Belum Selesai: ").append(tugasBelumSelesai.size()).append("\n\n");

        // Persentase penyelesaian
        double persentase = semuaTugas.isEmpty() ? 0 :
                (double) tugasSelesai.size() / semuaTugas.size() * 100;
        sb.append(String.format("Persentase Selesai: %.1f%%\n\n", persentase));

        // Detail per jenis
        sb.append("─────────────────────────────────────\n");
        sb.append("DETAIL PER JENIS:\n");
        sb.append("─────────────────────────────────────\n");

        String[] jenisArray = {"Pekerjaan", "Kuliah", "Pribadi"};
        for (String jenis : jenisArray) {
            List<Task> tugasJenis = daftarTugas.filterByJenis(jenis);
            long selesaiJenis = tugasJenis.stream().filter(Task::isSelesai).count();
            sb.append(String.format("%-12s: %d tugas (%d selesai)\n",
                    jenis, tugasJenis.size(), selesaiJenis));
        }

        sb.append("\n═══════════════════════════════════════\n");

        return sb.toString();
    }
}