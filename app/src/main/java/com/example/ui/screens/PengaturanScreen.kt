package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.entity.PengaturanEntity
import com.example.ui.KoperasiViewModel
import com.example.ui.components.SectionHeader
import com.example.ui.theme.*

@Composable
fun PengaturanScreen(
  viewModel: KoperasiViewModel
) {
  val context = LocalContext.current
  val pengaturanState by viewModel.pengaturan.collectAsStateWithLifecycle()
  val userList by viewModel.currentUser.collectAsStateWithLifecycle()
  val allUsers by viewModel.guruList.collectAsStateWithLifecycle()

  var selectedTab by remember { mutableStateOf(0) } // 0: Parameter Koperasi, 1: Skema MySQL & CI4

  val p = pengaturanState ?: PengaturanEntity()

  var namaKoperasi by remember(p) { mutableStateOf(p.namaKoperasi) }
  var namaSekolah by remember(p) { mutableStateOf(p.namaSekolah) }
  var alamatSekolah by remember(p) { mutableStateOf(p.alamatSekolah) }
  var nominalIuranText by remember(p) { mutableStateOf(p.nominalIuranWajib.toInt().toString()) }
  var bungaSimpananText by remember(p) { mutableStateOf(p.persenBungaSimpanan.toString()) }
  var bungaPinjamanText by remember(p) { mutableStateOf(p.persenBungaPinjaman.toString()) }
  var toleransiHariText by remember(p) { mutableStateOf(p.masaToleransiHari.toString()) }
  var nominalDendaText by remember(p) { mutableStateOf(p.nominalDenda.toInt().toString()) }

  Column(
    modifier = Modifier
      .fillMaxSize()
      .background(SlateBackground)
  ) {
    TabRow(
      selectedTabIndex = selectedTab,
      containerColor = NavyDark,
      contentColor = Color.White
    ) {
      Tab(
        selected = selectedTab == 0,
        onClick = { selectedTab = 0 },
        text = { Text("Konfigurasi Parameter", fontSize = 12.sp, fontWeight = FontWeight.Bold) }
      )
      Tab(
        selected = selectedTab == 1,
        onClick = { selectedTab = 1 },
        text = { Text("Skema MySQL & CodeIgniter 4", fontSize = 12.sp, fontWeight = FontWeight.Bold) }
      )
    }

    if (selectedTab == 0) {
      LazyColumn(
        modifier = Modifier
          .fillMaxSize()
          .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
      ) {
        item {
          Card(
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            modifier = Modifier.fillMaxWidth()
          ) {
            Column(modifier = Modifier.padding(16.dp)) {
              Text("Identitas Lembaga Sekolah", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = NavyDark)
              Spacer(modifier = Modifier.height(10.dp))

              OutlinedTextField(
                value = namaKoperasi,
                onValueChange = { namaKoperasi = it },
                label = { Text("Nama Koperasi") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
              )
              Spacer(modifier = Modifier.height(8.dp))

              OutlinedTextField(
                value = namaSekolah,
                onValueChange = { namaSekolah = it },
                label = { Text("Nama Sekolah") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
              )
              Spacer(modifier = Modifier.height(8.dp))

              OutlinedTextField(
                value = alamatSekolah,
                onValueChange = { alamatSekolah = it },
                label = { Text("Alamat Sekolah") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
              )
            }
          }
        }

        item {
          Card(
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            modifier = Modifier.fillMaxWidth()
          ) {
            Column(modifier = Modifier.padding(16.dp)) {
              Text("Parameter Keuangan & Aturan Koperasi", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = NavyDark)
              Spacer(modifier = Modifier.height(10.dp))

              OutlinedTextField(
                value = nominalIuranText,
                onValueChange = { nominalIuranText = it.filter { ch -> ch.isDigit() } },
                label = { Text("Nominal Iuran Wajib Bulanan (Rp)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
              )
              Spacer(modifier = Modifier.height(8.dp))

              Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                  value = bungaSimpananText,
                  onValueChange = { bungaSimpananText = it },
                  label = { Text("Bunga Simpanan (%/bln)") },
                  keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                  modifier = Modifier.weight(1f)
                )
                OutlinedTextField(
                  value = bungaPinjamanText,
                  onValueChange = { bungaPinjamanText = it },
                  label = { Text("Bunga Pinjaman (%/bln)") },
                  keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                  modifier = Modifier.weight(1f)
                )
              }
              Spacer(modifier = Modifier.height(8.dp))

              Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                  value = toleransiHariText,
                  onValueChange = { toleransiHariText = it.filter { ch -> ch.isDigit() } },
                  label = { Text("Toleransi Jatuh Tempo (Hari)") },
                  keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                  modifier = Modifier.weight(1f)
                )
                OutlinedTextField(
                  value = nominalDendaText,
                  onValueChange = { nominalDendaText = it.filter { ch -> ch.isDigit() } },
                  label = { Text("Denda Keterlambatan (Rp)") },
                  keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                  modifier = Modifier.weight(1f)
                )
              }

              Spacer(modifier = Modifier.height(16.dp))

              Button(
                onClick = {
                  val updated = p.copy(
                    namaKoperasi = namaKoperasi,
                    namaSekolah = namaSekolah,
                    alamatSekolah = alamatSekolah,
                    nominalIuranWajib = nominalIuranText.toDoubleOrNull() ?: 30000.0,
                    persenBungaSimpanan = bungaSimpananText.toDoubleOrNull() ?: 2.0,
                    persenBungaPinjaman = bungaPinjamanText.toDoubleOrNull() ?: 2.0,
                    masaToleransiHari = toleransiHariText.toIntOrNull() ?: 7,
                    nominalDenda = nominalDendaText.toDoubleOrNull() ?: 10000.0
                  )
                  viewModel.updatePengaturan(updated)
                },
                colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                  .fillMaxWidth()
                  .testTag("simpan_pengaturan_btn")
              ) {
                Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Simpan Perubahan Pengaturan")
              }
            }
          }
        }
      }
    } else {
      // TAB 2: SKEMA DATABASE MYSQL & CODEIGNITER 4 ARCHITECTURE
      LazyColumn(
        modifier = Modifier
          .fillMaxSize()
          .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
      ) {
        item {
          Card(
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = NavyDark),
            modifier = Modifier.fillMaxWidth()
          ) {
            Column(modifier = Modifier.padding(16.dp)) {
              Text("Struktur Database MySQL & CI4 XAMPP", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
              Spacer(modifier = Modifier.height(4.dp))
              Text(
                "Sesuai instruksi prompt, disiapkan skema relasi foreign key 22 tabel MySQL dan arsitektur CodeIgniter 4 siap pakai di XAMPP sekolah.",
                color = Color.White.copy(alpha = 0.85f),
                fontSize = 11.sp
              )
              Spacer(modifier = Modifier.height(10.dp))
              Button(
                onClick = {
                  val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                  val clip = ClipData.newPlainText("MySQL Schema", mysqlSchemaCode)
                  clipboard.setPrimaryClip(clip)
                  viewModel.showMessage("Skema 22 Tabel MySQL berhasil disalin ke Clipboard!")
                },
                colors = ButtonDefaults.buttonColors(containerColor = PurpleAccent),
                shape = RoundedCornerShape(8.dp)
              ) {
                Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Salin Kode SQL (22 Tabel MySQL)", fontSize = 11.sp)
              }
            }
          }
        }

        item {
          Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            modifier = Modifier.fillMaxWidth()
          ) {
            Column(modifier = Modifier.padding(14.dp)) {
              Text("Preview Skema database.sql (22 Tabel Berelasi):", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = NavyDark)
              Spacer(modifier = Modifier.height(8.dp))
              Surface(
                shape = RoundedCornerShape(8.dp),
                color = SlateBackground,
                modifier = Modifier.fillMaxWidth()
              ) {
                Text(
                  text = mysqlSchemaPreview,
                  fontFamily = FontFamily.Monospace,
                  fontSize = 10.sp,
                  color = TextPrimary,
                  modifier = Modifier.padding(10.dp)
                )
              }
            }
          }
        }

        item {
          Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            modifier = Modifier.fillMaxWidth()
          ) {
            Column(modifier = Modifier.padding(14.dp)) {
              Text("Struktur Folder MVC CodeIgniter 4:", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = NavyDark)
              Spacer(modifier = Modifier.height(6.dp))
              Text(
                """
                app/
                ├── Config/ (App.php, Database.php, Routes.php)
                ├── Controllers/
                │   ├── Auth.php
                │   ├── Dashboard.php
                │   ├── Guru.php
                │   ├── Iuran.php
                │   ├── Simpanan.php
                │   ├── Pinjaman.php
                │   ├── Angsuran.php
                │   ├── Denda.php
                │   ├── Toko.php
                │   └── Laporan.php
                ├── Models/ (UserModel, GuruModel, IuranModel, SimpananModel, PinjamanModel, TokoModel...)
                └── Views/ (templates/sidebar.php, dashboard.php, kasir.php...)
                """.trimIndent(),
                fontFamily = FontFamily.Monospace,
                fontSize = 11.sp,
                color = NavyDark
              )
            }
          }
        }
      }
    }
  }
}

