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
import com.example.data.entity.AngsuranEntity
import com.example.data.entity.DendaEntity
import com.example.data.entity.GuruEntity
import com.example.data.entity.PinjamanEntity
import com.example.ui.KoperasiViewModel
import com.example.ui.components.KwitansiReceiptDialog
import com.example.ui.components.SectionHeader
import com.example.ui.components.StatusBadge
import com.example.ui.components.formatRupiah
import com.example.ui.theme.*

// ==========================================
// 1. PINJAMAN SCREEN (Admin View & Approval)
// ==========================================
@Composable
fun PinjamanScreen(
  viewModel: KoperasiViewModel
) {
  val pinjamanList by viewModel.pinjamanList.collectAsStateWithLifecycle()
  val guruList by viewModel.guruList.collectAsStateWithLifecycle()

  var selectedFilter by remember { mutableStateOf("SEMUA") } // SEMUA, MENUNGGU, DISETUJUI, LUNAS, DITOLAK
  var pinjamanToReject by remember { mutableStateOf<PinjamanEntity?>(null) }
  var pinjamanDetailToView by remember { mutableStateOf<Pair<PinjamanEntity, GuruEntity>?>(null) }

  val filteredPinjaman = pinjamanList.filter {
    if (selectedFilter == "SEMUA") true else it.status == selectedFilter
  }

  LazyColumn(
    modifier = Modifier
      .fillMaxSize()
      .background(SlateBackground)
      .padding(16.dp),
    verticalArrangement = Arrangement.spacedBy(14.dp)
  ) {
    // Info Banner Syarat & Ketentuan Pinjaman
    item {
      Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = NavyDark),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.VerifiedUser, contentDescription = null, tint = NavyLight, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("Ketentuan Pengajuan & Bunga Pinjaman", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
          }
          Spacer(modifier = Modifier.height(6.dp))
          Text(
            "• Bunga Pinjaman: 2% per bulan FLAT berdasarkan pokok pinjaman awal.\n• Validasi Wajib: Anggota wajib telah melunasi iuran wajib bulan berjalan.\n• Penyetujuan: Menghasilkan jadwal cicilan bulanan secara otomatis.",
            color = Color.White.copy(alpha = 0.9f),
            fontSize = 11.sp,
            lineHeight = 16.sp
          )
        }
      }
    }

    // Filter Chips
    item {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
      ) {
        listOf("SEMUA", "MENUNGGU", "DISETUJUI", "LUNAS", "DITOLAK").forEach { status ->
          FilterChip(
            selected = selectedFilter == status,
            onClick = { selectedFilter = status },
            label = { Text(status, fontSize = 11.sp) }
          )
        }
      }
    }

    item {
      SectionHeader(title = "Daftar Pinjaman Anggota (${filteredPinjaman.size})")
    }

    if (filteredPinjaman.isEmpty()) {
      item {
        Card(
          shape = RoundedCornerShape(12.dp),
          colors = CardDefaults.cardColors(containerColor = Color.White),
          modifier = Modifier.fillMaxWidth()
        ) {
          Box(modifier = Modifier.padding(24.dp), contentAlignment = Alignment.Center) {
            Text("Tidak ada data pinjaman pada kategori ini", color = TextMuted)
          }
        }
      }
    } else {
      items(filteredPinjaman) { pinjaman ->
        val guru = guruList.find { it.id == pinjaman.guruId }
        Card(
          shape = RoundedCornerShape(12.dp),
          colors = CardDefaults.cardColors(containerColor = Color.White),
          elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(modifier = Modifier.padding(14.dp)) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Column {
                Text(pinjaman.noPinjaman, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = NavyPrimary)
                Text(guru?.namaLengkap ?: "Guru ID: ${pinjaman.guruId}", fontWeight = FontWeight.SemiBold, fontSize = 12.sp, color = NavyDark)
              }
              val (isSuccess, isWarning, isDanger) = when (pinjaman.status) {
                "DISETUJUI", "LUNAS" -> Triple(true, false, false)
                "MENUNGGU" -> Triple(false, true, false)
                else -> Triple(false, false, true)
              }
              StatusBadge(text = pinjaman.status, isSuccess = isSuccess, isWarning = isWarning, isDanger = isDanger)
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp), color = BorderLight)

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
              Column {
                Text("Pokok Pinjaman", fontSize = 10.sp, color = TextMuted)
                Text(formatRupiah(pinjaman.jumlahPinjaman), fontWeight = FontWeight.Bold, fontSize = 13.sp, color = TextPrimary)
              }
              Column {
                Text("Bunga 2% (Flat)", fontSize = 10.sp, color = TextMuted)
                Text(formatRupiah(pinjaman.totalBunga), fontWeight = FontWeight.Bold, fontSize = 13.sp, color = PurpleAccent)
              }
              Column {
                Text("Cicilan / Bulan", fontSize = 10.sp, color = TextMuted)
                Text(formatRupiah(pinjaman.angsuranPerBulan), fontWeight = FontWeight.Bold, fontSize = 13.sp, color = SuccessGreen)
              }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
              Text("Tenor: ${pinjaman.lamaAngsuranBulan} Bulan", fontSize = 11.sp, color = TextSecondary)
              Text("Tgl Pengajuan: ${pinjaman.tanggalPengajuan}", fontSize = 11.sp, color = TextMuted)
            }

            if (pinjaman.catatanAdmin.isNotBlank()) {
              Spacer(modifier = Modifier.height(6.dp))
              Text("Catatan: ${pinjaman.catatanAdmin}", fontSize = 11.sp, color = TextSecondary)
            }

            // Action Buttons for Approval
            if (pinjaman.status == "MENUNGGU") {
              Spacer(modifier = Modifier.height(12.dp))
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
              ) {
                OutlinedButton(
                  onClick = { pinjamanToReject = pinjaman },
                  colors = ButtonDefaults.outlinedButtonColors(contentColor = DangerRed),
                  shape = RoundedCornerShape(8.dp)
                ) {
                  Icon(Icons.Default.Close, contentDescription = null, modifier = Modifier.size(14.dp))
                  Spacer(modifier = Modifier.width(4.dp))
                  Text("Tolak", fontSize = 11.sp)
                }
                Spacer(modifier = Modifier.width(8.dp))
                Button(
                  onClick = { viewModel.setujuiPinjaman(pinjaman) },
                  colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen),
                  shape = RoundedCornerShape(8.dp),
                  modifier = Modifier.testTag("setujui_pinjaman_${pinjaman.id}")
                ) {
                  Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(14.dp))
                  Spacer(modifier = Modifier.width(4.dp))
                  Text("Setujui Pinjaman", fontSize = 11.sp)
                }
              }
            }
          }
        }
      }
    }
  }

  // Dialog Penolakan Pinjaman
  if (pinjamanToReject != null) {
    var alasan by remember { mutableStateOf("") }
    Dialog(onDismissRequest = { pinjamanToReject = null }) {
      Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = Modifier.padding(8.dp)
      ) {
        Column(modifier = Modifier.padding(20.dp)) {
          Text("Tolak Pengajuan Pinjaman", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = DangerRed)
          Spacer(modifier = Modifier.height(10.dp))
          OutlinedTextField(
            value = alasan,
            onValueChange = { alasan = it },
            label = { Text("Alasan Penolakan") },
            placeholder = { Text("Contoh: Kapasitas angsuran belum memadai") },
            modifier = Modifier.fillMaxWidth()
          )
          Spacer(modifier = Modifier.height(16.dp))
          Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
            TextButton(onClick = { pinjamanToReject = null }) { Text("Batal") }
            Spacer(modifier = Modifier.width(8.dp))
            Button(
              onClick = {
                viewModel.tolakPinjaman(pinjamanToReject!!, alasan.ifBlank { "Tidak memenuhi kriteria" })
                pinjamanToReject = null
              },
              colors = ButtonDefaults.buttonColors(containerColor = DangerRed)
            ) {
              Text("Konfirmasi Tolak")
            }
          }
        }
      }
    }
  }
}

