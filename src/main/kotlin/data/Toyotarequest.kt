package org.delcom.data

import kotlinx.serialization.Serializable
import org.delcom.entities.Toyota

@Serializable
data class ToyotaRequest(
    var namaMobil: String = "",
    var tipeMobil: String = "",
    var deskripsi: String = "",
    var harga: Long = 0L,
    var pathGambar: String = "",
) {
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "namaMobil" to namaMobil,
            "tipeMobil" to tipeMobil,
            "deskripsi" to deskripsi,
            "harga" to harga.toString(),
            "pathGambar" to pathGambar
        )
    }

    fun toEntity(): Toyota {
        return Toyota(
            namaMobil = namaMobil,
            tipeMobil = tipeMobil,
            deskripsi = deskripsi,
            harga = harga,
            pathGambar = pathGambar,
        )
    }
}