package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.ui.theme.*
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

fun formatRupiah(amount: Double): String {
  val symbols = DecimalFormatSymbols(Locale("id", "ID")).apply {
    groupingSeparator = '.'
    decimalSeparator = ','
  }
  val formatter = DecimalFormat("#,###", symbols)
  return "Rp " + formatter.format(amount)
}

@Composable
fun StatCard(
  title: String,
  value: String,
  icon: ImageVector,
  iconBgColor: Color = NavyPrimary,
  subtitle: String? = null,
  modifier: Modifier = Modifier,
  onClick: (() -> Unit)? = null
) {
  ElevatedCard(
    modifier = modifier
      .then(if (onClick != null) Modifier.clickable { onClick() } else Modifier),
    shape = RoundedCornerShape(14.dp),
    colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface),
    elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp)
  ) {
    Column(modifier = Modifier.padding(14.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = title,
          style = MaterialTheme.typography.labelMedium,
          color = TextSecondary,
          fontWeight = FontWeight.Medium
        )
        Box(
          modifier = Modifier
            .size(36.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(iconBgColor.copy(alpha = 0.15f)),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = icon,
            contentDescription = null,
            tint = iconBgColor,
            modifier = Modifier.size(20.dp)
          )
        }
      }
      Spacer(modifier = Modifier.height(8.dp))
      Text(
        text = value,
        style = MaterialTheme.typography.titleLarge.copy(fontSize = 19.sp),
        fontWeight = FontWeight.Bold,
        color = NavyDark,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis
      )
      if (subtitle != null) {
        Spacer(modifier = Modifier.height(4.dp))
        Text(
          text = subtitle,
          style = MaterialTheme.typography.bodySmall,
          color = TextMuted
        )
      }
    }
  }
}

@Composable
fun StatusBadge(
  text: String,
  isSuccess: Boolean = false,
  isWarning: Boolean = false,
  isDanger: Boolean = false
) {
  val (bgColor, textColor) = when {
    isSuccess -> SuccessBg to SuccessGreen
    isWarning -> WarningBg to WarningAmber
    isDanger -> DangerBg to DangerRed
    else -> Color(0xFFE2E8F0) to TextSecondary
  }

  Box(
    modifier = Modifier
      .clip(RoundedCornerShape(6.dp))
      .background(bgColor)
      .padding(horizontal = 8.dp, vertical = 3.dp)
  ) {
    Text(
      text = text,
      color = textColor,
      fontSize = 11.sp,
      fontWeight = FontWeight.SemiBold
    )
  }
}

@Composable
fun SectionHeader(
  title: String,
  actionText: String? = null,
  onActionClick: (() -> Unit)? = null,
  modifier: Modifier = Modifier
) {
  Row(
    modifier = modifier
      .fillMaxWidth()
      .padding(vertical = 8.dp),
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.CenterVertically
  ) {
    Text(
      text = title,
      style = MaterialTheme.typography.titleMedium,
      fontWeight = FontWeight.Bold,
      color = NavyDark
    )
    if (actionText != null && onActionClick != null) {
      TextButton(onClick = onActionClick) {
        Text(actionText, color = NavyPrimary, fontWeight = FontWeight.SemiBold)
      }
    }
  }
}

@Composable
fun KwitansiReceiptDialog(
  title: String,
  noKwitansi: String,
  tanggal: String,
  namaAnggota: String,
  nominal: Double,
  untukPembayaran: String,
  metode: String = "Tunai",
  petugas: String = "Kasir Koperasi",
  onDismiss: () -> Unit
) {
  Dialog(onDismissRequest = onDismiss) {
    Card(
      shape = RoundedCornerShape(16.dp),
      colors = CardDefaults.cardColors(containerColor = Color.White),
      modifier = Modifier
        .fillMaxWidth()
        .padding(8.dp)
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(20.dp)
      ) {
        // Kop Surat Koperasi
        Row(
          modifier = Modifier.fillMaxWidth(),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Box(
            modifier = Modifier
              .size(44.dp)
              .clip(CircleShape)
              .background(NavyDark),
            contentAlignment = Alignment.Center
          ) {
            Icon(Icons.Default.AccountBalance, contentDescription = null, tint = Color.White)
          }
          Spacer(modifier = Modifier.width(12.dp))
          Column {
            Text("KOPERASI SMP NURUL ANSOR", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = NavyDark)
            Text("Kuitansi Resmi Pembayaran", fontSize = 12.sp, color = TextSecondary)
            Text("Jl. Pendidikan No. 45, Wonocolo, Surabaya", fontSize = 10.sp, color = TextMuted)
          }
        }

        HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = BorderLight)

        // Metadata kuitansi
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
          Text("No: $noKwitansi", fontSize = 11.sp, fontWeight = FontWeight.Medium, color = NavyPrimary)
          Text(tanggal, fontSize = 11.sp, color = TextSecondary)
        }

        Spacer(modifier = Modifier.height(12.dp))

        Card(
          shape = RoundedCornerShape(8.dp),
          colors = CardDefaults.cardColors(containerColor = SlateBackground),
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(modifier = Modifier.padding(12.dp)) {
            ReceiptRow("Telah diterima dari", namaAnggota)
            Spacer(modifier = Modifier.height(6.dp))
            ReceiptRow("Uang sejumlah", formatRupiah(nominal), isBold = true, textColor = NavyDark)
            Spacer(modifier = Modifier.height(6.dp))
            ReceiptRow("Untuk pembayaran", untukPembayaran)
            Spacer(modifier = Modifier.height(6.dp))
            ReceiptRow("Metode Pembayaran", metode)
          }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Tanda tangan & cap
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("Penyetor", fontSize = 11.sp, color = TextSecondary)
            Spacer(modifier = Modifier.height(36.dp))
            Text("($namaAnggota)", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
          }

          Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("Petugas Koperasi", fontSize = 11.sp, color = TextSecondary)
            Spacer(modifier = Modifier.height(36.dp))
            Text("($petugas)", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
          }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.End
        ) {
          Button(
            onClick = onDismiss,
            colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary),
            shape = RoundedCornerShape(8.dp)
          ) {
            Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text("Tutup Kuitansi")
          }
        }
      }
    }
  }
}

@Composable
fun ReceiptRow(label: String, value: String, isBold: Boolean = false, textColor: Color = TextPrimary) {
  Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
    Text(label, fontSize = 11.sp, color = TextSecondary)
    Text(
      value,
      fontSize = 11.sp,
      fontWeight = if (isBold) FontWeight.Bold else FontWeight.Normal,
      color = textColor,
      textAlign = TextAlign.End
    )
  }
}