// ==========================================
// 2. ANGSURAN SCREEN (Installments & Pay)
// ==========================================
@Composable
fun AngsuranScreen(
  viewModel: KoperasiViewModel
) {
  val angsuranList by viewModel.angsuranList.collectAsStateWithLifecycle()
  val guruList by viewModel.guruList.collectAsStateWithLifecycle()
  val pinjamanList by viewModel.pinjamanList.collectAsStateWithLifecycle()

  var selectedFilter by remember { mutableStateOf("SEMUA") } // SEMUA, BELUM_BAYAR, LUNAS, TERLAMBAT
  var kwitansiToShow by remember { mutableStateOf<Triple<AngsuranEntity, GuruEntity, String>?>(null) }

  val filteredAngsuran = angsuranList.filter {
    if (selectedFilter == "SEMUA") true else it.status == selectedFilter
  }

  LazyColumn(
    modifier = Modifier
      .fillMaxSize()
      .background(SlateBackground)
      .padding(16.dp),
    verticalArrangement = Arrangement.spacedBy(14.dp)
  ) {
    item {
      SectionHeader(title = "Jadwal & Pembayaran Angsuran Pinjaman")
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
      ) {
        listOf("SEMUA", "BELUM_BAYAR", "LUNAS", "TERLAMBAT").forEach { status ->
          FilterChip(
            selected = selectedFilter == status,
            onClick = { selectedFilter = status },
            label = { Text(status, fontSize = 11.sp) }
          )
        }
      }
    }

    if (filteredAngsuran.isEmpty()) {
      item {
        Card(
          shape = RoundedCornerShape(12.dp),
          colors = CardDefaults.cardColors(containerColor = Color.White),
          modifier = Modifier.fillMaxWidth()
        ) {
          Box(modifier = Modifier.padding(24.dp), contentAlignment = Alignment.Center) {
            Text("Tidak ada daftar angsuran", color = TextMuted)
          }
        }
      }
    } else {
      items(filteredAngsuran) { angsuran ->
        val guru = guruList.find { it.id == angsuran.guruId }
        val pinjaman = pinjamanList.find { it.id == angsuran.pinjamanId }

        Card(
          shape = RoundedCornerShape(12.dp),
          colors = CardDefaults.cardColors(containerColor = Color.White),
          elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
          modifier = Modifier.fillMaxWidth()
        ) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column(modifier = Modifier.weight(1f)) {
              Text(
                "${guru?.namaLengkap ?: "Anggota"} - Angsuran Ke-${angsuran.angsuranKe}",
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                color = NavyDark
              )
              Text("No Pinjaman: ${pinjaman?.noPinjaman ?: "-"}", fontSize = 11.sp, color = NavyPrimary)
              Spacer(modifier = Modifier.height(2.dp))
              Text("Jatuh Tempo: ${angsuran.tanggalJatuhTempo}", fontSize = 11.sp, color = TextMuted)
              Spacer(modifier = Modifier.height(2.dp))
              Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
              ) {
                StatusBadge(
                  text = angsuran.status,
                  isSuccess = angsuran.status == "LUNAS",
                  isWarning = angsuran.status == "BELUM_BAYAR",
                  isDanger = angsuran.status == "TERLAMBAT"
                )
                Text("Nominal: ${formatRupiah(angsuran.nominalAngsuran)}", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = TextPrimary)
              }
            }

            if (angsuran.status != "LUNAS") {
              Button(
                onClick = { viewModel.bayarAngsuran(angsuran) },
                colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.testTag("bayar_angsuran_${angsuran.id}")
              ) {
                Icon(Icons.Default.Payment, contentDescription = null, modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Bayar", fontSize = 11.sp)
              }
            } else {
              OutlinedButton(
                onClick = {
                  if (guru != null) {
                    kwitansiToShow = Triple(angsuran, guru, angsuran.noKwitansi ?: "KW-ANG-${angsuran.id}")
                  }
                },
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = NavyPrimary)
              ) {
                Icon(Icons.Default.Receipt, contentDescription = null, modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Kwitansi", fontSize = 11.sp)
              }
            }
          }
        }
      }
    }
  }

  // Kwitansi Dialog
  if (kwitansiToShow != null) {
    val (angsuran, guru, noKwitansi) = kwitansiToShow!!
    KwitansiReceiptDialog(
      title = "Kwitansi Angsuran Pinjaman",
      noKwitansi = noKwitansi,
      tanggal = angsuran.tanggalBayar ?: "2026-11-02",
      namaAnggota = guru.namaLengkap,
      nominal = angsuran.nominalAngsuran,
      untukPembayaran = "Pembayaran Angsuran Ke-${angsuran.angsuranKe} Pinjaman Koperasi",
      metode = "Tunai / Potong Gaji",
      petugas = "Kasir Koperasi SMP",
      onDismiss = { kwitansiToShow = null }
    )
  }
}

