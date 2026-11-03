package com.example.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.dao.KoperasiDao
import com.example.data.entity.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
  entities = [
    UserEntity::class,
    GuruEntity::class,
    IuranEntity::class,
    SimpananEntity::class,
    TransaksiSimpananEntity::class,
    PinjamanEntity::class,
    AngsuranEntity::class,
    DendaEntity::class,
    SupplierEntity::class,
    KategoriProdukEntity::class,
    ProdukEntity::class,
    VariasiProdukEntity::class,
    PembelianEntity::class,
    DetailPembelianEntity::class,
    TransaksiPenjualanEntity::class,
    DetailPenjualanEntity::class,
    RiwayatStokEntity::class,
    PengaturanEntity::class,
    LogAktivitasEntity::class
  ],
  version = 1,
  exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
  abstract fun koperasiDao(): KoperasiDao

  companion object {
    @Volatile
    private var INSTANCE: AppDatabase? = null

    fun getDatabase(context: Context, scope: CoroutineScope): AppDatabase {
      return INSTANCE ?: synchronized(this) {
        val instance = Room.databaseBuilder(
          context.applicationContext,
          AppDatabase::class.java,
          "koperasi_smp_nurul_ansor.db"
        )
          .addCallback(DatabaseCallback(scope))
          .fallbackToDestructiveMigration()
          .build()
        INSTANCE = instance
        instance
      }
    }

    private class DatabaseCallback(
      private val scope: CoroutineScope
    ) : RoomDatabase.Callback() {
      override fun onCreate(db: SupportSQLiteDatabase) {
        super.onCreate(db)
        INSTANCE?.let { database ->
          scope.launch(Dispatchers.IO) {
            populateInitialData(database.koperasiDao())
          }
        }
      }

      suspend fun populateInitialData(dao: KoperasiDao) {
        // 1. Pengaturan Default
        dao.insertOrUpdatePengaturan(
          PengaturanEntity(
            id = 1,
            namaKoperasi = "Koperasi Sekolah SMP Nurul Ansor",
            namaSekolah = "SMP Nurul Ansor",
            alamatSekolah = "Jl. Pendidikan No. 45, Kecamatan Wonocolo, Surabaya",
            logoSekolah = "",
            nominalIuranWajib = 30000.0,
            persenBungaSimpanan = 2.0,
            persenBungaPinjaman = 2.0,
            masaToleransiHari = 7,
            nominalDenda = 10000.0
          )
        )

        // 2. Data Guru
        val g1 = dao.insertGuru(
          GuruEntity(
            nip = "198204152008011005",
            namaLengkap = "H. Ahmad Dahlan, S.Pd.",
            jenisKelamin = "Laki-laki",
            alamat = "Jl. Melati No. 12, Surabaya",
            noHp = "081234567801",
            tanggalBergabung = "2021-01-15",
            statusAktif = true
          )
        )
        val g2 = dao.insertGuru(
          GuruEntity(
            nip = "198607222010012011",
            namaLengkap = "Siti Rahmah, S.Pd.",
            jenisKelamin = "Perempuan",
            alamat = "Jl. Mawar No. 4, Surabaya",
            noHp = "081234567802",
            tanggalBergabung = "2021-02-10",
            statusAktif = true
          )
        )
        val g3 = dao.insertGuru(
          GuruEntity(
            nip = "198411032009021003",
            namaLengkap = "Budi Santoso, M.Pd.",
            jenisKelamin = "Laki-laki",
            alamat = "Jl. Kenanga No. 8, Sidoarjo",
            noHp = "081234567803",
            tanggalBergabung = "2022-07-01",
            statusAktif = true
          )
        )
        val g4 = dao.insertGuru(
          GuruEntity(
            nip = "198901192014032002",
            namaLengkap = "Nur Hidayati, S.Ag.",
            jenisKelamin = "Perempuan",
            alamat = "Jl. Dahlia No. 15, Surabaya",
            noHp = "081234567804",
            tanggalBergabung = "2023-01-10",
            statusAktif = true
          )
        )

        // 3. User Accounts
        dao.insertUser(
          UserEntity(
            username = "admin",
            passwordHash = "admin123",
            role = "admin",
            namaLengkap = "Administrator Koperasi"
          )
        )
        dao.insertUser(
          UserEntity(
            username = "ahmad",
            passwordHash = "guru123",
            role = "guru",
            guruId = g1,
            namaLengkap = "H. Ahmad Dahlan, S.Pd."
          )
        )
        dao.insertUser(
          UserEntity(
            username = "siti",
            passwordHash = "guru123",
            role = "guru",
            guruId = g2,
            namaLengkap = "Siti Rahmah, S.Pd."
          )
        )
        dao.insertUser(
          UserEntity(
            username = "budi",
            passwordHash = "guru123",
            role = "guru",
            guruId = g3,
            namaLengkap = "Budi Santoso, M.Pd."
          )
        )
        dao.insertUser(
          UserEntity(
            username = "nur",
            passwordHash = "guru123",
            role = "guru",
            guruId = g4,
            namaLengkap = "Nur Hidayati, S.Ag."
          )
        )

        // 4. Iuran Wajib
        // Bulan November 2026 (current month)
        dao.insertIuran(
          IuranEntity(
            guruId = g1,
            bulan = 11,
            tahun = 2026,
            nominal = 30000.0,
            statusLunas = true,
            tanggalBayar = "2026-11-01",
            noKwitansi = "KW-IU-2611-001",
            keterangan = "Iuran wajib bulan November 2026 via tunai"
          )
        )
        dao.insertIuran(
          IuranEntity(
            guruId = g2,
            bulan = 11,
            tahun = 2026,
            nominal = 30000.0,
            statusLunas = true,
            tanggalBayar = "2026-11-02",
            noKwitansi = "KW-IU-2611-002",
            keterangan = "Iuran wajib bulan November 2026 via transfer"
          )
        )
        dao.insertIuran(
          IuranEntity(
            guruId = g3,
            bulan = 11,
            tahun = 2026,
            nominal = 30000.0,
            statusLunas = false,
            tanggalBayar = null,
            noKwitansi = null,
            keterangan = "Belum membayar iuran bulan berjalan"
          )
        )
        dao.insertIuran(
          IuranEntity(
            guruId = g4,
            bulan = 11,
            tahun = 2026,
            nominal = 30000.0,
            statusLunas = false,
            tanggalBayar = null,
            noKwitansi = null,
            keterangan = "Belum membayar iuran bulan berjalan"
          )
        )

        // Bulan Oktober 2026
        dao.insertIuran(IuranEntity(guruId = g1, bulan = 10, tahun = 2026, nominal = 30000.0, statusLunas = true, tanggalBayar = "2026-10-02", noKwitansi = "KW-IU-2610-001"))
        dao.insertIuran(IuranEntity(guruId = g2, bulan = 10, tahun = 2026, nominal = 30000.0, statusLunas = true, tanggalBayar = "2026-10-03", noKwitansi = "KW-IU-2610-002"))
        dao.insertIuran(IuranEntity(guruId = g3, bulan = 10, tahun = 2026, nominal = 30000.0, statusLunas = true, tanggalBayar = "2026-10-04", noKwitansi = "KW-IU-2610-003"))
        dao.insertIuran(IuranEntity(guruId = g4, bulan = 10, tahun = 2026, nominal = 30000.0, statusLunas = true, tanggalBayar = "2026-10-05", noKwitansi = "KW-IU-2610-004"))

        // 5. Simpanan & Transaksi Simpanan
        dao.insertOrUpdateSimpanan(SimpananEntity(guruId = g1, saldo = 1750000.0))
        dao.insertTransaksiSimpanan(TransaksiSimpananEntity(guruId = g1, jenis = "SETORAN", nominal = 1715000.0, saldoSetelah = 1715000.0, tanggal = "2026-10-01", keterangan = "Setoran simpanan sukarela"))
        dao.insertTransaksiSimpanan(TransaksiSimpananEntity(guruId = g1, jenis = "BUNGA", nominal = 35000.0, saldoSetelah = 1750000.0, tanggal = "2026-10-31", keterangan = "Bunga simpanan periode Oktober 2026", dasarPerhitunganBunga = "2.0% x Saldo Rp 1.715.000 = Rp 34.300 (dibulatkan Rp 35.000)"))

        dao.insertOrUpdateSimpanan(SimpananEntity(guruId = g2, saldo = 2500000.0))
        dao.insertTransaksiSimpanan(TransaksiSimpananEntity(guruId = g2, jenis = "SETORAN", nominal = 2500000.0, saldoSetelah = 2500000.0, tanggal = "2026-09-15", keterangan = "Setoran simpanan pokok dan sukarela"))

        dao.insertOrUpdateSimpanan(SimpananEntity(guruId = g3, saldo = 950000.0))
        dao.insertTransaksiSimpanan(TransaksiSimpananEntity(guruId = g3, jenis = "SETORAN", nominal = 950000.0, saldoSetelah = 950000.0, tanggal = "2026-10-10", keterangan = "Setoran simpanan berkala"))

        dao.insertOrUpdateSimpanan(SimpananEntity(guruId = g4, saldo = 600000.0))
        dao.insertTransaksiSimpanan(TransaksiSimpananEntity(guruId = g4, jenis = "SETORAN", nominal = 600000.0, saldoSetelah = 600000.0, tanggal = "2026-10-12", keterangan = "Setoran simpanan awal"))

        // 6. Pinjaman & Angsuran Sampel
        // Ahmad mengajukan pinjaman Rp 3.000.000 tenor 6 bulan (flat 2% = 6 x 2% = 12% = 360.000, total = 3.360.000, per bulan = 560.000)
        val p1 = dao.insertPinjaman(
          PinjamanEntity(
            noPinjaman = "PJ-202610-001",
            guruId = g1,
            tanggalPengajuan = "2026-10-05",
            jumlahPinjaman = 3000000.0,
            bungaPersen = 2.0,
            lamaAngsuranBulan = 6,
            totalBunga = 360000.0,
            totalPinjaman = 3360000.0,
            angsuranPerBulan = 560000.0,
            tanggalMulaiAngsuran = "2026-11-10",
            status = "DISETUJUI",
            catatanAdmin = "Iuran lunas, pengajuan disetujui"
          )
        )

        // Generate 6 installments
        val angsuranList = listOf(
          AngsuranEntity(pinjamanId = p1, guruId = g1, angsuranKe = 1, nominalAngsuran = 560000.0, tanggalJatuhTempo = "2026-11-10", status = "BELUM_BAYAR", sisaPinjaman = 2800000.0),
          AngsuranEntity(pinjamanId = p1, guruId = g1, angsuranKe = 2, nominalAngsuran = 560000.0, tanggalJatuhTempo = "2026-12-10", status = "BELUM_BAYAR", sisaPinjaman = 2240000.0),
          AngsuranEntity(pinjamanId = p1, guruId = g1, angsuranKe = 3, nominalAngsuran = 560000.0, tanggalJatuhTempo = "2027-01-10", status = "BELUM_BAYAR", sisaPinjaman = 1680000.0),
          AngsuranEntity(pinjamanId = p1, guruId = g1, angsuranKe = 4, nominalAngsuran = 560000.0, tanggalJatuhTempo = "2027-02-10", status = "BELUM_BAYAR", sisaPinjaman = 1120000.0),
          AngsuranEntity(pinjamanId = p1, guruId = g1, angsuranKe = 5, nominalAngsuran = 560000.0, tanggalJatuhTempo = "2027-03-10", status = "BELUM_BAYAR", sisaPinjaman = 560000.0),
          AngsuranEntity(pinjamanId = p1, guruId = g1, angsuranKe = 6, nominalAngsuran = 560000.0, tanggalJatuhTempo = "2027-04-10", status = "BELUM_BAYAR", sisaPinjaman = 0.0)
        )
        dao.insertAngsuranList(angsuranList)

        // Pinjaman Siti yang menunggu persetujuan
        dao.insertPinjaman(
          PinjamanEntity(
            noPinjaman = "PJ-202611-002",
            guruId = g2,
            tanggalPengajuan = "2026-11-02",
            jumlahPinjaman = 5000000.0,
            bungaPersen = 2.0,
            lamaAngsuranBulan = 10,
            totalBunga = 1000000.0,
            totalPinjaman = 6000000.0,
            angsuranPerBulan = 600000.0,
            tanggalMulaiAngsuran = "2026-12-05",
            status = "MENUNGGU",
            catatanAdmin = "Menunggu verifikasi admin"
          )
        )

        // Pinjaman Budi masa lalu yang punya keterlambatan untuk menguji Denda Rp 10.000
        val pOld = dao.insertPinjaman(
          PinjamanEntity(
            noPinjaman = "PJ-202608-000",
            guruId = g3,
            tanggalPengajuan = "2026-08-01",
            jumlahPinjaman = 1000000.0,
            bungaPersen = 2.0,
            lamaAngsuranBulan = 2,
            totalBunga = 40000.0,
            totalPinjaman = 1040000.0,
            angsuranPerBulan = 520000.0,
            tanggalMulaiAngsuran = "2026-09-05",
            status = "DISETUJUI",
            catatanAdmin = "Pinjaman disetujui"
          )
        )
        val ang1Old = AngsuranEntity(pinjamanId = pOld, guruId = g3, angsuranKe = 1, nominalAngsuran = 520000.0, tanggalJatuhTempo = "2026-09-05", status = "LUNAS", tanggalBayar = "2026-09-05", sisaPinjaman = 520000.0, noKwitansi = "KW-ANG-2609-01")
        val ang2Old = AngsuranEntity(pinjamanId = pOld, guruId = g3, angsuranKe = 2, nominalAngsuran = 520000.0, tanggalJatuhTempo = "2026-10-05", status = "TERLAMBAT", tanggalBayar = null, sisaPinjaman = 520000.0)
        dao.insertAngsuranList(listOf(ang1Old, ang2Old))

        // Denda untuk angsuran 2 yang terlambat (> 7 hari dari 2026-10-05)
        dao.insertDenda(
          DendaEntity(
            angsuranId = 8, // ID ang2Old
            pinjamanId = pOld,
            guruId = g3,
            nominalDenda = 10000.0,
            statusDenda = "BELUM_BAYAR",
            tanggalDikenakan = "2026-10-13",
            keterangan = "Terlambat lebih dari 7 hari jatuh tempo 5 Okt 2026"
          )
        )

        // 7. Toko Koperasi - Kategori & Supplier
        val k1 = dao.insertKategori(KategoriProdukEntity(namaKategori = "Seragam Sekolah"))
        val k2 = dao.insertKategori(KategoriProdukEntity(namaKategori = "Atribut Sekolah"))
        val k3 = dao.insertKategori(KategoriProdukEntity(namaKategori = "Alat Tulis"))
        val k4 = dao.insertKategori(KategoriProdukEntity(namaKategori = "Buku & LKS"))

        val s1 = dao.insertSupplier(SupplierEntity(namaSupplier = "Konveksi Ansor Berkah", kontakPerson = "Ibu Halimah", noHp = "081399887711", alamat = "Komp. Industri Tekstil No. 9, Sidoarjo"))
        val s2 = dao.insertSupplier(SupplierEntity(namaSupplier = "CV Mitra Stationery", kontakPerson = "Bpk. Hendro", noHp = "081255443322", alamat = "Jl. Kertajaya No. 101, Surabaya"))

        // Produk 1: Seragam Batik SMP Nurul Ansor (Dengan Variasi Ukuran XS..XXL)
        val pr1 = dao.insertProduk(
          ProdukEntity(
            kodeBarang = "SRG-BTK-01",
            namaBarang = "Seragam Batik SMP Nurul Ansor",
            kategoriId = k1,
            hargaBeli = 65000.0,
            hargaJual = 85000.0,
            satuan = "Pcs",
            supplierId = s1,
            stokTotal = 56
          )
        )
        dao.insertVariasi(VariasiProdukEntity(produkId = pr1, ukuran = "XS", stok = 8))
        dao.insertVariasi(VariasiProdukEntity(produkId = pr1, ukuran = "S", stok = 10))
        dao.insertVariasi(VariasiProdukEntity(produkId = pr1, ukuran = "M", stok = 15))
        dao.insertVariasi(VariasiProdukEntity(produkId = pr1, ukuran = "L", stok = 12))
        dao.insertVariasi(VariasiProdukEntity(produkId = pr1, ukuran = "XL", stok = 7))
        dao.insertVariasi(VariasiProdukEntity(produkId = pr1, ukuran = "XXL", stok = 4))

        // Produk 2: Seragam Putih Biru SMP (Dengan Variasi)
        val pr2 = dao.insertProduk(
          ProdukEntity(
            kodeBarang = "SRG-PB-02",
            namaBarang = "Seragam Putih Biru SMP (Kemeja & Celana/Rok)",
            kategoriId = k1,
            hargaBeli = 80000.0,
            hargaJual = 105000.0,
            satuan = "Set",
            supplierId = s1,
            stokTotal = 44
          )
        )
        dao.insertVariasi(VariasiProdukEntity(produkId = pr2, ukuran = "S", stok = 10))
        dao.insertVariasi(VariasiProdukEntity(produkId = pr2, ukuran = "M", stok = 14))
        dao.insertVariasi(VariasiProdukEntity(produkId = pr2, ukuran = "L", stok = 12))
        dao.insertVariasi(VariasiProdukEntity(produkId = pr2, ukuran = "XL", stok = 8))

        // Produk 3: Dasi Bordir Logo SMP Nurul Ansor
        val pr3 = dao.insertProduk(
          ProdukEntity(
            kodeBarang = "ATR-DSI-03",
            namaBarang = "Dasi Bordir Logo SMP Nurul Ansor",
            kategoriId = k2,
            hargaBeli = 12000.0,
            hargaJual = 20000.0,
            satuan = "Pcs",
            supplierId = s1,
            stokTotal = 3
          ) // Low stock warning test! (<= 5)
        )
        dao.insertVariasi(VariasiProdukEntity(produkId = pr3, ukuran = "STANDAR", stok = 3))

        // Produk 4: Topi Upacara Bordir SMP
        val pr4 = dao.insertProduk(
          ProdukEntity(
            kodeBarang = "ATR-TPI-04",
            namaBarang = "Topi Upacara Bordir SMP Nurul Ansor",
            kategoriId = k2,
            hargaBeli = 15000.0,
            hargaJual = 25000.0,
            satuan = "Pcs",
            supplierId = s1,
            stokTotal = 35
          )
        )
        dao.insertVariasi(VariasiProdukEntity(produkId = pr4, ukuran = "STANDAR", stok = 35))

        // Produk 5: Buku Tulis Kotak & Bergaris 38 Lembar
        val pr5 = dao.insertProduk(
          ProdukEntity(
            kodeBarang = "ATK-BK-05",
            namaBarang = "Buku Tulis Koperasi 38 Lembar",
            kategoriId = k4,
            hargaBeli = 3500.0,
            hargaJual = 5000.0,
            satuan = "Buku",
            supplierId = s2,
            stokTotal = 120
          )
        )
        dao.insertVariasi(VariasiProdukEntity(produkId = pr5, ukuran = "STANDAR", stok = 120))

        // Sample POS Penjualan
        val pj1 = dao.insertPenjualan(
          TransaksiPenjualanEntity(
            noPenjualan = "TRX-202611-0001",
            pembeliNama = "H. Ahmad Dahlan, S.Pd.",
            guruId = g1,
            tanggalPenjualan = "2026-11-01 09:30",
            totalBayar = 105000.0,
            jumlahUang = 110000.0,
            kembalian = 5000.0,
            metodeBayar = "TUNAI",
            kasirUsername = "admin"
          )
        )
        dao.insertDetailPenjualanList(
          listOf(
            DetailPenjualanEntity(penjualanId = pj1, produkId = pr1, namaBarang = "Seragam Batik SMP Nurul Ansor", ukuran = "L", jumlah = 1, hargaSatuan = 85000.0, subtotal = 85000.0),
            DetailPenjualanEntity(penjualanId = pj1, produkId = pr3, namaBarang = "Dasi Bordir Logo SMP Nurul Ansor", ukuran = "STANDAR", jumlah = 1, hargaSatuan = 20000.0, subtotal = 20000.0)
          )
        )

        // Log Aktivitas Awal
        dao.insertLog(
          LogAktivitasEntity(
            username = "admin",
            peran = "admin",
            aktivitas = "Inisialisasi sistem database Koperasi Sekolah SMP Nurul Ansor",
            tanggalAktivitas = "2026-11-01 08:00"
          )
        )
      }
    }
  }
}
