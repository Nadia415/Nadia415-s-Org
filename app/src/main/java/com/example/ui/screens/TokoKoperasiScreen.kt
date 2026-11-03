package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.entity.*
import com.example.data.repository.CartItem
import com.example.ui.KoperasiViewModel
import com.example.ui.components.KwitansiReceiptDialog
import com.example.ui.components.SectionHeader
import com.example.ui.components.StatusBadge
import com.example.ui.components.formatRupiah
import com.example.ui.theme.*

@Composable
fun TokoKoperasiScreen(
  viewModel: KoperasiViewModel
) {
  val produkList by viewModel.produkList.collectAsStateWithLifecycle()
  val kategoriList by viewModel.kategoriList.collectAsStateWithLifecycle()
  val supplierList by viewModel.supplierList.collectAsStateWithLifecycle()
  val cart by viewModel.cart.collectAsStateWithLifecycle()
  val penjualanList by viewModel.penjualanList.collectAsStateWithLifecycle()
  val riwayatStokList by viewModel.riwayatStokList.collectAsStateWithLifecycle()
  val guruList by viewModel.guruList.collectAsStateWithLifecycle()

  var selectedTab by remember { mutableStateOf(0) } // 0: Kasir POS, 1: Produk & Stok, 2: Pembelian, 3: Riwayat

  // POS State
  var searchQuery by remember { mutableStateOf("") }
  var selectedKategoriId by remember { mutableStateOf<Long?>(null) }
  var showCheckoutDialog by remember { mutableStateOf(false) }
  var receiptToShow by remember { mutableStateOf<TransaksiPenjualanEntity?>(null) }

  // Size Picker State for Uniform Products
  var productForSizePick by remember { mutableStateOf<ProdukEntity?>(null) }

  // Add Product State
  var showAddProductDialog by remember { mutableStateOf(false) }

  Column(
    modifier = Modifier
      .fillMaxSize()
      .background(SlateBackground)
  ) {
    // Navigation Tabs for Store
    ScrollableTabRow(
      selectedTabIndex = selectedTab,
      containerColor = NavyDark,
      contentColor = Color.White,
      edgePadding = 12.dp
    ) {
      Tab(
        selected = selectedTab == 0,
        onClick = { selectedTab = 0 },
        text = {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.PointOfSale, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text("Kasir POS (${cart.size})", fontWeight = FontWeight.Bold, fontSize = 12.sp)
          }
        }
      )
      Tab(
        selected = selectedTab == 1,
        onClick = { selectedTab = 1 },
        text = {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Inventory2, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text("Katalog & Stok", fontWeight = FontWeight.Bold, fontSize = 12.sp)
          }
        }
      )
      Tab(
        selected = selectedTab == 2,
        onClick = { selectedTab = 2 },
        text = {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.LocalShipping, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text("Restok Masuk", fontWeight = FontWeight.Bold, fontSize = 12.sp)
          }
        }
      )
      Tab(
        selected = selectedTab == 3,
        onClick = { selectedTab = 3 },
        text = {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.History, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text("Riwayat Penjualan", fontWeight = FontWeight.Bold, fontSize = 12.sp)
          }
        }
      )
    }

    when (selectedTab) {
      0 -> PosKasirView(
        produkList = produkList,
        kategoriList = kategoriList,
        cart = cart,
        searchQuery = searchQuery,
        onSearchChange = { searchQuery = it },
        selectedKategoriId = selectedKategoriId,
        onSelectKategori = { selectedKategoriId = it },
        onAddToCart = { produk ->
          if (produk.namaBarang.contains("Seragam", ignoreCase = true)) {
            productForSizePick = produk
          } else {
            viewModel.addToCart(produk, "STANDAR")
          }
        },
        onUpdateCartQty = { item, delta -> viewModel.updateCartQty(item, delta) },
        onClearCart = { viewModel.clearCart() },
        onOpenCheckout = { showCheckoutDialog = true }
      )
      1 -> KatalogProdukView(
        produkList = produkList,
        kategoriList = kategoriList,
        supplierList = supplierList,
        onOpenAddProduct = { showAddProductDialog = true },
        onDeleteProduk = { viewModel.hapusProduk(it) }
      )
      2 -> RestokPembelianView(
        produkList = produkList,
        supplierList = supplierList,
        onRestokComplete = {
          viewModel.showMessage("Restok barang supplier berhasil dicatat dan stok bertambah!")
          selectedTab = 1
        }
      )
      3 -> RiwayatPenjualanView(
        penjualanList = penjualanList,
        riwayatStokList = riwayatStokList,
        onViewReceipt = { receiptToShow = it }
      )
    }
  }

  // Size Picker Dialog (XS, S, M, L, XL, XXL)
  if (productForSizePick != null) {
    val pr = productForSizePick!!
    Dialog(onDismissRequest = { productForSizePick = null }) {
      Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = Modifier.padding(10.dp)
      ) {
        Column(modifier = Modifier.padding(20.dp)) {
          Text("Pilih Ukuran Seragam", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = NavyDark)
          Spacer(modifier = Modifier.height(4.dp))
          Text(pr.namaBarang, fontSize = 12.sp, color = TextSecondary)
          Spacer(modifier = Modifier.height(14.dp))

          val sizes = listOf("XS", "S", "M", "L", "XL", "XXL")
          Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            sizes.chunked(3).forEach { rowSizes ->
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
              ) {
                rowSizes.forEach { sz ->
                  OutlinedButton(
                    onClick = {
                      viewModel.addToCart(pr, sz)
                      productForSizePick = null
                    },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(8.dp)
                  ) {
                    Text(sz, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                  }
                }
              }
            }
          }

          Spacer(modifier = Modifier.height(16.dp))
          Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
            TextButton(onClick = { productForSizePick = null }) { Text("Batal") }
          }
        }
      }
    }
  }

  // Checkout Dialog (Tunai, Transfer, QRIS)
  if (showCheckoutDialog) {
    CheckoutPOSDialog(
      cart = cart,
      guruList = guruList,
      onDismiss = { showCheckoutDialog = false },
      onConfirmCheckout = { pembeli, guruId, uang, metode ->
        viewModel.checkoutPOS(pembeli, guruId, uang, metode) { noStruk ->
          showCheckoutDialog = false
        }
      }
    )
  }

  // Add Product Dialog
  if (showAddProductDialog) {
    AddProductDialog(
      kategoriList = kategoriList,
      supplierList = supplierList,
      onDismiss = { showAddProductDialog = false },
      onSubmit = { produk, variasi ->
        viewModel.tambahProduk(produk, variasi)
        showAddProductDialog = false
      }
    )
  }

  // View Receipt Modal
  if (receiptToShow != null) {
    val trx = receiptToShow!!
    KwitansiReceiptDialog(
      title = "Struk Penjualan Toko Koperasi",
      noKwitansi = trx.noPenjualan,
      tanggal = trx.tanggalPenjualan,
      namaAnggota = trx.pembeliNama,
      nominal = trx.totalBayar,
      untukPembayaran = "Pembelian Kebutuhan Sekolah di Toko Koperasi",
      metode = trx.metodeBayar,
      petugas = "Kasir Toko SMP",
      onDismiss = { receiptToShow = null }
    )
  }
}

