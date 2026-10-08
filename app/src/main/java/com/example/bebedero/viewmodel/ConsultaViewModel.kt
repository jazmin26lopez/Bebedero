package com.example.bebedero.viewmodel

import androidx.lifecycle.ViewModel
import com.example.bebedero.model.Galpon
import com.example.bebedero.model.Granja
import com.example.bebedero.model.LineaBebedero
import com.example.bebedero.repository.BebederoRepository
import com.example.bebedero.model.MedicionTemperatura
/**
 * Centraliza el acceso al Repository para el flujo de consulta
 * (Granjas -> Galpones -> Lineas -> Detalle). La UI nunca llama al
 * Repository directamente, siempre pasa por aqui (RNF-07: gestion de estado).
 */
class ConsultaViewModel(
    private val repo: BebederoRepository = BebederoRepository()
) : ViewModel() {

    suspend fun granjas(): List<Granja> = repo.obtenerGranjas()

    suspend fun granja(id: Int): Granja = repo.obtenerGranja(id)

    suspend fun galpones(granjaId: Int): List<Galpon> = repo.obtenerGalpones(granjaId)

    suspend fun galpon(id: Int): Galpon = repo.obtenerGalpon(id)

    suspend fun lineas(galponId: Int): List<LineaBebedero> = repo.obtenerLineas(galponId)

    suspend fun linea(id: Int): LineaBebedero = repo.obtenerLinea(id)


    suspend fun historial(lineaId: Int): List<MedicionTemperatura> =
        repo.obtenerHistorial(lineaId)

}
