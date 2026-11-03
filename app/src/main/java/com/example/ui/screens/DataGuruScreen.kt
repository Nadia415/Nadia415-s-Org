package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.entity.GuruEntity
import com.example.ui.KoperasiViewModel
import com.example.ui.components.SectionHeader
import com.example.ui.components.StatusBadge
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun DataGuruScreen(
  viewModel: KoperasiViewModel
) {
  val guruList by viewModel.guruList.collectAsStateWithLifecycle()
  var searchQuery by remember { mutableStateOf("") }
  var showAddDialog by remember { mutableStateOf(false) }
  var editingGuru by remember { mutableStateOf<GuruEntity?>(null) }
  var guruDetailToView by remember { mutableStateOf<GuruEntity?>(null) }
  var guruToDelete by remember { mutableStateOf<GuruEntity?>(null) }

  val filteredGuru = guruList.filter {
    it.namaLengkap.contains(searchQuery, ignoreCase = true) ||
    it.nip.contains(searchQuery, ignoreCase = true)
  }

  Scaffold(
    floatingActionButton = {
      FloatingActionButton(
        onClick = { showAddDialog = true },
        containerColor = NavyPrimary,
        contentColor = Color.White,
        modifier = Modifier.testTag("tambah_guru_fab")
      ) {
        Icon(Icons.Default.Add, contentDescription = "Tambah Guru")
      }
    }
  ) { padding ->
    Column(
      modifier = Modifier
        .fillMaxSize()
        .background(SlateBackground)
        .padding(padding)
        .padding(16.dp)
    ) {
      // Search Bar
      OutlinedTextField(
        value = searchQuery,
        onValueChange = { searchQuery = it },
        placeholder = { Text("Cari berdasarkan Nama atau NIP...") },
        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = NavyPrimary) },
        trailingIcon = {
          if (searchQuery.isNotEmpty()) {
            IconButton(onClick = { searchQuery = "" }) {
              Icon(Icons.Default.Clear, contentDescription = "Hapus")
            }
          }
        },
        singleLine = true,
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
          focusedContainerColor = Color.White,
          unfocusedContainerColor = Color.White
        ),
        modifier = Modifier
          .fillMaxWidth()
          .testTag("search_guru_input")
      )

      Spacer(modifier = Modifier.height(12.dp))

      SectionHeader(
        title = "Daftar Anggota Guru (${filteredGuru.size} Orang)"
      )

      if (filteredGuru.isEmpty()) {
        Card(
          shape = RoundedCornerShape(12.dp),
          colors = CardDefaults.cardColors(containerColor = Color.White),
          modifier = Modifier.fillMaxWidth()
        ) {
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .padding(32.dp),
            contentAlignment = Alignment.Center
          ) {
            Text("Tidak ada data guru yang cocok", color = TextMuted)
          }
        }
      } else {
        LazyColumn(
          verticalArrangement = Arrangement.spacedBy(10.dp),
          modifier = Modifier.fillMaxSize()
        ) {
          items(filteredGuru) { guru ->
            Card(
              shape = RoundedCornerShape(12.dp),
              colors = CardDefaults.cardColors(containerColor = Color.White),
              elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
              modifier = Modifier
                .fillMaxWidth()
                .clickable { guruDetailToView = guru }
                .testTag("guru_card_${guru.id}")
            ) {
              Row(
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically
              ) {
                // Avatar
                Box(
                  modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(if (guru.jenisKelamin == "Laki-laki") NavyLight.copy(alpha = 0.4f) else PurpleAccentLight),
                  contentAlignment = Alignment.Center
                ) {
                  Icon(
                    if (guru.jenisKelamin == "Laki-laki") Icons.Default.Face else Icons.Default.Face3,
                    contentDescription = null,
                    tint = if (guru.jenisKelamin == "Laki-laki") NavyPrimary else PurpleAccent
                  )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                  Text(
                    guru.namaLengkap,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = NavyDark
                  )
                  Text(
                    "NIP: ${guru.nip}",
                    fontSize = 11.sp,
                    color = TextSecondary
                  )
                  Spacer(modifier = Modifier.height(2.dp))
                  Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                  ) {
                    Text(
                      guru.noHp,
                      fontSize = 11.sp,
                      color = TextMuted
                    )
                    Text("•", fontSize = 11.sp, color = TextMuted)
                    StatusBadge(
                      text = if (guru.statusAktif) "Aktif" else "Nonaktif",
                      isSuccess = guru.statusAktif,
                      isDanger = !guru.statusAktif
                    )
                  }
                }

                // Action Menu
                IconButton(onClick = { editingGuru = guru }) {
                  Icon(Icons.Default.Edit, contentDescription = "Edit", tint = NavyPrimary, modifier = Modifier.size(18.dp))
                }
                IconButton(onClick = { guruToDelete = guru }) {
                  Icon(Icons.Default.Delete, contentDescription = "Hapus", tint = DangerRed, modifier = Modifier.size(18.dp))
                }
              }
            }
          }
        }
      }
    }
  }

  // Dialog Tambah Guru
  if (showAddDialog) {
    GuruFormDialog(
      title = "Tambah Guru Baru",
      guru = null,
      onDismiss = { showAddDialog = false },
      onSubmit = { newGuru, username, pass ->
        viewModel.tambahGuru(newGuru, username, pass)
        showAddDialog = false
      }
    )
  }

  // Dialog Edit Guru
  if (editingGuru != null) {
    GuruFormDialog(
      title = "Edit Data Guru",
      guru = editingGuru,
      onDismiss = { editingGuru = null },
      onSubmit = { updatedGuru, _, _ ->
        viewModel.editGuru(updatedGuru)
        editingGuru = null
      }
    )
  }

  // Dialog Detail Guru
  if (guruDetailToView != null) {
    GuruDetailDialog(
      guru = guruDetailToView!!,
      onDismiss = { guruDetailToView = null }
    )
  }

  // Dialog Konfirmasi Hapus
  if (guruToDelete != null) {
    AlertDialog(
      onDismissRequest = { guruToDelete = null },
      title = { Text("Konfirmasi Hapus Guru") },
      text = { Text("Apakah Anda yakin ingin menghapus data anggota ${guruToDelete!!.namaLengkap}? Seluruh data riwayat iuran dan simpanan terkait akan ikut terhapus.") },
      confirmButton = {
        Button(
          onClick = {
            viewModel.hapusGuru(guruToDelete!!)
            guruToDelete = null
          },
          colors = ButtonDefaults.buttonColors(containerColor = DangerRed)
        ) {
          Text("Ya, Hapus")
        }
      },
      dismissButton = {
        OutlinedButton(onClick = { guruToDelete = null }) {
          Text("Batal")
        }
      }
    )
  }
}

