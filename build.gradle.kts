
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.ksp) apply false
}

// Saca la carpeta build de OneDrive: OneDrive bloquea archivos y rompe el build en Windows.
System.getenv("LOCALAPPDATA")?.let { localAppData ->
    allprojects {
        layout.buildDirectory.set(file("$localAppData/gradle-builds/${rootProject.name}/${project.name}"))
    }
}
