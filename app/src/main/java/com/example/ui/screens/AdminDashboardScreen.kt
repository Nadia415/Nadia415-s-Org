package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.KoperasiViewModel
import com.example.ui.Screen
import com.example.ui.components.SectionHeader
import com.example.ui.components.StatCard
import com.example.ui.components.formatRupiah
import com.example.ui.theme.*

@Composable
fun AdminDashboardScreen(
  viewModel: KoperasiViewModel,
  onNavigate: (Screen) -> Unit
) {
  val activeGuruCount by viewModel.activeGuruCount.collectAsStateWithLifecycle()
  val totalIuran by viewModel.totalIuranLunas.collectAsStateWithLifecycle()
  val totalSimpanan by viewModel.totalSaldoSimpanan.collectAsStateWithLifecycle()
  val totalPinjaman by viewModel.totalPinjamanBerjalan.collectAsStateWithLifecycle()
  val totalAngsuran by viewModel.totalAngsuranDiterima.collectAsStateWithLifecycle()
  val totalDenda by viewModel.totalDendaLunas.collectAsStateWithLifecycle()
  val totalProduk by viewModel.totalProdukCount.collectAsStateWithLifecycle()
  val totalPenjualan by viewModel.totalPenjualan.collectAsStateWithLifecycle()
  val recentLogs by viewModel.recentLogs.collectAsStateWithLifecycle()
  val pinjamanList by viewModel.pinjamanList.collectAsStateWithLifecycle()

  val pendingLoans = pinjamanList.filter { it.status == "MENUNGGU" }

  LazyColumn(
    modifier = Modifier
      .fillMaxSize()
      .background(SlateBackground)
      .padding(16.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    // Banner Sambutan
    item {
      Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = NavyDark),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(20.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column(modifier = Modifier.weight(1f)) {
              Text(
                "Sistem Koperasi SMP Nurul Ansor",
                color = NavyLight,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium
              )
              Spacer(modifier = Modifier.height(4.dp))
              Text(
                "Panel Utama Administrator",
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
              )
              Spacer(modifier = Modifier.height(6.dp))
              Text(
                "Pantau simpan pinjam, iuran wajib guru, kasir toko, dan stok barang secara real-time.",
                color = Color.White.copy(alpha = 0.8f),
                fontSize = 12.sp
              )
            }
          }

          if (pendingLoans.isNotEmpty()) {
            Spacer(modifier = Modifier.height(14.dp))
            Surface(
              shape = RoundedCornerShape(8.dp),
              color = WarningAmber.copy(alpha = 0.2f),
              modifier = Modifier.fillMaxWidth()
            ) {
              Row(
                modifier = Modifier.padding(10.dp),
                verticalAlignment = Alignment.CenterVertically
              ) {
                Icon(Icons.Default.NotificationsActive, contentDescription = null, tint = WarningAmber, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                  "Pemberitahuan: Ada ${pendingLoans.size} pengajuan pinjaman menunggu persetujuan Anda!",
                  color = Color.White,
                  fontSize = 11.sp,
                  fontWeight = FontWeight.SemiBold
                )
              }
            }
          }
        }
      }
    }

    // Quick Actions
    item {
      SectionHeader(title = "Aksi Cepat")
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        Button(
          onClick = { onNavigate(Screen.TokoKoperasi) },
          colors = ButtonDefaults.buttonColors(containerColor = PurpleAccent),
          shape = RoundedCornerShape(10.dp),
          modifier = Modifier
            .weight(1f)
            .testTag("quick_action_kasir")
        ) {
          Icon(Icons.Default.PointOfSale, contentDescription = null, modifier = Modifier.size(16.dp))
          Spacer(modifier = Modifier.width(6.dp))
          Text("Kasir Toko", fontSize = 12.sp)
        }

        Button(
          onClick = { onNavigate(Screen.IuranWajib) },
          colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary),
          shape = RoundedCornerShape(10.dp),
          modifier = Modifier
            .weight(1f)
            .testTag("quick_action_iuran")
        ) {
          Icon(Icons.Default.Payments, contentDescription = null, modifier = Modifier.size(16.dp))
          Spacer(modifier = Modifier.width(6.dp))
          Text("Catat Iuran", fontSize = 12.sp)
        }

        Button(
          onClick = { onNavigate(Screen.Laporan) },
          colors = ButtonDefaults.buttonColors(containerColor = NavySecondary),
          shape = RoundedCornerShape(10.dp),
          modifier = Modifier
            .weight(1f)
            .testTag("quick_action_laporan")
        ) {
          Icon(Icons.Default.Assessment, contentDescription = null, modifier = Modifier.size(16.dp))
          Spacer(modifier = Modifier.width(6.dp))
          Text("Laporan", fontSize = 12.sp)
        }
      }
    }

    // 8 Kartu Informasi Sesuai Spesifikasi Prompt Bagian 4
    item {
      SectionHeader(title = "Statistik Koperasi Sekolah")
      Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        // Baris 1: Anggota & Iuran Wajib
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          StatCard(
            title = "Anggota Guru",
            value = "$activeGuruCount Orang",
            icon = Icons.Default.Groups,
            iconBgColor = NavyPrimary,
            subtitle = "Terdaftar & Aktif",
            modifier = Modifier.weight(1f),
            onClick = { onNavigate(Screen.DataGuru) }
          )
          StatCard(
            title = "Iuran Wajib",
            value = formatRupiah(totalIuran),
            icon = Icons.Default.Payments,
            iconBgColor = SuccessGreen,
            subtitle = "Rp 30.000 / bln",
            modifier = Modifier.weight(1f),
            onClick = { onNavigate(Screen.IuranWajib) }
          )
        }

        // Baris 2: Simpanan Anggota & Pinjaman Berjalan
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          StatCard(
            title = "Simpanan Anggota",
            value = formatRupiah(totalSimpanan),
            icon = Icons.Default.AccountBalanceWallet,
            iconBgColor = InfoBlue,
            subtitle = "Bunga 2% / bulan",
            modifier = Modifier.weight(1f),
            onClick = { onNavigate(Screen.SimpananGuru) }
          )
          StatCard(
            title = "Pinjaman Berjalan",
            value = formatRupiah(totalPinjaman),
            icon = Icons.Default.RequestQuote,
            iconBgColor = WarningAmber,
            subtitle = "Bunga flat 2%",
            modifier = Modifier.weight(1f),
            onClick = { onNavigate(Screen.Pinjaman) }
          )
        }

        // Baris 3: Angsuran Diterima & Total Denda
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          StatCard(
            title = "Angsuran Diterima",
            value = formatRupiah(totalAngsuran),
            icon = Icons.Default.CalendarMonth,
            iconBgColor = SuccessGreen,
            subtitle = "Total cicilan lunas",
            modifier = Modifier.weight(1f),
            onClick = { onNavigate(Screen.Angsuran) }
          )
          StatCard(
            title = "Total Denda",
            value = formatRupiah(totalDenda),
            icon = Icons.Default.Warning,
            iconBgColor = DangerRed,
            subtitle = "Rp 10.000 / kasus",
            modifier = Modifier.weight(1f),
            onClick = { onNavigate(Screen.Denda) }
          )
        }

        // Baris 4: Produk Toko & Total Penjualan
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          StatCard(
            title = "Produk Toko",
            value = "$totalProduk Item",
            icon = Icons.Default.Storefront,
            iconBgColor = PurpleAccent,
            subtitle = "Seragam, ATK, Buku",
            modifier = Modifier.weight(1f),
            onClick = { onNavigate(Screen.TokoKoperasi) }
          )
          StatCard(
            title = "Penjualan Toko",
            value = formatRupiah(totalPenjualan),
            icon = Icons.Default.ReceiptLong,
            iconBgColor = NavyDark,
            subtitle = "Kasir POS terkomputerisasi",
            modifier = Modifier.weight(1f),
            onClick = { onNavigate(Screen.TokoKoperasi) }
          )
        }
      }
    }

    // Grafik Komposisi Arus Kas
    item {
      Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Text(
            "Ringkasan Pemasukan & Keuangan",
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            color = NavyDark
          )
          Spacer(modifier = Modifier.height(12.dp))

          val totalKasMasuk = totalIuran + totalSimpanan + totalAngsuran + totalDenda + totalPenjualan
          Text(
            "Total Kas Masuk Terakumulasi: ${formatRupiah(totalKasMasuk)}",
            fontWeight = FontWeight.Bold,
            fontSize = 13.sp,
            color = SuccessGreen
          )
          Spacer(modifier = Modifier.height(10.dp))

          // Progress bar distribution
          LinearProgressIndicator(
            progress = { 0.75f },
            modifier = Modifier
              .fillMaxWidth()
              .height(10.dp)
              .clip(RoundedCornerShape(5.dp)),
            color = NavyPrimary,
            trackColor = BorderLight
          )
          Spacer(modifier = Modifier.height(8.dp))
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Text("Simpanan & Iuran: 55%", fontSize = 11.sp, color = TextSecondary)
            Text("Angsuran & Toko: 45%", fontSize = 11.sp, color = TextSecondary)
          }
        }
      }
    }

    // Tabel Aktivitas Terbaru
    item {
      SectionHeader(title = "Aktivitas & Log Sistem Terbaru")
    }

    if (recentLogs.isEmpty()) {
      item {
        Card(
          shape = RoundedCornerShape(12.dp),
          colors = CardDefaults.cardColors(containerColor = Color.White),
          modifier = Modifier.fillMaxWidth()
        ) {
          Box(modifier = Modifier.padding(24.dp), contentAlignment = Alignment.Center) {
            Text("Belum ada log aktivitas", color = TextMuted, fontSize = 13.sp)
          }
        }
      }
    } else {
      items(recentLogs.take(5)) { log ->
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
            Box(
              modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(NavyLight.copy(alpha = 0.3f)),
              contentAlignment = Alignment.Center
            ) {
              Icon(Icons.Default.History, contentDescription = null, tint = NavyPrimary, modifier = Modifier.size(18.dp))
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
              Text(
                log.aktivitas,
                fontWeight = FontWeight.Medium,
                fontSize = 12.sp,
                color = TextPrimary
              )
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
              ) {
                Text(
                  "Oleh: ${log.username} (${log.peran})",
                  fontSize = 11.sp,
                  color = TextMuted
                )
                Text(
                  log.tanggalAktivitas,
                  fontSize = 10.sp,
                  color = TextMuted
                )
              }
            }
          }
        }
      }
    }
  }
}
