package com.example.data.dao

import androidx.room.*
import com.example.data.entity.*
import kotlinx.coroutines.flow.Flow

@Dao
interface KoperasiDao {

  // --- Users & Auth ---
  @Query("SELECT * FROM users WHERE username = :username LIMIT 1")
  suspend fun getUserByUsername(username: String): UserEntity?

  @Query("SELECT * FROM users ORDER BY id ASC")
  fun getAllUsers(): Flow<List<UserEntity>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertUser(user: UserEntity): Long

  @Delete
  suspend fun deleteUser(user: UserEntity)

  // --- Guru (Anggota) ---
  @Query("SELECT * FROM guru ORDER BY namaLengkap ASC")
  fun getAllGuru(): Flow<List<GuruEntity>>

  @Query("SELECT * FROM guru WHERE id = :id LIMIT 1")
  suspend fun getGuruById(id: Long): GuruEntity?

  @Query("SELECT * FROM guru WHERE namaLengkap LIKE '%' || :query || '%' OR nip LIKE '%' || :query || '%'")
  fun searchGuru(query: String): Flow<List<GuruEntity>>

  @Query("SELECT COUNT(*) FROM guru WHERE statusAktif = 1")
  fun countActiveGuru(): Flow<Int>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertGuru(guru: GuruEntity): Long

  @Update
  suspend fun updateGuru(guru: GuruEntity)

  @Delete
  suspend fun deleteGuru(guru: GuruEntity)

  // --- Iuran Wajib ---
  @Query("SELECT * FROM iuran ORDER BY tahun DESC, bulan DESC, id DESC")
  fun getAllIuran(): Flow<List<IuranEntity>>

  @Query("SELECT * FROM iuran WHERE guruId = :guruId ORDER BY tahun DESC, bulan DESC")
  fun getIuranByGuru(guruId: Long): Flow<List<IuranEntity>>

  @Query("SELECT * FROM iuran WHERE bulan = :bulan AND tahun = :tahun")
  fun getIuranByPeriod(bulan: Int, tahun: Int): Flow<List<IuranEntity>>

  @Query("SELECT * FROM iuran WHERE guruId = :guruId AND bulan = :bulan AND tahun = :tahun LIMIT 1")
  suspend fun getIuranByGuruAndPeriod(guruId: Long, bulan: Int, tahun: Int): IuranEntity?

  @Query("SELECT SUM(nominal) FROM iuran WHERE statusLunas = 1")
  fun getTotalIuranLunas(): Flow<Double?>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertIuran(iuran: IuranEntity): Long

  @Update
  suspend fun updateIuran(iuran: IuranEntity)

  // --- Simpanan & Transaksi Simpanan ---
  @Query("SELECT * FROM simpanan WHERE guruId = :guruId LIMIT 1")
  suspend fun getSimpananByGuru(guruId: Long): SimpananEntity?

  @Query("SELECT * FROM simpanan")
  fun getAllSimpanan(): Flow<List<SimpananEntity>>

  @Query("SELECT SUM(saldo) FROM simpanan")
  fun getTotalSaldoSimpanan(): Flow<Double?>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertOrUpdateSimpanan(simpanan: SimpananEntity): Long

  @Query("SELECT * FROM transaksi_simpanan WHERE guruId = :guruId ORDER BY id DESC")
  fun getTransaksiSimpananByGuru(guruId: Long): Flow<List<TransaksiSimpananEntity>>

  @Query("SELECT * FROM transaksi_simpanan ORDER BY id DESC")
  fun getAllTransaksiSimpanan(): Flow<List<TransaksiSimpananEntity>>

  @Insert
  suspend fun insertTransaksiSimpanan(transaksi: TransaksiSimpananEntity): Long

  // --- Pinjaman ---
  @Query("SELECT * FROM pinjaman ORDER BY id DESC")
  fun getAllPinjaman(): Flow<List<PinjamanEntity>>

  @Query("SELECT * FROM pinjaman WHERE guruId = :guruId ORDER BY id DESC")
  fun getPinjamanByGuru(guruId: Long): Flow<List<PinjamanEntity>>

  @Query("SELECT * FROM pinjaman WHERE id = :id LIMIT 1")
  suspend fun getPinjamanById(id: Long): PinjamanEntity?

  @Query("SELECT SUM(totalPinjaman) FROM pinjaman WHERE status = 'DISETUJUI'")
  fun getTotalPinjamanBerjalan(): Flow<Double?>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertPinjaman(pinjaman: PinjamanEntity): Long

  @Update
  suspend fun updatePinjaman(pinjaman: PinjamanEntity)

  // --- Angsuran ---
  @Query("SELECT * FROM angsuran WHERE pinjamanId = :pinjamanId ORDER BY angsuranKe ASC")
  fun getAngsuranByPinjaman(pinjamanId: Long): Flow<List<AngsuranEntity>>

  @Query("SELECT * FROM angsuran WHERE guruId = :guruId ORDER BY id DESC")
  fun getAngsuranByGuru(guruId: Long): Flow<List<AngsuranEntity>>

  @Query("SELECT * FROM angsuran ORDER BY id DESC")
  fun getAllAngsuran(): Flow<List<AngsuranEntity>>

  @Query("SELECT * FROM angsuran WHERE id = :id LIMIT 1")
  suspend fun getAngsuranById(id: Long): AngsuranEntity?

