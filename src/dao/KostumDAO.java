package dao;

import model.Kostum;
import java.util.List;

public interface KostumDAO {
    void tambahKostum(Kostum kostum);
    void updateKostum(Kostum kostum);
    void sewaKostum(int id, int jumlahHari);
    List<Kostum> ambilSemuaKostum();
    List<Kostum> cariBerdasarkanNama(String nama);
    Kostum cariBerdasarkanId(int id);
    void hapusKostum(int id);
}
