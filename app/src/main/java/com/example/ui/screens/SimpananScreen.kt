package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.draw.clip
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.entity.GuruEntity
import com.example.data.entity.SimpananEntity
import com.example.ui.KoperasiViewModel
import com.example.ui.components.SectionHeader
import com.example.ui.components.StatusBadge
import com.example.ui.components.formatRupiah
import com.example.ui.theme.*

@Composable
fun SimpananScreen(
  viewModel: KoperasiViewModel
) {
  val guruList by viewModel.guruList.collectAsStateWithLifecycle()
  val simpananList by viewModel.simpananList.collectAsStateWithLifecycle()
  val totalSaldo by viewModel.totalSaldoSimpanan.collectAsStateWithLifecycle()
  val allTransaksi by viewModel.allTransaksiSimpanan.collectAsStateWithLifecycle()

  var selectedTab by remember { mutableStateOf(0) } // 0: Saldo Anggota, 1: Riwayat Mutasi
  var showDepositWithdrawDialog by remember { mutableStateOf<Pair<GuruEntity, String>?>(null) }
  var showBungaCalculatorDialog by remember { mutableStateOf(false) }

  LazyColumn(
    modifier = Modifier
      .fillMaxSize()
      .background(SlateBackground)
      .padding(16.dp),
    verticalArrangement = Arrangement.spacedBy(14.dp)
  ) {
    // Total Saldo Simpanan Card
    item {
      Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = NavyDark),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(20.dp)) {
          Text("Total Simpanan Anggota Koperasi", color = NavyLight, fontSize = 12.sp)
          Spacer(modifier = Modifier.height(4.dp))
          Text(formatRupiah(totalSaldo), fontWeight = FontWeight.Bold, fontSize = 24.sp, color = Color.White)
          Spacer(modifier = Modifier.height(8.dp))
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text("Bunga Simpanan: 2% / bulan flat", color = Color.White.copy(alpha = 0.8f), fontSize = 11.sp)
            Button(
              onClick = { showBungaCalculatorDialog = true },
              colors = ButtonDefaults.buttonColors(containerColor = PurpleAccent),
              shape = RoundedCornerShape(8.dp),
              modifier = Modifier.testTag("hitung_bunga_button")
            ) {
              Icon(Icons.Default.Calculate, contentDescription = null, modifier = Modifier.size(16.dp))
              Spacer(modifier = Modifier.width(6.dp))
              Text("Hitung Bunga 2%", fontSize = 11.sp)
            }
          }
        }
      }
    }

    // Tabs: Saldo per Guru vs Riwayat Mutasi
    item {
      TabRow(
        selectedTabIndex = selectedTab,
        containerColor = Color.White,
        contentColor = NavyPrimary,
        modifier = Modifier.clip(RoundedCornerShape(10.dp))
      ) {
        Tab(
          selected = selectedTab == 0,
          onClick = { selectedTab = 0 },
          text = { Text("Saldo Anggota", fontWeight = FontWeight.Bold, fontSize = 12.sp) }
        )
        Tab(
          selected = selectedTab == 1,
          onClick = { selectedTab = 1 },
          text = { Text("Riwayat Mutasi Transaksi", fontWeight = FontWeight.Bold, fontSize = 12.sp) }
        )
      }
    }

    if (selectedTab == 0) {
      item {
        SectionHeader(title = "Daftar Saldo Guru (${guruList.size} Anggota)")
      }

      items(guruList) { guru ->
        val simpanan = simpananList.find { it.guruId == guru.id }
        val saldo = simpanan?.saldo ?: 0.0

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
              Text(guru.namaLengkap, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = NavyDark)
              Text("NIP: ${guru.nip}", fontSize = 11.sp, color = TextSecondary)
              Spacer(modifier = Modifier.height(4.dp))
              Text("Saldo: ${formatRupiah(saldo)}", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = NavyPrimary)
            }

            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
              Button(
                onClick = { showDepositWithdrawDialog = guru to "SETORAN" },
                colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen),
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
              ) {
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Setor", fontSize = 11.sp)
              }

              OutlinedButton(
                onClick = { showDepositWithdrawDialog = guru to "PENARIKAN" },
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = DangerRed),
                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
              ) {
                Icon(Icons.Default.Remove, contentDescription = null, modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Tarik", fontSize = 11.sp)
              }
            }
          }
        }
      }
    } else {
      item {
        SectionHeader(title = "Riwayat Mutasi Simpanan (${allTransaksi.size} Transaksi)")
      }

      if (allTransaksi.isEmpty()) {
        item {
          Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            modifier = Modifier.fillMaxWidth()
          ) {
            Box(modifier = Modifier.padding(24.dp), contentAlignment = Alignment.Center) {
              Text("Belum ada mutasi simpanan", color = TextMuted)
            }
          }
        }
      } else {
        items(allTransaksi) { trx ->
          val guru = guruList.find { it.id == trx.guruId }
          Card(
            shape = RoundedCornerShape(10.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
            modifier = Modifier.fillMaxWidth()
          ) {
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              val (color, label) = when (trx.jenis) {
                "SETORAN" -> SuccessGreen to "+ SETORAN"
                "PENARIKAN" -> DangerRed to "- PENARIKAN"
                else -> PurpleAccent to "+ BUNGA 2%"
              }

              Column(modifier = Modifier.weight(1f)) {
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween
                ) {
                  Text(guru?.namaLengkap ?: "Guru ID: ${trx.guruId}", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = NavyDark)
                  Text(trx.tanggal, fontSize = 10.sp, color = TextMuted)
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(trx.keterangan, fontSize = 11.sp, color = TextSecondary)
                if (trx.dasarPerhitunganBunga != null) {
                  Text("Rincian: ${trx.dasarPerhitunganBunga}", fontSize = 10.sp, color = PurpleAccent, fontWeight = FontWeight.Medium)
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text("Saldo Akhir: ${formatRupiah(trx.saldoSetelah)}", fontSize = 10.sp, color = TextMuted)
              }

              Spacer(modifier = Modifier.width(8.dp))

              Text(
                "${if (trx.jenis == "PENARIKAN") "-" else "+"}${formatRupiah(trx.nominal)}",
                color = color,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp
              )
            }
          }
        }
      }
    }
  }

  // Dialog Setor / Tarik Simpanan
  if (showDepositWithdrawDialog != null) {
    val (guru, jenis) = showDepositWithdrawDialog!!
    DepositWithdrawDialog(
      guru = guru,
      jenis = jenis,
      onDismiss = { showDepositWithdrawDialog = null },
      onSubmit = { nominal, keterangan ->
        viewModel.tambahTransaksiSimpanan(guru.id, jenis, nominal, keterangan)
        showDepositWithdrawDialog = null
      }
    )
  }

  // Dialog Rincian & Hitung Bunga 2%
  if (showBungaCalculatorDialog) {
    BungaSimpananDialog(
      guruList = guruList,
      simpananList = simpananList,
      onDismiss = { showBungaCalculatorDialog = false },
      onApplyBunga = { listToApply ->
        listToApply.forEach { (guruId, nominal, rincian) ->
          viewModel.tambahTransaksiSimpanan(
            guruId = guruId,
            jenis = "BUNGA",
            nominal = nominal,
            keterangan = "Bunga Simpanan 2% ($rincian)"
          )
        }
        showBungaCalculatorDialog = false
      }
    )
  }
}

