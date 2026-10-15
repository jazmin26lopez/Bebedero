
package com.example.bebedero.viewmodel

import androidx.lifecycle.ViewModel
import com.example.bebedero.model.Flushing
import com.example.bebedero.model.Usuario
import com.example.bebedero.repository.BebederoRepository

class FlushingViewModel(
    private val repo: BebederoRepository = BebederoRepository()
) : ViewModel() {

    /**
     * Validación de la observación del flushing.
     * Se conserva la lógica implementada anteriormente.
     */
    fun observacionValida(observacion: String): Boolean =
        observacion.isNotBlank()

    // CREAR: Registrar flushing
    suspend fun registrarFlushing(
        lineaId: Int,
        usuarioId: Int,
        fechaHora: String,
        observacion: String,
        fotoUri: String? = null
    ): Flushing {
        return repo.registrarFlushing(
            lineaId = lineaId,
            usuarioId = usuarioId,
            fechaHora = fechaHora,
            observacion = observacion,
            fotoUri = fotoUri
        )
    }

    // CONSULTAR: Obtener registros de flushing
    suspend fun obtenerFlushings(): List<Flushing> {
        return repo.obtenerFlushings()
    }

    // EDITAR: Actualizar únicamente la observación
    suspend fun actualizarObservacionFlushing(
        flushingId: Int,
        observacion: String
    ): Boolean {

        if (!observacionValida(observacion)) {
            return false
        }

        return repo.actualizarObservacionFlushing(
            flushingId = flushingId,
            observacion = observacion.trim()
        )
    }

    // ELIMINAR: Borrar un registro de flushing
    suspend fun eliminarFlushing(
        flushingId: Int
    ): Boolean {
        return repo.eliminarFlushing(flushingId)
    }

    // CONSULTAR: Datos del operario
    suspend fun obtenerUsuario(id: Int): Usuario {
        return repo.obtenerUsuario(id)
    }
}