// ==========================================
// VIEW 1: POS KASIR
// ==========================================
@Composable
fun PosKasirView(
  produkList: List<ProdukEntity>,
  kategoriList: List<KategoriProdukEntity>,
  cart: List<CartItem>,
  searchQuery: String,
  onSearchChange: (String) -> Unit,
  selectedKategoriId: Long?,
  onSelectKategori: (Long?) -> Unit,
  onAddToCart: (ProdukEntity) -> Unit,
  onUpdateCartQty: (CartItem, Int) -> Unit,
  onClearCart: () -> Unit,
  onOpenCheckout: () -> Unit
) {
  val filteredProducts = produkList.filter {
    (selectedKategoriId == null || it.kategoriId == selectedKategoriId) &&
    it.namaBarang.contains(searchQuery, ignoreCase = true)
  }

  val totalCart = cart.sumOf { it.subtotal }

  Row(modifier = Modifier.fillMaxSize()) {
    // Left: Product Catalog
    Column(
      modifier = Modifier
        .weight(1.3f)
        .fillMaxHeight()
        .padding(12.dp)
    ) {
      // Search
      OutlinedTextField(
        value = searchQuery,
        onValueChange = onSearchChange,
        placeholder = { Text("Cari barang...", fontSize = 12.sp) },
        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, modifier = Modifier.size(18.dp)) },
        singleLine = true,
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier.fillMaxWidth()
      )

      Spacer(modifier = Modifier.height(8.dp))

      // Kategori Filter row
      ScrollableTabRow(
        selectedTabIndex = if (selectedKategoriId == null) 0 else kategoriList.indexOfFirst { it.id == selectedKategoriId } + 1,
        containerColor = Color.Transparent,
        contentColor = NavyPrimary,
        edgePadding = 0.dp
      ) {
        Tab(
          selected = selectedKategoriId == null,
          onClick = { onSelectKategori(null) },
          text = { Text("Semua", fontSize = 11.sp) }
        )
        kategoriList.forEach { kat ->
          Tab(
            selected = selectedKategoriId == kat.id,
            onClick = { onSelectKategori(kat.id) },
            text = { Text(kat.namaKategori, fontSize = 11.sp) }
          )
        }
      }

      Spacer(modifier = Modifier.height(8.dp))

      LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        items(filteredProducts) { pr ->
          Card(
            shape = RoundedCornerShape(10.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            modifier = Modifier
              .fillMaxWidth()
              .clickable { onAddToCart(pr) }
              .testTag("pos_item_${pr.id}")
          ) {
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Box(
                modifier = Modifier
                  .size(36.dp)
                  .clip(RoundedCornerShape(6.dp))
                  .background(NavyPrimary.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
              ) {
                Icon(Icons.Default.ShoppingBag, contentDescription = null, tint = NavyPrimary, modifier = Modifier.size(20.dp))
              }
              Spacer(modifier = Modifier.width(10.dp))
              Column(modifier = Modifier.weight(1f)) {
                Text(pr.namaBarang, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = NavyDark, maxLines = 1)
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                  Text(formatRupiah(pr.hargaJual), fontWeight = FontWeight.Bold, fontSize = 11.sp, color = NavyPrimary)
                  Text("• Stok: ${pr.stokTotal}", fontSize = 10.sp, color = if (pr.stokTotal <= 5) DangerRed else TextMuted)
                }
              }
              IconButton(onClick = { onAddToCart(pr) }) {
                Icon(Icons.Default.AddCircle, contentDescription = "Tambah", tint = NavyPrimary)
              }
            }
          }
        }
      }
    }

    // Right: Cart Drawer
    Card(
      shape = RoundedCornerShape(topStart = 16.dp),
      colors = CardDefaults.cardColors(containerColor = Color.White),
      elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
      modifier = Modifier
        .weight(1f)
        .fillMaxHeight()
    ) {
      Column(
        modifier = Modifier
          .fillMaxSize()
          .padding(12.dp)
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text("Keranjang (${cart.sumOf { it.jumlah }})", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = NavyDark)
          if (cart.isNotEmpty()) {
            IconButton(onClick = onClearCart) {
              Icon(Icons.Default.DeleteOutline, contentDescription = "Kosongkan", tint = DangerRed, modifier = Modifier.size(18.dp))
            }
          }
        }

        HorizontalDivider(modifier = Modifier.padding(vertical = 6.dp), color = BorderLight)

        if (cart.isEmpty()) {
          Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
            Text("Keranjang kosong\nPilih barang di sebelah kiri", textAlign = TextAlign.Center, color = TextMuted, fontSize = 11.sp)
          }
        } else {
          LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(6.dp)
          ) {
            items(cart) { item ->
              Card(
                shape = RoundedCornerShape(6.dp),
                colors = CardDefaults.cardColors(containerColor = SlateBackground),
                modifier = Modifier.fillMaxWidth()
              ) {
                Column(modifier = Modifier.padding(8.dp)) {
                  Text(item.namaBarang, fontWeight = FontWeight.SemiBold, fontSize = 11.sp, maxLines = 1)
                  Text("Ukuran: ${item.ukuran}", fontSize = 10.sp, color = PurpleAccent)
                  Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                  ) {
                    Text(formatRupiah(item.subtotal), fontWeight = FontWeight.Bold, fontSize = 11.sp, color = NavyPrimary)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                      IconButton(onClick = { onUpdateCartQty(item, -1) }, modifier = Modifier.size(24.dp)) {
                        Icon(Icons.Default.Remove, contentDescription = "Kurang", modifier = Modifier.size(14.dp))
                      }
                      Text("${item.jumlah}", fontWeight = FontWeight.Bold, fontSize = 11.sp, modifier = Modifier.padding(horizontal = 4.dp))
                      IconButton(onClick = { onUpdateCartQty(item, 1) }, modifier = Modifier.size(24.dp)) {
                        Icon(Icons.Default.Add, contentDescription = "Tambah", modifier = Modifier.size(14.dp))
                      }
                    }
                  }
                }
              }
            }
          }
        }

        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = BorderLight)

        // Total & Checkout
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Text("Total Bayar:", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = TextSecondary)
          Text(formatRupiah(totalCart), fontWeight = FontWeight.ExtraBold, fontSize = 15.sp, color = NavyDark)
        }

        Spacer(modifier = Modifier.height(10.dp))

        Button(
          onClick = onOpenCheckout,
          enabled = cart.isNotEmpty(),
          colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen),
          shape = RoundedCornerShape(8.dp),
          modifier = Modifier
            .fillMaxWidth()
            .testTag("checkout_pos_button")
        ) {
          Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
          Spacer(modifier = Modifier.width(6.dp))
          Text("Bayar Sekarang", fontWeight = FontWeight.Bold, fontSize = 12.sp)
        }
      }
    }
  }
}

