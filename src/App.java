import com.github.freva.asciitable.AsciiTable;
import com.github.freva.asciitable.Column;
import com.github.freva.asciitable.HorizontalAlign;
import dao.KostumDAOImpl;
import model.Kostum;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;
import service.KostumService;
import service.KostumService.SewaResult;

import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class App {
    private static final Scanner scanner = new Scanner(System.in);
    private static final KostumService service = new KostumService(new KostumDAOImpl());

    public static void main(String[] args) {
        while (true) {
            tampilkanMenu();
            String input = scanner.nextLine().trim();

            switch (input) {
                case "1" -> tambahKostum();
                case "2" -> lihatSemuaKostum();
                case "3" -> cariBerdasarkanId();
                case "4" -> cariBerdasarkanNama();
                case "5" -> updateKostum();
                case "6" -> sewaKostum();
                case "7" -> hapusKostum();
                case "8" -> {
                    System.out.println("Terima kasih telah menggunakan Rental Cosplay Nusantara!");
                    return;
                }
                default -> System.out.println("Pilihan tidak valid. Masukkan 1-8.");
            }
        }
    }

    private static void clearScreen() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

    private static void tampilkanMenu() {
        clearScreen();
        List<String[]> menuData = Arrays.asList(
            new String[]{"1", "Tambah Kostum"},
            new String[]{"2", "Lihat Semua Kostum"},
            new String[]{"3", "Cari Kostum (by ID)"},
            new String[]{"4", "Cari Kostum (by Nama)"},
            new String[]{"5", "Update Kostum"},
            new String[]{"6", "Sewa Kostum"},
            new String[]{"7", "Hapus Kostum"},
            new String[]{"8", "Keluar"}
        );
        System.out.println("\n" + AsciiTable.builder()
            .border(AsciiTable.FANCY_ASCII)
            .data(menuData, Arrays.asList(
                new Column().header("RENTAL COSPLAY NUSANTARA").headerAlign(HorizontalAlign.CENTER).with((String[] r) -> r[0] + ". " + r[1])
            ))
            .asString());
        System.out.print("Pilih [1-8]: ");
    }

    private static boolean konfirmasi(String prompt, boolean defaultYes) {
        if (defaultYes) {
            System.out.print(prompt + " [Y/n]: ");
        } else {
            System.out.print(prompt + " [y/N]: ");
        }
        String input = scanner.nextLine().trim().toLowerCase();
        if (input.isEmpty()) return defaultYes;
        return input.equals("y");
    }

    private static void tambahKostum() {
        System.out.println("\n:: Tambah Kostum Baru");

        String nama = bacaString("Nama Karakter");
        if (nama == null) return;
        String kategori = bacaString("Kategori");
        if (kategori == null) return;
        Double harga = bacaDouble("Harga Sewa/hari");
        if (harga == null) return;

        List<String[]> varian = new java.util.ArrayList<>();
        System.out.println("  Masukkan ukuran & stok (kosongkan ukuran untuk selesai):");
        while (true) {
            String ukuran = bacaString("  Ukuran (S/M/L/XL)");
            if (ukuran == null) break;
            if (varian.stream().anyMatch(v -> v[0].equalsIgnoreCase(ukuran))) {
                System.out.println("  Ukuran " + ukuran + " sudah ada!");
                continue;
            }
            Integer stok = bacaInt("  Stok");
            if (stok == null) break;
            varian.add(new String[]{ukuran.toUpperCase(), String.valueOf(stok)});
        }

        if (varian.isEmpty()) { System.out.println("Dibatalkan."); tekanEnter(); return; }

        System.out.println(tabelKostumVarian(nama, kategori, harga, varian));

        if (!konfirmasi("Tambah " + varian.size() + " varian kostum ini", true)) {
            System.out.println("Dibatalkan."); tekanEnter(); return;
        }

        int count = 0;
        try {
            for (String[] v : varian) {
                Kostum k = new Kostum(nama, v[0], kategori, Integer.parseInt(v[1]), harga, null);
                service.tambahKostum(k);
                count++;
            }
            System.out.println("Berhasil menambah " + count + " varian!");
        } catch (Exception e) {
            System.out.println("Gagal: " + e.getMessage());
        }
        tekanEnter();
    }

    private static void lihatSemuaKostum() {
        System.out.println("\n:: Daftar Semua Kostum");
        try {
            List<Kostum> list = service.ambilSemuaKostum();
            if (list.isEmpty()) {
                System.out.println("Belum ada kostum tersedia.");
            } else {
                System.out.println(tabelKostum(list));
            }
        } catch (Exception e) {
            System.out.println("Gagal: " + e.getMessage());
        }
        tekanEnter();
    }

    private static void cariBerdasarkanId() {
        System.out.println("\n:: Cari Kostum");
        Integer id = bacaInt("ID kostum");
        if (id == null) return;

        try {
            Kostum k = service.cariBerdasarkanId(id);
            if (k == null) {
                System.out.println("Kostum dengan ID " + id + " tidak ditemukan.");
            } else {
                System.out.println(tabelKostum(List.of(k)));
            }
        } catch (Exception e) {
            System.out.println("Gagal: " + e.getMessage());
        }
        tekanEnter();
    }

    private static void cariBerdasarkanNama() {
        System.out.println("\n:: Cari Kostum (ketik untuk mencari, Enter=lihat detail, ESC=batal)");
        System.out.print("  > ");

        StringBuilder query = new StringBuilder();
        int prevResultLines = 0;

        try (Terminal terminal = TerminalBuilder.builder().system(true).build()) {
            terminal.enterRawMode();
            var reader = terminal.reader();

            while (true) {
                int c = reader.read();

                if (c == 10 || c == 13) break;
                if (c == 27) { query.setLength(0); break; }
                if (c == 127 || c == 8) {
                    if (query.length() > 0) query.deleteCharAt(query.length() - 1);
                } else if (c >= 32 && c <= 126) {
                    if (query.length() < 40) query.append((char) c);
                }

                if (prevResultLines > 0) System.out.print("\033[" + prevResultLines + "A");
                System.out.print("\033[J");
                System.out.print("  > " + query);

                if (query.length() > 0) {
                    try {
                        List<Kostum> results = service.cariBerdasarkanNama(query.toString());
                        if (results.isEmpty()) {
                            System.out.print("\n  (tidak ada hasil)");
                            prevResultLines = 1;
                        } else {
                            for (Kostum k : results) {
                                System.out.print("\n  " + k.getId() + ". " + k.getNamaKarakter()
                                    + " (" + k.getUkuran() + ", " + k.getKategori()
                                    + ", stok: " + k.getStok() + ")");
                            }
                            prevResultLines = results.size();
                        }
                    } catch (Exception e) {
                        prevResultLines = 0;
                    }
                } else {
                    prevResultLines = 0;
                }
            }
        } catch (Exception e) {
            System.out.println("Gagal: " + e.getMessage());
            tekanEnter();
            return;
        }

        if (prevResultLines > 0) System.out.print("\033[" + prevResultLines + "A");
        System.out.print("\033[J");

        if (query.length() == 0) {
            System.out.println("  Pencarian dibatalkan.");
            tekanEnter();
            return;
        }

        System.out.println(":: Cari Kostum (by Nama)");
        try {
            List<Kostum> hasil = service.cariBerdasarkanNama(query.toString());
            if (hasil.isEmpty()) {
                System.out.println("  Tidak ditemukan.");
            } else {
                System.out.println("  Ditemukan " + hasil.size() + " hasil:");
                System.out.println(tabelKostum(hasil));
            }
        } catch (Exception e) {
            System.out.println("Gagal: " + e.getMessage());
        }
        tekanEnter();
    }

    private static void updateKostum() {
        System.out.println("\n:: Update Kostum");
        try {
            List<Kostum> semua = service.ambilSemuaKostum();
            System.out.println(tabelKostum(semua));

            Integer id = bacaInt("ID kostum yang akan diupdate");
            if (id == null) return;

            Kostum existing = service.cariBerdasarkanId(id);
            if (existing == null) {
                System.out.println("Kostum dengan ID " + id + " tidak ditemukan.");
                tekanEnter(); return;
            }

            System.out.println("\nData lama:");
            System.out.println(tabelKostum(List.of(existing)));

            String nama = bacaStringOpsional("Nama Karakter", existing.getNamaKarakter());
            String ukuran = bacaStringOpsional("Ukuran", existing.getUkuran());
            String kategori = bacaStringOpsional("Kategori", existing.getKategori());
            String stokStr = bacaStringOpsional("Stok", String.valueOf(existing.getStok()));
            String hargaStr = bacaStringOpsional("Harga Sewa/hari", String.valueOf((int) existing.getHargaSewa()));
            String keterangan = bacaStringOpsional("Keterangan", existing.getKeterangan());

            Kostum updated = new Kostum();
            updated.setId(existing.getId());
            updated.setNamaKarakter(nama);
            updated.setUkuran(ukuran.toUpperCase());
            updated.setKategori(kategori);
            updated.setStok(Integer.parseInt(stokStr));
            updated.setHargaSewa(Double.parseDouble(hargaStr));
            updated.setKeterangan(keterangan);

            if (!konfirmasi("Simpan perubahan", true)) {
                System.out.println("Dibatalkan."); tekanEnter(); return;
            }

            service.updateKostum(updated);
            System.out.println("Kostum berhasil diupdate!");
        } catch (Exception e) {
            System.out.println("Gagal: " + e.getMessage());
        }
        tekanEnter();
    }

    private static void sewaKostum() {
        System.out.println("\n:: Sewa Kostum");
        try {
            List<Kostum> tersedia = service.ambilSemuaKostum().stream()
                .filter(k -> k.getStok() > 0).toList();

            if (tersedia.isEmpty()) {
                System.out.println("Tidak ada kostum yang tersedia.");
                tekanEnter(); return;
            }

            System.out.println("Kostum tersedia:");
            System.out.println(tabelKostum(tersedia));

            Integer id = bacaInt("ID kostum");
            if (id == null) return;

            Kostum k = service.cariBerdasarkanId(id);
            if (k == null || k.getStok() <= 0) {
                System.out.println("Kostum tidak valid atau stok habis.");
                tekanEnter(); return;
            }

            Integer hari = bacaInt("Jumlah hari sewa");
            if (hari == null) return;

            double total = k.getHargaSewa() * hari;
            System.out.println(tabelStruk(
                "Kostum", k.getNamaKarakter(),
                "Kategori", k.getKategori(),
                "Ukuran", k.getUkuran(),
                "Lama Sewa", hari + " hari",
                "Harga/hari", "Rp " + String.format("%,.0f", k.getHargaSewa()),
                "TOTAL", "Rp " + String.format("%,.0f", total)
            ));

            if (!konfirmasi("Konfirmasi sewa", true)) {
                System.out.println("Dibatalkan."); tekanEnter(); return;
            }

            SewaResult result = service.sewaKostum(id, hari);

            System.out.println("\n" + tabelStruk(
                "Kostum", result.getKostum().getNamaKarakter(),
                "Kategori", result.getKostum().getKategori(),
                "Ukuran", result.getKostum().getUkuran(),
                "Lama Sewa", result.getJumlahHari() + " hari",
                "Harga/hari", "Rp " + String.format("%,.0f", result.getKostum().getHargaSewa()),
                "TOTAL", "Rp " + String.format("%,.0f", result.getTotalHarga())
            ));
            System.out.println("Stok tersisa: " + (result.getKostum().getStok() - 1));
        } catch (Exception e) {
            System.out.println("Gagal: " + e.getMessage());
        }
        tekanEnter();
    }

    private static void hapusKostum() {
        System.out.println("\n:: Hapus Kostum");
        try {
            List<Kostum> semua = service.ambilSemuaKostum();
            System.out.println(tabelKostum(semua));

            Integer id = bacaInt("ID kostum yang akan dihapus");
            if (id == null) return;

            Kostum k = service.cariBerdasarkanId(id);
            if (k == null) {
                System.out.println("Kostum dengan ID " + id + " tidak ditemukan.");
                tekanEnter(); return;
            }

            System.out.println(tabelKostum(List.of(k)));

            if (!konfirmasi("Hapus kostum ini", false)) {
                System.out.println("Dibatalkan."); tekanEnter(); return;
            }

            service.hapusKostum(id);
            System.out.println("Kostum berhasil dihapus!");
        } catch (Exception e) {
            System.out.println("Gagal: " + e.getMessage());
        }
        tekanEnter();
    }

    private static String tabelKostum(List<Kostum> list) {
        return AsciiTable.builder()
            .border(AsciiTable.FANCY_ASCII)
            .data(list, Arrays.asList(
                new Column().header("ID").headerAlign(HorizontalAlign.CENTER).with((Kostum k) -> String.valueOf(k.getId())),
                new Column().header("Nama Karakter").with((Kostum k) -> k.getNamaKarakter()),
                new Column().header("Ukuran").headerAlign(HorizontalAlign.CENTER).with((Kostum k) -> k.getUkuran()),
                new Column().header("Kategori").headerAlign(HorizontalAlign.CENTER).with((Kostum k) -> k.getKategori()),
                new Column().header("Stok").headerAlign(HorizontalAlign.CENTER).with((Kostum k) -> String.valueOf(k.getStok())),
                new Column().header("Harga/hari").headerAlign(HorizontalAlign.RIGHT).with((Kostum k) -> "Rp" + (int) k.getHargaSewa()),
                new Column().header("Keterangan").headerAlign(HorizontalAlign.CENTER).with((Kostum k) -> k.getKeterangan())
            ))
            .asString();
    }

    private static String tabelKostumVarian(String nama, String kategori, double harga, List<String[]> varian) {
        List<String[]> data = new java.util.ArrayList<>();
        for (String[] v : varian) {
            data.add(new String[]{nama, kategori, v[0], v[1], "Rp " + String.format("%,.0f", harga)});
        }
        return AsciiTable.builder()
            .border(AsciiTable.FANCY_ASCII)
            .data(data, Arrays.asList(
                new Column().header("Nama").with((String[] r) -> r[0]),
                new Column().header("Kategori").with((String[] r) -> r[1]),
                new Column().header("Ukuran").headerAlign(HorizontalAlign.CENTER).with((String[] r) -> r[2]),
                new Column().header("Stok").headerAlign(HorizontalAlign.CENTER).with((String[] r) -> r[3]),
                new Column().header("Harga/hari").headerAlign(HorizontalAlign.RIGHT).with((String[] r) -> r[4])
            ))
            .asString();
    }

    private static String tabelStruk(String... pasangan) {
        List<String[]> data = new java.util.ArrayList<>();
        for (int i = 0; i < pasangan.length; i += 2) {
            data.add(new String[]{pasangan[i], pasangan[i + 1]});
        }
        return AsciiTable.builder()
            .border(AsciiTable.FANCY_ASCII)
            .data(data, Arrays.asList(
                new Column().with((String[] r) -> r[0]),
                new Column().headerAlign(HorizontalAlign.RIGHT).with((String[] r) -> r[1])
            ))
            .asString();
    }

    private static String bacaString(String prompt) {
        System.out.print("  " + prompt + " : ");
        String input = scanner.nextLine().trim();
        return input.isEmpty() ? null : input;
    }

    private static String bacaStringOpsional(String prompt, String defaultValue) {
        System.out.print("  " + prompt + " [" + defaultValue + "]: ");
        String input = scanner.nextLine().trim();
        return input.isEmpty() ? defaultValue : input;
    }

    private static Integer bacaInt(String prompt) {
        System.out.print("  " + prompt + " : ");
        String input = scanner.nextLine().trim();
        if (input.isEmpty()) return null;
        try {
            return Integer.parseInt(input);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private static Double bacaDouble(String prompt) {
        System.out.print("  " + prompt + " : ");
        String input = scanner.nextLine().trim();
        if (input.isEmpty()) return null;
        try {
            return Double.parseDouble(input);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private static void tekanEnter() {
        System.out.print("\nTekan Enter untuk melanjutkan...");
        scanner.nextLine();
    }
}
