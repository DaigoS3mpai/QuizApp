package com.example.quizapp

import android.os.Bundle
import com.example.quizapp.navigation.RootNavigation
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.core.view.WindowCompat
import androidx.compose.material3.Surface
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            MaterialTheme {
                Surface(color = MaterialTheme.colorScheme.background) {

                    val windowSizeClass = calculateWindowSizeClass(activity = this)

                    RootNavigation(windowSizeClass)
                }
            }
        }
    }
}
