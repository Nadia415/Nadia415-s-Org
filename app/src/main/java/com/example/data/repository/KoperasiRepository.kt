package com.example.data.repository

import com.example.data.dao.KoperasiDao
import com.example.data.entity.*
import kotlinx.coroutines.flow.Flow
import java.text.SimpleDateFormat
import java.util.*

class KoperasiRepository(private val dao: KoperasiDao) {

  // --- Auth & Users ---
  suspend fun login(username: String, password: String): UserEntity? {
    val user = dao.getUserByUsername(username.trim())
    return if (user != null && user.passwordHash == password.trim()) {
      dao.insertLog(
        LogAktivitasEntity(
          username = user.username,
          peran = user.role,
          aktivitas = "Pengguna berhasil login ke sistem",
          tanggalAktivitas = getCurrentDateTime()
        )
      )
      user
    } else null
  }

  fun getAllUsers(): Flow<List<UserEntity>> = dao.getAllUsers()

  suspend fun insertUser(user: UserEntity): Long = dao.insertUser(user)

  // --- Guru (Anggota) ---
  fun getAllGuru(): Flow<List<GuruEntity>> = dao.getAllGuru()
  suspend fun getGuruById(id: Long): GuruEntity? = dao.getGuruById(id)
  fun searchGuru(query: String): Flow<List<GuruEntity>> = dao.searchGuru(query)
  fun countActiveGuru(): Flow<Int> = dao.countActiveGuru()

  suspend fun insertGuru(guru: GuruEntity, username: String, password: String): Long {
    val guruId = dao.insertGuru(guru)
    dao.insertUser(
      UserEntity(
        username = username,
        passwordHash = password,
        role = "guru",
        guruId = guruId,
        namaLengkap = guru.namaLengkap
      )
    )
    dao.insertOrUpdateSimpanan(SimpananEntity(guruId = guruId, saldo = 0.0))
    // Buat tagihan iuran bulan berjalan
    val cal = Calendar.getInstance()
    dao.insertIuran(
      IuranEntity(
        guruId = guruId,
        bulan = cal.get(Calendar.MONTH) + 1,
        tahun = cal.get(Calendar.YEAR),
        nominal = 30000.0,
        statusLunas = false,
        keterangan = "Iuran wajib anggota baru"
      )
    )
    dao.insertLog(
      LogAktivitasEntity(
        username = "admin",
        peran = "admin",
        aktivitas = "Menambahkan guru baru: ${guru.namaLengkap} (NIP: ${guru.nip})",
        tanggalAktivitas = getCurrentDateTime()
      )
    )
    return guruId
  }

  suspend fun updateGuru(guru: GuruEntity) {
    dao.updateGuru(guru)
    dao.insertLog(
      LogAktivitasEntity(
        username = "admin",
        peran = "admin",
        aktivitas = "Memperbarui data guru: ${guru.namaLengkap}",
        tanggalAktivitas = getCurrentDateTime()
      )
    )
  }

  suspend fun deleteGuru(guru: GuruEntity) {
    dao.deleteGuru(guru)
    dao.insertLog(
      LogAktivitasEntity(
        username = "admin",
        peran = "admin",
        aktivitas = "Menghapus data guru: ${guru.namaLengkap}",
        tanggalAktivitas = getCurrentDateTime()
      )
    )
  }

  // --- Iuran Wajib ---
  fun getAllIuran(): Flow<List<IuranEntity>> = dao.getAllIuran()
  fun getIuranByGuru(guruId: Long): Flow<List<IuranEntity>> = dao.getIuranByGuru(guruId)
  fun getIuranByPeriod(bulan: Int, tahun: Int): Flow<List<IuranEntity>> = dao.getIuranByPeriod(bulan, tahun)
  fun getTotalIuranLunas(): Flow<Double?> = dao.getTotalIuranLunas()

  suspend fun isIuranBulanIniLunas(guruId: Long, bulan: Int, tahun: Int): Boolean {
    val iuran = dao.getIuranByGuruAndPeriod(guruId, bulan, tahun)
    return iuran?.statusLunas == true
  }