// ==========================================
// VIEW 2: KATALOG & STOK BARANG
// ==========================================
@Composable
fun KatalogProdukView(
  produkList: List<ProdukEntity>,
  kategoriList: List<KategoriProdukEntity>,
  supplierList: List<SupplierEntity>,
  onOpenAddProduct: () -> Unit,
  onDeleteProduk: (ProdukEntity) -> Unit
) {
  LazyColumn(
    modifier = Modifier
      .fillMaxSize()
      .padding(16.dp),
    verticalArrangement = Arrangement.spacedBy(10.dp)
  ) {
    item {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text("Daftar Inventaris Produk (${produkList.size})", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = NavyDark)
        Button(
          onClick = onOpenAddProduct,
          colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary),
          shape = RoundedCornerShape(8.dp),
          modifier = Modifier.testTag("tambah_produk_btn")
        ) {
          Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(14.dp))
          Spacer(modifier = Modifier.width(4.dp))
          Text("Tambah Produk", fontSize = 11.sp)
        }
      }
    }

    items(produkList) { pr ->
      val kat = kategoriList.find { it.id == pr.kategoriId }
      val sup = supplierList.find { it.id == pr.supplierId }

      Card(
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Text(pr.namaBarang, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = NavyDark)
              if (pr.stokTotal <= 5) {
                Spacer(modifier = Modifier.width(6.dp))
                StatusBadge(text = "STOK MENIPIS", isDanger = true)
              }
            }
            Text("Kode: ${pr.kodeBarang} • Kat: ${kat?.namaKategori ?: "Umum"}", fontSize = 11.sp, color = TextSecondary)
            Text("Supplier: ${sup?.namaSupplier ?: "-"}", fontSize = 10.sp, color = TextMuted)
            Spacer(modifier = Modifier.height(4.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
              Text("Beli: ${formatRupiah(pr.hargaBeli)}", fontSize = 11.sp, color = TextMuted)
              Text("Jual: ${formatRupiah(pr.hargaJual)}", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = SuccessGreen)
              Text("Total Stok: ${pr.stokTotal} ${pr.satuan}", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = NavyPrimary)
            }
          }
          IconButton(onClick = { onDeleteProduk(pr) }) {
            Icon(Icons.Default.Delete, contentDescription = "Hapus", tint = DangerRed, modifier = Modifier.size(18.dp))
          }
        }
      }
    }
  }
}

