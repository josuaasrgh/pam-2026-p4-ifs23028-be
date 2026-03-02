package org.delcom.dao

import org.delcom.tables.ToyotaTable
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.Entity
import org.jetbrains.exposed.dao.EntityClass
import java.util.UUID

class ToyotaDAO(id: EntityID<UUID>) : Entity<UUID>(id) {
    companion object : EntityClass<UUID, ToyotaDAO>(ToyotaTable)

    var namaMobil by ToyotaTable.namaMobil
    var tipeMobil by ToyotaTable.tipeMobil
    var deskripsi by ToyotaTable.deskripsi
    var harga by ToyotaTable.harga
    var pathGambar by ToyotaTable.pathGambar
    var createdAt by ToyotaTable.createdAt
    var updatedAt by ToyotaTable.updatedAt
}