// ==========================================
// 3. DENDA SCREEN (Late Penalties & Rp 10.000)
// ==========================================
@Composable
fun DendaScreen(
  viewModel: KoperasiViewModel
) {
  val dendaList by viewModel.dendaList.collectAsStateWithLifecycle()
  val guruList by viewModel.guruList.collectAsStateWithLifecycle()
  val totalDendaLunas by viewModel.totalDendaLunas.collectAsStateWithLifecycle()

  val totalDendaTertunggak = dendaList.filter { it.statusDenda == "BELUM_BAYAR" }.sumOf { it.nominalDenda }

  LazyColumn(
    modifier = Modifier
      .fillMaxSize()
      .background(SlateBackground)
      .padding(16.dp),
    verticalArrangement = Arrangement.spacedBy(14.dp)
  ) {
    // Penjelasan Aturan Denda Sesuai Prompt Bagian 10
    item {
      Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = DangerRed.copy(alpha = 0.9f)),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Warning, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("Aturan Denda Keterlambatan Angsuran", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
          }
          Spacer(modifier = Modifier.height(6.dp))
          Text(
            "• Masa toleransi keterlambatan adalah 7 hari setelah tanggal jatuh tempo angsuran.\n• Melewati masa toleransi, dikenakan denda Rp 10.000 per angsuran yang terlambat.\n• Denda hanya dikenakan 1 kali dan disimpan terpisah dari pokok angsuran untuk mencegah pencatatan ganda.",
            color = Color.White.copy(alpha = 0.95f),
            fontSize = 11.sp,
            lineHeight = 16.sp
          )
        }
      }
    }

    // Rekap Denda Cards
    item {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        Card(
          shape = RoundedCornerShape(12.dp),
          colors = CardDefaults.cardColors(containerColor = DangerBg),
          modifier = Modifier.weight(1f)
        ) {
          Column(modifier = Modifier.padding(12.dp)) {
            Text("DENDA BELUM BAYAR", color = DangerRed, fontWeight = FontWeight.Bold, fontSize = 11.sp)
            Text(formatRupiah(totalDendaTertunggak), fontWeight = FontWeight.ExtraBold, fontSize = 16.sp, color = DangerRed)
            Text("${dendaList.count { it.statusDenda == "BELUM_BAYAR" }} Angsuran Terlambat", fontSize = 11.sp, color = DangerRed)
          }
        }

        Card(
          shape = RoundedCornerShape(12.dp),
          colors = CardDefaults.cardColors(containerColor = SuccessBg),
          modifier = Modifier.weight(1f)
        ) {
          Column(modifier = Modifier.padding(12.dp)) {
            Text("DENDA TERKUMPUL", color = SuccessGreen, fontWeight = FontWeight.Bold, fontSize = 11.sp)
            Text(formatRupiah(totalDendaLunas), fontWeight = FontWeight.ExtraBold, fontSize = 16.sp, color = SuccessGreen)
            Text("${dendaList.count { it.statusDenda == "LUNAS" }} Denda Terbayar", fontSize = 11.sp, color = SuccessGreen)
          }
        }
      }
    }

    item {
      SectionHeader(title = "Daftar Kasus Denda Keterlambatan (${dendaList.size})")
    }

    if (dendaList.isEmpty()) {
      item {
        Card(
          shape = RoundedCornerShape(12.dp),
          colors = CardDefaults.cardColors(containerColor = Color.White),
          modifier = Modifier.fillMaxWidth()
        ) {
          Box(modifier = Modifier.padding(24.dp), contentAlignment = Alignment.Center) {
            Text("Alhamdulillah, tidak ada denda keterlambatan saat ini", color = SuccessGreen, fontWeight = FontWeight.Medium)
          }
        }
      }
    } else {
      items(dendaList) { denda ->
        val guru = guruList.find { it.id == denda.guruId }
        Card(
          shape = RoundedCornerShape(12.dp),
          colors = CardDefaults.cardColors(containerColor = Color.White),
          elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
          modifier = Modifier.fillMaxWidth()
        ) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column(modifier = Modifier.weight(1f)) {
              Text(guru?.namaLengkap ?: "Anggota", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = NavyDark)
              Text("NIP: ${guru?.nip ?: "-"}", fontSize = 11.sp, color = TextSecondary)
              Spacer(modifier = Modifier.height(2.dp))
              Text(denda.keterangan, fontSize = 11.sp, color = TextMuted)
              Spacer(modifier = Modifier.height(4.dp))
              Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
              ) {
                StatusBadge(
                  text = denda.statusDenda,
                  isSuccess = denda.statusDenda == "LUNAS",
                  isDanger = denda.statusDenda == "BELUM_BAYAR"
                )
                Text(
                  "Denda: ${formatRupiah(denda.nominalDenda)}",
                  fontWeight = FontWeight.Bold,
                  fontSize = 12.sp,
                  color = DangerRed
                )
              }
            }

            if (denda.statusDenda == "BELUM_BAYAR") {
              Button(
                onClick = { viewModel.bayarDenda(denda) },
                colors = ButtonDefaults.buttonColors(containerColor = DangerRed),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.testTag("bayar_denda_${denda.id}")
              ) {
                Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Bayar Denda", fontSize = 11.sp)
              }
            } else {
              Text(
                "Lunas: ${denda.tanggalBayar ?: ""}",
                color = SuccessGreen,
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp
              )
            }
          }
        }
      }
    }
  }
}
