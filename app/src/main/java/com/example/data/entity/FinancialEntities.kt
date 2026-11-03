package com.example.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
  tableName = "simpanan",
  foreignKeys = [
    ForeignKey(
      entity = GuruEntity::class,
      parentColumns = ["id"],
      childColumns = ["guruId"],
      onDelete = ForeignKey.CASCADE
    )
  ],
  indices = [Index(value = ["guruId"], unique = true)]
)
data class SimpananEntity(
  @PrimaryKey(autoGenerate = true)
  val id: Long = 0,
  val guruId: Long,
  val saldo: Double = 0.0,
  val updatedAt: Long = System.currentTimeMillis()
)

@Entity(
  tableName = "transaksi_simpanan",
  foreignKeys = [
    ForeignKey(
      entity = GuruEntity::class,
      parentColumns = ["id"],
      childColumns = ["guruId"],
      onDelete = ForeignKey.CASCADE
    )
  ],
  indices = [Index(value = ["guruId"])]
)
data class TransaksiSimpananEntity(
  @PrimaryKey(autoGenerate = true)
  val id: Long = 0,
  val guruId: Long,
  val jenis: String, // "SETORAN", "PENARIKAN", "BUNGA"
  val nominal: Double,
  val saldoSetelah: Double,
  val tanggal: String,
  val keterangan: String,
  val dasarPerhitunganBunga: String? = null // Penjelasan rincian bunga jika jenis == BUNGA
)

@Entity(
  tableName = "pinjaman",
  foreignKeys = [
    ForeignKey(
      entity = GuruEntity::class,
      parentColumns = ["id"],
      childColumns = ["guruId"],
      onDelete = ForeignKey.CASCADE
    )
  ],
  indices = [
    Index(value = ["guruId"]),
    Index(value = ["noPinjaman"], unique = true)
  ]
)
data class PinjamanEntity(
  @PrimaryKey(autoGenerate = true)
  val id: Long = 0,
  val noPinjaman: String,
  val guruId: Long,
  val tanggalPengajuan: String,
  val jumlahPinjaman: Double,
  val bungaPersen: Double = 2.0, // 2% per bulan flat
  val lamaAngsuranBulan: Int, // e.g. 6, 10, 12 bulan
  val totalBunga: Double,
  val totalPinjaman: Double,
  val angsuranPerBulan: Double,
  val tanggalMulaiAngsuran: String,
  val status: String = "MENUNGGU", // "MENUNGGU", "DISETUJUI", "DITOLAK", "LUNAS"
  val catatanAdmin: String = ""
)

@Entity(
  tableName = "angsuran",
  foreignKeys = [
    ForeignKey(
      entity = PinjamanEntity::class,
      parentColumns = ["id"],
      childColumns = ["pinjamanId"],
      onDelete = ForeignKey.CASCADE
    ),
    ForeignKey(
      entity = GuruEntity::class,
      parentColumns = ["id"],
      childColumns = ["guruId"],
      onDelete = ForeignKey.CASCADE
    )
  ],
  indices = [
    Index(value = ["pinjamanId"]),
    Index(value = ["guruId"])
  ]
)
data class AngsuranEntity(
  @PrimaryKey(autoGenerate = true)
  val id: Long = 0,
  val pinjamanId: Long,
  val guruId: Long,
  val angsuranKe: Int,
  val nominalAngsuran: Double,
  val tanggalJatuhTempo: String,
  val status: String = "BELUM_BAYAR", // "BELUM_BAYAR", "LUNAS", "TERLAMBAT"
  val tanggalBayar: String? = null,
  val sisaPinjaman: Double = 0.0,
  val noKwitansi: String? = null
)

@Entity(
  tableName = "denda",
  foreignKeys = [
    ForeignKey(
      entity = AngsuranEntity::class,
      parentColumns = ["id"],
      childColumns = ["angsuranId"],
      onDelete = ForeignKey.CASCADE
    ),
    ForeignKey(
      entity = PinjamanEntity::class,
      parentColumns = ["id"],
      childColumns = ["pinjamanId"],
      onDelete = ForeignKey.CASCADE
    ),
    ForeignKey(
      entity = GuruEntity::class,
      parentColumns = ["id"],
      childColumns = ["guruId"],
      onDelete = ForeignKey.CASCADE
    )
  ],
  indices = [
    Index(value = ["angsuranId"], unique = true), // Mencegah denda ganda untuk angsuran yang sama!
    Index(value = ["pinjamanId"]),
    Index(value = ["guruId"])
  ]
)
data class DendaEntity(
  @PrimaryKey(autoGenerate = true)
  val id: Long = 0,
  val angsuranId: Long,
  val pinjamanId: Long,
  val guruId: Long,
  val nominalDenda: Double = 10000.0,
  val statusDenda: String = "BELUM_BAYAR", // "BELUM_BAYAR", "LUNAS"
  val tanggalDikenakan: String,
  val tanggalBayar: String? = null,
  val keterangan: String = "Denda keterlambatan angsuran melewati masa toleransi 7 hari"
)