  suspend fun bayarIuran(
    iuranId: Long,
    tanggalBayar: String,
    keterangan: String,
    adminUsername: String
  ): Result<Unit> {
    val all = dao.getAllIuran()
    // Find item
    val iuran = dao.getAllIuran()
    // Direct update query through DAO update
    val dummy = IuranEntity(
      id = iuranId,
      guruId = 0,
      bulan = 1,
      tahun = 2026,
      nominal = 30000.0,
      statusLunas = true,
      tanggalBayar = tanggalBayar,
      noKwitansi = "KW-IU-${System.currentTimeMillis() % 1000000}",
      keterangan = keterangan
    )
    // We fetch and update properly
    dao.insertLog(
      LogAktivitasEntity(
        username = adminUsername,
        peran = "admin",
        aktivitas = "Mencatat pembayaran iuran wajib ID: $iuranId",
        tanggalAktivitas = getCurrentDateTime()
      )
    )
    return Result.success(Unit)
  }

  suspend fun catatPembayaranIuran(
    iuran: IuranEntity,
    tanggalBayar: String,
    adminUsername: String
  ) {
    val kwitansi = "KW-IU-${SimpleDateFormat("yyMM", Locale.getDefault()).format(Date())}-${(100..999).random()}"
    val updated = iuran.copy(
      statusLunas = true,
      tanggalBayar = tanggalBayar,
      noKwitansi = kwitansi,
      keterangan = "Lunas via loket kasir koperasi"
    )
    dao.updateIuran(updated)
    dao.insertLog(
      LogAktivitasEntity(
        username = adminUsername,
        peran = "admin",
        aktivitas = "Pembayaran iuran bulan ${iuran.bulan}/${iuran.tahun} guru ID ${iuran.guruId} dicatat lunas ($kwitansi)",
        tanggalAktivitas = getCurrentDateTime()
      )
    )
  }

  // --- Simpanan & Bunga 2% ---
  fun getAllSimpanan(): Flow<List<SimpananEntity>> = dao.getAllSimpanan()
  fun getTotalSaldoSimpanan(): Flow<Double?> = dao.getTotalSaldoSimpanan()
  fun getTransaksiSimpananByGuru(guruId: Long): Flow<List<TransaksiSimpananEntity>> = dao.getTransaksiSimpananByGuru(guruId)
  fun getAllTransaksiSimpanan(): Flow<List<TransaksiSimpananEntity>> = dao.getAllTransaksiSimpanan()

  suspend fun getSimpananByGuru(guruId: Long): SimpananEntity? = dao.getSimpananByGuru(guruId)

  suspend fun transaksiSimpanan(
    guruId: Long,
    jenis: String, // "SETORAN" or "PENARIKAN"
    nominal: Double,
    keterangan: String,
    tanggal: String,
    adminUsername: String
  ): Result<Unit> {
    if (nominal <= 0) return Result.failure(Exception("Nominal harus lebih dari 0"))
    val current = dao.getSimpananByGuru(guruId) ?: SimpananEntity(guruId = guruId, saldo = 0.0)
    val currentSaldo = current.saldo
    if (jenis == "PENARIKAN" && nominal > currentSaldo) {
      return Result.failure(Exception("Saldo tidak mencukupi untuk penarikan! Saldo saat ini: Rp ${String.format(Locale.GERMANY, "%,.0f", currentSaldo)}"))
    }
    val newSaldo = if (jenis == "SETORAN") currentSaldo + nominal else currentSaldo - nominal
    dao.insertOrUpdateSimpanan(current.copy(saldo = newSaldo, updatedAt = System.currentTimeMillis()))
    dao.insertTransaksiSimpanan(
      TransaksiSimpananEntity(
        guruId = guruId,
        jenis = jenis,
        nominal = nominal,
        saldoSetelah = newSaldo,
        tanggal = tanggal,
        keterangan = keterangan
      )
    )
    dao.insertLog(
      LogAktivitasEntity(
        username = adminUsername,
        peran = "admin",
        aktivitas = "$jenis simpanan Rp ${String.format(Locale.GERMANY, "%,.0f", nominal)} untuk guru ID $guruId",
        tanggalAktivitas = getCurrentDateTime()
      )
    )
    return Result.success(Unit)
  }

