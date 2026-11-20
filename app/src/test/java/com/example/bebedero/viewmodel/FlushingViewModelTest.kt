package com.example.bebedero.viewmodel

import com.example.bebedero.model.Flushing
import com.example.bebedero.model.RolUsuario
import com.example.bebedero.model.Usuario
import com.example.bebedero.repository.BebederoRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class FlushingViewModelTest {

    private lateinit var repo: BebederoRepository
    private lateinit var viewModel: FlushingViewModel

    @Before
    fun setUp() {
        repo = mockk()
        viewModel = FlushingViewModel(repo)
    }

    // ---- observacionValida ----

    @Test
    fun `observacionValida es false cuando la observacion esta vacia`() {
        assertFalse(viewModel.observacionValida(""))
    }

    @Test
    fun `observacionValida es false cuando la observacion es solo espacios`() {
        assertFalse(viewModel.observacionValida("   "))
    }

    @Test
    fun `observacionValida es true cuando hay texto real`() {
        assertTrue(viewModel.observacionValida("Limpieza realizada correctamente"))
    }

    // ---- registrarFlushing ----

    @Test
    fun `registrarFlushing delega en el repository y devuelve lo que este entrega`() = runTest {
        val esperado = Flushing(
            id = 1,
            lineaId = 6,
            usuarioId = 1,
            fechaHora = "29/09/2026 16:10",
            observacion = "Limpieza realizada correctamente"
        )
        coEvery {
            repo.registrarFlushing(6, 1, "29/09/2026 16:10", "Limpieza realizada correctamente", null)
        } returns esperado

        val resultado = viewModel.registrarFlushing(
            lineaId = 6,
            usuarioId = 1,
            fechaHora = "29/09/2026 16:10",
            observacion = "Limpieza realizada correctamente"
        )

        assertEquals(esperado, resultado)
        coVerify(exactly = 1) {
            repo.registrarFlushing(6, 1, "29/09/2026 16:10", "Limpieza realizada correctamente", null)
        }
    }

    // ---- obtenerFlushings ----

    @Test
    fun `obtenerFlushings devuelve la lista del repository`() = runTest {
        val lista = listOf(
            Flushing(1, 6, 1, "29/09/2026 16:10", "Observacion A"),
            Flushing(2, 12, 1, "29/09/2026 13:40", "Observacion B")
        )
        coEvery { repo.obtenerFlushings() } returns lista

        val resultado = viewModel.obtenerFlushings()

        assertEquals(lista, resultado)
    }

    // ---- actualizarObservacionFlushing ----

    @Test
    fun `actualizarObservacionFlushing no llama al repository si la observacion es invalida`() = runTest {
        val resultado = viewModel.actualizarObservacionFlushing(flushingId = 1, observacion = "   ")

        assertFalse(resultado)
        coVerify(exactly = 0) { repo.actualizarObservacionFlushing(any(), any()) }
    }

    @Test
    fun `actualizarObservacionFlushing recorta espacios y delega en el repository`() = runTest {
        coEvery {
            repo.actualizarObservacionFlushing(1, "Observacion corregida")
        } returns true

        val resultado = viewModel.actualizarObservacionFlushing(
            flushingId = 1,
            observacion = "  Observacion corregida  "
        )

        assertTrue(resultado)
        coVerify(exactly = 1) { repo.actualizarObservacionFlushing(1, "Observacion corregida") }
    }

    // ---- eliminarFlushing ----

    @Test
    fun `eliminarFlushing delega en el repository`() = runTest {
        coEvery { repo.eliminarFlushing(5) } returns true

        val resultado = viewModel.eliminarFlushing(5)

        assertTrue(resultado)
        coVerify(exactly = 1) { repo.eliminarFlushing(5) }
    }

    // ---- obtenerUsuario ----

    @Test
    fun `obtenerUsuario devuelve el usuario del repository`() = runTest {
        val usuario = Usuario(id = 1, nombre = "Operario Demo", rol = RolUsuario.OPERARIO)
        coEvery { repo.obtenerUsuario(1) } returns usuario

        val resultado = viewModel.obtenerUsuario(1)

        assertEquals(usuario, resultado)
    }
}