@Composable
fun DepositWithdrawDialog(
  guru: GuruEntity,
  jenis: String,
  onDismiss: () -> Unit,
  onSubmit: (Double, String) -> Unit
) {
  var nominalText by remember { mutableStateOf("") }
  var keterangan by remember { mutableStateOf(if (jenis == "SETORAN") "Setoran simpanan sukarela" else "Penarikan simpanan") }
  var errorMsg by remember { mutableStateOf<String?>(null) }

  Dialog(onDismissRequest = onDismiss) {
    Card(
      shape = RoundedCornerShape(16.dp),
      colors = CardDefaults.cardColors(containerColor = Color.White),
      modifier = Modifier
        .fillMaxWidth()
        .padding(8.dp)
    ) {
      Column(modifier = Modifier.padding(20.dp)) {
        Text("Catat $jenis Simpanan", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = NavyDark)
        Spacer(modifier = Modifier.height(4.dp))
        Text("Anggota: ${guru.namaLengkap}", fontSize = 12.sp, color = TextSecondary)
        Spacer(modifier = Modifier.height(14.dp))

        OutlinedTextField(
          value = nominalText,
          onValueChange = { nominalText = it.filter { ch -> ch.isDigit() } },
          label = { Text("Nominal (Rp)") },
          placeholder = { Text("Contoh: 100000") },
          keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
          singleLine = true,
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(8.dp)
        )

        Spacer(modifier = Modifier.height(10.dp))

        OutlinedTextField(
          value = keterangan,
          onValueChange = { keterangan = it },
          label = { Text("Keterangan") },
          singleLine = true,
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(8.dp)
        )

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
              val nominal = nominalText.toDoubleOrNull() ?: 0.0
              if (nominal <= 0) {
                errorMsg = "Nominal harus lebih dari 0!"
                return@Button
              }
              onSubmit(nominal, keterangan)
            },
            colors = ButtonDefaults.buttonColors(containerColor = if (jenis == "SETORAN") SuccessGreen else DangerRed),
            shape = RoundedCornerShape(8.dp)
          ) {
            Text("Simpan $jenis")
          }
        }
      }
    }
  }
}

