package org.delcom.repositories

import org.delcom.dao.ToyotaDAO
import org.delcom.entities.Toyota
import org.delcom.helpers.suspendTransaction
import org.delcom.tables.ToyotaTable
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.lowerCase
import java.util.UUID

class ToyotaRepository : IToyotaRepository {

    private fun daoToModel(dao: ToyotaDAO) = Toyota(
        id = dao.id.value.toString(),
        namaMobil = dao.namaMobil,
        tipeMobil = dao.tipeMobil,
        deskripsi = dao.deskripsi,
        harga = dao.harga,
        pathGambar = dao.pathGambar,
        createdAt = dao.createdAt,
        updatedAt = dao.updatedAt,
    )

    override suspend fun getToyotas(search: String): List<Toyota> = suspendTransaction {
        if (search.isBlank()) {
            ToyotaDAO.all()
                .orderBy(ToyotaTable.createdAt to SortOrder.DESC)
                .limit(20)
                .map(::daoToModel)
        } else {
            val keyword = "%${search.lowercase()}%"
            ToyotaDAO
                .find { ToyotaTable.namaMobil.lowerCase() like keyword }
                .orderBy(ToyotaTable.namaMobil to SortOrder.ASC)
                .limit(20)
                .map(::daoToModel)
        }
    }

    override suspend fun getToyotaById(id: String): Toyota? = suspendTransaction {
        ToyotaDAO
            .find { ToyotaTable.id eq UUID.fromString(id) }
            .limit(1)
            .map(::daoToModel)
            .firstOrNull()
    }

    override suspend fun getToyotaByName(name: String): Toyota? = suspendTransaction {
        ToyotaDAO
            .find { ToyotaTable.namaMobil eq name }
            .limit(1)
            .map(::daoToModel)
            .firstOrNull()
    }

    override suspend fun addToyota(toyota: Toyota): String = suspendTransaction {
        val dao = ToyotaDAO.new {
            namaMobil = toyota.namaMobil
            tipeMobil = toyota.tipeMobil
            deskripsi = toyota.deskripsi
            harga = toyota.harga
            pathGambar = toyota.pathGambar
            createdAt = toyota.createdAt
            updatedAt = toyota.updatedAt
        }
        dao.id.value.toString()
    }

    override suspend fun updateToyota(id: String, newToyota: Toyota): Boolean = suspendTransaction {
        val dao = ToyotaDAO
            .find { ToyotaTable.id eq UUID.fromString(id) }
            .limit(1)
            .firstOrNull()

        if (dao != null) {
            dao.namaMobil = newToyota.namaMobil
            dao.tipeMobil = newToyota.tipeMobil
            dao.deskripsi = newToyota.deskripsi
            dao.harga = newToyota.harga
            dao.pathGambar = newToyota.pathGambar
            dao.updatedAt = newToyota.updatedAt
            true
        } else {
            false
        }
    }

    override suspend fun removeToyota(id: String): Boolean = suspendTransaction {
        val rowsDeleted = ToyotaTable.deleteWhere {
            ToyotaTable.id eq UUID.fromString(id)
        }
        rowsDeleted == 1
    }
}