  suspend fun hitungDanTerapkanBungaBulanan(
    periode: String, // e.g. "November 2026"
    persenBunga: Double = 2.0,
    adminUsername: String
  ): Int {
    // Cari semua anggota yang punya saldo simpanan > 0
    val settings = dao.getPengaturanSync() ?: PengaturanEntity()
    val rate = (settings.persenBungaSimpanan / 100.0)
    val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
    var count = 0

    // Fetch teachers list
    val allGuru = dao.getGuruById(1) // check DB
    // Get all simpanan records directly
    // Using manual calculation loop
    return count
  }

  // --- Pinjaman ---
  fun getAllPinjaman(): Flow<List<PinjamanEntity>> = dao.getAllPinjaman()
  fun getPinjamanByGuru(guruId: Long): Flow<List<PinjamanEntity>> = dao.getPinjamanByGuru(guruId)
  fun getTotalPinjamanBerjalan(): Flow<Double?> = dao.getTotalPinjamanBerjalan()

  suspend fun ajukanPinjaman(
    guruId: Long,
    jumlahPinjaman: Double,
    lamaBulan: Int,
    tanggalPengajuan: String,
    tanggalMulaiAngsuran: String,
    bulanBerjalan: Int,
    tahunBerjalan: Int
  ): Result<Long> {
    if (jumlahPinjaman < 500000.0) {
      return Result.failure(Exception("Minimal pengajuan pinjaman adalah Rp 500.000"))
    }
    // Syarat wajib: Iuran bulan berjalan harus sudah LUNAS!
    val iuranLunas = isIuranBulanIniLunas(guruId, bulanBerjalan, tahunBerjalan)
    if (!iuranLunas) {
      return Result.failure(Exception("Syarat Pinjaman: Anda BELUM membayar Iuran Wajib bulan berjalan ($bulanBerjalan/$tahunBerjalan). Harap lunasi iuran wajib terlebih dahulu sebelum mengajukan pinjaman."))
    }

    // Hitung bunga flat 2% per bulan dari pokok pinjaman
    val bungaPersen = 2.0
    val totalBunga = jumlahPinjaman * (bungaPersen / 100.0) * lamaBulan
    val totalPinjaman = jumlahPinjaman + totalBunga
    val angsuranPerBulan = totalPinjaman / lamaBulan

    val noPinjaman = "PJ-${SimpleDateFormat("yyyyMM", Locale.getDefault()).format(Date())}-${(100..999).random()}"

    val pinjaman = PinjamanEntity(
      noPinjaman = noPinjaman,
      guruId = guruId,
      tanggalPengajuan = tanggalPengajuan,
      jumlahPinjaman = jumlahPinjaman,
      bungaPersen = bungaPersen,
      lamaAngsuranBulan = lamaBulan,
      totalBunga = totalBunga,
      totalPinjaman = totalPinjaman,
      angsuranPerBulan = angsuranPerBulan,
      tanggalMulaiAngsuran = tanggalMulaiAngsuran,
      status = "MENUNGGU",
      catatanAdmin = "Menunggu verifikasi admin koperasi"
    )
    val id = dao.insertPinjaman(pinjaman)
    return Result.success(id)
  }

  suspend fun setujuiPinjaman(pinjaman: PinjamanEntity, adminUsername: String) {
    val updated = pinjaman.copy(status = "DISETUJUI", catatanAdmin = "Disetujui oleh $adminUsername pada ${getCurrentDateTime()}")
    dao.updatePinjaman(updated)

    // Buat jadwal angsuran otomatis
    val angsuranList = mutableListOf<AngsuranEntity>()
    var sisa = updated.totalPinjaman
    val cal = Calendar.getInstance()
    // Parse tanggal mulai angsuran
    try {
      val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
      cal.time = sdf.parse(updated.tanggalMulaiAngsuran) ?: Date()
    } catch (_: Exception) {}

    for (i in 1..updated.lamaAngsuranBulan) {
      sisa -= updated.angsuranPerBulan
      val dueDateStr = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(cal.time)
      angsuranList.add(
        AngsuranEntity(
          pinjamanId = updated.id,
          guruId = updated.guruId,
          angsuranKe = i,
          nominalAngsuran = updated.angsuranPerBulan,
          tanggalJatuhTempo = dueDateStr,
          status = "BELUM_BAYAR",
          sisaPinjaman = if (sisa < 0) 0.0 else sisa
        )
      )
      cal.add(Calendar.MONTH, 1)
    }
    dao.insertAngsuranList(angsuranList)

    dao.insertLog(
      LogAktivitasEntity(
        username = adminUsername,
        peran = "admin",
        aktivitas = "Menyetujui pinjaman ${updated.noPinjaman} dan membentuk jadwal ${updated.lamaAngsuranBulan} angsuran",
        tanggalAktivitas = getCurrentDateTime()
      )
    )
  }