@Composable
fun GuruFormDialog(
  title: String,
  guru: GuruEntity?,
  onDismiss: () -> Unit,
  onSubmit: (GuruEntity, String, String) -> Unit
) {
  var nip by remember { mutableStateOf(guru?.nip ?: "") }
  var namaLengkap by remember { mutableStateOf(guru?.namaLengkap ?: "") }
  var jenisKelamin by remember { mutableStateOf(guru?.jenisKelamin ?: "Laki-laki") }
  var alamat by remember { mutableStateOf(guru?.alamat ?: "") }
  var noHp by remember { mutableStateOf(guru?.noHp ?: "") }
  var tanggalBergabung by remember {
    mutableStateOf(guru?.tanggalBergabung ?: SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date()))
  }
  var statusAktif by remember { mutableStateOf(guru?.statusAktif ?: true) }

  // Account login fields (only required when creating new teacher)
  var username by remember { mutableStateOf("") }
  var password by remember { mutableStateOf("") }
  var errorMsg by remember { mutableStateOf<String?>(null) }

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
        Text(title, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = NavyDark)
        Spacer(modifier = Modifier.height(14.dp))

        OutlinedTextField(
          value = nip,
          onValueChange = { nip = it },
          label = { Text("NIP / No Identitas") },
          singleLine = true,
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(8.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
          value = namaLengkap,
          onValueChange = { namaLengkap = it },
          label = { Text("Nama Lengkap & Gelar") },
          singleLine = true,
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(8.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
          Text("Jenis Kelamin: ", fontSize = 12.sp, color = TextSecondary)
          Row(verticalAlignment = Alignment.CenterVertically) {
            RadioButton(selected = jenisKelamin == "Laki-laki", onClick = { jenisKelamin = "Laki-laki" })
            Text("Laki-laki", fontSize = 12.sp)
          }
          Row(verticalAlignment = Alignment.CenterVertically) {
            RadioButton(selected = jenisKelamin == "Perempuan", onClick = { jenisKelamin = "Perempuan" })
            Text("Perempuan", fontSize = 12.sp)
          }
        }

        OutlinedTextField(
          value = noHp,
          onValueChange = { noHp = it },
          label = { Text("Nomor HP (WhatsApp)") },
          singleLine = true,
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(8.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
          value = alamat,
          onValueChange = { alamat = it },
          label = { Text("Alamat") },
          singleLine = true,
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(8.dp)
        )

        if (guru == null) {
          Spacer(modifier = Modifier.height(12.dp))
          Text("Akun Pengguna untuk Masuk:", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = NavyPrimary)
          Spacer(modifier = Modifier.height(4.dp))

          OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("Username Login") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp)
          )
          Spacer(modifier = Modifier.height(8.dp))

          OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password Akun") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp)
          )
        }

        if (errorMsg != null) {
          Spacer(modifier = Modifier.height(8.dp))
          Text(errorMsg!!, color = DangerRed, fontSize = 11.sp)
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.End
        ) {
          TextButton(onClick = onDismiss) { Text("Batal") }
          Spacer(modifier = Modifier.width(8.dp))
          Button(
            onClick = {
              if (namaLengkap.isBlank() || nip.isBlank()) {
                errorMsg = "NIP dan Nama Lengkap wajib diisi!"
                return@Button
              }
              if (guru == null && (username.isBlank() || password.isBlank())) {
                errorMsg = "Username dan password akun guru wajib diisi!"
                return@Button
              }
              val resultGuru = (guru ?: GuruEntity(
                nip = nip,
                namaLengkap = namaLengkap,
                jenisKelamin = jenisKelamin,
                alamat = alamat,
                noHp = noHp,
                tanggalBergabung = tanggalBergabung,
                statusAktif = statusAktif
              )).copy(
                nip = nip,
                namaLengkap = namaLengkap,
                jenisKelamin = jenisKelamin,
                alamat = alamat,
                noHp = noHp,
                statusAktif = statusAktif
              )
              onSubmit(resultGuru, username, password)
            },
            colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary),
            shape = RoundedCornerShape(8.dp)
          ) {
            Text("Simpan")
          }
        }
      }
    }
  }
}

