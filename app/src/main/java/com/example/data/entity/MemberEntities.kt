package com.example.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
  tableName = "users",
  indices = [Index(value = ["username"], unique = true)]
)
data class UserEntity(
  @PrimaryKey(autoGenerate = true)
  val id: Long = 0,
  val username: String,
  val passwordHash: String,
  val role: String, // "admin" or "guru"
  val guruId: Long? = null,
  val namaLengkap: String,
  val createdAt: Long = System.currentTimeMillis()
)

@Entity(
  tableName = "guru",
  indices = [Index(value = ["nip"], unique = true)]
)
data class GuruEntity(
  @PrimaryKey(autoGenerate = true)
  val id: Long = 0,
  val nip: String,
  val namaLengkap: String,
  val jenisKelamin: String, // "Laki-laki" / "Perempuan"
  val alamat: String,
  val noHp: String,
  val tanggalBergabung: String,
  val statusAktif: Boolean = true
)

@Entity(
  tableName = "iuran",
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
    Index(value = ["guruId", "bulan", "tahun"], unique = true)
  ]
)
data class IuranEntity(
  @PrimaryKey(autoGenerate = true)
  val id: Long = 0,
  val guruId: Long,
  val bulan: Int, // 1 to 12
  val tahun: Int, // e.g. 2026
  val nominal: Double = 30000.0,
  val statusLunas: Boolean = false,
  val tanggalBayar: String? = null,
  val noKwitansi: String? = null,
  val keterangan: String = ""
)
