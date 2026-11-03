package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.entity.*
import com.example.ui.KoperasiViewModel
import com.example.ui.Screen
import com.example.ui.components.SectionHeader
import com.example.ui.components.StatusBadge
import com.example.ui.components.formatRupiah
import com.example.ui.theme.*
import java.util.*

// ==========================================
// GURU DASHBOARD
// ==========================================
@Composable
fun GuruDashboardScreen(
  viewModel: KoperasiViewModel,
  onNavigate: (Screen) -> Unit
) {
  val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()
  val guruList by viewModel.guruList.collectAsStateWithLifecycle()
  val iuranList by viewModel.iuranList.collectAsStateWithLifecycle()
  val simpananList by viewModel.simpananList.collectAsStateWithLifecycle()
  val pinjamanList by viewModel.pinjamanList.collectAsStateWithLifecycle()
  val angsuranList by viewModel.angsuranList.collectAsStateWithLifecycle()
  val dendaList by viewModel.dendaList.collectAsStateWithLifecycle()

  val myGuru = guruList.find { it.id == currentUser?.guruId } ?: guruList.firstOrNull()
  val myGuruId = myGuru?.id ?: 1L

  // Check current month iuran status (November 2026)
  val myIuranBulanIni = iuranList.find { it.guruId == myGuruId && it.bulan == 11 && it.tahun == 2026 }
  val isIuranLunas = myIuranBulanIni?.statusLunas == true

  val mySimpanan = simpananList.find { it.guruId == myGuruId }?.saldo ?: 0.0
  val myLoans = pinjamanList.filter { it.guruId == myGuruId }
  val myActiveLoan = myLoans.find { it.status == "DISETUJUI" }
  val myDendaUnpaid = dendaList.filter { it.guruId == myGuruId && it.statusDenda == "BELUM_BAYAR" }

  LazyColumn(
    modifier = Modifier
      .fillMaxSize()
      .background(SlateBackground)
      .padding(16.dp),
    verticalArrangement = Arrangement.spacedBy(14.dp)
  ) {
    // Welcome Header Card
    item {
      Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = NavyDark),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(20.dp)) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
              modifier = Modifier
                .size(50.dp)
                .clip(CircleShape)
                .background(NavyLight.copy(alpha = 0.25f)),
              contentAlignment = Alignment.Center
            ) {
              Icon(Icons.Default.School, contentDescription = null, tint = Color.White, modifier = Modifier.size(28.dp))
            }
            Spacer(modifier = Modifier.width(14.dp))
            Column {
              Text("Selamat Datang, Bapak/Ibu", color = NavyLight, fontSize = 12.sp)
              Text(
                myGuru?.namaLengkap ?: (currentUser?.namaLengkap ?: "Guru SMP"),
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = Color.White
              )
              Text("NIP: ${myGuru?.nip ?: "-"}", fontSize = 11.sp, color = Color.White.copy(alpha = 0.8f))
            }
          }
        }
      }
    }

    // Status Iuran Wajib Bulan Ini Alert
    item {
      Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = if (isIuranLunas) SuccessBg else DangerBg),
        modifier = Modifier.fillMaxWidth()
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(14.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Icon(
            if (isIuranLunas) Icons.Default.CheckCircle else Icons.Default.Warning,
            contentDescription = null,
            tint = if (isIuranLunas) SuccessGreen else DangerRed,
            modifier = Modifier.size(28.dp)
          )
          Spacer(modifier = Modifier.width(12.dp))
          Column(modifier = Modifier.weight(1f)) {
            Text(
              if (isIuranLunas) "Iuran Wajib Bulan Ini: LUNAS" else "Iuran Wajib Bulan Ini: BELUM LUNAS",
              fontWeight = FontWeight.Bold,
              fontSize = 13.sp,
              color = if (isIuranLunas) SuccessGreen else DangerRed
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
              if (isIuranLunas)
                "Terima kasih telah membayar iuran wajib Rp 30.000. Anda berhak mengajukan pinjaman koperasi."
              else
                "Iuran wajib Rp 30.000 belum dibayar. Mohon bayar ke bendahara agar dapat mengajukan pinjaman.",
              fontSize = 11.sp,
              color = if (isIuranLunas) SuccessGreen else DangerRed
            )
          }
        }
      }
    }

    // Quick Stats Overview
    item {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        // Saldo Simpanan Card
        Card(
          shape = RoundedCornerShape(12.dp),
          colors = CardDefaults.cardColors(containerColor = Color.White),
          elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
          modifier = Modifier.weight(1f)
        ) {
          Column(modifier = Modifier.padding(14.dp)) {
            Text("Saldo Simpanan", fontSize = 11.sp, color = TextSecondary)
            Spacer(modifier = Modifier.height(4.dp))
            Text(formatRupiah(mySimpanan), fontWeight = FontWeight.Bold, fontSize = 16.sp, color = NavyPrimary)
            Spacer(modifier = Modifier.height(4.dp))
            Text("+ Bunga 2% / bulan", fontSize = 10.sp, color = PurpleAccent)
          }
        }

        // Pinjaman Berjalan Card
        Card(
          shape = RoundedCornerShape(12.dp),
          colors = CardDefaults.cardColors(containerColor = Color.White),
          elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
          modifier = Modifier.weight(1f)
        ) {
          Column(modifier = Modifier.padding(14.dp)) {
            Text("Pinjaman Berjalan", fontSize = 11.sp, color = TextSecondary)
            Spacer(modifier = Modifier.height(4.dp))
            Text(
              if (myActiveLoan != null) formatRupiah(myActiveLoan.totalPinjaman) else "Tidak Ada",
              fontWeight = FontWeight.Bold,
              fontSize = 15.sp,
              color = if (myActiveLoan != null) WarningAmber else SuccessGreen
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
              if (myActiveLoan != null) "Cicilan: ${formatRupiah(myActiveLoan.angsuranPerBulan)}/bln" else "Bebas tanggungan",
              fontSize = 10.sp,
              color = TextMuted
            )
          }
        }
      }
    }

    // Quick Menu Shortcuts for Guru
    item {
      SectionHeader(title = "Layanan Mandiri Anggota")
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        Button(
          onClick = { onNavigate(Screen.GuruPinjaman) },
          colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary),
          shape = RoundedCornerShape(10.dp),
          modifier = Modifier
            .weight(1f)
            .testTag("guru_ajukan_pinjaman_btn")
        ) {
          Icon(Icons.Default.RequestQuote, contentDescription = null, modifier = Modifier.size(16.dp))
          Spacer(modifier = Modifier.width(6.dp))
          Text("Ajukan Pinjaman", fontSize = 11.sp)
        }

        Button(
          onClick = { onNavigate(Screen.GuruSimpanan) },
          colors = ButtonDefaults.buttonColors(containerColor = PurpleAccent),
          shape = RoundedCornerShape(10.dp),
          modifier = Modifier.weight(1f)
        ) {
          Icon(Icons.Default.Savings, contentDescription = null, modifier = Modifier.size(16.dp))
          Spacer(modifier = Modifier.width(6.dp))
          Text("Simpanan Saya", fontSize = 11.sp)
        }
      }
    }

    if (myDendaUnpaid.isNotEmpty()) {
      item {
        Card(
          shape = RoundedCornerShape(12.dp),
          colors = CardDefaults.cardColors(containerColor = DangerBg),
          modifier = Modifier.fillMaxWidth()
        ) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Icon(Icons.Default.Warning, contentDescription = null, tint = DangerRed)
            Spacer(modifier = Modifier.width(10.dp))
            Column {
              Text("Pemberitahuan Denda Keterlambatan", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = DangerRed)
              Text("Anda memiliki ${myDendaUnpaid.size} denda keterlambatan (Rp 10.000). Silakan lunasi di loket koperasi.", fontSize = 11.sp, color = DangerRed)
            }
          }
        }
      }
    }
  }
}

