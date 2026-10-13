
package com.example.bebedero.viewmodel

import androidx.lifecycle.ViewModel
import com.example.bebedero.model.Flushing
import com.example.bebedero.model.Usuario
import com.example.bebedero.repository.BebederoRepository

class FlushingViewModel(
    private val repo: BebederoRepository = BebederoRepository()
) : ViewModel() {

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

    suspend fun obtenerFlushings(): List<Flushing> {
        return repo.obtenerFlushings()
    }

    suspend fun obtenerUsuario(id: Int): Usuario {
        return repo.obtenerUsuario(id)
    }
}
