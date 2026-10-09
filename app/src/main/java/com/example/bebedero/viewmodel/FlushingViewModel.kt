package com.example.bebedero.viewmodel

import androidx.lifecycle.ViewModel
import com.example.bebedero.model.Flushing
import com.example.bebedero.repository.BebederoRepository
import com.example.bebedero.model.Usuario
class FlushingViewModel(
    private val repo: BebederoRepository = BebederoRepository()
) : ViewModel() {

    suspend fun registrarFlushing(
        lineaId: Int,
        usuarioId: Int,
        fechaHora: String,
        observacion: String
    ): Flushing {
        return repo.registrarFlushing(
            lineaId = lineaId,
            usuarioId = usuarioId,
            fechaHora = fechaHora,
            observacion = observacion
        )
    }

    suspend fun obtenerFlushings(): List<Flushing> {
        return repo.obtenerFlushings()
    }

    suspend fun obtenerUsuario(id: Int): Usuario {
        return repo.obtenerUsuario(id)
    }

}