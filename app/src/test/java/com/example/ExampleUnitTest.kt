package com.example

import org.junit.Assert.*
import org.junit.Test

class ExampleUnitTest {
  @Test
  fun testIuranWajibCalculation() {
    val nominalBulanan = 30000.0
    val total3Bulan = nominalBulanan * 3
    assertEquals(90000.0, total3Bulan, 0.001)
  }

  @Test
  fun testBungaSimpananDuaPersen() {
    val saldoSimpanan = 1750000.0
    val bungaPersen = 0.02 // 2% per bulan
    val expectedBunga = saldoSimpanan * bungaPersen
    assertEquals(35000.0, expectedBunga, 0.001)
  }

  @Test
  fun testPinjamanBungaFlatDuaPersen() {
    val pokokPinjaman = 3000000.0
    val tenorBulan = 6
    val bungaPerBulan = 0.02
    val totalBunga = pokokPinjaman * bungaPerBulan * tenorBulan
    val totalKewajiban = pokokPinjaman + totalBunga
    val cicilanPerBulan = totalKewajiban / tenorBulan

    assertEquals(360000.0, totalBunga, 0.001)
    assertEquals(3360000.0, totalKewajiban, 0.001)
    assertEquals(560000.0, cicilanPerBulan, 0.001)
  }

  @Test
  fun testDendaKeterlambatanToleransiTujuhHari() {
    val nominalDenda = 10000.0
    val masaToleransiHari = 7
    assertTrue("Masa toleransi harus 7 hari", masaToleransiHari == 7)
    assertEquals(10000.0, nominalDenda, 0.001)
  }
}
