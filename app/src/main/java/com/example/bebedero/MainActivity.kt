package com.example.bebedero

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.bebedero.ui.AppNavigation
import com.example.bebedero.ui.theme.BebederoTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BebederoTheme {
                AppNavigation()
            }
        }
    }
}