  @Query("SELECT SUM(nominalAngsuran) FROM angsuran WHERE status = 'LUNAS'")
  fun getTotalAngsuranDiterima(): Flow<Double?>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertAngsuranList(angsuranList: List<AngsuranEntity>)

  @Update
  suspend fun updateAngsuran(angsuran: AngsuranEntity)

  // --- Denda ---
  @Query("SELECT * FROM denda ORDER BY id DESC")
  fun getAllDenda(): Flow<List<DendaEntity>>

  @Query("SELECT * FROM denda WHERE guruId = :guruId ORDER BY id DESC")
  fun getDendaByGuru(guruId: Long): Flow<List<DendaEntity>>

  @Query("SELECT * FROM denda WHERE angsuranId = :angsuranId LIMIT 1")
  suspend fun getDendaByAngsuran(angsuranId: Long): DendaEntity?

  @Query("SELECT SUM(nominalDenda) FROM denda WHERE statusDenda = 'LUNAS'")
  fun getTotalDendaLunas(): Flow<Double?>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertDenda(denda: DendaEntity): Long

  @Update
  suspend fun updateDenda(denda: DendaEntity)

  // --- Toko: Kategori, Supplier, Produk, Variasi ---
  @Query("SELECT * FROM kategori_produk ORDER BY namaKategori ASC")
  fun getAllKategori(): Flow<List<KategoriProdukEntity>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertKategori(kategori: KategoriProdukEntity): Long

  @Query("SELECT * FROM supplier ORDER BY namaSupplier ASC")
  fun getAllSupplier(): Flow<List<SupplierEntity>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertSupplier(supplier: SupplierEntity): Long

  @Query("SELECT * FROM produk ORDER BY namaBarang ASC")
  fun getAllProduk(): Flow<List<ProdukEntity>>

  @Query("SELECT * FROM produk WHERE id = :id LIMIT 1")
  suspend fun getProdukById(id: Long): ProdukEntity?

  @Query("SELECT COUNT(*) FROM produk")
  fun countProduk(): Flow<Int>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertProduk(produk: ProdukEntity): Long

  @Update
  suspend fun updateProduk(produk: ProdukEntity)

  @Delete
  suspend fun deleteProduk(produk: ProdukEntity)

  @Query("SELECT * FROM variasi_produk WHERE produkId = :produkId")
  fun getVariasiByProduk(produkId: Long): Flow<List<VariasiProdukEntity>>

  @Query("SELECT * FROM variasi_produk WHERE produkId = :produkId")
  suspend fun getVariasiListByProduk(produkId: Long): List<VariasiProdukEntity>

  @Query("SELECT * FROM variasi_produk WHERE produkId = :produkId AND ukuran = :ukuran LIMIT 1")
  suspend fun getVariasiByProdukAndUkuran(produkId: Long, ukuran: String): VariasiProdukEntity?

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertVariasi(variasi: VariasiProdukEntity): Long

  @Update
  suspend fun updateVariasi(variasi: VariasiProdukEntity)

  // --- Pembelian Toko ---
  @Query("SELECT * FROM pembelian ORDER BY id DESC")
  fun getAllPembelian(): Flow<List<PembelianEntity>>

  @Insert
  suspend fun insertPembelian(pembelian: PembelianEntity): Long

  @Insert
  suspend fun insertDetailPembelianList(details: List<DetailPembelianEntity>)

  // --- Penjualan Toko (POS) ---
  @Query("SELECT * FROM transaksi_penjualan ORDER BY id DESC")
  fun getAllPenjualan(): Flow<List<TransaksiPenjualanEntity>>

  @Query("SELECT * FROM transaksi_penjualan WHERE guruId = :guruId ORDER BY id DESC")
  fun getPenjualanByGuru(guruId: Long): Flow<List<TransaksiPenjualanEntity>>

  @Query("SELECT SUM(totalBayar) FROM transaksi_penjualan")
  fun getTotalPenjualan(): Flow<Double?>

  @Insert
  suspend fun insertPenjualan(penjualan: TransaksiPenjualanEntity): Long

  @Insert
  suspend fun insertDetailPenjualanList(details: List<DetailPenjualanEntity>)

  @Query("SELECT * FROM detail_penjualan WHERE penjualanId = :penjualanId")
  fun getDetailPenjualan(penjualanId: Long): Flow<List<DetailPenjualanEntity>>

  @Query("SELECT * FROM detail_penjualan WHERE penjualanId = :penjualanId")
  suspend fun getDetailPenjualanList(penjualanId: Long): List<DetailPenjualanEntity>

  // --- Riwayat Stok ---
  @Query("SELECT * FROM riwayat_stok ORDER BY id DESC")
  fun getAllRiwayatStok(): Flow<List<RiwayatStokEntity>>

  @Insert
  suspend fun insertRiwayatStok(riwayat: RiwayatStokEntity): Long

  // --- Pengaturan ---
  @Query("SELECT * FROM pengaturan WHERE id = 1 LIMIT 1")
  fun getPengaturan(): Flow<PengaturanEntity?>

  @Query("SELECT * FROM pengaturan WHERE id = 1 LIMIT 1")
  suspend fun getPengaturanSync(): PengaturanEntity?

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertOrUpdatePengaturan(pengaturan: PengaturanEntity)

  // --- Log Aktivitas ---
  @Query("SELECT * FROM log_aktivitas ORDER BY id DESC LIMIT 50")
  fun getRecentLogs(): Flow<List<LogAktivitasEntity>>

  @Insert
  suspend fun insertLog(log: LogAktivitasEntity): Long
}
