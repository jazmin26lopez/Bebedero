# Bebedero — Monitoreo de Bebederos

Aplicación Android para el monitoreo de temperatura del agua en bebederos de granjas avícolas. Permite a un **Operario** consultar el estado de las líneas de bebederos y registrar limpiezas (flushing), y a un **Supervisor** revisar el panorama general, los eventos críticos y los históricos.

Proyecto desarrollado para la asignatura **DSY1105 - Desarrollo de Aplicaciones Móviles** (Duoc UC).

## Integrantes

- Bray Arciniegas ([BrayArciniegas](https://github.com/BrayArciniegas))
- Jazmin López ([jazmin26lopez](https://github.com/jazmin26lopez))

## Descripción del proyecto

Cada granja tiene galpones, y cada galpón tiene líneas de bebederos. Cada línea reporta una temperatura actual que se clasifica en tres estados:

- **Normal**: 15°C – 21°C
- **Advertencia**: 21.1°C – 30°C
- **Crítico**: menor a 15°C o mayor a 30°C

Al registrar un flushing en una línea, esta vuelve a temperatura Normal de inmediato. Todos estos datos ya no son simulados: viven en una base de datos Oracle real, servida por dos microservicios Spring Boot propios.

## Funcionalidades implementadas

### Rol Operario
- Selección de rol en la pantalla de inicio.
- Consulta de Granjas → Galpones → Líneas → Detalle de una línea (temperatura, estado y última actualización).
- Temperatura ambiente real de la zona (API externa), para comparar con la del agua.
- Alertas activas: listado de líneas en Advertencia o Crítico, con acceso directo al detalle de cada una (RF-07).
- Registro de flushing: formulario con observación obligatoria (validada) y fotografía opcional tomada con la cámara del dispositivo.
- Edición y eliminación de registros de flushing propios.
- Pantalla de confirmación del registro.
- Notificación local cuando una línea pasa a estado Advertencia o Crítico (revisión periódica en segundo plano con WorkManager).

### Rol Supervisor
- Resumen general: cantidad de granjas, líneas monitoreadas, eventos críticos/advertencia y líneas en estado normal.
- Listado de eventos críticos, con acceso directo al detalle de cada línea.
- Historial de temperaturas de una línea.
- Historial de flushing registrados por los operarios, filtrable por día / mes / año.
- Notificación con el conteo de flushings realizados en el día.

### Transversal
- Persistencia local con Room: las granjas, galpones, líneas y flushings quedan cacheados para poder consultarlos aunque los microservicios no respondan.
- Navegación completa entre las pantallas de la app (Navigation Compose).
- Animaciones en botones y tarjetas (feedback al presionar), y transición animada en la vista previa de la fotografía.

## Arquitectura

### App móvil (MVVM)

```
model/        Clases de datos (Granja, Galpon, LineaBebedero, Flushing, etc.)
repository/   BebederoRepository: llama a los microservicios via Retrofit,
              y cae a la cache local (Room) si no responden.
repository/local/    Entidades, DAO y base de datos Room (respaldo offline)
repository/remote/   Clientes Retrofit: microservicios propios + API externa de clima
viewmodel/    ConsultaViewModel y FlushingViewModel
ui/           Pantallas Compose y componentes reutilizables
```

La UI solo conoce los ViewModels, y los ViewModels solo conocen al `BebederoRepository`; el Repository es el único punto que sabe que existen microservicios remotos, una API externa y una base de datos Room detrás.

### Backend (microservicios propios)

Dos microservicios Spring Boot independientes, cada uno con su propio esquema en Oracle, en el repositorio [`app-movil-backend`](https://github.com/jazmin26lopez/app-movil-backend):

- **[`ms-infraestructura`](https://github.com/jazmin26lopez/app-movil-backend/tree/integracion-oracle/ms-infraestructura)** (puerto 8081) — Granjas, Galpones, Líneas y Temperatura.
- **[`ms-flushing`](https://github.com/jazmin26lopez/app-movil-backend/tree/integracion-oracle/ms-flushing)** (puerto 8082) — CRUD de registros de flushing.

Ambos deben estar corriendo (y conectados a Oracle) para que la app muestre datos reales; si no responden, la app sigue funcionando con la última copia guardada en Room.

### API externa

**Open-Meteo** (`api.open-meteo.com`) — temperatura ambiente real de la zona, mostrada en la pantalla de Granjas. No requiere API key, por eso se eligió: evita tener que guardar una clave secreta en un repositorio público. Es independiente de los microservicios propios — si el clima falla, no afecta al resto de la pantalla.

## Endpoints utilizados

**API externa — Open-Meteo:**
- `GET https://api.open-meteo.com/v1/forecast?latitude=-33.45&longitude=-70.66&current=temperature_2m`

**Microservicio `ms-infraestructura` (puerto 8081):**
- `GET /api/granjas` (lista de granjas)
- `GET /api/galpones` (lista, filtrable por `?granjaId=`)
- `GET /api/lineas` (lista, filtrable por `?galponId=`) · `PUT /api/lineas/{id}` (actualiza temperatura y estado)
- `GET /api/lineas/{id}/mediciones` (historial de temperaturas)

Granjas, Galpones y Líneas son de solo lectura desde la app (se cargan desde Oracle, nunca se crean/editan/eliminan desde la interfaz), por eso no exponen `POST`/`DELETE`.

**Microservicio `ms-flushing` (puerto 8082):**
- `GET /api/flushings` (lista, filtrable por `lineaId`)
- `POST /api/flushings` (crear) · `PUT /api/flushings/{id}` (editar observación) · `DELETE /api/flushings/{id}` (eliminar)

Detalle completo de cada endpoint (bodies, respuestas) en el [README de cada microservicio](https://github.com/jazmin26lopez/app-movil-backend).

## Conexión con los microservicios (red)

La app se conecta a los microservicios por IP, configurada en:
`app/src/main/java/com/example/bebedero/repository/remote/RetrofitCliente.kt` → constante `IP_SERVIDOR`.

**Esta IP depende de la red de cada quien y no debe comitearse con un valor personal.** Dos formas de dejarla funcionando, probadas ambas:

1. **Hotspot del celular** (recomendado para la demo en vivo): comparte internet desde el celular, conecta el computador a esa red Wi-Fi, y usa la IPv4 que le queda al computador en esa red (`ipconfig` → adaptador Wi-Fi). Hay que revisarla cada vez que se reactiva el hotspot, porque puede cambiar.
2. **Emulador + `adb reverse`** (para desarrollo rápido en el mismo equipo): `adb reverse tcp:8081 tcp:8081` y `adb reverse tcp:8082 tcp:8082`, con `IP_SERVIDOR = "127.0.0.1"`.

## Pruebas unitarias

57 pruebas con JUnit4 + MockK + `kotlinx-coroutines-test`, cubriendo los dos ViewModel y el Repository:

| Clase | Pruebas | Qué cubre |
|---|---|---|
| `FlushingViewModelTest` | 9 | Validación de observación, CRUD de flushing |
| `ConsultaViewModelTest` | 12 | Consultas, agregación de galpones, orden de eventos críticos |
| `BebederoRepositoryTest` | 36 | Éxito y respaldo a Room de cada endpoint, códigos HTTP (404 vs otros), validaciones, notificaciones por rol |

Correrlas:
```bash
./gradlew.bat :app:testDebugUnitTest
```

**Cobertura (JaCoCo)**, medida sobre ViewModels y Repository (el código que exige la rúbrica, 80% mínimo):

```bash
./gradlew.bat :app:jacocoTestReport
```

| Módulo | Líneas | Instrucciones | Ramas |
|---|---:|---:|---:|
| ViewModels + Repository | 99.4 % | 96.3 % | 86.5 % |

Reporte HTML: `app/build/reports/jacoco/jacocoTestReport/html/index.html` (generado localmente, no se incluye en el repositorio).

## APK firmado (release)

1. Generar un keystore propio (una sola vez):
   ```bash
   keytool -genkeypair -alias <tu-alias> -keyalg RSA -keysize 2048 -validity 10000 -keystore <nombre>.jks
   ```
2. Agregar las credenciales a `local.properties` (nunca se sube al repositorio, ya está en `.gitignore`):
   ```properties
   RELEASE_STORE_FILE=<nombre>.jks
   RELEASE_STORE_PASSWORD=...
   RELEASE_KEY_ALIAS=<tu-alias>
   RELEASE_KEY_PASSWORD=...
   ```
3. Compilar:
   ```bash
   ./gradlew.bat :app:assembleRelease
   ```
   El resultado queda en `app/build/outputs/apk/release/app-release.apk`.
4. Verificar la firma:
   ```bash
   apksigner verify --print-certs app-release.apk
   ```

**Verificación real de este proyecto:**
```
Signer #1 certificate DN: CN=Bryam Arciniegas, OU=DSY1105, O=DuocUC, L=Santiago, ST=RM, C=CL
Signer #1 certificate SHA-256 digest: 66e38f812d1de763b56abbc1161da2c80e7be21b403d656425894ab2ee76711c
```

**Evidencia de los archivos generados:**
```
app-release.apk           47.47 MB   20-11-2026 19:40
bebedero-release.jks       2.68 KB   17-11-2026 12:10
```

**¿Para qué sirve el `.jks`?** Es la llave privada con la que se firmó digitalmente el APK: certifica que el instalador viene realmente de este equipo y no fue alterado después de compilarse. No se necesita "abrirla" para instalar la app (el APK ya viene firmado); sirve como respaldo de verificación y para firmar futuras actualizaciones de la misma app con la misma identidad.

**Nota de entrega:** por indicación de la docente, el `.apk` firmado y el `.jks` no se suben al repositorio (ver `.gitignore`); se entregan directamente junto al .zip del proyecto (no publicados en un repositorio público, al ser el `.jks` una llave privada).

## Tecnologías

- Kotlin + Jetpack Compose (Material 3)
- Navigation Compose
- Retrofit + Gson (consumo de los microservicios y de la API externa)
- Room (cache/respaldo local)
- WorkManager (tareas en segundo plano)
- JUnit4 + MockK + kotlinx-coroutines-test (pruebas unitarias)
- Arquitectura MVVM

## Requisitos previos

- Android Studio actualizado
- JDK 11 o superior
- SDK de Android con `compileSdk`/`targetSdk` 37 y `minSdk` 35 instalados
- Un emulador o dispositivo físico con Android 15 (API 35) o superior
- Los microservicios de [`app-movil-backend`](https://github.com/jazmin26lopez/app-movil-backend) corriendo (rama `integracion-oracle`), con Oracle configurado

## Pasos para ejecutar el proyecto

1. Clonar y cambiar a la rama de integración:
   ```bash
   git clone -b integracion-oracle https://github.com/jazmin26lopez/Bebedero.git
   ```
2. Levantar los dos microservicios del backend (ver sus propios README) antes de abrir la app.
3. Abrir la carpeta del proyecto con Android Studio y esperar a que Gradle sincronice.
4. Ajustar `IP_SERVIDOR` en `RetrofitCliente.kt` según tu red (ver sección "Conexión con los microservicios" arriba) — **sin comitear ese cambio**.
5. Seleccionar un emulador o conectar un dispositivo físico.
6. Ejecutar la app con el botón **Run** (▶) o:
   ```bash
   ./gradlew installDebug
   ```

## Planificación

La planificación y el reparto de tareas del equipo está disponible en Trello, en el tablero **"APP Bebederos"**.
