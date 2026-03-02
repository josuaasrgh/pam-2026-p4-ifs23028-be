package org.delcom.services

import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.util.cio.*
import io.ktor.utils.io.*
import org.delcom.data.AppException
import org.delcom.data.DataResponse
import org.delcom.data.ToyotaRequest
import org.delcom.helpers.ValidatorHelper
import org.delcom.repositories.IToyotaRepository
import java.io.File
import java.util.*

class ToyotaService(private val toyotaRepository: IToyotaRepository) {

    // Mengambil semua data Toyota
    suspend fun getAllToyotas(call: ApplicationCall) {
        val search = call.request.queryParameters["search"] ?: ""
        val toyotas = toyotaRepository.getToyotas(search)

        val response = DataResponse(
            "success",
            "Berhasil mengambil daftar mobil Toyota",
            mapOf(Pair("toyotas", toyotas))
        )
        call.respond(response)
    }

    // Mengambil data Toyota berdasarkan id
    suspend fun getToyotaById(call: ApplicationCall) {
        val id = call.parameters["id"]
            ?: throw AppException(400, "ID mobil tidak boleh kosong!")

        val toyota = toyotaRepository.getToyotaById(id)
            ?: throw AppException(404, "Data mobil tidak tersedia!")

        val response = DataResponse(
            "success",
            "Berhasil mengambil data mobil Toyota",
            mapOf(Pair("toyota", toyota))
        )
        call.respond(response)
    }

    // Ambil data request multipart
    private suspend fun getToyotaRequest(call: ApplicationCall): ToyotaRequest {
        val toyotaReq = ToyotaRequest()

        val multipartData = call.receiveMultipart(formFieldLimit = 1024 * 1024 * 5)
        multipartData.forEachPart { part ->
            when (part) {
                is PartData.FormItem -> {
                    when (part.name) {
                        "namaMobil" -> toyotaReq.namaMobil = part.value.trim()
                        "tipeMobil" -> toyotaReq.tipeMobil = part.value.trim()
                        "deskripsi" -> toyotaReq.deskripsi = part.value
                        "harga" -> toyotaReq.harga = part.value.toLongOrNull() ?: 0L
                    }
                }
                is PartData.FileItem -> {
                    val ext = part.originalFileName
                        ?.substringAfterLast('.', "")
                        ?.let { if (it.isNotEmpty()) ".$it" else "" }
                        ?: ""

                    val fileName = UUID.randomUUID().toString() + ext
                    val filePath = "uploads/toyotas/$fileName"

                    val file = File(filePath)
                    file.parentFile.mkdirs()

                    part.provider().copyAndClose(file.writeChannel())
                    toyotaReq.pathGambar = filePath
                }
                else -> {}
            }
            part.dispose()
        }

        return toyotaReq
    }

    // Validasi request
    private fun validateToyotaRequest(toyotaReq: ToyotaRequest) {
        val validatorHelper = ValidatorHelper(toyotaReq.toMap())
        validatorHelper.required("namaMobil", "Nama mobil tidak boleh kosong")
        validatorHelper.required("tipeMobil", "Tipe mobil tidak boleh kosong")
        validatorHelper.required("deskripsi", "Deskripsi tidak boleh kosong")
        validatorHelper.required("harga", "Harga tidak boleh kosong")
        validatorHelper.required("pathGambar", "Gambar tidak boleh kosong")
        validatorHelper.validate()

        val file = File(toyotaReq.pathGambar)
        if (!file.exists()) {
            throw AppException(400, "Gambar mobil gagal diupload!")
        }
    }

    // Menambahkan data Toyota
    suspend fun createToyota(call: ApplicationCall) {
        val toyotaReq = getToyotaRequest(call)

        validateToyotaRequest(toyotaReq)

        val existToyota = toyotaRepository.getToyotaByName(toyotaReq.namaMobil)
        if (existToyota != null) {
            val tmpFile = File(toyotaReq.pathGambar)
            if (tmpFile.exists()) tmpFile.delete()
            throw AppException(409, "Mobil Toyota dengan nama ini sudah terdaftar!")
        }

        val toyotaId = toyotaRepository.addToyota(toyotaReq.toEntity())

        val response = DataResponse(
            "success",
            "Berhasil menambahkan data mobil Toyota",
            mapOf(Pair("toyotaId", toyotaId))
        )
        call.respond(response)
    }

    // Mengubah data Toyota
    suspend fun updateToyota(call: ApplicationCall) {
        val id = call.parameters["id"]
            ?: throw AppException(400, "ID mobil tidak boleh kosong!")

        val oldToyota = toyotaRepository.getToyotaById(id)
            ?: throw AppException(404, "Data mobil tidak tersedia!")

        val toyotaReq = getToyotaRequest(call)

        if (toyotaReq.pathGambar.isEmpty()) {
            toyotaReq.pathGambar = oldToyota.pathGambar
        }

        validateToyotaRequest(toyotaReq)

        if (toyotaReq.namaMobil != oldToyota.namaMobil) {
            val existToyota = toyotaRepository.getToyotaByName(toyotaReq.namaMobil)
            if (existToyota != null) {
                val tmpFile = File(toyotaReq.pathGambar)
                if (tmpFile.exists()) tmpFile.delete()
                throw AppException(409, "Mobil Toyota dengan nama ini sudah terdaftar!")
            }
        }

        if (toyotaReq.pathGambar != oldToyota.pathGambar) {
            val oldFile = File(oldToyota.pathGambar)
            if (oldFile.exists()) oldFile.delete()
        }

        val isUpdated = toyotaRepository.updateToyota(id, toyotaReq.toEntity())
        if (!isUpdated) {
            throw AppException(400, "Gagal memperbarui data mobil Toyota!")
        }

        val response = DataResponse(
            "success",
            "Berhasil mengubah data mobil Toyota",
            null
        )
        call.respond(response)
    }

    // Menghapus data Toyota
    suspend fun deleteToyota(call: ApplicationCall) {
        val id = call.parameters["id"]
            ?: throw AppException(400, "ID mobil tidak boleh kosong!")

        val oldToyota = toyotaRepository.getToyotaById(id)
            ?: throw AppException(404, "Data mobil tidak tersedia!")

        val oldFile = File(oldToyota.pathGambar)

        val isDeleted = toyotaRepository.removeToyota(id)
        if (!isDeleted) {
            throw AppException(400, "Gagal menghapus data mobil Toyota!")
        }

        if (oldFile.exists()) oldFile.delete()

        val response = DataResponse(
            "success",
            "Berhasil menghapus data mobil Toyota",
            null
        )
        call.respond(response)
    }

    // Mengambil gambar Toyota
    suspend fun getToyotaImage(call: ApplicationCall) {
        val id = call.parameters["id"]
            ?: return call.respond(HttpStatusCode.BadRequest)

        val toyota = toyotaRepository.getToyotaById(id)
            ?: return call.respond(HttpStatusCode.NotFound)

        val file = File(toyota.pathGambar)

        if (!file.exists()) {
            return call.respond(HttpStatusCode.NotFound)
        }

        call.respondFile(file)
    }
}