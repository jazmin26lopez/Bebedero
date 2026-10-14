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

Las temperaturas cambian solas con el tiempo (simulación en segundo plano, cada 25 segundos), para poder probar la app sin depender de datos fijos. Al registrar un flushing en una línea, esta vuelve a temperatura Normal de inmediato.

## Funcionalidades implementadas

### Rol Operario
- Selección de rol en la pantalla de inicio.
- Consulta de Granjas → Galpones → Líneas → Detalle de una línea (temperatura, estado y última actualización).
- Alertas activas: listado de líneas en Advertencia o Crítico, con acceso directo al detalle de cada una (RF-07).
- Registro de flushing: formulario con observación obligatoria (validada) y fotografía opcional tomada con la cámara del dispositivo.
- Pantalla de confirmación del registro.
- Notificación local cuando una línea pasa a estado Advertencia o Crítico (revisión periódica en segundo plano con WorkManager).

### Rol Supervisor
- Resumen general: cantidad de granjas, líneas monitoreadas, eventos críticos/advertencia y líneas en estado normal.
- Listado de eventos críticos, con acceso directo al detalle de cada línea.
- Historial de temperaturas de una línea.
- Historial de flushing registrados por los operarios, filtrable por día / mes / año.
- Notificación con el conteo de flushings realizados en el día.

### Transversal
- Persistencia local con Room: las granjas, galpones, líneas y flushings quedan cacheados para poder consultarlos aunque la "red" simulada falle.
- Navegación completa entre las 10 pantallas de la app (Navigation Compose).
- Animaciones en botones y tarjetas (feedback al presionar), y transición animada en la vista previa de la fotografía.

## Arquitectura

El proyecto sigue el patrón **MVVM**, organizado en paquetes:

```
model/        Clases de datos (Granja, Galpon, LineaBebedero, Flushing, etc.)
repository/   ApiSimulada (datos en memoria) + BebederoRepository (combina API simulada y cache Room)
repository/local/   Entidades, DAO y base de datos Room
viewmodel/    ConsultaViewModel y FlushingViewModel
ui/           Pantallas Compose y componentes reutilizables
```

La UI solo conoce los ViewModels, y los ViewModels solo conocen al `BebederoRepository`; el Repository es el único punto que sabe que existen una API simulada y una base de datos Room detrás.

## Recursos nativos utilizados

- **Cámara**: captura de fotografía al registrar un flushing (`ActivityResultContracts.TakePicturePreview`).
- **Notificaciones locales**: alertas de temperatura para el Operario y conteo de flushings para el Supervisor.
- **Almacenamiento local (Room)**: persistencia de granjas, galpones, líneas y flushings.
- **WorkManager**: revisión periódica de temperaturas en segundo plano.

## Tecnologías

- Kotlin + Jetpack Compose (Material 3)
- Navigation Compose
- Room (persistencia local)
- WorkManager (tareas en segundo plano)
- Arquitectura MVVM

## Requisitos previos

- Android Studio actualizado
- JDK 11 o superior
- SDK de Android con `compileSdk`/`targetSdk` 37 y `minSdk` 35 instalados
- Un emulador o dispositivo físico con Android 15 (API 35) o superior

## Pasos para ejecutar el proyecto

1. Clonar el repositorio:
   ```bash
   git clone https://github.com/jazmin26lopez/Bebedero.git
   ```
2. Abrir la carpeta del proyecto con Android Studio.
3. Esperar a que Gradle sincronice las dependencias (requiere conexión a internet la primera vez).
4. Seleccionar un emulador o conectar un dispositivo físico.
5. Ejecutar la app con el botón **Run** (▶) o:
   ```bash
   ./gradlew installDebug
   ```

## Planificación

La planificación y el reparto de tareas del equipo está disponible en Trello, en el tablero **"APP Bebederos"**.
