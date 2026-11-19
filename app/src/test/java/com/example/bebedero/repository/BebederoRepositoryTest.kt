package com.example.bebedero.repository

import android.content.SharedPreferences
import android.util.Log
import com.example.bebedero.BebederoApp
import com.example.bebedero.NotificacionesBebedero
import com.example.bebedero.model.Alerta
import com.example.bebedero.model.EstadoTemperatura
import com.example.bebedero.model.Flushing
import com.example.bebedero.model.Galpon
import com.example.bebedero.model.Granja
import com.example.bebedero.model.LineaBebedero
import com.example.bebedero.model.Usuario
import com.example.bebedero.model.RolUsuario
import com.example.bebedero.repository.local.BebederoDao
import com.example.bebedero.repository.local.FlushingEntity
import com.example.bebedero.repository.local.GalponEntity
import com.example.bebedero.repository.local.GranjaEntity
import com.example.bebedero.repository.local.LineaBebederoEntity
import com.example.bebedero.repository.remote.ActualizarFlushingRequest
import com.example.bebedero.repository.remote.ActualizarTemperaturaRequest
import com.example.bebedero.repository.remote.CrearFlushingRequest
import com.example.bebedero.repository.remote.FlushingApiService
import com.example.bebedero.repository.remote.FlushingDto
import com.example.bebedero.repository.remote.GalponDto
import com.example.bebedero.repository.remote.GranjaDto
import com.example.bebedero.repository.remote.InfraestructuraApiService
import com.example.bebedero.repository.remote.LineaBebederoDto
import com.example.bebedero.repository.remote.MedicionTemperaturaDto
import com.example.bebedero.repository.remote.RetrofitCliente
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.mockkStatic
import io.mockk.verify
import io.mockk.unmockkAll
import kotlinx.coroutines.test.runTest
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Before
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Response

class BebederoRepositoryTest {

    private lateinit var api: ApiSimulada
    private lateinit var dao: BebederoDao
    private lateinit var infraApi: InfraestructuraApiService
    private lateinit var flushingApi: FlushingApiService
    private lateinit var repo: BebederoRepository

