package com.example.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(tableName = "supplier")
data class SupplierEntity(
  @PrimaryKey(autoGenerate = true)
  val id: Long = 0,
  val namaSupplier: String,
  val kontakPerson: String,
  val noHp: String,
  val alamat: String
)

@Entity(tableName = "kategori_produk")
data class KategoriProdukEntity(
  @PrimaryKey(autoGenerate = true)
  val id: Long = 0,
  val namaKategori: String
)

@Entity(
  tableName = "produk",
  foreignKeys = [
    ForeignKey(
      entity = KategoriProdukEntity::class,
      parentColumns = ["id"],
      childColumns = ["kategoriId"],
      onDelete = ForeignKey.SET_NULL
    ),
    ForeignKey(
      entity = SupplierEntity::class,
      parentColumns = ["id"],
      childColumns = ["supplierId"],
      onDelete = ForeignKey.SET_NULL
    )
  ],
  indices = [
    Index(value = ["kodeBarang"], unique = true),
    Index(value = ["kategoriId"]),
    Index(value = ["supplierId"])
  ]
)
data class ProdukEntity(
  @PrimaryKey(autoGenerate = true)
  val id: Long = 0,
  val kodeBarang: String,
  val namaBarang: String,
  val kategoriId: Long?,
  val fotoUrl: String? = null,
  val hargaBeli: Double,
  val hargaJual: Double,
  val satuan: String = "Pcs", // "Pcs", "Set", "Buku", "Lembar"
  val supplierId: Long?,
  val stokTotal: Int = 0
)

@Entity(
  tableName = "variasi_produk",
  foreignKeys = [
    ForeignKey(
      entity = ProdukEntity::class,
      parentColumns = ["id"],
      childColumns = ["produkId"],
      onDelete = ForeignKey.CASCADE
    )
  ],
  indices = [
    Index(value = ["produkId"]),
    Index(value = ["produkId", "ukuran"], unique = true)
  ]
)
data class VariasiProdukEntity(
  @PrimaryKey(autoGenerate = true)
  val id: Long = 0,
  val produkId: Long,
  val ukuran: String, // "XS", "S", "M", "L", "XL", "XXL", or "STANDAR"
  val stok: Int = 0
)

@Entity(
  tableName = "pembelian",
  foreignKeys = [
    ForeignKey(
      entity = SupplierEntity::class,
      parentColumns = ["id"],
      childColumns = ["supplierId"],
      onDelete = ForeignKey.SET_NULL
    )
  ],
  indices = [
    Index(value = ["noPembelian"], unique = true),
    Index(value = ["supplierId"])
  ]
)
data class PembelianEntity(
  @PrimaryKey(autoGenerate = true)
  val id: Long = 0,
  val noPembelian: String,
  val supplierId: Long?,
  val tanggalPembelian: String,
  val totalPembelian: Double,
  val keterangan: String = ""
)

@Entity(
  tableName = "detail_pembelian",
  foreignKeys = [
    ForeignKey(
      entity = PembelianEntity::class,
      parentColumns = ["id"],
      childColumns = ["pembelianId"],
      onDelete = ForeignKey.CASCADE
    ),
    ForeignKey(
      entity = ProdukEntity::class,
      parentColumns = ["id"],
      childColumns = ["produkId"],
      onDelete = ForeignKey.CASCADE
    )
  ],
  indices = [
    Index(value = ["pembelianId"]),
    Index(value = ["produkId"])
  ]
)
data class DetailPembelianEntity(
  @PrimaryKey(autoGenerate = true)
  val id: Long = 0,
  val pembelianId: Long,
  val produkId: Long,
  val ukuran: String = "STANDAR",
  val jumlah: Int,
  val hargaBeli: Double,
  val subtotal: Double
)

@Entity(
  tableName = "transaksi_penjualan",
  foreignKeys = [
    ForeignKey(
      entity = GuruEntity::class,
      parentColumns = ["id"],
      childColumns = ["guruId"],
      onDelete = ForeignKey.SET_NULL
    )
  ],
  indices = [
    Index(value = ["noPenjualan"], unique = true),
    Index(value = ["guruId"])
  ]
)
data class TransaksiPenjualanEntity(
  @PrimaryKey(autoGenerate = true)
  val id: Long = 0,
  val noPenjualan: String,
  val pembeliNama: String,
  val guruId: Long? = null,
  val tanggalPenjualan: String,
  val totalBayar: Double,
  val jumlahUang: Double,
  val kembalian: Double,
  val metodeBayar: String = "TUNAI", // "TUNAI", "TRANSFER", "QRIS"
  val kasirUsername: String = "admin"
)

@Entity(
  tableName = "detail_penjualan",
  foreignKeys = [
    ForeignKey(
      entity = TransaksiPenjualanEntity::class,
      parentColumns = ["id"],
      childColumns = ["penjualanId"],
      onDelete = ForeignKey.CASCADE
    ),
    ForeignKey(
      entity = ProdukEntity::class,
      parentColumns = ["id"],
      childColumns = ["produkId"],
      onDelete = ForeignKey.CASCADE
    )
  ],
  indices = [
    Index(value = ["penjualanId"]),
    Index(value = ["produkId"])
  ]
)
data class DetailPenjualanEntity(
  @PrimaryKey(autoGenerate = true)
  val id: Long = 0,
  val penjualanId: Long,
  val produkId: Long,
  val namaBarang: String,
  val ukuran: String = "STANDAR",
  val jumlah: Int,
  val hargaSatuan: Double,
  val subtotal: Double
)

@Entity(
  tableName = "riwayat_stok",
  foreignKeys = [
    ForeignKey(
      entity = ProdukEntity::class,
      parentColumns = ["id"],
      childColumns = ["produkId"],
      onDelete = ForeignKey.CASCADE
    )
  ],
  indices = [Index(value = ["produkId"])]
)
data class RiwayatStokEntity(
  @PrimaryKey(autoGenerate = true)
  val id: Long = 0,
  val produkId: Long,
  val variasiUkuran: String = "STANDAR",
  val jenis: String, // "MASUK" or "KELUAR"
  val jumlah: Int,
  val stokSisa: Int,
  val keterangan: String,
  val tanggal: String
)
