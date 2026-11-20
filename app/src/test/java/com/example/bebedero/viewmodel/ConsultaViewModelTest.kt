package com.example.bebedero.viewmodel

import com.example.bebedero.model.EstadoTemperatura
import com.example.bebedero.model.Galpon
import com.example.bebedero.model.Granja
import com.example.bebedero.model.LineaBebedero
import com.example.bebedero.model.MedicionTemperatura
import com.example.bebedero.repository.BebederoRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class ConsultaViewModelTest {

    private lateinit var repo: BebederoRepository
    private lateinit var viewModel: ConsultaViewModel

    @Before
    fun setUp() {
        repo = mockk()
        viewModel = ConsultaViewModel(repo)
    }

    @Test
    fun `granjas devuelve la lista del repository`() = runTest {
        val lista = listOf(Granja(1, "Granja 1"), Granja(2, "Granja 2"))
        coEvery { repo.obtenerGranjas() } returns lista

        assertEquals(lista, viewModel.granjas())
    }

    @Test
    fun `granja devuelve la granja del repository`() = runTest {
        val granja = Granja(1, "Granja 1")
        coEvery { repo.obtenerGranja(1) } returns granja

        assertEquals(granja, viewModel.granja(1))
    }

    @Test
    fun `galpones devuelve los galpones de una granja`() = runTest {
        val galpones = listOf(Galpon(1, 1, "Galpon 1"), Galpon(2, 1, "Galpon 2"))
        coEvery { repo.obtenerGalpones(1) } returns galpones

        assertEquals(galpones, viewModel.galpones(1))
    }

    @Test
    fun `galpon devuelve el galpon del repository`() = runTest {
        val galpon = Galpon(1, 1, "Galpon 1")
        coEvery { repo.obtenerGalpon(1) } returns galpon

        assertEquals(galpon, viewModel.galpon(1))
    }

    @Test
    fun `lineas devuelve las lineas de un galpon`() = runTest {
        val linea = LineaBebedero(1, 1, "Linea 1", 18.0, "29/09/2026 16:10", EstadoTemperatura.NORMAL)
        coEvery { repo.obtenerLineas(1) } returns listOf(linea)

        assertEquals(listOf(linea), viewModel.lineas(1))
    }

    @Test
    fun `linea devuelve la linea del repository`() = runTest {
        val linea = LineaBebedero(1, 1, "Linea 1", 18.0, "29/09/2026 16:10", EstadoTemperatura.NORMAL)
        coEvery { repo.obtenerLinea(1) } returns linea

        assertEquals(linea, viewModel.linea(1))
    }

    @Test
    fun `historial devuelve las mediciones de una linea`() = runTest {
        val mediciones = listOf(MedicionTemperatura(1, 1, 18.5, "29/09/2026 16:10"))
        coEvery { repo.obtenerHistorial(1) } returns mediciones

        assertEquals(mediciones, viewModel.historial(1))
    }

    @Test
    fun `todasLasLineas devuelve la lista del repository`() = runTest {
        val lineas = listOf(
            LineaBebedero(1, 1, "Linea 1", 18.0, "29/09/2026 16:10", EstadoTemperatura.NORMAL)
        )
        coEvery { repo.obtenerTodasLasLineas() } returns lineas

        assertEquals(lineas, viewModel.todasLasLineas())
    }

    @Test
    fun `todosLosGalpones junta los galpones de todas las granjas`() = runTest {
        val granjas = listOf(Granja(1, "Granja 1"), Granja(2, "Granja 2"))
        val galponesGranja1 = listOf(Galpon(1, 1, "Galpon 1A"), Galpon(2, 1, "Galpon 1B"))
        val galponesGranja2 = listOf(Galpon(3, 2, "Galpon 2A"))

        coEvery { repo.obtenerGranjas() } returns granjas
        coEvery { repo.obtenerGalpones(1) } returns galponesGranja1
        coEvery { repo.obtenerGalpones(2) } returns galponesGranja2

        val resultado = viewModel.todosLosGalpones()

        assertEquals(galponesGranja1 + galponesGranja2, resultado)
    }

    @Test
    fun `eventosCriticos descarta las lineas normales`() = runTest {
        val normal = LineaBebedero(1, 1, "Linea normal", 18.0, "f", EstadoTemperatura.NORMAL)
        val critico = LineaBebedero(2, 1, "Linea critica", 35.0, "f", EstadoTemperatura.CRITICO)
        coEvery { repo.obtenerTodasLasLineas() } returns listOf(normal, critico)

        val resultado = viewModel.eventosCriticos()

        assertEquals(listOf(critico), resultado)
    }

    @Test
    fun `eventosCriticos deja los criticos antes que las advertencias`() = runTest {
        val advertencia = LineaBebedero(1, 1, "Linea advertencia", 25.0, "f", EstadoTemperatura.ADVERTENCIA)
        val critico = LineaBebedero(2, 1, "Linea critica", 35.0, "f", EstadoTemperatura.CRITICO)
        // A proposito en este orden (advertencia primero), para probar que el metodo reordena.
        coEvery { repo.obtenerTodasLasLineas() } returns listOf(advertencia, critico)

        val resultado = viewModel.eventosCriticos()

        assertEquals(listOf(critico, advertencia), resultado)
    }

    @Test
    fun `eventosCriticos ordena por id cuando el estado es el mismo`() = runTest {
        val criticoB = LineaBebedero(5, 1, "Linea critica B", 35.0, "f", EstadoTemperatura.CRITICO)
        val criticoA = LineaBebedero(2, 1, "Linea critica A", 36.0, "f", EstadoTemperatura.CRITICO)
        coEvery { repo.obtenerTodasLasLineas() } returns listOf(criticoB, criticoA)

        val resultado = viewModel.eventosCriticos()

        assertEquals(listOf(criticoA, criticoB), resultado)
    }
}