@Composable
fun GuruDetailDialog(
  guru: GuruEntity,
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
      Column(modifier = Modifier.padding(20.dp)) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Box(
            modifier = Modifier
              .size(48.dp)
              .clip(CircleShape)
              .background(NavyPrimary),
            contentAlignment = Alignment.Center
          ) {
            Icon(Icons.Default.Person, contentDescription = null, tint = Color.White)
          }
          Spacer(modifier = Modifier.width(12.dp))
          Column {
            Text(guru.namaLengkap, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = NavyDark)
            Text("NIP: ${guru.nip}", fontSize = 12.sp, color = TextSecondary)
          }
        }

        HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = BorderLight)

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
          DetailItem("Jenis Kelamin", guru.jenisKelamin)
          DetailItem("Nomor HP", guru.noHp)
          DetailItem("Alamat Lengkap", guru.alamat)
          DetailItem("Tanggal Bergabung", guru.tanggalBergabung)
          DetailItem("Status Keanggotaan", if (guru.statusAktif) "Aktif" else "Nonaktif")
          DetailItem("Iuran Wajib Bulanan", "Rp 30.000 / bulan")
          DetailItem("Bunga Simpanan Berhak", "2% per bulan")
        }

        Spacer(modifier = Modifier.height(20.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
          Button(
            onClick = onDismiss,
            colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary),
            shape = RoundedCornerShape(8.dp)
          ) {
            Text("Tutup")
          }
        }
      }
    }
  }
}

@Composable
fun DetailItem(label: String, value: String) {
  Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
    Text(label, fontSize = 12.sp, color = TextMuted)
    Text(value, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
  }
}
