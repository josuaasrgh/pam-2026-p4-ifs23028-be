package org.delcom.tables

import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.kotlin.datetime.timestamp

object ToyotaTable : UUIDTable("toyotas") {
    val namaMobil = varchar("nama_mobil", 100)
    val tipeMobil = varchar("tipe_mobil", 100)
    val deskripsi = text("deskripsi")
    val harga = long("harga")
    val pathGambar = varchar("path_gambar", 255)
    val createdAt = timestamp("created_at")
    val updatedAt = timestamp("updated_at")
}