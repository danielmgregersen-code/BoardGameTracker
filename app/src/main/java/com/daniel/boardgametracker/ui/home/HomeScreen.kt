package com.daniel.boardgametracker.ui.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.daniel.boardgametracker.data.db.Session
import com.daniel.boardgametracker.data.model.GameType
import com.daniel.boardgametracker.data.repository.SessionRepository
import com.daniel.boardgametracker.navigation.Screen
import com.daniel.boardgametracker.ui.components.toDisplayDate

private val gameAccentColors = mapOf(
    GameType.VOIDFALL       to Color(0xFF1E3A6E),
    GameType.FINAL_GIRL     to Color(0xFF6E1E1E),
    GameType.SPIRIT_ISLAND  to Color(0xFF1E4E1E)
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    repository: SessionRepository
) {
    val viewModel: HomeViewModel = viewModel(initializer = { HomeViewModel(repository) })
    val sessions by viewModel.sessions.collectAsState()

    Scaffold(containerColor = androidx.compose.ui.graphics.Color.Transparent, topBar = { TopAppBar(title = { Text("Solo Tracker") }) }) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(listOf(GameType.VOIDFALL, GameType.FINAL_GIRL, GameType.SPIRIT_ISLAND)) { gameType ->
                GameCard(
                    gameType = gameType,
                    sessions = sessions.filter { it.gameType == gameType },
                    onAddSession = { navController.navigate(Screen.NewSession.route(gameType)) }
                )
            }
        }
    }
}

@Composable
private fun GameCard(
    gameType: String,
    sessions: List<Session>,
    onAddSession: () -> Unit
) {
    val lastPlayed = sessions.firstOrNull()
    val wins = sessions.count { it.won }
    val losses = sessions.size - wins
    val accentColor = gameAccentColors[gameType] ?: MaterialTheme.colorScheme.surfaceVariant

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = GameType.displayName(gameType),
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(4.dp))
                if (lastPlayed != null) {
                    Text(
                        text = "Last played ${lastPlayed.dateMillis.toDisplayDate()}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "${wins}W / ${losses}L  (${sessions.size} total)",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                } else {
                    Text(
                        text = "No sessions yet",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            FilledTonalIconButton(onClick = onAddSession) {
                Icon(Icons.Default.Add, contentDescription = "Add session")
            }
        }

        if (sessions.isNotEmpty()) {
            HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                Text(
                    text = "Recent",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                sessions.take(3).forEach { session ->
                    Text(
                        text = "${session.dateMillis.toDisplayDate()}  •  ${if (session.won) "Win" else "Loss"}",
                        style = MaterialTheme.typography.bodySmall,
                        color = if (session.won) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(vertical = 2.dp)
                    )
                }
            }
        }
    }
}
