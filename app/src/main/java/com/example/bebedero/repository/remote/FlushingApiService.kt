
package com.example.bebedero.repository.remote

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

data class FlushingDto(
    val id: Long,
    val lineaId: Long,
    val usuarioId: Long,
    val fechaHora: String,
    val observacion: String?,
    val fotoUri: String?
)

data class CrearFlushingRequest(
    val lineaId: Long,
    val usuarioId: Long,
    val fechaHora: String,
    val observacion: String,
    val fotoUri: String?
)

data class ActualizarFlushingRequest(
    val observacion: String
)

interface FlushingApiService {

    @GET("api/flushings")
    suspend fun listar(
        @Query("lineaId") lineaId: Long? = null
    ): List<FlushingDto>

    @GET("api/flushings/{id}")
    suspend fun obtenerPorId(
        @Path("id") id: Long
    ): FlushingDto

    @POST("api/flushings")
    suspend fun crear(
        @Body request: CrearFlushingRequest
    ): FlushingDto

    @PUT("api/flushings/{id}")
    suspend fun actualizar(
        @Path("id") id: Long,
        @Body request: ActualizarFlushingRequest
    ): FlushingDto

    @DELETE("api/flushings/{id}")
    suspend fun eliminar(
        @Path("id") id: Long
    ): Response<Unit>
}
