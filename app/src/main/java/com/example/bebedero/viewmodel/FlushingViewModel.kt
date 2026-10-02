package com.example.bebedero.viewmodel

import androidx.lifecycle.ViewModel
import com.example.bebedero.model.Flushing
import com.example.bebedero.repository.BebederoRepository

class FlushingViewModel(
    private val repo: BebederoRepository = BebederoRepository()
) : ViewModel() {

    suspend fun registrarFlushing(
        lineaId: Int,
        usuarioId: Int,
        fechaHora: String
    ): Flushing {
        return repo.registrarFlushing(
            lineaId = lineaId,
            usuarioId = usuarioId,
            fechaHora = fechaHora
        )
    }

    suspend fun obtenerFlushings(): List<Flushing> {
        return repo.obtenerFlushings()
    }
}