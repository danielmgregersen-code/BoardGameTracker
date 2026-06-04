package com.daniel.boardgametracker.ui.stats

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.daniel.boardgametracker.data.model.GameType
import com.daniel.boardgametracker.data.repository.SessionRepository
import kotlin.math.abs

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatsScreen(repository: SessionRepository) {
    val vm: StatsViewModel = viewModel(initializer = { StatsViewModel(repository) })
    val stats by vm.stats.collectAsState()

    Scaffold(containerColor = androidx.compose.ui.graphics.Color.Transparent, topBar = { TopAppBar(title = { Text("Stats") }) }) { padding ->
        if (stats.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(stats) { gameStats ->
                    GameStatsCard(gameStats)
                }
            }
        }
    }
}

@Composable
private fun GameStatsCard(stats: GameStats) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = GameType.displayName(stats.gameType),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(Modifier.height(8.dp))

            if (stats.totalSessions == 0) {
                Text("No sessions yet", color = MaterialTheme.colorScheme.onSurfaceVariant)
            } else {
                Row(horizontalArrangement = Arrangement.spacedBy(24.dp)) {
                    StatItem(label = "Total", value = "${stats.totalSessions}")
                    StatItem(label = "Wins", value = "${stats.wins}")
                    StatItem(label = "Win Rate", value = "${(stats.winRate * 100).toInt()}%")
                }
                Spacer(Modifier.height(8.dp))
                val streakText = when {
                    stats.currentStreak > 0 -> "🔥 ${stats.currentStreak}-game win streak"
                    stats.currentStreak < 0 -> "${abs(stats.currentStreak)}-game loss streak"
                    else -> "No current streak"
                }
                Text(streakText, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                stats.mostPlayedSpirit?.let { spirit ->
                    Text(
                        "Favourite spirit: $spirit",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun StatItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, style = MaterialTheme.typography.headlineSmall)
        Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}
