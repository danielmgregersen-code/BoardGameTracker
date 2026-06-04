package com.daniel.boardgametracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.daniel.boardgametracker.navigation.AppNavigation
import com.daniel.boardgametracker.ui.components.BoardGameBackground
import com.daniel.boardgametracker.ui.theme.BoardGameTrackerTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val app = application as BoardGameTrackerApp
        setContent {
            BoardGameTrackerTheme {
                BoardGameBackground(modifier = Modifier.fillMaxSize()) {
                    val navController = rememberNavController()
                    AppNavigation(
                        navController = navController,
                        repository = app.repository,
                        prefs = app.prefs
                    )
                }
            }
        }
    }
}