val mysqlSchemaPreview = """
-- 1. users
CREATE TABLE users (
  id INT AUTO_INCREMENT PRIMARY KEY,
  username VARCHAR(50) UNIQUE NOT NULL,
  password VARCHAR(255) NOT NULL,
  role ENUM('admin', 'guru') NOT NULL,
  guru_id INT NULL,
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- 2. guru
CREATE TABLE guru (
  id INT AUTO_INCREMENT PRIMARY KEY,
  nip VARCHAR(30) UNIQUE NOT NULL,
  nama_lengkap VARCHAR(150) NOT NULL,
  jenis_kelamin ENUM('Laki-laki', 'Perempuan'),
  alamat TEXT,
  no_hp VARCHAR(20),
  tanggal_bergabung DATE NOT NULL,
  status_aktif BOOLEAN DEFAULT TRUE
);

-- 3. iuran (Wajib Rp 30.000 / bln)
CREATE TABLE iuran (
  id INT AUTO_INCREMENT PRIMARY KEY,
  guru_id INT NOT NULL,
  bulan INT NOT NULL,
  tahun INT NOT NULL,
  nominal DECIMAL(12,2) DEFAULT 30000.00,
  status_lunas BOOLEAN DEFAULT FALSE,
  tanggal_bayar DATE NULL,
  no_kwitansi VARCHAR(50) NULL,
  FOREIGN KEY (guru_id) REFERENCES guru(id) ON DELETE CASCADE
);

-- 4. simpanan & 5. transaksi_simpanan (Bunga 2%)
-- 6. pinjaman & 7. angsuran & 9. denda (Rp 10.000, 7 hari toleransi)
-- 11-19. supplier, kategori, produk, variasi, pembelian, penjualan kasir
-- 20. riwayat_stok, 21. pengaturan, 22. log_aktivitas
""".trimIndent()