// ==========================================
// VIEW 3: RESTOK PEMBELIAN BARANG
// ==========================================
@Composable
fun RestokPembelianView(
  produkList: List<ProdukEntity>,
  supplierList: List<SupplierEntity>,
  onRestokComplete: () -> Unit
) {
  var selectedProduct by remember { mutableStateOf<ProdukEntity?>(null) }
  var selectedSupplier by remember { mutableStateOf<SupplierEntity?>(null) }
  var jumlahText by remember { mutableStateOf("") }
  var hargaBeliText by remember { mutableStateOf("") }
  var ukuran by remember { mutableStateOf("STANDAR") }
  var keterangan by remember { mutableStateOf("Pembelian stok baru") }

  LazyColumn(
    modifier = Modifier
      .fillMaxSize()
      .padding(16.dp),
    verticalArrangement = Arrangement.spacedBy(12.dp)
  ) {
    item {
      Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Text("Form Pembelian Barang dari Supplier", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = NavyDark)
          Spacer(modifier = Modifier.height(12.dp))

          // Pilih Produk
          Text("Pilih Produk:", fontSize = 11.sp, color = TextSecondary)
          var expandedProd by remember { mutableStateOf(false) }
          Box(modifier = Modifier.fillMaxWidth()) {
            OutlinedButton(
              onClick = { expandedProd = true },
              modifier = Modifier.fillMaxWidth(),
              shape = RoundedCornerShape(8.dp)
            ) {
              Text(selectedProduct?.namaBarang ?: "Pilih Barang yang dibeli...", fontSize = 12.sp)
            }
            DropdownMenu(expanded = expandedProd, onDismissRequest = { expandedProd = false }) {
              produkList.forEach { p ->
                DropdownMenuItem(
                  text = { Text(p.namaBarang, fontSize = 12.sp) },
                  onClick = {
                    selectedProduct = p
                    hargaBeliText = p.hargaBeli.toInt().toString()
                    expandedProd = false
                  }
                )
              }
            }
          }

          Spacer(modifier = Modifier.height(10.dp))

          // Jika seragam, pilih ukuran
          if (selectedProduct?.namaBarang?.contains("Seragam", ignoreCase = true) == true) {
            Text("Pilih Ukuran:", fontSize = 11.sp, color = TextSecondary)
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
              listOf("XS", "S", "M", "L", "XL", "XXL").forEach { sz ->
                FilterChip(
                  selected = ukuran == sz,
                  onClick = { ukuran = sz },
                  label = { Text(sz, fontSize = 10.sp) }
                )
              }
            }
            Spacer(modifier = Modifier.height(10.dp))
          }

          OutlinedTextField(
            value = jumlahText,
            onValueChange = { jumlahText = it.filter { ch -> ch.isDigit() } },
            label = { Text("Jumlah Barang Masuk (Pcs)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
          )

          Spacer(modifier = Modifier.height(10.dp))

          OutlinedTextField(
            value = hargaBeliText,
            onValueChange = { hargaBeliText = it.filter { ch -> ch.isDigit() } },
            label = { Text("Harga Beli Satuan (Rp)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
          )

          Spacer(modifier = Modifier.height(16.dp))

          Button(
            onClick = onRestokComplete,
            colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.fillMaxWidth()
          ) {
            Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text("Simpan Pembelian & Tambah Stok")
          }
        }
      }
    }
  }
}

// ==========================================
// VIEW 4: RIWAYAT PENJUALAN
// ==========================================
@Composable
fun RiwayatPenjualanView(
  penjualanList: List<TransaksiPenjualanEntity>,
  riwayatStokList: List<RiwayatStokEntity>,
  onViewReceipt: (TransaksiPenjualanEntity) -> Unit
) {
  LazyColumn(
    modifier = Modifier
      .fillMaxSize()
      .padding(16.dp),
    verticalArrangement = Arrangement.spacedBy(10.dp)
  ) {
    item {
      SectionHeader(title = "Riwayat Transaksi Penjualan Kasir (${penjualanList.size})")
    }

    items(penjualanList) { trx ->
      Card(
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column(modifier = Modifier.weight(1f)) {
            Text(trx.noPenjualan, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = NavyPrimary)
            Text("Pembeli: ${trx.pembeliNama}", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = NavyDark)
            Text("Tgl: ${trx.tanggalPenjualan} • Kasir: ${trx.kasirUsername}", fontSize = 10.sp, color = TextMuted)
            Spacer(modifier = Modifier.height(2.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
              StatusBadge(text = trx.metodeBayar, isSuccess = true)
              Text("Total: ${formatRupiah(trx.totalBayar)}", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = SuccessGreen)
            }
          }
          OutlinedButton(
            onClick = { onViewReceipt(trx) },
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = NavyPrimary)
          ) {
            Icon(Icons.Default.Receipt, contentDescription = null, modifier = Modifier.size(14.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text("Struk", fontSize = 11.sp)
          }
        }
      }
    }
  }
}

// Checkout Dialog
@Composable
fun CheckoutPOSDialog(
  cart: List<CartItem>,
  guruList: List<GuruEntity>,
  onDismiss: () -> Unit,
  onConfirmCheckout: (String, Long?, Double, String) -> Unit
) {
  val total = cart.sumOf { it.subtotal }
  var pembeliNama by remember { mutableStateOf("Umum / Siswa") }
  var selectedGuruId by remember { mutableStateOf<Long?>(null) }
  var uangBayarText by remember { mutableStateOf(total.toInt().toString()) }
  var metodeBayar by remember { mutableStateOf("TUNAI") } // TUNAI, TRANSFER, QRIS
  var errorMsg by remember { mutableStateOf<String?>(null) }

  val uangDiberikan = uangBayarText.toDoubleOrNull() ?: 0.0
  val kembalian = (uangDiberikan - total).coerceAtLeast(0.0)

  Dialog(onDismissRequest = onDismiss) {
    Card(
      shape = RoundedCornerShape(16.dp),
      colors = CardDefaults.cardColors(containerColor = Color.White),
      modifier = Modifier
        .fillMaxWidth()
        .padding(8.dp)
    ) {
      Column(modifier = Modifier.padding(20.dp)) {
        Text("Pembayaran Kasir Toko", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = NavyDark)
        Spacer(modifier = Modifier.height(6.dp))
        Text("Total Tagihan: ${formatRupiah(total)}", fontWeight = FontWeight.ExtraBold, fontSize = 18.sp, color = SuccessGreen)

        Spacer(modifier = Modifier.height(14.dp))

        // Pembeli
        OutlinedTextField(
          value = pembeliNama,
          onValueChange = { pembeliNama = it },
          label = { Text("Nama Pembeli") },
          singleLine = true,
          modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Metode Pembayaran
        Text("Metode Pembayaran:", fontSize = 11.sp, color = TextSecondary)
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
          listOf("TUNAI", "TRANSFER", "QRIS").forEach { m ->
            FilterChip(
              selected = metodeBayar == m,
              onClick = { metodeBayar = m },
              label = { Text(m, fontSize = 11.sp) }
            )
          }
        }

        Spacer(modifier = Modifier.height(10.dp))

        if (metodeBayar == "TUNAI") {
          OutlinedTextField(
            value = uangBayarText,
            onValueChange = { uangBayarText = it.filter { ch -> ch.isDigit() } },
            label = { Text("Uang Tunai Diterima (Rp)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
          )

          Spacer(modifier = Modifier.height(6.dp))
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Text("Kembalian:", fontSize = 12.sp, color = TextSecondary)
            Text(formatRupiah(kembalian), fontWeight = FontWeight.Bold, fontSize = 14.sp, color = NavyPrimary)
          }
        }

        if (errorMsg != null) {
          Spacer(modifier = Modifier.height(6.dp))
          Text(errorMsg!!, color = DangerRed, fontSize = 11.sp)
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
          TextButton(onClick = onDismiss) { Text("Batal") }
          Spacer(modifier = Modifier.width(8.dp))
          Button(
            onClick = {
              if (metodeBayar == "TUNAI" && uangDiberikan < total) {
                errorMsg = "Uang pembayaran kurang dari total tagihan!"
                return@Button
              }
              onConfirmCheckout(pembeliNama, selectedGuruId, uangDiberikan, metodeBayar)
            },
            colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen),
            shape = RoundedCornerShape(8.dp)
          ) {
            Text("Selesaikan Transaksi")
          }
        }
      }
    }
  }
}

// Add Product Dialog
@Composable
fun AddProductDialog(
  kategoriList: List<KategoriProdukEntity>,
  supplierList: List<SupplierEntity>,
  onDismiss: () -> Unit,
  onSubmit: (ProdukEntity, List<VariasiProdukEntity>) -> Unit
) {
  var kode by remember { mutableStateOf("PRD-${(100..999).random()}") }
  var nama by remember { mutableStateOf("") }
  var hargaBeli by remember { mutableStateOf("") }
  var hargaJual by remember { mutableStateOf("") }
  var satuan by remember { mutableStateOf("Pcs") }
  var selectedKategoriId by remember { mutableStateOf(kategoriList.firstOrNull()?.id) }
  var isUniform by remember { mutableStateOf(false) }

  Dialog(onDismissRequest = onDismiss) {
    Card(
      shape = RoundedCornerShape(16.dp),
      colors = CardDefaults.cardColors(containerColor = Color.White),
      modifier = Modifier
        .fillMaxWidth()
        .padding(8.dp)
    ) {
      Column(modifier = Modifier.padding(20.dp)) {
        Text("Tambah Produk Baru", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = NavyDark)
        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(value = kode, onValueChange = { kode = it }, label = { Text("Kode Barang") }, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(value = nama, onValueChange = { nama = it; isUniform = it.contains("Seragam", ignoreCase = true) }, label = { Text("Nama Barang") }, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(8.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
          OutlinedTextField(
            value = hargaBeli,
            onValueChange = { hargaBeli = it.filter { ch -> ch.isDigit() } },
            label = { Text("Harga Beli (Rp)") },
            modifier = Modifier.weight(1f)
          )
          OutlinedTextField(
            value = hargaJual,
            onValueChange = { hargaJual = it.filter { ch -> ch.isDigit() } },
            label = { Text("Harga Jual (Rp)") },
            modifier = Modifier.weight(1f)
          )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
          TextButton(onClick = onDismiss) { Text("Batal") }
          Spacer(modifier = Modifier.width(8.dp))
          Button(
            onClick = {
              val pr = ProdukEntity(
                kodeBarang = kode,
                namaBarang = nama,
                kategoriId = selectedKategoriId,
                hargaBeli = hargaBeli.toDoubleOrNull() ?: 0.0,
                hargaJual = hargaJual.toDoubleOrNull() ?: 0.0,
                satuan = satuan,
                supplierId = supplierList.firstOrNull()?.id,
                stokTotal = if (isUniform) 30 else 20
              )
              val variations = if (isUniform) {
                listOf(
                  VariasiProdukEntity(produkId = 0, ukuran = "XS", stok = 5),
                  VariasiProdukEntity(produkId = 0, ukuran = "S", stok = 5),
                  VariasiProdukEntity(produkId = 0, ukuran = "M", stok = 5),
                  VariasiProdukEntity(produkId = 0, ukuran = "L", stok = 5),
                  VariasiProdukEntity(produkId = 0, ukuran = "XL", stok = 5),
                  VariasiProdukEntity(produkId = 0, ukuran = "XXL", stok = 5)
                )
              } else {
                listOf(VariasiProdukEntity(produkId = 0, ukuran = "STANDAR", stok = 20))
              }
              onSubmit(pr, variations)
            },
            colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary)
          ) {
            Text("Simpan Produk")
          }
        }
      }
    }
  }
}