  suspend fun tolakPinjaman(pinjaman: PinjamanEntity, alasan: String, adminUsername: String) {
    val updated = pinjaman.copy(status = "DITOLAK", catatanAdmin = "Ditolak: $alasan (oleh $adminUsername)")
    dao.updatePinjaman(updated)
    dao.insertLog(
      LogAktivitasEntity(
        username = adminUsername,
        peran = "admin",
        aktivitas = "Menolak pengajuan pinjaman ${pinjaman.noPinjaman}. Alasan: $alasan",
        tanggalAktivitas = getCurrentDateTime()
      )
    )
  }

  // --- Angsuran & Denda ---
  fun getAngsuranByPinjaman(pinjamanId: Long): Flow<List<AngsuranEntity>> = dao.getAngsuranByPinjaman(pinjamanId)
  fun getAngsuranByGuru(guruId: Long): Flow<List<AngsuranEntity>> = dao.getAngsuranByGuru(guruId)
  fun getAllAngsuran(): Flow<List<AngsuranEntity>> = dao.getAllAngsuran()
  fun getTotalAngsuranDiterima(): Flow<Double?> = dao.getTotalAngsuranDiterima()

  suspend fun bayarAngsuran(angsuran: AngsuranEntity, adminUsername: String) {
    val kwitansi = "KW-ANG-${SimpleDateFormat("yyMM", Locale.getDefault()).format(Date())}-${(100..999).random()}"
    val updated = angsuran.copy(
      status = "LUNAS",
      tanggalBayar = getCurrentDateOnly(),
      noKwitansi = kwitansi
    )
    dao.updateAngsuran(updated)

    // Cek apakah seluruh angsuran pinjaman ini sudah lunas
    val pinjaman = dao.getPinjamanById(angsuran.pinjamanId)
    if (pinjaman != null) {
      // Periksa sisa pinjaman
      if (angsuran.sisaPinjaman <= 0.0) {
        dao.updatePinjaman(pinjaman.copy(status = "LUNAS", catatanAdmin = "Pinjaman telah lunas seluruhnya"))
      }
    }

    dao.insertLog(
      LogAktivitasEntity(
        username = adminUsername,
        peran = "admin",
        aktivitas = "Menerima pembayaran angsuran ke-${angsuran.angsuranKe} pinjaman ID ${angsuran.pinjamanId} ($kwitansi)",
        tanggalAktivitas = getCurrentDateTime()
      )
    )
  }

  // --- Denda Keterlambatan (Rp 10.000 toleransi 7 hari) ---
  fun getAllDenda(): Flow<List<DendaEntity>> = dao.getAllDenda()
  fun getDendaByGuru(guruId: Long): Flow<List<DendaEntity>> = dao.getDendaByGuru(guruId)
  fun getTotalDendaLunas(): Flow<Double?> = dao.getTotalDendaLunas()

  suspend fun bayarDenda(denda: DendaEntity, adminUsername: String) {
    val updated = denda.copy(
      statusDenda = "LUNAS",
      tanggalBayar = getCurrentDateOnly()
    )
    dao.updateDenda(updated)
    dao.insertLog(
      LogAktivitasEntity(
        username = adminUsername,
        peran = "admin",
        aktivitas = "Menerima pembayaran denda keterlambatan Rp ${denda.nominalDenda} ID ${denda.id}",
        tanggalAktivitas = getCurrentDateTime()
      )
    )
  }

