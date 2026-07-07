package com.example.hiatoo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.example.hiatoo.ui.HomeScreen
import com.example.hiatoo.ui.SettingsScreen
import com.example.hiatoo.ui.theme.HIATOOTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            var currentScreen by remember { mutableStateOf("Home") }

            HIATOOTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    if (currentScreen == "Home") {
                        HomeScreen(onNavigateToSettings = { currentScreen = "Settings" })
                    } else {
                        SettingsScreen(onNavigateBack = { currentScreen = "Home" })
                    }
                }
            }
        }
    }
}
