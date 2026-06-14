package model;

public class Kostum {
    private int id;
    private String namaKarakter;
    private String ukuran;
    private String kategori;
    private int stok;
    private double hargaSewa;
    private String keterangan;

    public Kostum() {}

    public Kostum(String namaKarakter, String ukuran, String kategori, int stok, double hargaSewa, String keterangan) {
        this.namaKarakter = namaKarakter;
        this.ukuran = ukuran;
        this.kategori = kategori;
        this.stok = stok;
        this.hargaSewa = hargaSewa;
        this.keterangan = keterangan;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getNamaKarakter() { return namaKarakter; }
    public void setNamaKarakter(String namaKarakter) { this.namaKarakter = namaKarakter; }
    public String getUkuran() { return ukuran; }
    public void setUkuran(String ukuran) { this.ukuran = ukuran; }
    public String getKategori() { return kategori; }
    public void setKategori(String kategori) { this.kategori = kategori; }
    public int getStok() { return stok; }
    public void setStok(int stok) { this.stok = stok; }
    public double getHargaSewa() { return hargaSewa; }
    public void setHargaSewa(double hargaSewa) { this.hargaSewa = hargaSewa; }
    public String getKeterangan() { return keterangan; }
    public void setKeterangan(String keterangan) { this.keterangan = keterangan; }
}
