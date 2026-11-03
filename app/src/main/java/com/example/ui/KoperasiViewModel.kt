package com.example.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.entity.*
import com.example.data.repository.CartItem
import com.example.data.repository.KoperasiRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

sealed class Screen(val title: String, val route: String) {
  object Login : Screen("Masuk Sistem", "login")

  // Admin Screens
  object AdminDashboard : Screen("Dashboard Utama", "admin_dashboard")
  object DataGuru : Screen("Data Anggota Guru", "data_guru")
  object IuranWajib : Screen("Iuran Wajib", "iuran_wajib")
  object SimpananGuru : Screen("Simpanan Anggota", "simpanan_guru")
  object Pinjaman : Screen("Kelola Pinjaman", "pinjaman")
  object Angsuran : Screen("Jadwal Angsuran", "angsuran")
  object Denda : Screen("Denda Keterlambatan", "denda")
  object TokoKoperasi : Screen("Toko Koperasi", "toko_koperasi")
  object Laporan : Screen("Laporan Keuangan", "laporan")
  object Pengaturan : Screen("Pengaturan Sistem", "pengaturan")

  // Guru Screens
  object GuruDashboard : Screen("Beranda Guru", "guru_dashboard")
  object GuruProfil : Screen("Profil Saya", "guru_profil")
  object GuruIuran : Screen("Iuran Wajib Saya", "guru_iuran")
  object GuruSimpanan : Screen("Simpanan Saya", "guru_simpanan")
  object GuruPinjaman : Screen("Pinjaman Saya", "guru_pinjaman")
  object GuruAngsuran : Screen("Jadwal Angsuran Saya", "guru_angsuran")
  object GuruDenda : Screen("Denda Saya", "guru_denda")
  object GuruRiwayatBelanja : Screen("Riwayat Belanja", "guru_belanja")
}

