package com.daniel.boardgametracker.ui.history

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.daniel.boardgametracker.data.db.Session
import com.daniel.boardgametracker.data.model.GameType
import com.daniel.boardgametracker.data.repository.SessionRepository
import com.daniel.boardgametracker.navigation.Screen
import com.daniel.boardgametracker.ui.components.WinBadge
import com.daniel.boardgametracker.ui.components.toDisplayDate

private val filterOptions = listOf(
    "ALL" to "All",
    GameType.VOIDFALL to "Voidfall",
    GameType.FINAL_GIRL to "Final Girl",
    GameType.SPIRIT_ISLAND to "Spirit Island"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    navController: NavController,
    repository: SessionRepository
) {
    val vm: HistoryViewModel = viewModel(initializer = { HistoryViewModel(repository) })
    val sessions by vm.filteredSessions.collectAsState()

    Scaffold(topBar = { TopAppBar(title = { Text("History") }) }) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(filterOptions) { (key, label) ->
                    FilterChip(
                        selected = vm.selectedFilter == key,
                        onClick = { vm.selectedFilter = key },
                        label = { Text(label) }
                    )
                }
            }

            if (sessions.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No sessions yet", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            } else {
                LazyColumn(contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp)) {
                    items(sessions, key = { it.id }) { session ->
                        SessionRow(
                            session = session,
                            onClick = { navController.navigate(Screen.SessionDetail.route(session.id)) }
                        )
                        HorizontalDivider()
                    }
                }
            }
        }
    }
}

@Composable
private fun SessionRow(session: Session, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = GameType.displayName(session.gameType),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = session.dateMillis.toDisplayDate(),
                style = MaterialTheme.typography.bodyMedium
            )
        }
        WinBadge(won = session.won)
    }
}