    @Before
    fun setUp() {
        mockkStatic(Log::class)
        every { Log.e(any(), any(), any()) } returns 0

        mockkObject(RetrofitCliente)
        infraApi = mockk()
        flushingApi = mockk()
        every { RetrofitCliente.infraestructuraApi } returns infraApi
        every { RetrofitCliente.flushingApi } returns flushingApi

        api = mockk()
        dao = mockk()
        repo = BebederoRepository(api = api, dao = dao)
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    // ==========================================
    // GRANJAS
    // ==========================================

    @Test
    fun `obtenerGranjas devuelve lo que entrega Oracle y lo guarda en cache`() = runTest {
        coEvery { infraApi.obtenerGranjas() } returns listOf(
            GranjaDto(1, "Granja 1"),
            GranjaDto(2, "Granja 2")
        )
        coEvery { dao.guardarGranjas(any()) } just Runs

        val resultado = repo.obtenerGranjas()

        assertEquals(listOf(Granja(1, "Granja 1"), Granja(2, "Granja 2")), resultado)
        coVerify(exactly = 1) { dao.guardarGranjas(any()) }
    }

    @Test
    fun `obtenerGranjas cae al cache local si Oracle falla`() = runTest {
        coEvery { infraApi.obtenerGranjas() } throws RuntimeException("sin conexion")
        coEvery { dao.obtenerGranjas() } returns listOf(GranjaEntity(9, "Granja desde cache"))

        val resultado = repo.obtenerGranjas()

        assertEquals(listOf(Granja(9, "Granja desde cache")), resultado)
    }

    @Test
    fun `obtenerGranja encuentra la granja por id`() = runTest {
        coEvery { infraApi.obtenerGranjas() } returns listOf(
            GranjaDto(1, "Granja 1"),
            GranjaDto(2, "Granja 2")
        )
        coEvery { dao.guardarGranjas(any()) } just Runs

        val resultado = repo.obtenerGranja(2)

        assertEquals(Granja(2, "Granja 2"), resultado)
    }

    // ==========================================
    // GALPONES
    // ==========================================

    @Test
    fun `obtenerGalpones filtra solo los de la granja pedida`() = runTest {
        coEvery { infraApi.obtenerGalpones() } returns listOf(
            GalponDto(1, 1, "Galpon 1A"),
            GalponDto(2, 1, "Galpon 1B"),
            GalponDto(3, 2, "Galpon de otra granja")
        )
        coEvery { dao.guardarGalpones(any()) } just Runs

        val resultado = repo.obtenerGalpones(1)

        assertEquals(listOf(Galpon(1, 1, "Galpon 1A"), Galpon(2, 1, "Galpon 1B")), resultado)
    }

    @Test
    fun `obtenerGalpones cae al cache local si Oracle falla`() = runTest {
        coEvery { infraApi.obtenerGalpones() } throws RuntimeException("sin conexion")
        coEvery { dao.obtenerGalpones(1) } returns listOf(GalponEntity(1, 1, "Galpon cache"))

        val resultado = repo.obtenerGalpones(1)

        assertEquals(listOf(Galpon(1, 1, "Galpon cache")), resultado)
    }

    @Test
    fun `obtenerGalpon encuentra el galpon por id`() = runTest {
        coEvery { infraApi.obtenerGalpones() } returns listOf(GalponDto(5, 1, "Galpon 5"))
        coEvery { dao.guardarGalpones(any()) } just Runs

        val resultado = repo.obtenerGalpon(5)

        assertEquals(Galpon(5, 1, "Galpon 5"), resultado)
    }

    @Test
    fun `obtenerGalpon relanza el error si no hay nada en cache tampoco`() = runTest {
        coEvery { infraApi.obtenerGalpones() } throws RuntimeException("sin conexion")
        coEvery { dao.obtenerGalpon(5) } returns null

        esperarExcepcion(RuntimeException::class.java) { repo.obtenerGalpon(5) }
    }

    // ==========================================
    // LINEAS
    // ==========================================

    @Test
    fun `obtenerLineas convierte el estado sin importar mayusculas o minusculas`() = runTest {
        coEvery { infraApi.obtenerLineas() } returns listOf(
            LineaBebederoDto(1, 1, "Linea 1", 18.0, "29/09/2026 16:10", "normal")
        )
        coEvery { dao.guardarLineas(any()) } just Runs

        val resultado = repo.obtenerLineas(1)

        assertEquals(
            listOf(LineaBebedero(1, 1, "Linea 1", 18.0, "29/09/2026 16:10", EstadoTemperatura.NORMAL)),
            resultado
        )
    }

    @Test
    fun `obtenerTodasLasLineas cae al cache local si Oracle falla`() = runTest {
        coEvery { infraApi.obtenerLineas() } throws RuntimeException("sin conexion")
        coEvery { dao.obtenerTodasLasLineas() } returns listOf(
            LineaBebederoEntity(1, 1, "Linea cache", 20.0, "f", "CRITICO")
        )

        val resultado = repo.obtenerTodasLasLineas()

        assertEquals(
            listOf(LineaBebedero(1, 1, "Linea cache", 20.0, "f", EstadoTemperatura.CRITICO)),
            resultado
        )
    }

    @Test
    fun `obtenerLinea usa el cache cuando la linea no aparece en la respuesta de Oracle`() = runTest {
        coEvery { infraApi.obtenerLineas() } returns emptyList()
        coEvery { dao.guardarLineas(any()) } just Runs
        coEvery { dao.obtenerLinea(7) } returns LineaBebederoEntity(7, 1, "Linea 7", 18.0, "f", "NORMAL")

        val resultado = repo.obtenerLinea(7)

        assertEquals(LineaBebedero(7, 1, "Linea 7", 18.0, "f", EstadoTemperatura.NORMAL), resultado)
    }

    // ==========================================
    // ALERTAS
    // ==========================================

    @Test
    fun `obtenerAlertasActivas descarta las lineas normales y numera el resto`() = runTest {
        coEvery { infraApi.obtenerLineas() } returns listOf(
            LineaBebederoDto(1, 1, "Normal", 18.0, "f", "NORMAL"),
            LineaBebederoDto(2, 1, "Critica", 35.0, "f", "CRITICO"),
            LineaBebederoDto(3, 1, "Advertencia", 25.0, "f", "ADVERTENCIA")
        )
        coEvery { dao.guardarLineas(any()) } just Runs

        val resultado = repo.obtenerAlertasActivas()

        assertEquals(
            listOf(
                Alerta(1, 2, "f", EstadoTemperatura.CRITICO, true),
                Alerta(2, 3, "f", EstadoTemperatura.ADVERTENCIA, true)
            ),
            resultado
        )
    }

    // ==========================================
    // HISTORIAL
    // ==========================================

    @Test
    fun `obtenerHistorial mapea el historial de mediciones`() = runTest {
        coEvery { infraApi.obtenerHistorialTemperaturas(6) } returns listOf(
            MedicionTemperaturaDto(1, 6, 18.5, "29/09/2026 16:10")
        )

        val resultado = repo.obtenerHistorial(6)

        assertEquals(1, resultado.size)
        assertEquals(18.5, resultado[0].temperatura, 0.001)
    }

    @Test
    fun `obtenerHistorial no tiene respaldo local, relanza el error`() = runTest {
        coEvery { infraApi.obtenerHistorialTemperaturas(6) } throws RuntimeException("sin conexion")

        esperarExcepcion(RuntimeException::class.java) { repo.obtenerHistorial(6) }
    }

    // ==========================================
    // FLUSHING - GET
    // ==========================================

    @Test
    fun `obtenerFlushings mapea la lista remota`() = runTest {
        coEvery { flushingApi.listar(null) } returns listOf(
            FlushingDto(1L, 6L, 1L, "29/09/2026 16:10", "Observacion", null)
        )

        val resultado = repo.obtenerFlushings()

        assertEquals(listOf(Flushing(1, 6, 1, "29/09/2026 16:10", "Observacion")), resultado)
    }

    @Test
    fun `obtenerFlushings cae al cache local si Oracle falla`() = runTest {
        coEvery { flushingApi.listar(null) } throws RuntimeException("sin conexion")
        coEvery { dao.obtenerFlushings() } returns listOf(
            FlushingEntity(id = 2, lineaId = 6, usuarioId = 1, fechaHora = "f", observacion = "Desde cache")
        )

        val resultado = repo.obtenerFlushings()

        assertEquals(listOf(Flushing(2, 6, 1, "f", "Desde cache")), resultado)
    }

    // ==========================================
    // FLUSHING - POST
    // ==========================================

    @Test
    fun `registrarFlushing devuelve el flushing creado por el microservicio`() = runTest {
        val request = CrearFlushingRequest(6L, 1L, "29/09/2026 16:10", "Limpieza ok", null)
        coEvery { flushingApi.crear(request) } returns FlushingDto(10L, 6L, 1L, "29/09/2026 16:10", "Limpieza ok", null)

        val resultado = repo.registrarFlushing(
            lineaId = 6,
            usuarioId = 1,
            fechaHora = "29/09/2026 16:10",
            observacion = "Limpieza ok"
        )

        assertEquals(Flushing(10, 6, 1, "29/09/2026 16:10", "Limpieza ok"), resultado)
        coVerify(exactly = 1) { flushingApi.crear(request) }
    }

    @Test
    fun `registrarFlushing resetea de verdad la temperatura de la linea en ms-infraestructura`() = runTest {
        val request = CrearFlushingRequest(6L, 1L, "29/09/2026 16:10", "Limpieza ok", null)
        coEvery { flushingApi.crear(request) } returns FlushingDto(10L, 6L, 1L, "29/09/2026 16:10", "Limpieza ok", null)
        coEvery { infraApi.actualizarTemperatura(6, any()) } returns
            LineaBebederoDto(6, 1, "Linea 6", 18.0, "29/09/2026 16:10", "NORMAL")

        repo.registrarFlushing(
            lineaId = 6,
            usuarioId = 1,
            fechaHora = "29/09/2026 16:10",
            observacion = "Limpieza ok"
        )

        coVerify(exactly = 1) {
            infraApi.actualizarTemperatura(6, ActualizarTemperaturaRequest(18.0, "29/09/2026 16:10"))
        }
    }

    @Test
    fun `registrarFlushing no falla si ms-infraestructura no responde al resetear la temperatura`() = runTest {
        val request = CrearFlushingRequest(6L, 1L, "29/09/2026 16:10", "Limpieza ok", null)
        coEvery { flushingApi.crear(request) } returns FlushingDto(10L, 6L, 1L, "29/09/2026 16:10", "Limpieza ok", null)
        coEvery { infraApi.actualizarTemperatura(any(), any()) } throws RuntimeException("sin conexion")

        val resultado = repo.registrarFlushing(
            lineaId = 6,
            usuarioId = 1,
            fechaHora = "29/09/2026 16:10",
            observacion = "Limpieza ok"
        )

        assertEquals(Flushing(10, 6, 1, "29/09/2026 16:10", "Limpieza ok"), resultado)
    }

    // ==========================================
    // FLUSHING - PUT
    // ==========================================

    @Test
    fun `actualizarObservacionFlushing rechaza observacion vacia antes de llamar al microservicio`() = runTest {
        esperarExcepcion(IllegalArgumentException::class.java) {
            repo.actualizarObservacionFlushing(1, "   ")
        }
        coVerify(exactly = 0) { flushingApi.actualizar(any(), any()) }
    }

    @Test
    fun `actualizarObservacionFlushing devuelve true cuando el microservicio responde bien`() = runTest {
        coEvery { flushingApi.actualizar(1L, ActualizarFlushingRequest("Nueva observacion")) } returns
            FlushingDto(1L, 6L, 1L, "f", "Nueva observacion", null)

        val resultado = repo.actualizarObservacionFlushing(1, "Nueva observacion")

        assertTrue(resultado)
    }

    @Test
    fun `actualizarObservacionFlushing devuelve false cuando el flushing no existe (404)`() = runTest {
        coEvery { flushingApi.actualizar(any(), any()) } throws crear404()

        val resultado = repo.actualizarObservacionFlushing(1, "Nueva observacion")

        assertFalse(resultado)
    }

    @Test
    fun `actualizarObservacionFlushing relanza otros errores HTTP distintos de 404`() = runTest {
        coEvery { flushingApi.actualizar(any(), any()) } throws crearHttpException(500)

        esperarExcepcion(HttpException::class.java) {
            repo.actualizarObservacionFlushing(1, "Nueva observacion")
        }
    }

    // ==========================================
    // FLUSHING - DELETE
    // ==========================================

    @Test
    fun `eliminarFlushing devuelve true cuando el microservicio confirma`() = runTest {
        coEvery { flushingApi.eliminar(1L) } returns Response.success(204, Unit)

        val resultado = repo.eliminarFlushing(1)

        assertTrue(resultado)
    }

    @Test
    fun `eliminarFlushing devuelve false cuando el flushing no existe (404)`() = runTest {
        coEvery { flushingApi.eliminar(1L) } returns Response.error(404, cuerpoVacio())

        val resultado = repo.eliminarFlushing(1)

        assertFalse(resultado)
    }

    @Test
    fun `eliminarFlushing lanza excepcion si el microservicio responde error distinto de 404`() = runTest {
        coEvery { flushingApi.eliminar(1L) } returns Response.error(500, cuerpoVacio())

        esperarExcepcion(HttpException::class.java) {
            repo.eliminarFlushing(1)
        }
    }

    // ==========================================
    // USUARIOS
    // ==========================================

    @Test
    fun `obtenerUsuario delega en la api simulada`() = runTest {
        val usuario = Usuario(1, "Operario Demo", RolUsuario.OPERARIO)
        coEvery { api.obtenerUsuario(1) } returns usuario

        val resultado = repo.obtenerUsuario(1)

        assertEquals(usuario, resultado)
    }

    // ==========================================
    // GALPON / LINEAS - caminos de respaldo que faltaban
    // ==========================================

    @Test
    fun `obtenerGalpon cae al cache cuando Oracle no responde (no solo cuando no encuentra el id)`() = runTest {
        coEvery { infraApi.obtenerGalpones() } throws RuntimeException("sin conexion")
        coEvery { dao.obtenerGalpon(5) } returns GalponEntity(5, 1, "Galpon cache")

        val resultado = repo.obtenerGalpon(5)

        assertEquals(Galpon(5, 1, "Galpon cache"), resultado)
    }

    @Test
    fun `obtenerLineas cae al cache local si Oracle falla`() = runTest {
        coEvery { infraApi.obtenerLineas() } throws RuntimeException("sin conexion")
        coEvery { dao.obtenerLineas(1) } returns listOf(
            LineaBebederoEntity(1, 1, "Linea cache", 18.0, "f", "NORMAL")
        )

        val resultado = repo.obtenerLineas(1)

        assertEquals(listOf(LineaBebedero(1, 1, "Linea cache", 18.0, "f", EstadoTemperatura.NORMAL)), resultado)
    }

    @Test
    fun `obtenerLinea encuentra la linea exitosamente sin necesitar el cache`() = runTest {
        coEvery { infraApi.obtenerLineas() } returns listOf(
            LineaBebederoDto(7, 1, "Linea 7", 18.0, "f", "NORMAL")
        )
        coEvery { dao.guardarLineas(any()) } just Runs

        val resultado = repo.obtenerLinea(7)

        assertEquals(LineaBebedero(7, 1, "Linea 7", 18.0, "f", EstadoTemperatura.NORMAL), resultado)
    }

    // ==========================================
    // FLUSHING - rama null de observacion (?: "")
    // ==========================================

    @Test
    fun `obtenerFlushings deja la observacion como texto vacio cuando el microservicio manda null`() = runTest {
        coEvery { flushingApi.listar(null) } returns listOf(
            FlushingDto(1L, 6L, 1L, "f", null, null)
        )

        val resultado = repo.obtenerFlushings()

        assertEquals("", resultado[0].observacion)
    }

    @Test
    fun `registrarFlushing deja la observacion como texto vacio cuando el microservicio responde null`() = runTest {
        val request = CrearFlushingRequest(6L, 1L, "f", "obs", null)
        coEvery { flushingApi.crear(request) } returns FlushingDto(10L, 6L, 1L, "f", null, null)
        coEvery { infraApi.actualizarTemperatura(any(), any()) } returns
            LineaBebederoDto(6, 1, "L", 18.0, "f", "NORMAL")

        val resultado = repo.registrarFlushing(6, 1, "f", "obs")

        assertEquals("", resultado.observacion)
    }

    // ==========================================
    // NOTIFICACIONES - rol Supervisor vs Operario
    // ==========================================

    private fun simularRolActual(rol: String): BebederoApp {
        mockkObject(BebederoApp.Companion)
        val appMock = mockk<BebederoApp>(relaxed = true)
        every { BebederoApp.instancia } returns appMock

        val prefsMock = mockk<SharedPreferences>()
        every { appMock.getSharedPreferences(any(), any()) } returns prefsMock
        every { prefsMock.getString("rol_actual", "") } returns rol

        mockkObject(NotificacionesBebedero)
        every { NotificacionesBebedero.notificarFlushings(any(), any()) } just Runs

        return appMock
    }

    @Test
    fun `registrarFlushing notifica cuando el rol activo es supervisor`() = runTest {
        val request = CrearFlushingRequest(6L, 1L, "f", "obs", null)
        coEvery { flushingApi.crear(request) } returns FlushingDto(10L, 6L, 1L, "f", "obs", null)
        coEvery { infraApi.actualizarTemperatura(any(), any()) } returns
            LineaBebederoDto(6, 1, "L", 18.0, "f", "NORMAL")
        coEvery { flushingApi.listar(null) } returns emptyList()
        simularRolActual("supervisor")

        repo.registrarFlushing(6, 1, "f", "obs")

        verify(exactly = 1) { NotificacionesBebedero.notificarFlushings(any(), any()) }
    }

    @Test
    fun `registrarFlushing no notifica cuando el rol activo no es supervisor`() = runTest {
        val request = CrearFlushingRequest(6L, 1L, "f", "obs", null)
        coEvery { flushingApi.crear(request) } returns FlushingDto(10L, 6L, 1L, "f", "obs", null)
        coEvery { infraApi.actualizarTemperatura(any(), any()) } returns
            LineaBebederoDto(6, 1, "L", 18.0, "f", "NORMAL")
        simularRolActual("operario")

        repo.registrarFlushing(6, 1, "f", "obs")

        verify(exactly = 0) { NotificacionesBebedero.notificarFlushings(any(), any()) }
    }

    @Test
    fun `eliminarFlushing notifica cuando el rol activo es supervisor`() = runTest {
        coEvery { flushingApi.eliminar(1L) } returns Response.success(204, Unit)
        coEvery { flushingApi.listar(null) } returns emptyList()
        simularRolActual("supervisor")

        repo.eliminarFlushing(1)

        verify(exactly = 1) { NotificacionesBebedero.notificarFlushings(any(), any()) }
    }

    @Test
    fun `eliminarFlushing no notifica cuando el rol activo no es supervisor`() = runTest {
        coEvery { flushingApi.eliminar(1L) } returns Response.success(204, Unit)
        simularRolActual("operario")

        repo.eliminarFlushing(1)

        verify(exactly = 0) { NotificacionesBebedero.notificarFlushings(any(), any()) }
    }

    @Test
    fun `actualizarNotificacionFlushing cuenta solo los flushings de hoy, sin importar el formato de fecha`() = runTest {
        val hoy = java.text.SimpleDateFormat("dd/MM/yyyy", java.util.Locale.getDefault()).format(java.util.Date())
        coEvery { flushingApi.listar(null) } returns listOf(
            FlushingDto(1L, 1L, 1L, "$hoy 10:00", "de hoy", null),
            FlushingDto(2L, 1L, 1L, "01/01/2000 10:00", "viejo", null)
        )
        val appMock = mockk<BebederoApp>(relaxed = true)
        mockkObject(BebederoApp.Companion)
        every { BebederoApp.instancia } returns appMock
        mockkObject(NotificacionesBebedero)
        every { NotificacionesBebedero.notificarFlushings(any(), any()) } just Runs

        repo.actualizarNotificacionFlushing()

        verify(exactly = 1) { NotificacionesBebedero.notificarFlushings(appMock, 1) }
    }

    // ==========================================
    // AUXILIARES
    // ==========================================

    private fun cuerpoVacio() =
        "".toResponseBody("application/json".toMediaTypeOrNull())

    private fun crear404(): HttpException = crearHttpException(404)

    private fun crearHttpException(codigo: Int): HttpException =
        HttpException(Response.error<Any>(codigo, cuerpoVacio()))

    private suspend fun <T : Throwable> esperarExcepcion(tipo: Class<T>, bloque: suspend () -> Unit) {
        try {
            bloque()
            fail("Se esperaba que lanzara ${tipo.simpleName}, pero no lanzo nada")
        } catch (e: Throwable) {
            assertTrue(
                "Se esperaba ${tipo.simpleName} pero se lanzo ${e::class.java.simpleName}",
                tipo.isInstance(e)
            )
        }
    }
}