@Composable
fun BungaSimpananDialog(
  guruList: List<GuruEntity>,
  simpananList: List<SimpananEntity>,
  onDismiss: () -> Unit,
  onApplyBunga: (List<Triple<Long, Double, String>>) -> Unit
) {
  // Hitung bunga 2% secara transparan untuk setiap guru
  val calculations = guruList.mapNotNull { guru ->
    val saldo = simpananList.find { it.guruId == guru.id }?.saldo ?: 0.0
    if (saldo > 0) {
      val bunga = saldo * 0.02
      val rincian = "2% x Saldo Rp ${formatRupiah(saldo)} = ${formatRupiah(bunga)}"
      Triple(guru, bunga, rincian)
    } else null
  }

  val totalBungaDihitung = calculations.sumOf { it.second }

  Dialog(onDismissRequest = onDismiss) {
    Card(
      shape = RoundedCornerShape(16.dp),
      colors = CardDefaults.cardColors(containerColor = Color.White),
      modifier = Modifier
        .fillMaxWidth()
        .padding(8.dp)
    ) {
      Column(modifier = Modifier.padding(20.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(Icons.Default.Calculate, contentDescription = null, tint = PurpleAccent)
          Spacer(modifier = Modifier.width(8.dp))
          Text("Rincian Perhitungan Bunga 2%", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = NavyDark)
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(
          "Dasar Perhitungan: 2.0% per bulan dari saldo simpanan saat ini sesuai ketentuan AD/ART Koperasi SMP Nurul Ansor.",
          fontSize = 11.sp,
          color = TextSecondary
        )

        Spacer(modifier = Modifier.height(12.dp))

        Card(
          shape = RoundedCornerShape(8.dp),
          colors = CardDefaults.cardColors(containerColor = PurpleAccentLight),
          modifier = Modifier.fillMaxWidth()
        ) {
          Row(
            modifier = Modifier.padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text("Total Bunga Diberikan:", fontWeight = FontWeight.SemiBold, fontSize = 12.sp, color = PurpleAccentDark)
            Text(formatRupiah(totalBungaDihitung), fontWeight = FontWeight.Bold, fontSize = 14.sp, color = PurpleAccentDark)
          }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Breakdown List
        LazyColumn(
          modifier = Modifier
            .fillMaxWidth()
            .heightIn(max = 240.dp),
          verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          items(calculations) { (guru, bunga, rincian) ->
            Card(
              shape = RoundedCornerShape(6.dp),
              colors = CardDefaults.cardColors(containerColor = SlateBackground),
              modifier = Modifier.fillMaxWidth()
            ) {
              Column(modifier = Modifier.padding(8.dp)) {
                Text(guru.namaLengkap, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = NavyDark)
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween
                ) {
                  Text(rincian, fontSize = 10.sp, color = TextSecondary)
                  Text("+ ${formatRupiah(bunga)}", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = SuccessGreen)
                }
              }
            }
          }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
          TextButton(onClick = onDismiss) { Text("Batal") }
          Spacer(modifier = Modifier.width(8.dp))
          Button(
            onClick = {
              val list = calculations.map { Triple(it.first.id, it.second, it.third) }
              onApplyBunga(list)
            },
            colors = ButtonDefaults.buttonColors(containerColor = PurpleAccent),
            shape = RoundedCornerShape(8.dp)
          ) {
            Text("Terapkan Bunga ke Saldo")
          }
        }
      }
    }
  }
}
