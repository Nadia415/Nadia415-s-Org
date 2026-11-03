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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.entity.GuruEntity
import com.example.data.entity.IuranEntity
import com.example.ui.KoperasiViewModel
import com.example.ui.components.KwitansiReceiptDialog
import com.example.ui.components.SectionHeader
import com.example.ui.components.StatusBadge
import com.example.ui.components.formatRupiah
import com.example.ui.theme.*
import java.util.*

@Composable
fun IuranWajibScreen(
  viewModel: KoperasiViewModel
) {
  val guruList by viewModel.guruList.collectAsStateWithLifecycle()
  val iuranList by viewModel.iuranList.collectAsStateWithLifecycle()

  var selectedMonth by remember { mutableStateOf(11) } // November
  var selectedYear by remember { mutableStateOf(2026) }

  var kwitansiToShow by remember { mutableStateOf<Triple<IuranEntity, GuruEntity, String>?>(null) }

  val monthNames = listOf(
    "Januari", "Februari", "Maret", "April", "Mei", "Juni",
    "Juli", "Agustus", "September", "Oktober", "November", "Desember"
  )

  // Map guru to their iuran record for the selected month/year
  val teacherIuranPairs = guruList.map { guru ->
    val iuranRecord = iuranList.find { it.guruId == guru.id && it.bulan == selectedMonth && it.tahun == selectedYear }
      ?: IuranEntity(
        guruId = guru.id,
        bulan = selectedMonth,
        tahun = selectedYear,
        nominal = 30000.0,
        statusLunas = false,
        keterangan = "Belum dibayar"
      )
    guru to iuranRecord
  }

  val totalLunas = teacherIuranPairs.count { it.second.statusLunas }
  val totalBelumLunas = teacherIuranPairs.size - totalLunas
  val totalUangTerkumpul = totalLunas * 30000.0

  LazyColumn(
    modifier = Modifier
      .fillMaxSize()
      .background(SlateBackground)
      .padding(16.dp),
    verticalArrangement = Arrangement.spacedBy(14.dp)
  ) {
    // Info Banner & Policy Reminder
    item {
      Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = NavyDark),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Info, contentDescription = null, tint = NavyLight, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("Ketentuan Iuran Wajib Anggota", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
          }
          Spacer(modifier = Modifier.height(6.dp))
          Text(
            "• Iuran wajib sebesar Rp 30.000 per bulan untuk setiap guru terdaftar.\n• Syarat Pengajuan Pinjaman: Guru hanya dapat mengajukan pinjaman jika iuran wajib bulan berjalan telah LUNAS.",
            color = Color.White.copy(alpha = 0.9f),
            fontSize = 11.sp,
            lineHeight = 16.sp
          )
        }
      }
    }

    // Filter Periode Bulan & Tahun
    item {
      Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(14.dp)) {
          Text("Pilih Periode Iuran:", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = NavyDark)
          Spacer(modifier = Modifier.height(10.dp))
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            // Month Dropdown selector
            var expandedMonth by remember { mutableStateOf(false) }
            Box(modifier = Modifier.weight(1f)) {
              OutlinedButton(
                onClick = { expandedMonth = true },
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth()
              ) {
                Text(monthNames[selectedMonth - 1], fontSize = 12.sp)
                Icon(Icons.Default.ArrowDropDown, contentDescription = null)
              }
              DropdownMenu(expanded = expandedMonth, onDismissRequest = { expandedMonth = false }) {
                monthNames.forEachIndexed { index, name ->
                  DropdownMenuItem(
                    text = { Text(name, fontSize = 12.sp) },
                    onClick = {
                      selectedMonth = index + 1
                      expandedMonth = false
                    }
                  )
                }
              }
            }

            // Year Selector
            Text("$selectedYear", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = NavyPrimary)
          }
        }
      }
    }

    // Rekap Bulanan Cards
    item {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        Card(
          shape = RoundedCornerShape(12.dp),
          colors = CardDefaults.cardColors(containerColor = SuccessBg),
          modifier = Modifier.weight(1f)
        ) {
          Column(modifier = Modifier.padding(12.dp)) {
            Text("LUNAS", color = SuccessGreen, fontWeight = FontWeight.Bold, fontSize = 11.sp)
            Text("$totalLunas Guru", fontWeight = FontWeight.ExtraBold, fontSize = 16.sp, color = SuccessGreen)
            Text(formatRupiah(totalUangTerkumpul), fontSize = 11.sp, color = SuccessGreen)
          }
        }

        Card(
          shape = RoundedCornerShape(12.dp),
          colors = CardDefaults.cardColors(containerColor = DangerBg),
          modifier = Modifier.weight(1f)
        ) {
          Column(modifier = Modifier.padding(12.dp)) {
            Text("BELUM BAYAR", color = DangerRed, fontWeight = FontWeight.Bold, fontSize = 11.sp)
            Text("$totalBelumLunas Guru", fontWeight = FontWeight.ExtraBold, fontSize = 16.sp, color = DangerRed)
            Text(formatRupiah(totalBelumLunas * 30000.0), fontSize = 11.sp, color = DangerRed)
          }
        }
      }
    }

    item {
      SectionHeader(
        title = "Daftar Pembayaran Guru (${monthNames[selectedMonth - 1]} $selectedYear)"
      )
    }

    items(teacherIuranPairs) { (guru, iuran) ->
      Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier
          .fillMaxWidth()
          .testTag("iuran_card_${guru.id}")
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(14.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column(modifier = Modifier.weight(1f)) {
            Text(
              guru.namaLengkap,
              fontWeight = FontWeight.Bold,
              fontSize = 13.sp,
              color = NavyDark
            )
            Text(
              "NIP: ${guru.nip}",
              fontSize = 11.sp,
              color = TextSecondary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Row(
              horizontalArrangement = Arrangement.spacedBy(8.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              StatusBadge(
                text = if (iuran.statusLunas) "LUNAS" else "BELUM BAYAR",
                isSuccess = iuran.statusLunas,
                isDanger = !iuran.statusLunas
              )
              if (iuran.statusLunas && iuran.tanggalBayar != null) {
                Text(
                  "Tgl: ${iuran.tanggalBayar}",
                  fontSize = 10.sp,
                  color = TextMuted
                )
              }
            }
          }

          // Action Button
          if (!iuran.statusLunas) {
            Button(
              onClick = { viewModel.bayarIuran(iuran) },
              colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen),
              shape = RoundedCornerShape(8.dp),
              modifier = Modifier.testTag("bayar_iuran_${guru.id}")
            ) {
              Icon(Icons.Default.Payment, contentDescription = null, modifier = Modifier.size(14.dp))
              Spacer(modifier = Modifier.width(4.dp))
              Text("Bayar", fontSize = 11.sp)
            }
          } else {
            OutlinedButton(
              onClick = {
                kwitansiToShow = Triple(iuran, guru, iuran.noKwitansi ?: "KW-IU-${guru.id}")
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

  // Kwitansi Dialog
  if (kwitansiToShow != null) {
    val (iuran, guru, noKwitansi) = kwitansiToShow!!
    KwitansiReceiptDialog(
      title = "Bukti Iuran Wajib",
      noKwitansi = noKwitansi,
      tanggal = iuran.tanggalBayar ?: "2026-11-02",
      namaAnggota = guru.namaLengkap,
      nominal = iuran.nominal,
      untukPembayaran = "Iuran Wajib Anggota Periode ${monthNames[iuran.bulan - 1]} ${iuran.tahun}",
      metode = "Tunai / Transfer",
      petugas = "Bendahara Koperasi SMP",
      onDismiss = { kwitansiToShow = null }
    )
  }
}
