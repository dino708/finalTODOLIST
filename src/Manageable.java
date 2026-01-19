public interface Manageable {
    // Method untuk menandai tugas selesai
    void tandaiSelesai();

    // Method untuk menandai tugas belum selesai
    void tandaiBelumSelesai();

    // Method untuk mengecek apakah tugas sudah lewat deadline
    boolean isOverdue();

    // Method untuk mendapatkan info ringkas tugas
    String getInfoRingkas();
}