class KoperasiViewModel(
  private val repository: KoperasiRepository
) : ViewModel() {

  // Current logged in user
  private val _currentUser = MutableStateFlow<UserEntity?>(null)
  val currentUser: StateFlow<UserEntity?> = _currentUser.asStateFlow()

  // Current active navigation screen
  private val _currentScreen = MutableStateFlow<Screen>(Screen.Login)
  val currentScreen: StateFlow<Screen> = _currentScreen.asStateFlow()

  // Snackbar / Alert message
  private val _snackbarMessage = MutableStateFlow<String?>(null)
  val snackbarMessage: StateFlow<String?> = _snackbarMessage.asStateFlow()

  // Reactive Data Flows
  val guruList: StateFlow<List<GuruEntity>> = repository.getAllGuru()
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val activeGuruCount: StateFlow<Int> = repository.countActiveGuru()
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

  val iuranList: StateFlow<List<IuranEntity>> = repository.getAllIuran()
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val totalIuranLunas: StateFlow<Double> = repository.getTotalIuranLunas()
    .map { it ?: 0.0 }
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

  val simpananList: StateFlow<List<SimpananEntity>> = repository.getAllSimpanan()
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val totalSaldoSimpanan: StateFlow<Double> = repository.getTotalSaldoSimpanan()
    .map { it ?: 0.0 }
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

  val pinjamanList: StateFlow<List<PinjamanEntity>> = repository.getAllPinjaman()
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val totalPinjamanBerjalan: StateFlow<Double> = repository.getTotalPinjamanBerjalan()
    .map { it ?: 0.0 }
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

  val angsuranList: StateFlow<List<AngsuranEntity>> = repository.getAllAngsuran()
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val totalAngsuranDiterima: StateFlow<Double> = repository.getTotalAngsuranDiterima()
    .map { it ?: 0.0 }
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

  val dendaList: StateFlow<List<DendaEntity>> = repository.getAllDenda()
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val totalDendaLunas: StateFlow<Double> = repository.getTotalDendaLunas()
    .map { it ?: 0.0 }
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

  val produkList: StateFlow<List<ProdukEntity>> = repository.getAllProduk()
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val totalProdukCount: StateFlow<Int> = repository.countProduk()
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

  val totalPenjualan: StateFlow<Double> = repository.getTotalPenjualan()
    .map { it ?: 0.0 }
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

  val kategoriList: StateFlow<List<KategoriProdukEntity>> = repository.getAllKategori()
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val supplierList: StateFlow<List<SupplierEntity>> = repository.getAllSupplier()
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val penjualanList: StateFlow<List<TransaksiPenjualanEntity>> = repository.getAllPenjualan()
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val riwayatStokList: StateFlow<List<RiwayatStokEntity>> = repository.getAllRiwayatStok()
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val pengaturan: StateFlow<PengaturanEntity?> = repository.getPengaturan()
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

  val recentLogs: StateFlow<List<LogAktivitasEntity>> = repository.getRecentLogs()
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val allTransaksiSimpanan: StateFlow<List<TransaksiSimpananEntity>> = repository.getAllTransaksiSimpanan()
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  // POS Kasir Cart State
  private val _cart = MutableStateFlow<List<CartItem>>(emptyList())
  val cart: StateFlow<List<CartItem>> = _cart.asStateFlow()

  // Navigation action
  fun navigateTo(screen: Screen) {
    _currentScreen.value = screen
  }

  fun clearSnackbar() {
    _snackbarMessage.value = null
  }

  fun showMessage(msg: String) {
    _snackbarMessage.value = msg
  }

  // --- Auth ---
  fun login(username: String, password: String, onComplete: (Boolean) -> Unit) {
    viewModelScope.launch {
      val user = repository.login(username, password)
      if (user != null) {
        _currentUser.value = user
        if (user.role == "admin") {
          _currentScreen.value = Screen.AdminDashboard
        } else {
          _currentScreen.value = Screen.GuruDashboard
        }
        onComplete(true)
      } else {
        _snackbarMessage.value = "Username atau password salah!"
        onComplete(false)
      }
    }
  }

  fun logout() {
    _currentUser.value = null
    _cart.value = emptyList()
    _currentScreen.value = Screen.Login
  }

  // Quick switch for demo/test
  fun switchUserRole(role: String) {
    viewModelScope.launch {
      if (role == "admin") {
        login("admin", "admin123") {}
      } else {
        login("ahmad", "guru123") {}
      }
    }
  }

  // --- Data Guru CRUD ---
  fun tambahGuru(guru: GuruEntity, username: String, pass: String) {
    viewModelScope.launch {
      try {
        repository.insertGuru(guru, username, pass)
        _snackbarMessage.value = "Guru berhasil ditambahkan beserta akun login"
      } catch (e: Exception) {
        _snackbarMessage.value = "Gagal menambah guru: ${e.message}"
      }
    }
  }

  fun editGuru(guru: GuruEntity) {
    viewModelScope.launch {
      try {
        repository.updateGuru(guru)
        _snackbarMessage.value = "Data guru berhasil diperbarui"
      } catch (e: Exception) {
        _snackbarMessage.value = "Gagal memperbarui guru: ${e.message}"
      }
    }
  }

  fun hapusGuru(guru: GuruEntity) {
    viewModelScope.launch {
      try {
        repository.deleteGuru(guru)
        _snackbarMessage.value = "Data guru telah dihapus"
      } catch (e: Exception) {
        _snackbarMessage.value = "Gagal menghapus guru: ${e.message}"
      }
    }
  }

  // --- Iuran Wajib ---
  fun bayarIuran(iuran: IuranEntity) {
    viewModelScope.launch {
      val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
      repository.catatPembayaranIuran(iuran, today, _currentUser.value?.username ?: "admin")
      _snackbarMessage.value = "Pembayaran iuran berhasil dicatat lunas"
    }
  }

  // --- Simpanan ---
  fun tambahTransaksiSimpanan(
    guruId: Long,
    jenis: String,
    nominal: Double,
    keterangan: String
  ) {
    viewModelScope.launch {
      val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
      val res = repository.transaksiSimpanan(
        guruId = guruId,
        jenis = jenis,
        nominal = nominal,
        keterangan = keterangan,
        tanggal = today,
        adminUsername = _currentUser.value?.username ?: "admin"
      )
      if (res.isSuccess) {
        _snackbarMessage.value = "$jenis simpanan berhasil dicatat"
      } else {
        _snackbarMessage.value = res.exceptionOrNull()?.message ?: "Terjadi kesalahan"
      }
    }
  }

  // --- Pinjaman ---
  fun ajukanPinjaman(
    guruId: Long,
    jumlah: Double,
    tenor: Int,
    onSuccess: () -> Unit
  ) {
    viewModelScope.launch {
      val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
      val cal = Calendar.getInstance()
      cal.add(Calendar.MONTH, 1)
      val tglMulai = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(cal.time)

      val bulanIni = Calendar.getInstance().get(Calendar.MONTH) + 1
      val tahunIni = Calendar.getInstance().get(Calendar.YEAR)

      val result = repository.ajukanPinjaman(
        guruId = guruId,
        jumlahPinjaman = jumlah,
        lamaBulan = tenor,
        tanggalPengajuan = today,
        tanggalMulaiAngsuran = tglMulai,
        bulanBerjalan = bulanIni,
        tahunBerjalan = tahunIni
      )

      if (result.isSuccess) {
        _snackbarMessage.value = "Pengajuan pinjaman berhasil diajukan dan sedang menunggu persetujuan admin"
        onSuccess()
      } else {
        _snackbarMessage.value = result.exceptionOrNull()?.message ?: "Gagal mengajukan pinjaman"
      }
    }
  }

  fun setujuiPinjaman(pinjaman: PinjamanEntity) {
    viewModelScope.launch {
      repository.setujuiPinjaman(pinjaman, _currentUser.value?.username ?: "admin")
      _snackbarMessage.value = "Pinjaman ${pinjaman.noPinjaman} berhasil disetujui! Jadwal angsuran telah dibentuk."
    }
  }

  fun tolakPinjaman(pinjaman: PinjamanEntity, alasan: String) {
    viewModelScope.launch {
      repository.tolakPinjaman(pinjaman, alasan, _currentUser.value?.username ?: "admin")
      _snackbarMessage.value = "Pengajuan pinjaman telah ditolak."
    }
  }

  // --- Angsuran ---
  fun bayarAngsuran(angsuran: AngsuranEntity) {
    viewModelScope.launch {
      repository.bayarAngsuran(angsuran, _currentUser.value?.username ?: "admin")
      _snackbarMessage.value = "Pembayaran angsuran ke-${angsuran.angsuranKe} berhasil dicatat lunas"
    }
  }

  // --- Denda ---
  fun bayarDenda(denda: DendaEntity) {
    viewModelScope.launch {
      repository.bayarDenda(denda, _currentUser.value?.username ?: "admin")
      _snackbarMessage.value = "Denda keterlambatan berhasil dibayar lunas"
    }
  }

  // --- Toko: Produk & Variasi ---
  fun tambahProduk(produk: ProdukEntity, variasi: List<VariasiProdukEntity>) {
    viewModelScope.launch {
      try {
        repository.insertProduk(produk, variasi)
        _snackbarMessage.value = "Produk berhasil ditambahkan ke katalog toko"
      } catch (e: Exception) {
        _snackbarMessage.value = "Gagal menambah produk: ${e.message}"
      }
    }
  }

  fun hapusProduk(produk: ProdukEntity) {
    viewModelScope.launch {
      repository.deleteProduk(produk)
      _snackbarMessage.value = "Produk telah dihapus"
    }
  }

  // --- POS Cart Operations ---
  fun addToCart(produk: ProdukEntity, ukuran: String) {
    val current = _cart.value.toMutableList()
    val index = current.indexOfFirst { it.produkId == produk.id && it.ukuran == ukuran }
    if (index != -1) {
      val item = current[index]
      val newQty = item.jumlah + 1
      current[index] = item.copy(jumlah = newQty, subtotal = newQty * item.hargaJual)
    } else {
      current.add(
        CartItem(
          produkId = produk.id,
          namaBarang = produk.namaBarang,
          ukuran = ukuran,
          jumlah = 1,
          hargaBeli = produk.hargaBeli,
          hargaJual = produk.hargaJual,
          subtotal = produk.hargaJual
        )
      )
    }
    _cart.value = current
    _snackbarMessage.value = "Ditambahkan: ${produk.namaBarang} ($ukuran)"
  }

  fun updateCartQty(item: CartItem, delta: Int) {
    val current = _cart.value.toMutableList()
    val index = current.indexOfFirst { it.produkId == item.produkId && it.ukuran == item.ukuran }
    if (index != -1) {
      val newQty = item.jumlah + delta
      if (newQty <= 0) {
        current.removeAt(index)
      } else {
        current[index] = item.copy(jumlah = newQty, subtotal = newQty * item.hargaJual)
      }
      _cart.value = current
    }
  }

  fun clearCart() {
    _cart.value = emptyList()
  }

  fun checkoutPOS(
    pembeliNama: String,
    guruId: Long?,
    uangDiberikan: Double,
    metodeBayar: String,
    onSuccess: (String) -> Unit
  ) {
    viewModelScope.launch {
      val total = _cart.value.sumOf { it.subtotal }
      val kembalian = (uangDiberikan - total).coerceAtLeast(0.0)
      val res = repository.prosesPenjualan(
        pembeliNama = pembeliNama,
        guruId = guruId,
        cartItems = _cart.value,
        totalBayar = total,
        jumlahUang = uangDiberikan,
        kembalian = kembalian,
        metodeBayar = metodeBayar,
        kasirUsername = _currentUser.value?.username ?: "admin"
      )
      if (res.isSuccess) {
        val noStruk = res.getOrThrow()
        _cart.value = emptyList()
        _snackbarMessage.value = "Transaksi berhasil! No Struk: $noStruk"
        onSuccess(noStruk)
      } else {
        _snackbarMessage.value = res.exceptionOrNull()?.message ?: "Gagal memproses transaksi"
      }
    }
  }

  // --- Pengaturan ---
  fun updatePengaturan(pengaturan: PengaturanEntity) {
    viewModelScope.launch {
      repository.updatePengaturan(pengaturan, _currentUser.value?.username ?: "admin")
      _snackbarMessage.value = "Pengaturan koperasi berhasil disimpan"
    }
  }
}