// ==========================================
// FORM PENGAJUAN PINJAMAN GURU
// ==========================================
@Composable
fun GuruPinjamanScreen(
  viewModel: KoperasiViewModel
) {
  val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()
  val guruList by viewModel.guruList.collectAsStateWithLifecycle()
  val iuranList by viewModel.iuranList.collectAsStateWithLifecycle()
  val pinjamanList by viewModel.pinjamanList.collectAsStateWithLifecycle()

  val myGuru = guruList.find { it.id == currentUser?.guruId } ?: guruList.firstOrNull()
  val myGuruId = myGuru?.id ?: 1L

  // Cek apakah iuran bulan berjalan (Nov 2026) lunas
  val isIuranLunas = iuranList.find { it.guruId == myGuruId && it.bulan == 11 && it.tahun == 2026 }?.statusLunas == true

  val myLoans = pinjamanList.filter { it.guruId == myGuruId }

  var jumlahPinjamanText by remember { mutableStateOf("2000000") }
  var tenorBulan by remember { mutableStateOf(6) } // 3, 6, 10, 12 bulan

  val jumlah = jumlahPinjamanText.toDoubleOrNull() ?: 0.0
  val bungaFlat2Persen = jumlah * 0.02 * tenorBulan
  val totalPinjaman = jumlah + bungaFlat2Persen
  val cicilanPerBulan = if (tenorBulan > 0) totalPinjaman / tenorBulan else 0.0

  LazyColumn(
    modifier = Modifier
      .fillMaxSize()
      .background(SlateBackground)
      .padding(16.dp),
    verticalArrangement = Arrangement.spacedBy(14.dp)
  ) {
    // Syarat Iuran Alert
    item {
      Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = if (isIuranLunas) SuccessBg else DangerBg),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(14.dp)) {
          Text(
            if (isIuranLunas) "Syarat Pinjaman Terpenuhi: Iuran Wajib Lunas" else "Perhatian: Iuran Wajib Bulan Berjalan Belum Lunas!",
            fontWeight = FontWeight.Bold,
            fontSize = 13.sp,
            color = if (isIuranLunas) SuccessGreen else DangerRed
          )
          Spacer(modifier = Modifier.height(2.dp))
          Text(
            if (isIuranLunas)
              "Iuran wajib bulan November 2026 Anda telah lunas. Anda dapat mengajukan pinjaman dengan bunga flat 2%."
            else
              "Sesuai ketentuan Koperasi SMP Nurul Ansor, Anda hanya dapat mengajukan pinjaman jika iuran wajib bulan berjalan telah lunas.",
            fontSize = 11.sp,
            color = if (isIuranLunas) SuccessGreen else DangerRed
          )
        }
      }
    }

    // Formulir & Kalkulator Simulasi Pinjaman
    item {
      Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Text("Simulasi & Pengajuan Pinjaman Baru", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = NavyDark)
          Text("Bunga 2% flat per bulan berdasarkan pokok pinjaman awal", fontSize = 11.sp, color = TextSecondary)
          Spacer(modifier = Modifier.height(14.dp))

          OutlinedTextField(
            value = jumlahPinjamanText,
            onValueChange = { jumlahPinjamanText = it.filter { ch -> ch.isDigit() } },
            label = { Text("Jumlah Pinjaman (Rp)") },
            placeholder = { Text("Contoh: 3000000") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true,
            modifier = Modifier
              .fillMaxWidth()
              .testTag("input_jumlah_pinjaman")
          )

          Spacer(modifier = Modifier.height(10.dp))

          Text("Pilih Tenor Angsuran:", fontSize = 11.sp, color = TextSecondary)
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            listOf(3, 6, 10, 12).forEach { tenor ->
              FilterChip(
                selected = tenorBulan == tenor,
                onClick = { tenorBulan = tenor },
                label = { Text("$tenor Bulan", fontSize = 11.sp) }
              )
            }
          }

          Spacer(modifier = Modifier.height(14.dp))

          // Hasil Simulasi Breakdown
          Card(
            shape = RoundedCornerShape(10.dp),
            colors = CardDefaults.cardColors(containerColor = SlateBackground),
            modifier = Modifier.fillMaxWidth()
          ) {
            Column(modifier = Modifier.padding(12.dp)) {
              Text("Rincian Perhitungan Kewajiban:", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = NavyDark)
              Spacer(modifier = Modifier.height(6.dp))
              Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Pokok Pinjaman", fontSize = 11.sp, color = TextSecondary)
                Text(formatRupiah(jumlah), fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
              }
              Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Total Bunga (2% x $tenorBulan bln)", fontSize = 11.sp, color = TextSecondary)
                Text("+ ${formatRupiah(bungaFlat2Persen)}", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = PurpleAccent)
              }
              HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp), color = BorderLight)
              Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Total Pengembalian", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Text(formatRupiah(totalPinjaman), fontSize = 13.sp, fontWeight = FontWeight.ExtraBold, color = NavyDark)
              }
              Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Angsuran per Bulan", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Text(formatRupiah(cicilanPerBulan), fontSize = 14.sp, fontWeight = FontWeight.ExtraBold, color = SuccessGreen)
              }
            }
          }

          Spacer(modifier = Modifier.height(16.dp))

          Button(
            onClick = {
              viewModel.ajukanPinjaman(myGuruId, jumlah, tenorBulan) {}
            },
            enabled = isIuranLunas && jumlah >= 500000.0,
            colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier
              .fillMaxWidth()
              .testTag("kirim_pengajuan_pinjaman_btn")
          ) {
            Icon(Icons.Default.Send, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text("Kirim Pengajuan ke Admin", fontWeight = FontWeight.Bold)
          }
        }
      }
    }

    // Riwayat Pinjaman Saya
    item {
      SectionHeader(title = "Riwayat Pinjaman Saya (${myLoans.size})")
    }

    items(myLoans) { p ->
      Card(
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(12.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(p.noPinjaman, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = NavyPrimary)
            StatusBadge(
              text = p.status,
              isSuccess = p.status == "DISETUJUI" || p.status == "LUNAS",
              isWarning = p.status == "MENUNGGU",
              isDanger = p.status == "DITOLAK"
            )
          }
          Spacer(modifier = Modifier.height(4.dp))
          Text("Pokok: ${formatRupiah(p.jumlahPinjaman)} • Tenor: ${p.lamaAngsuranBulan} bln", fontSize = 11.sp, color = TextSecondary)
          Text("Cicilan: ${formatRupiah(p.angsuranPerBulan)} / bulan", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = SuccessGreen)
          if (p.catatanAdmin.isNotBlank()) {
            Text("Catatan Admin: ${p.catatanAdmin}", fontSize = 10.sp, color = TextMuted)
          }
        }
      }
    }
  }
}