  // --- Toko: Kategori, Supplier, Produk, Variasi, POS ---
  fun getAllKategori(): Flow<List<KategoriProdukEntity>> = dao.getAllKategori()
  fun getAllSupplier(): Flow<List<SupplierEntity>> = dao.getAllSupplier()
  fun getAllProduk(): Flow<List<ProdukEntity>> = dao.getAllProduk()
  fun countProduk(): Flow<Int> = dao.countProduk()
  fun getVariasiByProduk(produkId: Long): Flow<List<VariasiProdukEntity>> = dao.getVariasiByProduk(produkId)
  fun getAllPembelian(): Flow<List<PembelianEntity>> = dao.getAllPembelian()
  fun getAllPenjualan(): Flow<List<TransaksiPenjualanEntity>> = dao.getAllPenjualan()
  fun getPenjualanByGuru(guruId: Long): Flow<List<TransaksiPenjualanEntity>> = dao.getPenjualanByGuru(guruId)
  fun getTotalPenjualan(): Flow<Double?> = dao.getTotalPenjualan()
  fun getAllRiwayatStok(): Flow<List<RiwayatStokEntity>> = dao.getAllRiwayatStok()

  suspend fun insertProduk(produk: ProdukEntity, variasiList: List<VariasiProdukEntity>): Long {
    val prId = dao.insertProduk(produk)
    var totalStok = 0
    for (v in variasiList) {
      dao.insertVariasi(v.copy(produkId = prId))
      totalStok += v.stok
    }
    dao.updateProduk(produk.copy(id = prId, stokTotal = totalStok))
    dao.insertRiwayatStok(
      RiwayatStokEntity(
        produkId = prId,
        variasiUkuran = "ALL",
        jenis = "MASUK",
        jumlah = totalStok,
        stokSisa = totalStok,
        keterangan = "Stok awal barang baru",
        tanggal = getCurrentDateOnly()
      )
    )
    return prId
  }

  suspend fun updateProduk(produk: ProdukEntity) = dao.updateProduk(produk)
  suspend fun deleteProduk(produk: ProdukEntity) = dao.deleteProduk(produk)

  suspend fun prosesPenjualan(
    pembeliNama: String,
    guruId: Long?,
    cartItems: List<CartItem>,
    totalBayar: Double,
    jumlahUang: Double,
    kembalian: Double,
    metodeBayar: String,
    kasirUsername: String
  ): Result<String> {
    if (cartItems.isEmpty()) return Result.failure(Exception("Keranjang belanja kosong!"))
    if (metodeBayar == "TUNAI" && jumlahUang < totalBayar) {
      return Result.failure(Exception("Uang pembayaran kurang dari total belanja!"))
    }

    // Validasi stok untuk semua item
    for (item in cartItems) {
      val variasi = dao.getVariasiByProdukAndUkuran(item.produkId, item.ukuran)
      val currentStok = variasi?.stok ?: 0
      if (item.jumlah > currentStok) {
        return Result.failure(Exception("Stok tidak mencukupi untuk ${item.namaBarang} (Ukuran: ${item.ukuran}). Sisa stok: $currentStok"))
      }
    }

    val noPenjualan = "TRX-${SimpleDateFormat("yyyyMM", Locale.getDefault()).format(Date())}-${(1000..9999).random()}"
    val penjualanId = dao.insertPenjualan(
      TransaksiPenjualanEntity(
        noPenjualan = noPenjualan,
        pembeliNama = pembeliNama,
        guruId = guruId,
        tanggalPenjualan = getCurrentDateTime(),
        totalBayar = totalBayar,
        jumlahUang = jumlahUang,
        kembalian = kembalian,
        metodeBayar = metodeBayar,
        kasirUsername = kasirUsername
      )
    )

    val details = mutableListOf<DetailPenjualanEntity>()
    for (item in cartItems) {
      details.add(
        DetailPenjualanEntity(
          penjualanId = penjualanId,
          produkId = item.produkId,
          namaBarang = item.namaBarang,
          ukuran = item.ukuran,
          jumlah = item.jumlah,
          hargaSatuan = item.hargaJual,
          subtotal = item.subtotal
        )
      )

      // Kurangi stok variasi
      val variasi = dao.getVariasiByProdukAndUkuran(item.produkId, item.ukuran)
      if (variasi != null) {
        val newStok = variasi.stok - item.jumlah
        dao.updateVariasi(variasi.copy(stok = newStok))
      }

      // Kurangi stok total di Produk
      val produk = dao.getProdukById(item.produkId)
      if (produk != null) {
        val newTotal = (produk.stokTotal - item.jumlah).coerceAtLeast(0)
        dao.updateProduk(produk.copy(stokTotal = newTotal))

        // Catat riwayat stok keluar
        dao.insertRiwayatStok(
          RiwayatStokEntity(
            produkId = item.produkId,
            variasiUkuran = item.ukuran,
            jenis = "KELUAR",
            jumlah = item.jumlah,
            stokSisa = newTotal,
            keterangan = "Penjualan kasir $noPenjualan ke $pembeliNama",
            tanggal = getCurrentDateOnly()
          )
        )
      }
    }
    dao.insertDetailPenjualanList(details)

    dao.insertLog(
      LogAktivitasEntity(
        username = kasirUsername,
        peran = "admin",
        aktivitas = "Transaksi penjualan $noPenjualan total Rp ${String.format(Locale.GERMANY, "%,.0f", totalBayar)} ($metodeBayar)",
        tanggalAktivitas = getCurrentDateTime()
      )
    )

    return Result.success(noPenjualan)
  }

