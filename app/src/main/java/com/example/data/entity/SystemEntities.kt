package com.example.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "pengaturan")
data class PengaturanEntity(
  @PrimaryKey
  val id: Int = 1,
  val namaKoperasi: String = "Koperasi Sekolah SMP Nurul Ansor",
  val namaSekolah: String = "SMP Nurul Ansor",
  val alamatSekolah: String = "Jl. Pendidikan No. 45, Kecamatan Sukamaju, Jawa Timur",
  val logoSekolah: String = "",
  val nominalIuranWajib: Double = 30000.0,
  val persenBungaSimpanan: Double = 2.0, // 2% per bulan
  val persenBungaPinjaman: Double = 2.0, // 2% per bulan flat
  val masaToleransiHari: Int = 7, // 7 hari setelah jatuh tempo
  val nominalDenda: Double = 10000.0 // Rp 10.000 per angsuran terlambat
)

@Entity(tableName = "log_aktivitas")
data class LogAktivitasEntity(
  @PrimaryKey(autoGenerate = true)
  val id: Long = 0,
  val username: String,
  val peran: String,
  val aktivitas: String,
  val tanggalAktivitas: String,
  val timestamp: Long = System.currentTimeMillis()
)
