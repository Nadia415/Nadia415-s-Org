-- ====================================================================
-- SISTEM INFORMASI KOPERASI SEKOLAH SMP NURUL ANSOR
-- Skema Database Relasional MySQL dengan Foreign Key (22 Tabel)
-- Siap Import ke phpMyAdmin / MySQL Server (XAMPP)
-- ====================================================================

CREATE DATABASE IF NOT EXISTS `koperasi_smp_nurul_ansor` 
DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE `koperasi_smp_nurul_ansor`;

-- 1. Tabel users (Autentikasi Pengguna & Hak Akses)
CREATE TABLE `users` (
  `id` INT AUTO_INCREMENT PRIMARY KEY,
  `username` VARCHAR(50) NOT NULL UNIQUE,
  `password` VARCHAR(255) NOT NULL,
  `role` ENUM('admin', 'guru') NOT NULL DEFAULT 'guru',
  `guru_id` INT NULL,
  `nama_lengkap` VARCHAR(150) NOT NULL,
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

-- 2. Tabel guru (Master Data Anggota Guru)
CREATE TABLE `guru` (
  `id` INT AUTO_INCREMENT PRIMARY KEY,
  `nip` VARCHAR(30) NOT NULL UNIQUE,
  `nama_lengkap` VARCHAR(150) NOT NULL,
  `jenis_kelamin` ENUM('Laki-laki', 'Perempuan') NOT NULL,
  `alamat` TEXT NOT NULL,
  `no_hp` VARCHAR(25) NOT NULL,
  `tanggal_bergabung` DATE NOT NULL,
  `status_aktif` BOOLEAN DEFAULT TRUE,
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

-- Tambahkan FK users ke guru
ALTER TABLE `users` 
ADD CONSTRAINT `fk_users_guru` 
FOREIGN KEY (`guru_id`) REFERENCES `guru`(`id`) ON DELETE CASCADE;

-- 3. Tabel iuran (Iuran Wajib Rp 30.000 / bulan)
CREATE TABLE `iuran` (
  `id` INT AUTO_INCREMENT PRIMARY KEY,
  `guru_id` INT NOT NULL,
  `bulan` TINYINT NOT NULL,
  `tahun` SMALLINT NOT NULL,
  `nominal` DECIMAL(12,2) NOT NULL DEFAULT 30000.00,
  `status_lunas` BOOLEAN DEFAULT FALSE,
  `tanggal_bayar` DATE NULL,
  `no_kwitansi` VARCHAR(50) NULL,
  `keterangan` TEXT,
  FOREIGN KEY (`guru_id`) REFERENCES `guru`(`id`) ON DELETE CASCADE,
  UNIQUE KEY `unique_guru_bulan_tahun` (`guru_id`, `bulan`, `tahun`)
) ENGINE=InnoDB;

-- 4. Tabel simpanan (Rekening Saldo Anggota)
CREATE TABLE `simpanan` (
  `id` INT AUTO_INCREMENT PRIMARY KEY,
  `guru_id` INT NOT NULL UNIQUE,
  `saldo` DECIMAL(15,2) NOT NULL DEFAULT 0.00,
  `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  FOREIGN KEY (`guru_id`) REFERENCES `guru`(`id`) ON DELETE CASCADE
) ENGINE=InnoDB;

-- 5. Tabel transaksi_simpanan (Setoran, Penarikan, dan Bunga 2%)
CREATE TABLE `transaksi_simpanan` (
  `id` INT AUTO_INCREMENT PRIMARY KEY,
  `guru_id` INT NOT NULL,
  `jenis` ENUM('SETORAN', 'PENARIKAN', 'BUNGA') NOT NULL,
  `nominal` DECIMAL(12,2) NOT NULL,
  `saldo_setelah` DECIMAL(15,2) NOT NULL,
  `tanggal` DATE NOT NULL,
  `keterangan` TEXT NOT NULL,
  `dasar_perhitungan_bunga` TEXT NULL,
  FOREIGN KEY (`guru_id`) REFERENCES `guru`(`id`) ON DELETE CASCADE
) ENGINE=InnoDB;

-- 6. Tabel pinjaman (Pengajuan Pinjaman & Bunga Flat 2%)
CREATE TABLE `pinjaman` (
  `id` INT AUTO_INCREMENT PRIMARY KEY,
  `no_pinjaman` VARCHAR(50) NOT NULL UNIQUE,
  `guru_id` INT NOT NULL,
  `tanggal_pengajuan` DATE NOT NULL,
  `jumlah_pinjaman` DECIMAL(15,2) NOT NULL,
  `bunga_persen` DECIMAL(5,2) NOT NULL DEFAULT 2.00,
  `lama_angsuran_bulan` INT NOT NULL,
  `total_bunga` DECIMAL(15,2) NOT NULL,
  `total_pinjaman` DECIMAL(15,2) NOT NULL,
  `angsuran_per_bulan` DECIMAL(15,2) NOT NULL,
  `tanggal_mulai_angsuran` DATE NOT NULL,
  `status` ENUM('MENUNGGU', 'DISETUJUI', 'DITOLAK', 'LUNAS') DEFAULT 'MENUNGGU',
  `catatan_admin` TEXT,
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (`guru_id`) REFERENCES `guru`(`id`) ON DELETE CASCADE
) ENGINE=InnoDB;

-- 7. Tabel angsuran (Jadwal Angsuran Bulanan)
CREATE TABLE `angsuran` (
  `id` INT AUTO_INCREMENT PRIMARY KEY,
  `pinjaman_id` INT NOT NULL,
  `guru_id` INT NOT NULL,
  `angsuran_ke` INT NOT NULL,
  `nominal_angsuran` DECIMAL(15,2) NOT NULL,
  `tanggal_jatuh_tempo` DATE NOT NULL,
  `status` ENUM('BELUM_BAYAR', 'LUNAS', 'TERLAMBAT') DEFAULT 'BELUM_BAYAR',
  `tanggal_bayar` DATE NULL,
  `sisa_pinjaman` DECIMAL(15,2) NOT NULL DEFAULT 0.00,
  `no_kwitansi` VARCHAR(50) NULL,
  FOREIGN KEY (`pinjaman_id`) REFERENCES `pinjaman`(`id`) ON DELETE CASCADE,
  FOREIGN KEY (`guru_id`) REFERENCES `guru`(`id`) ON DELETE CASCADE
) ENGINE=InnoDB;

-- 8. Tabel pembayaran_angsuran
CREATE TABLE `pembayaran_angsuran` (
  `id` INT AUTO_INCREMENT PRIMARY KEY,
  `angsuran_id` INT NOT NULL,
  `tanggal_bayar` DATE NOT NULL,
  `jumlah_bayar` DECIMAL(15,2) NOT NULL,
  `metode` VARCHAR(50) DEFAULT 'TUNAI',
  `no_kwitansi` VARCHAR(50) NOT NULL,
  FOREIGN KEY (`angsuran_id`) REFERENCES `angsuran`(`id`) ON DELETE CASCADE
) ENGINE=InnoDB;

-- 9. Tabel denda (Denda Keterlambatan Rp 10.000 toleransi 7 hari)
CREATE TABLE `denda` (
  `id` INT AUTO_INCREMENT PRIMARY KEY,
  `angsuran_id` INT NOT NULL UNIQUE,
  `pinjaman_id` INT NOT NULL,
  `guru_id` INT NOT NULL,
  `nominal_denda` DECIMAL(12,2) NOT NULL DEFAULT 10000.00,
  `status_denda` ENUM('BELUM_BAYAR', 'LUNAS') DEFAULT 'BELUM_BAYAR',
  `tanggal_dikenakan` DATE NOT NULL,
  `tanggal_bayar` DATE NULL,
  `keterangan` TEXT,
  FOREIGN KEY (`angsuran_id`) REFERENCES `angsuran`(`id`) ON DELETE CASCADE,
  FOREIGN KEY (`pinjaman_id`) REFERENCES `pinjaman`(`id`) ON DELETE CASCADE,
  FOREIGN KEY (`guru_id`) REFERENCES `guru`(`id`) ON DELETE CASCADE
) ENGINE=InnoDB;

-- 10. Tabel pembayaran_denda
CREATE TABLE `pembayaran_denda` (
  `id` INT AUTO_INCREMENT PRIMARY KEY,
  `denda_id` INT NOT NULL,
  `tanggal_bayar` DATE NOT NULL,
  `jumlah_bayar` DECIMAL(12,2) NOT NULL,
  `metode` VARCHAR(50) DEFAULT 'TUNAI',
  FOREIGN KEY (`denda_id`) REFERENCES `denda`(`id`) ON DELETE CASCADE
) ENGINE=InnoDB;

-- 11. Tabel supplier
CREATE TABLE `supplier` (
  `id` INT AUTO_INCREMENT PRIMARY KEY,
  `nama_supplier` VARCHAR(150) NOT NULL,
  `kontak_person` VARCHAR(100),
  `no_hp` VARCHAR(25),
  `alamat` TEXT
) ENGINE=InnoDB;

-- 12. Tabel kategori_produk
CREATE TABLE `kategori_produk` (
  `id` INT AUTO_INCREMENT PRIMARY KEY,
  `nama_kategori` VARCHAR(100) NOT NULL
) ENGINE=InnoDB;

-- 13. Tabel produk
CREATE TABLE `produk` (
  `id` INT AUTO_INCREMENT PRIMARY KEY,
  `kode_barang` VARCHAR(50) NOT NULL UNIQUE,
  `nama_barang` VARCHAR(200) NOT NULL,
  `kategori_id` INT NULL,
  `foto_url` VARCHAR(255) NULL,
  `harga_beli` DECIMAL(12,2) NOT NULL,
  `harga_jual` DECIMAL(12,2) NOT NULL,
  `satuan` VARCHAR(30) DEFAULT 'Pcs',
  `supplier_id` INT NULL,
  `stok_total` INT DEFAULT 0,
  FOREIGN KEY (`kategori_id`) REFERENCES `kategori_produk`(`id`) ON DELETE SET NULL,
  FOREIGN KEY (`supplier_id`) REFERENCES `supplier`(`id`) ON DELETE SET NULL
) ENGINE=InnoDB;

-- 14. Tabel variasi_produk (Variasi ukuran Seragam XS..XXL)
CREATE TABLE `variasi_produk` (
  `id` INT AUTO_INCREMENT PRIMARY KEY,
  `produk_id` INT NOT NULL,
  `ukuran` VARCHAR(20) NOT NULL,
  `stok` INT NOT NULL DEFAULT 0,
  FOREIGN KEY (`produk_id`) REFERENCES `produk`(`id`) ON DELETE CASCADE,
  UNIQUE KEY `unique_produk_ukuran` (`produk_id`, `ukuran`)
) ENGINE=InnoDB;

-- 15. Tabel pembelian (Barang masuk dari supplier)
CREATE TABLE `pembelian` (
  `id` INT AUTO_INCREMENT PRIMARY KEY,
  `no_pembelian` VARCHAR(50) NOT NULL UNIQUE,
  `supplier_id` INT NULL,
  `tanggal_pembelian` DATE NOT NULL,
  `total_pembelian` DECIMAL(15,2) NOT NULL,
  `keterangan` TEXT,
  FOREIGN KEY (`supplier_id`) REFERENCES `supplier`(`id`) ON DELETE SET NULL
) ENGINE=InnoDB;

-- 16. Tabel detail_pembelian
CREATE TABLE `detail_pembelian` (
  `id` INT AUTO_INCREMENT PRIMARY KEY,
  `pembelian_id` INT NOT NULL,
  `produk_id` INT NOT NULL,
  `ukuran` VARCHAR(20) DEFAULT 'STANDAR',
  `jumlah` INT NOT NULL,
  `harga_beli` DECIMAL(12,2) NOT NULL,
  `subtotal` DECIMAL(15,2) NOT NULL,
  FOREIGN KEY (`pembelian_id`) REFERENCES `pembelian`(`id`) ON DELETE CASCADE,
  FOREIGN KEY (`produk_id`) REFERENCES `produk`(`id`) ON DELETE CASCADE
) ENGINE=InnoDB;

-- 17. Tabel transaksi_penjualan (Kasir POS)
CREATE TABLE `transaksi_penjualan` (
  `id` INT AUTO_INCREMENT PRIMARY KEY,
  `no_penjualan` VARCHAR(50) NOT NULL UNIQUE,
  `pembeli_nama` VARCHAR(150) NOT NULL,
  `guru_id` INT NULL,
  `tanggal_penjualan` DATETIME NOT NULL,
  `total_bayar` DECIMAL(15,2) NOT NULL,
  `jumlah_uang` DECIMAL(15,2) NOT NULL,
  `kembalian` DECIMAL(15,2) NOT NULL,
  `metode_bayar` ENUM('TUNAI', 'TRANSFER', 'QRIS') DEFAULT 'TUNAI',
  `kasir_username` VARCHAR(50) NOT NULL,
  FOREIGN KEY (`guru_id`) REFERENCES `guru`(`id`) ON DELETE SET NULL
) ENGINE=InnoDB;

-- 18. Tabel detail_penjualan
CREATE TABLE `detail_penjualan` (
  `id` INT AUTO_INCREMENT PRIMARY KEY,
  `penjualan_id` INT NOT NULL,
  `produk_id` INT NOT NULL,
  `nama_barang` VARCHAR(200) NOT NULL,
  `ukuran` VARCHAR(20) DEFAULT 'STANDAR',
  `jumlah` INT NOT NULL,
  `harga_satuan` DECIMAL(12,2) NOT NULL,
  `subtotal` DECIMAL(15,2) NOT NULL,
  FOREIGN KEY (`penjualan_id`) REFERENCES `transaksi_penjualan`(`id`) ON DELETE CASCADE,
  FOREIGN KEY (`produk_id`) REFERENCES `produk`(`id`) ON DELETE CASCADE
) ENGINE=InnoDB;

-- 19. Tabel pembayaran_penjualan
CREATE TABLE `pembayaran_penjualan` (
  `id` INT AUTO_INCREMENT PRIMARY KEY,
  `penjualan_id` INT NOT NULL,
  `metode` VARCHAR(50) NOT NULL,
  `nominal` DECIMAL(15,2) NOT NULL,
  `bukti_referensi` VARCHAR(100) NULL,
  FOREIGN KEY (`penjualan_id`) REFERENCES `transaksi_penjualan`(`id`) ON DELETE CASCADE
) ENGINE=InnoDB;

-- 20. Tabel riwayat_stok (Mutasi Masuk & Keluar)
CREATE TABLE `riwayat_stok` (
  `id` INT AUTO_INCREMENT PRIMARY KEY,
  `produk_id` INT NOT NULL,
  `variasi_ukuran` VARCHAR(20) DEFAULT 'STANDAR',
  `jenis` ENUM('MASUK', 'KELUAR') NOT NULL,
  `jumlah` INT NOT NULL,
  `stok_sisa` INT NOT NULL,
  `keterangan` TEXT,
  `tanggal` DATE NOT NULL,
  FOREIGN KEY (`produk_id`) REFERENCES `produk`(`id`) ON DELETE CASCADE
) ENGINE=InnoDB;

-- 21. Tabel pengaturan
CREATE TABLE `pengaturan` (
  `id` INT PRIMARY KEY,
  `nama_koperasi` VARCHAR(150) NOT NULL,
  `nama_sekolah` VARCHAR(150) NOT NULL,
  `alamat_sekolah` TEXT NOT NULL,
  `logo_sekolah` VARCHAR(255) NULL,
  `nominal_iuran_wajib` DECIMAL(12,2) DEFAULT 30000.00,
  `persen_bunga_simpanan` DECIMAL(5,2) DEFAULT 2.00,
  `persen_bunga_pinjaman` DECIMAL(5,2) DEFAULT 2.00,
  `masa_toleransi_hari` INT DEFAULT 7,
  `nominal_denda` DECIMAL(12,2) DEFAULT 10000.00
) ENGINE=InnoDB;

-- 22. Tabel log_aktivitas
CREATE TABLE `log_aktivitas` (
  `id` INT AUTO_INCREMENT PRIMARY KEY,
  `username` VARCHAR(50) NOT NULL,
  `peran` VARCHAR(30) NOT NULL,
  `aktivitas` TEXT NOT NULL,
  `tanggal_aktivitas` DATETIME NOT NULL
) ENGINE=InnoDB;

-- ============================================================
-- DATA AWAL (SEED DATA SMP NURUL ANSOR)
-- ============================================================

INSERT INTO `pengaturan` (`id`, `nama_koperasi`, `nama_sekolah`, `alamat_sekolah`, `nominal_iuran_wajib`, `persen_bunga_simpanan`, `persen_bunga_pinjaman`, `masa_toleransi_hari`, `nominal_denda`)
VALUES (1, 'Koperasi Sekolah SMP Nurul Ansor', 'SMP Nurul Ansor', 'Jl. Pendidikan No. 45, Wonocolo, Surabaya', 30000.00, 2.00, 2.00, 7, 10000.00);

INSERT INTO `users` (`username`, `password`, `role`, `nama_lengkap`)
VALUES ('admin', MD5('admin123'), 'admin', 'Administrator Koperasi');