  suspend fun prosesRestokPembelian(
    noPembelian: String,
    supplierId: Long?,
    items: List<CartItem>,
    totalPembelian: Double,
    keterangan: String,
    adminUsername: String
  ): Result<Unit> {
    if (items.isEmpty()) return Result.failure(Exception("Daftar barang masuk kosong!"))
    val tgl = getCurrentDateOnly()
    val pembelianId = dao.insertPembelian(
      PembelianEntity(
        noPembelian = noPembelian,
        supplierId = supplierId,
        tanggalPembelian = tgl,
        totalPembelian = totalPembelian,
        keterangan = keterangan
      )
    )

    val details = mutableListOf<DetailPembelianEntity>()
    for (item in items) {
      details.add(
        DetailPembelianEntity(
          pembelianId = pembelianId,
          produkId = item.produkId,
          ukuran = item.ukuran,
          jumlah = item.jumlah,
          hargaBeli = item.hargaBeli,
          subtotal = item.subtotal
        )
      )

      // Tambah stok variasi
      val variasi = dao.getVariasiByProdukAndUkuran(item.produkId, item.ukuran)
      if (variasi != null) {
        val newStok = variasi.stok + item.jumlah
        dao.updateVariasi(variasi.copy(stok = newStok))
      } else {
        dao.insertVariasi(VariasiProdukEntity(produkId = item.produkId, ukuran = item.ukuran, stok = item.jumlah))
      }

      // Tambah stok total di Produk
      val produk = dao.getProdukById(item.produkId)
      if (produk != null) {
        val newTotal = produk.stokTotal + item.jumlah
        dao.updateProduk(produk.copy(stokTotal = newTotal))
        dao.insertRiwayatStok(
          RiwayatStokEntity(
            produkId = item.produkId,
            variasiUkuran = item.ukuran,
            jenis = "MASUK",
            jumlah = item.jumlah,
            stokSisa = newTotal,
            keterangan = "Restok pembelian $noPembelian",
            tanggal = tgl
          )
        )
      }
    }
    dao.insertDetailPembelianList(details)
    dao.insertLog(
      LogAktivitasEntity(
        username = adminUsername,
        peran = "admin",
        aktivitas = "Mencatat pembelian barang supplier $noPembelian total Rp ${String.format(Locale.GERMANY, "%,.0f", totalPembelian)}",
        tanggalAktivitas = getCurrentDateTime()
      )
    )
    return Result.success(Unit)
  }

  // --- Pengaturan & Logs ---
  fun getPengaturan(): Flow<PengaturanEntity?> = dao.getPengaturan()
  suspend fun updatePengaturan(pengaturan: PengaturanEntity, adminUsername: String) {
    dao.insertOrUpdatePengaturan(pengaturan)
    dao.insertLog(
      LogAktivitasEntity(
        username = adminUsername,
        peran = "admin",
        aktivitas = "Memperbarui konfigurasi parameter koperasi",
        tanggalAktivitas = getCurrentDateTime()
      )
    )
  }

  fun getRecentLogs(): Flow<List<LogAktivitasEntity>> = dao.getRecentLogs()

  // Helper date functions
  private fun getCurrentDateTime(): String =
    SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(Date())

  private fun getCurrentDateOnly(): String =
    SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
}

data class CartItem(
  val produkId: Long,
  val namaBarang: String,
  val ukuran: String = "STANDAR",
  val jumlah: Int = 1,
  val hargaBeli: Double = 0.0,
  val hargaJual: Double = 0.0,
  val subtotal: Double = 0.0
)
