package service;

import dao.KostumDAO;
import model.Kostum;
import java.util.List;

public class KostumService {
    private final KostumDAO kostumDAO;

    public KostumService(KostumDAO kostumDAO) {
        this.kostumDAO = kostumDAO;
    }

    public void tambahKostum(Kostum kostum) {
        if (kostum.getNamaKarakter() == null || kostum.getNamaKarakter().trim().isEmpty())
            throw new IllegalArgumentException("Nama karakter tidak boleh kosong!");
        if (kostum.getUkuran() == null || kostum.getUkuran().trim().isEmpty())
            throw new IllegalArgumentException("Ukuran tidak boleh kosong!");
        if (kostum.getKategori() == null || kostum.getKategori().trim().isEmpty())
            throw new IllegalArgumentException("Kategori tidak boleh kosong!");
        if (kostum.getStok() < 0)
            throw new IllegalArgumentException("Stok tidak boleh negatif!");
        if (kostum.getHargaSewa() < 0)
            throw new IllegalArgumentException("Harga sewa tidak boleh negatif!");

        kostum.setKeterangan("Tersedia");
        kostumDAO.tambahKostum(kostum);
    }

    public void updateKostum(Kostum kostum) {
        if (kostum.getId() <= 0)
            throw new IllegalArgumentException("ID tidak valid!");
        if (kostum.getNamaKarakter() == null || kostum.getNamaKarakter().trim().isEmpty())
            throw new IllegalArgumentException("Nama karakter tidak boleh kosong!");
        if (kostum.getStok() < 0)
            throw new IllegalArgumentException("Stok tidak boleh negatif!");
        if (kostum.getHargaSewa() < 0)
            throw new IllegalArgumentException("Harga sewa tidak boleh negatif!");

        Kostum existing = kostumDAO.cariBerdasarkanId(kostum.getId());
        if (existing == null)
            throw new IllegalArgumentException("Kostum dengan ID " + kostum.getId() + " tidak ditemukan!");

        kostumDAO.updateKostum(kostum);
    }

    public SewaResult sewaKostum(int id, int jumlahHari) {
        if (id <= 0) throw new IllegalArgumentException("ID tidak valid!");
        if (jumlahHari <= 0) throw new IllegalArgumentException("Jumlah hari harus lebih dari 0!");

        Kostum kostum = kostumDAO.cariBerdasarkanId(id);
        if (kostum == null) throw new IllegalArgumentException("Kostum tidak ditemukan!");
        if (kostum.getStok() <= 0) throw new IllegalArgumentException("Stok kostum habis!");

        double totalHarga = kostum.getHargaSewa() * jumlahHari;
        kostumDAO.sewaKostum(id, jumlahHari);

        return new SewaResult(kostum, jumlahHari, totalHarga);
    }

    public List<Kostum> ambilSemuaKostum() {
        return kostumDAO.ambilSemuaKostum();
    }

    public Kostum cariBerdasarkanId(int id) {
        if (id <= 0) throw new IllegalArgumentException("ID harus lebih dari 0!");
        return kostumDAO.cariBerdasarkanId(id);
    }

    public List<Kostum> cariBerdasarkanNama(String nama) {
        if (nama == null || nama.trim().isEmpty())
            throw new IllegalArgumentException("Nama tidak boleh kosong!");
        return kostumDAO.cariBerdasarkanNama(nama.trim());
    }

    public void hapusKostum(int id) {
        if (id <= 0) throw new IllegalArgumentException("ID harus lebih dari 0!");
        Kostum existing = kostumDAO.cariBerdasarkanId(id);
        if (existing == null) throw new IllegalArgumentException("Kostum dengan ID " + id + " tidak ditemukan!");
        kostumDAO.hapusKostum(id);
    }

    public static class SewaResult {
        private final Kostum kostum;
        private final int jumlahHari;
        private final double totalHarga;

        public SewaResult(Kostum kostum, int jumlahHari, double totalHarga) {
            this.kostum = kostum;
            this.jumlahHari = jumlahHari;
            this.totalHarga = totalHarga;
        }

        public Kostum getKostum() { return kostum; }
        public int getJumlahHari() { return jumlahHari; }
        public double getTotalHarga() { return totalHarga; }
    }
}
