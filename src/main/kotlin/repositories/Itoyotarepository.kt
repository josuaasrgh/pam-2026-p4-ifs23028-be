package org.delcom.repositories

import org.delcom.entities.Toyota

interface IToyotaRepository {
    suspend fun getToyotas(search: String): List<Toyota>
    suspend fun getToyotaById(id: String): Toyota?
    suspend fun getToyotaByName(name: String): Toyota?
    suspend fun addToyota(toyota: Toyota): String
    suspend fun updateToyota(id: String, newToyota: Toyota): Boolean
    suspend fun removeToyota(id: String): Boolean
}