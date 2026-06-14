package config;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;

public class DatabaseConfig {
    public static Connection getConnection() {
        try {
            Properties props = new Properties();
            InputStream is = DatabaseConfig.class.getClassLoader().getResourceAsStream("database.properties");

            if (is == null) {
                throw new RuntimeException("File database.properties tidak ditemukan!");
            }
            props.load(is);

            String url = props.getProperty("db.url");
            String user = props.getProperty("db.username");
            String pass = props.getProperty("db.password");

            return DriverManager.getConnection(url, user, pass);
        } catch (Exception e) {
            throw new RuntimeException("Gagal koneksi ke database: " + e.getMessage(), e);
        }
    }
}