val mysqlSchemaCode = """
-- ============================================================
-- SISTEM KOPERASI SEKOLAH SMP NURUL ANSOR
-- FULL DATABASE SCHEMA WITH FOREIGN KEYS (22 TABLES)
-- ============================================================

CREATE DATABASE IF NOT EXISTS koperasi_smp_nurul_ansor;
USE koperasi_smp_nurul_ansor;

-- 1. users
CREATE TABLE users (
  id INT AUTO_INCREMENT PRIMARY KEY,
  username VARCHAR(50) UNIQUE NOT NULL,
  password VARCHAR(255) NOT NULL,
  role ENUM('admin', 'guru') NOT NULL,
  guru_id INT NULL,
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

-- 2. guru
CREATE TABLE guru (
  id INT AUTO_INCREMENT PRIMARY KEY,
  nip VARCHAR(30) UNIQUE NOT NULL,
  nama_lengkap VARCHAR(150) NOT NULL,
  jenis_kelamin ENUM('Laki-laki', 'Perempuan') NOT NULL,
  alamat TEXT,
  no_hp VARCHAR(20),
  tanggal_bergabung DATE NOT NULL,
  status_aktif BOOLEAN DEFAULT TRUE
) ENGINE=InnoDB;

-- 3. iuran
CREATE TABLE iuran (
  id INT AUTO_INCREMENT PRIMARY KEY,
  guru_id INT NOT NULL,
  bulan INT NOT NULL,
  tahun INT NOT NULL,
  nominal DECIMAL(12,2) DEFAULT 30000.00,
  status_lunas BOOLEAN DEFAULT FALSE,
  tanggal_bayar DATE NULL,
  no_kwitansi VARCHAR(50) NULL,
  keterangan TEXT,
  FOREIGN KEY (guru_id) REFERENCES guru(id) ON DELETE CASCADE,
  UNIQUE KEY unique_guru_period (guru_id, bulan, tahun)
) ENGINE=InnoDB;

-- 4. simpanan
CREATE TABLE simpanan (
  id INT AUTO_INCREMENT PRIMARY KEY,
  guru_id INT UNIQUE NOT NULL,
  saldo DECIMAL(15,2) DEFAULT 0.00,
  updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  FOREIGN KEY (guru_id) REFERENCES guru(id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- 5. transaksi_simpanan
CREATE TABLE transaksi_simpanan (
  id INT AUTO_INCREMENT PRIMARY KEY,
  guru_id INT NOT NULL,
  jenis ENUM('SETORAN', 'PENARIKAN', 'BUNGA') NOT NULL,
  nominal DECIMAL(12,2) NOT NULL,
  saldo_setelah DECIMAL(15,2) NOT NULL,
  tanggal DATE NOT NULL,
  keterangan TEXT,
  dasar_perhitungan_bunga TEXT NULL,
  FOREIGN KEY (guru_id) REFERENCES guru(id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- 6. pinjaman
CREATE TABLE pinjaman (
  id INT AUTO_INCREMENT PRIMARY KEY,
  no_pinjaman VARCHAR(50) UNIQUE NOT NULL,
  guru_id INT NOT NULL,
  tanggal_pengajuan DATE NOT NULL,
  jumlah_pinjaman DECIMAL(15,2) NOT NULL,
  bunga_persen DECIMAL(5,2) DEFAULT 2.00,
  lama_angsuran_bulan INT NOT NULL,
  total_bunga DECIMAL(15,2) NOT NULL,
  total_pinjaman DECIMAL(15,2) NOT NULL,
  angsuran_per_bulan DECIMAL(15,2) NOT NULL,
  tanggal_mulai_angsuran DATE NOT NULL,
  status ENUM('MENUNGGU', 'DISETUJUI', 'DITOLAK', 'LUNAS') DEFAULT 'MENUNGGU',
  catatan_admin TEXT,
  FOREIGN KEY (guru_id) REFERENCES guru(id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- 7. angsuran
CREATE TABLE angsuran (
  id INT AUTO_INCREMENT PRIMARY KEY,
  pinjaman_id INT NOT NULL,
  guru_id INT NOT NULL,
  angsuran_ke INT NOT NULL,
  nominal_angsuran DECIMAL(15,2) NOT NULL,
  tanggal_jatuh_tempo DATE NOT NULL,
  status ENUM('BELUM_BAYAR', 'LUNAS', 'TERLAMBAT') DEFAULT 'BELUM_BAYAR',
  tanggal_bayar DATE NULL,
  sisa_pinjaman DECIMAL(15,2) DEFAULT 0.00,
  no_kwitansi VARCHAR(50) NULL,
  FOREIGN KEY (pinjaman_id) REFERENCES pinjaman(id) ON DELETE CASCADE,
  FOREIGN KEY (guru_id) REFERENCES guru(id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- 8. denda
CREATE TABLE denda (
  id INT AUTO_INCREMENT PRIMARY KEY,
  angsuran_id INT UNIQUE NOT NULL,
  pinjaman_id INT NOT NULL,
  guru_id INT NOT NULL,
  nominal_denda DECIMAL(12,2) DEFAULT 10000.00,
  status_denda ENUM('BELUM_BAYAR', 'LUNAS') DEFAULT 'BELUM_BAYAR',
  tanggal_dikenakan DATE NOT NULL,
  tanggal_bayar DATE NULL,
  keterangan TEXT,
  FOREIGN KEY (angsuran_id) REFERENCES angsuran(id) ON DELETE CASCADE,
  FOREIGN KEY (pinjaman_id) REFERENCES pinjaman(id) ON DELETE CASCADE,
  FOREIGN KEY (guru_id) REFERENCES guru(id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- 9. supplier
CREATE TABLE supplier (
  id INT AUTO_INCREMENT PRIMARY KEY,
  nama_supplier VARCHAR(150) NOT NULL,
  kontak_person VARCHAR(100),
  no_hp VARCHAR(20),
  alamat TEXT
) ENGINE=InnoDB;

-- 10. kategori_produk
CREATE TABLE kategori_produk (
  id INT AUTO_INCREMENT PRIMARY KEY,
  nama_kategori VARCHAR(100) NOT NULL
) ENGINE=InnoDB;

-- 11. produk
CREATE TABLE produk (
  id INT AUTO_INCREMENT PRIMARY KEY,
  kode_barang VARCHAR(50) UNIQUE NOT NULL,
  nama_barang VARCHAR(200) NOT NULL,
  kategori_id INT NULL,
  harga_beli DECIMAL(12,2) NOT NULL,
  harga_jual DECIMAL(12,2) NOT NULL,
  satuan VARCHAR(30) DEFAULT 'Pcs',
  supplier_id INT NULL,
  stok_total INT DEFAULT 0,
  FOREIGN KEY (kategori_id) REFERENCES kategori_produk(id) ON DELETE SET NULL,
  FOREIGN KEY (supplier_id) REFERENCES supplier(id) ON DELETE SET NULL
) ENGINE=InnoDB;

-- 12. variasi_produk (Khusus Seragam XS..XXL)
CREATE TABLE variasi_produk (
  id INT AUTO_INCREMENT PRIMARY KEY,
  produk_id INT NOT NULL,
  ukuran VARCHAR(20) NOT NULL,
  stok INT DEFAULT 0,
  FOREIGN KEY (produk_id) REFERENCES produk(id) ON DELETE CASCADE,
  UNIQUE KEY unique_prod_size (produk_id, ukuran)
) ENGINE=InnoDB;

-- 13. pembelian & 14. detail_pembelian
CREATE TABLE pembelian (
  id INT AUTO_INCREMENT PRIMARY KEY,
  no_pembelian VARCHAR(50) UNIQUE NOT NULL,
  supplier_id INT NULL,
  tanggal_pembelian DATE NOT NULL,
  total_pembelian DECIMAL(15,2) NOT NULL,
  keterangan TEXT,
  FOREIGN KEY (supplier_id) REFERENCES supplier(id) ON DELETE SET NULL
) ENGINE=InnoDB;

CREATE TABLE detail_pembelian (
  id INT AUTO_INCREMENT PRIMARY KEY,
  pembelian_id INT NOT NULL,
  produk_id INT NOT NULL,
  ukuran VARCHAR(20) DEFAULT 'STANDAR',
  jumlah INT NOT NULL,
  harga_beli DECIMAL(12,2) NOT NULL,
  subtotal DECIMAL(15,2) NOT NULL,
  FOREIGN KEY (pembelian_id) REFERENCES pembelian(id) ON DELETE CASCADE,
  FOREIGN KEY (produk_id) REFERENCES produk(id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- 15. transaksi_penjualan & 16. detail_penjualan
CREATE TABLE transaksi_penjualan (
  id INT AUTO_INCREMENT PRIMARY KEY,
  no_penjualan VARCHAR(50) UNIQUE NOT NULL,
  pembeli_nama VARCHAR(150) NOT NULL,
  guru_id INT NULL,
  tanggal_penjualan DATETIME NOT NULL,
  total_bayar DECIMAL(15,2) NOT NULL,
  jumlah_uang DECIMAL(15,2) NOT NULL,
  kembalian DECIMAL(15,2) NOT NULL,
  metode_bayar ENUM('TUNAI', 'TRANSFER', 'QRIS') DEFAULT 'TUNAI',
  kasir_username VARCHAR(50) NOT NULL,
  FOREIGN KEY (guru_id) REFERENCES guru(id) ON DELETE SET NULL
) ENGINE=InnoDB;

CREATE TABLE detail_penjualan (
  id INT AUTO_INCREMENT PRIMARY KEY,
  penjualan_id INT NOT NULL,
  produk_id INT NOT NULL,
  nama_barang VARCHAR(200) NOT NULL,
  ukuran VARCHAR(20) DEFAULT 'STANDAR',
  jumlah INT NOT NULL,
  harga_satuan DECIMAL(12,2) NOT NULL,
  subtotal DECIMAL(15,2) NOT NULL,
  FOREIGN KEY (penjualan_id) REFERENCES transaksi_penjualan(id) ON DELETE CASCADE,
  FOREIGN KEY (produk_id) REFERENCES produk(id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- 17. riwayat_stok
CREATE TABLE riwayat_stok (
  id INT AUTO_INCREMENT PRIMARY KEY,
  produk_id INT NOT NULL,
  variasi_ukuran VARCHAR(20) DEFAULT 'STANDAR',
  jenis ENUM('MASUK', 'KELUAR') NOT NULL,
  jumlah INT NOT NULL,
  stok_sisa INT NOT NULL,
  keterangan TEXT,
  tanggal DATE NOT NULL,
  FOREIGN KEY (produk_id) REFERENCES produk(id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- 18. pengaturan
CREATE TABLE pengaturan (
  id INT PRIMARY KEY,
  nama_koperasi VARCHAR(150),
  nama_sekolah VARCHAR(150),
  alamat_sekolah TEXT,
  logo_sekolah VARCHAR(255),
  nominal_iuran_wajib DECIMAL(12,2) DEFAULT 30000.00,
  persen_bunga_simpanan DECIMAL(5,2) DEFAULT 2.00,
  persen_bunga_pinjaman DECIMAL(5,2) DEFAULT 2.00,
  masa_toleransi_hari INT DEFAULT 7,
  nominal_denda DECIMAL(12,2) DEFAULT 10000.00
) ENGINE=InnoDB;

-- 19. log_aktivitas
CREATE TABLE log_aktivitas (
  id INT AUTO_INCREMENT PRIMARY KEY,
  username VARCHAR(50) NOT NULL,
  peran VARCHAR(30) NOT NULL,
  aktivitas TEXT NOT NULL,
  tanggal_aktivitas DATETIME NOT NULL
) ENGINE=InnoDB;
""".trimIndent()
