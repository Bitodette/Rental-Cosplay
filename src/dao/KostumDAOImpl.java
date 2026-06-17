package dao;

import config.DatabaseConfig;
import model.Kostum;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class KostumDAOImpl implements KostumDAO {

    @Override
    public void tambahKostum(Kostum kostum) {
        String sql = "INSERT INTO kostum (nama_karakter, ukuran, kategori, stok, harga_sewa, keterangan) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, kostum.getNamaKarakter());
            stmt.setString(2, kostum.getUkuran());
            stmt.setString(3, kostum.getKategori());
            stmt.setInt(4, kostum.getStok());
            stmt.setDouble(5, kostum.getHargaSewa());
            stmt.setString(6, kostum.getKeterangan());
            stmt.executeUpdate();

            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) kostum.setId(rs.getInt(1));
        } catch (SQLException e) {
            throw new RuntimeException("Gagal menambah kostum: " + e.getMessage(), e);
        }
    }

    @Override
    public void updateKostum(Kostum kostum) {
        String sql = "UPDATE kostum SET nama_karakter=?, ukuran=?, kategori=?, stok=?, harga_sewa=?, keterangan=? WHERE id=?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, kostum.getNamaKarakter());
            stmt.setString(2, kostum.getUkuran());
            stmt.setString(3, kostum.getKategori());
            stmt.setInt(4, kostum.getStok());
            stmt.setDouble(5, kostum.getHargaSewa());
            stmt.setString(6, kostum.getKeterangan());
            stmt.setInt(7, kostum.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Gagal update kostum: " + e.getMessage(), e);
        }
    }

    @Override
    public void sewaKostum(int id, int jumlahHari) {
        String sql = "UPDATE kostum SET stok = stok - 1 WHERE id = ? AND stok > 0";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            int affected = stmt.executeUpdate();
            if (affected == 0) {
                throw new RuntimeException("Stok tidak mencukupi atau kostum tidak ditemukan!");
            }

            try (PreparedStatement stmt2 = conn.prepareStatement(
                    "UPDATE kostum SET keterangan = 'Stok Habis' WHERE id = ? AND stok = 0")) {
                stmt2.setInt(1, id);
                stmt2.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Gagal memproses sewa: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Kostum> ambilSemuaKostum() {
        List<Kostum> daftar = new ArrayList<>();
        String sql = "SELECT * FROM kostum ORDER BY id";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                daftar.add(petakanKostum(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Gagal mengambil data kostum: " + e.getMessage(), e);
        }
        return daftar;
    }

    @Override
    public Kostum cariBerdasarkanId(int id) {
        String sql = "SELECT * FROM kostum WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return petakanKostum(rs);
        } catch (SQLException e) {
            throw new RuntimeException("Gagal mencari kostum: " + e.getMessage(), e);
        }
        return null;
    }

    @Override
    public List<Kostum> cariBerdasarkanNama(String nama) {
        List<Kostum> daftar = new ArrayList<>();
        String sql = "SELECT * FROM kostum WHERE nama_karakter LIKE ? ORDER BY id";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, "%" + nama + "%");
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                daftar.add(petakanKostum(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Gagal mencari kostum: " + e.getMessage(), e);
        }
        return daftar;
    }

    @Override
    public void hapusKostum(int id) {
        String sql = "DELETE FROM kostum WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            int affected = stmt.executeUpdate();
            if (affected == 0) {
                throw new RuntimeException("Kostum dengan ID " + id + " tidak ditemukan.");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Gagal menghapus kostum: " + e.getMessage(), e);
        }
    }

    private Kostum petakanKostum(ResultSet rs) throws SQLException {
        Kostum k = new Kostum();
        k.setId(rs.getInt("id"));
        k.setNamaKarakter(rs.getString("nama_karakter"));
        k.setUkuran(rs.getString("ukuran"));
        k.setKategori(rs.getString("kategori"));
        int stok = rs.getInt("stok");
        k.setStok(stok);
        k.setHargaSewa(rs.getDouble("harga_sewa"));
        k.setKeterangan(stok == 0 ? "Stok Habis" : "Tersedia");
        return k;
    }
}
