CREATE DATABASE IF NOT EXISTS rental_cosplay;
USE rental_cosplay;

DROP TABLE IF EXISTS kostum;

CREATE TABLE kostum (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nama_karakter VARCHAR(100) NOT NULL,
    ukuran VARCHAR(10) NOT NULL,
    kategori VARCHAR(50) NOT NULL DEFAULT 'Anime',
    stok INT NOT NULL DEFAULT 1 CHECK (stok >= 0),
    harga_sewa DOUBLE NOT NULL CHECK (harga_sewa >= 0),
    keterangan VARCHAR(50) DEFAULT 'Tersedia',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

INSERT INTO kostum (nama_karakter, ukuran, kategori, stok, harga_sewa, keterangan) VALUES
('Gojo Satoru', 'L', 'Anime', 3, 150000, 'Tersedia'),
('Hatsune Miku', 'M', 'Game', 2, 120000, 'Tersedia'),
('Spider-Man', 'XL', 'Superhero', 1, 200000, 'Tersedia'),
('Naruto Uzumaki', 'M', 'Anime', 4, 100000, 'Tersedia'),
('Pikachu', 'S', 'Game', 2, 80000, 'Tersedia'),
('Iron Man', 'L', 'Superhero', 1, 250000, 'Tersedia'),
('Elsa (Frozen)', 'M', 'Fantasy', 2, 130000, 'Tersedia'),
('Kirito (SAO)', 'L', 'Anime', 2, 140000, 'Tersedia'),
('Mario', 'M', 'Game', 3, 90000, 'Tersedia'),
('Wonder Woman', 'XL', 'Superhero', 1, 180000, 'Tersedia');

SELECT * FROM kostum;
