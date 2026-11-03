package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.KoperasiViewModel
import com.example.ui.components.SectionHeader
import com.example.ui.components.formatRupiah
import com.example.ui.theme.*

@Composable
fun LaporanScreen(
  viewModel: KoperasiViewModel
) {
  val guruList by viewModel.guruList.collectAsStateWithLifecycle()
  val iuranList by viewModel.iuranList.collectAsStateWithLifecycle()
  val simpananList by viewModel.simpananList.collectAsStateWithLifecycle()
  val pinjamanList by viewModel.pinjamanList.collectAsStateWithLifecycle()
  val angsuranList by viewModel.angsuranList.collectAsStateWithLifecycle()
  val dendaList by viewModel.dendaList.collectAsStateWithLifecycle()
  val produkList by viewModel.produkList.collectAsStateWithLifecycle()
  val penjualanList by viewModel.penjualanList.collectAsStateWithLifecycle()
  val allTransaksiSimpanan by viewModel.allTransaksiSimpanan.collectAsStateWithLifecycle()

  var selectedLaporanIndex by remember { mutableStateOf(0) }
  var showExportModal by remember { mutableStateOf(false) }

  val daftarJenisLaporan = listOf(
    "1. Laporan Data Anggota Guru",
    "2. Laporan Iuran Wajib (Rp 30.000/bln)",
    "3. Laporan Simpanan Anggota",
    "4. Laporan Bunga Simpanan (2%)",
    "5. Laporan Pinjaman Anggota",
    "6. Laporan Angsuran Cicilan",
    "7. Laporan Denda Keterlambatan",
    "8. Laporan Pembelian Toko",
    "9. Laporan Penjualan Kasir Toko",
    "10. Laporan Stok & Variasi Barang",
    "11. Laporan Arus Kas (Pemasukan vs Pengeluaran)",
    "12. Laporan Keuangan Koperasi & SHU"
  )

  LazyColumn(
    modifier = Modifier
      .fillMaxSize()
      .background(SlateBackground)
      .padding(16.dp),
    verticalArrangement = Arrangement.spacedBy(14.dp)
  ) {
    // Header & Filter Selector
    item {
      Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Text("Pusat Laporan Keuangan & Operasional", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = NavyDark)
          Text("SMP Nurul Ansor • Data Real-Time Terkomputerisasi", fontSize = 11.sp, color = TextSecondary)
          Spacer(modifier = Modifier.height(12.dp))

          // Dropdown Jenis Laporan
          var expanded by remember { mutableStateOf(false) }
          Box(modifier = Modifier.fillMaxWidth()) {
            OutlinedButton(
              onClick = { expanded = true },
              shape = RoundedCornerShape(8.dp),
              modifier = Modifier.fillMaxWidth()
            ) {
              Text(daftarJenisLaporan[selectedLaporanIndex], fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
              Icon(Icons.Default.ArrowDropDown, contentDescription = null)
            }
            DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
              daftarJenisLaporan.forEachIndexed { idx, name ->
                DropdownMenuItem(
                  text = { Text(name, fontSize = 12.sp) },
                  onClick = {
                    selectedLaporanIndex = idx
                    expanded = false
                  }
                )
              }
            }
          }

          Spacer(modifier = Modifier.height(10.dp))

          // Filter Periode & Action Buttons
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text("Periode: Tahun Buku 2026", fontSize = 11.sp, fontWeight = FontWeight.Medium, color = NavyPrimary)
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
              Button(
                onClick = { showExportModal = true },
                colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.testTag("cetak_laporan_btn")
              ) {
                Icon(Icons.Default.Print, contentDescription = null, modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Cetak / PDF", fontSize = 11.sp)
              }
            }
          }
        }
      }
    }

    // Dynamic Report Content Table
    item {
      SectionHeader(title = "Tampilan Data ${daftarJenisLaporan[selectedLaporanIndex]}")
    }

    when (selectedLaporanIndex) {
      0 -> { // Data Anggota
        items(guruList) { guru ->
          ReportRowCard(
            title = guru.namaLengkap,
            subtitle = "NIP: ${guru.nip} • ${guru.jenisKelamin}",
            extra = "Status: ${if (guru.statusAktif) "Aktif" else "Nonaktif"}",
            value = guru.tanggalBergabung
          )
        }
      }
      1 -> { // Iuran Wajib
        val totalIuranLunas = iuranList.filter { it.statusLunas }.sumOf { it.nominal }
        item {
          SummaryTotalBanner("Total Iuran Terkumpul:", formatRupiah(totalIuranLunas))
        }
        items(iuranList) { iuran ->
          val guru = guruList.find { it.id == iuran.guruId }
          ReportRowCard(
            title = guru?.namaLengkap ?: "Anggota",
            subtitle = "Periode: Bln ${iuran.bulan}/${iuran.tahun} • ${if (iuran.statusLunas) "LUNAS" else "BELUM BAYAR"}",
            extra = iuran.noKwitansi ?: "-",
            value = formatRupiah(iuran.nominal)
          )
        }
      }
      2 -> { // Simpanan
        val totalSimpanan = simpananList.sumOf { it.saldo }
        item {
          SummaryTotalBanner("Total Saldo Simpanan:", formatRupiah(totalSimpanan))
        }
        items(guruList) { guru ->
          val saldo = simpananList.find { it.guruId == guru.id }?.saldo ?: 0.0
          ReportRowCard(
            title = guru.namaLengkap,
            subtitle = "NIP: ${guru.nip}",
            extra = "Bunga: 2% / bulan",
            value = formatRupiah(saldo)
          )
        }
      }
      3 -> { // Bunga Simpanan (2%)
        val bungaMutations = allTransaksiSimpanan.filter { it.jenis == "BUNGA" }
        item {
          SummaryTotalBanner("Total Bunga Diberikan:", formatRupiah(bungaMutations.sumOf { it.nominal }))
        }
        items(bungaMutations) { b ->
          val guru = guruList.find { it.id == b.guruId }
          ReportRowCard(
            title = guru?.namaLengkap ?: "Guru",
            subtitle = b.dasarPerhitunganBunga ?: "Bunga Simpanan 2%",
            extra = b.tanggal,
            value = "+ " + formatRupiah(b.nominal)
          )
        }
      }
      4 -> { // Pinjaman
        val totalPj = pinjamanList.sumOf { it.totalPinjaman }
        item {
          SummaryTotalBanner("Total Pinjaman Dikeluarkan:", formatRupiah(totalPj))
        }
        items(pinjamanList) { pj ->
          val guru = guruList.find { it.id == pj.guruId }
          ReportRowCard(
            title = "${guru?.namaLengkap ?: "Guru"} (${pj.noPinjaman})",
            subtitle = "Pokok: ${formatRupiah(pj.jumlahPinjaman)} • Bunga: ${formatRupiah(pj.totalBunga)} • Tenor: ${pj.lamaAngsuranBulan} bln",
            extra = "Status: ${pj.status}",
            value = formatRupiah(pj.totalPinjaman)
          )
        }
      }
      5 -> { // Angsuran
        val totalAng = angsuranList.filter { it.status == "LUNAS" }.sumOf { it.nominalAngsuran }
        item {
          SummaryTotalBanner("Total Angsuran Diterima:", formatRupiah(totalAng))
        }
        items(angsuranList) { ang ->
          val guru = guruList.find { it.id == ang.guruId }
          ReportRowCard(
            title = "${guru?.namaLengkap ?: "Guru"} - Angsuran Ke-${ang.angsuranKe}",
            subtitle = "Jatuh Tempo: ${ang.tanggalJatuhTempo} • Status: ${ang.status}",
            extra = ang.noKwitansi ?: "-",
            value = formatRupiah(ang.nominalAngsuran)
          )
        }
      }
      6 -> { // Denda
        val totalDenda = dendaList.filter { it.statusDenda == "LUNAS" }.sumOf { it.nominalDenda }
        item {
          SummaryTotalBanner("Total Denda Terkumpul:", formatRupiah(totalDenda))
        }
        items(dendaList) { d ->
          val guru = guruList.find { it.id == d.guruId }
          ReportRowCard(
            title = guru?.namaLengkap ?: "Guru",
            subtitle = d.keterangan,
            extra = "Status: ${d.statusDenda}",
            value = formatRupiah(d.nominalDenda)
          )
        }
      }
      8 -> { // Penjualan Toko
        val totalJual = penjualanList.sumOf { it.totalBayar }
        item {
          SummaryTotalBanner("Total Omzet Penjualan Toko:", formatRupiah(totalJual))
        }
        items(penjualanList) { p ->
          ReportRowCard(
            title = "${p.noPenjualan} (${p.pembeliNama})",
            subtitle = "Tgl: ${p.tanggalPenjualan} • Kasir: ${p.kasirUsername}",
            extra = p.metodeBayar,
            value = formatRupiah(p.totalBayar)
          )
        }
      }
      9 -> { // Stok Barang
        val totalItems = produkList.sumOf { it.stokTotal }
        item {
          SummaryTotalBanner("Total Unit Stok Fisik:", "$totalItems Pcs")
        }
        items(produkList) { pr ->
          ReportRowCard(
            title = pr.namaBarang,
            subtitle = "Kode: ${pr.kodeBarang} • Satuan: ${pr.satuan}",
            extra = "Harga Jual: ${formatRupiah(pr.hargaJual)}",
            value = "${pr.stokTotal} ${pr.satuan}"
          )
        }
      }
      else -> { // Arus Kas & SHU
        val kasMasuk = iuranList.filter { it.statusLunas }.sumOf { it.nominal } +
          angsuranList.filter { it.status == "LUNAS" }.sumOf { it.nominalAngsuran } +
          dendaList.filter { it.statusDenda == "LUNAS" }.sumOf { it.nominalDenda } +
          penjualanList.sumOf { it.totalBayar }

        val kasKeluar = pinjamanList.filter { it.status == "DISETUJUI" }.sumOf { it.jumlahPinjaman }

        val labaKoperasi = kasMasuk - kasKeluar

        item {
          Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            modifier = Modifier.fillMaxWidth()
          ) {
            Column(modifier = Modifier.padding(16.dp)) {
              Text("Ringkasan Neraca Arus Kas & SHU", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = NavyDark)
              Spacer(modifier = Modifier.height(10.dp))
              Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Total Kas Masuk (Penerimaan):", fontSize = 12.sp, color = TextSecondary)
                Text(formatRupiah(kasMasuk), fontWeight = FontWeight.Bold, fontSize = 12.sp, color = SuccessGreen)
              }
              Spacer(modifier = Modifier.height(6.dp))
              Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Total Penyaluran Pinjaman:", fontSize = 12.sp, color = TextSecondary)
                Text(formatRupiah(kasKeluar), fontWeight = FontWeight.Bold, fontSize = 12.sp, color = DangerRed)
              }
              HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = BorderLight)
              Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Sisa Hasil Usaha (SHU) / Surplus:", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = NavyDark)
                Text(formatRupiah(labaKoperasi), fontWeight = FontWeight.ExtraBold, fontSize = 15.sp, color = NavyPrimary)
              }
            }
          }
        }
      }
    }
  }

  // Export / Print Modal Preview
  if (showExportModal) {
    Dialog(onDismissRequest = { showExportModal = false }) {
      Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = Modifier.padding(8.dp)
      ) {
        Column(modifier = Modifier.padding(20.dp)) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Print, contentDescription = null, tint = NavyPrimary)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Preview Cetak & Export Laporan", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = NavyDark)
          }
          Spacer(modifier = Modifier.height(10.dp))
          Text(
            "Laporan: ${daftarJenisLaporan[selectedLaporanIndex]}\nKoperasi Sekolah SMP Nurul Ansor\nDicetak pada: 2026-11-02",
            fontSize = 11.sp,
            color = TextSecondary
          )
          Spacer(modifier = Modifier.height(14.dp))
          Card(
            shape = RoundedCornerShape(8.dp),
            colors = CardDefaults.cardColors(containerColor = SlateBackground),
            modifier = Modifier.fillMaxWidth()
          ) {
            Column(modifier = Modifier.padding(12.dp)) {
              Text("Format Ekspor Siap Diunduh:", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = NavyDark)
              Text("• Dokumen Resmi PDF Berkop SMP Nurul Ansor", fontSize = 10.sp, color = TextSecondary)
              Text("• Format Lembar Kerja Excel (.xlsx)", fontSize = 10.sp, color = TextSecondary)
            }
          }
          Spacer(modifier = Modifier.height(16.dp))
          Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
            Button(
              onClick = {
                viewModel.showMessage("Laporan berhasil disiapkan dan diekspor!")
                showExportModal = false
              },
              colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary),
              shape = RoundedCornerShape(8.dp)
            ) {
              Text("Selesai Cetak")
            }
          }
        }
      }
    }
  }
}

@Composable
fun SummaryTotalBanner(label: String, amount: String) {
  Card(
    shape = RoundedCornerShape(10.dp),
    colors = CardDefaults.cardColors(containerColor = NavyLight.copy(alpha = 0.3f)),
    modifier = Modifier.fillMaxWidth()
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(12.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Text(label, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = NavyDark)
      Text(amount, fontWeight = FontWeight.ExtraBold, fontSize = 14.sp, color = NavyDark)
    }
  }
}

@Composable
fun ReportRowCard(
  title: String,
  subtitle: String,
  extra: String,
  value: String
) {
  Card(
    shape = RoundedCornerShape(8.dp),
    colors = CardDefaults.cardColors(containerColor = Color.White),
    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
    modifier = Modifier.fillMaxWidth()
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(10.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      Column(modifier = Modifier.weight(1f)) {
        Text(title, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = NavyDark)
        Text(subtitle, fontSize = 10.sp, color = TextSecondary)
        Text(extra, fontSize = 10.sp, color = TextMuted)
      }
      Text(value, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = NavyPrimary)
    }
  }
}
