package com.daniel.boardgametracker.ui.detail

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.daniel.boardgametracker.data.db.Session
import com.daniel.boardgametracker.data.constants.VoidfallConstants
import com.daniel.boardgametracker.data.model.*
import com.daniel.boardgametracker.data.repository.SessionRepository
import com.daniel.boardgametracker.navigation.Screen
import com.daniel.boardgametracker.ui.components.WinBadge
import com.daniel.boardgametracker.ui.components.toDisplayDate
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SessionDetailScreen(
    sessionId: Long,
    navController: NavController,
    repository: SessionRepository
) {
    var session by remember { mutableStateOf<Session?>(null) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(sessionId) {
        session = repository.getSessionById(sessionId)
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete Session") },
            text = { Text("This session will be permanently deleted.") },
            confirmButton = {
                TextButton(onClick = {
                    session?.let { s ->
                        scope.launch {
                            repository.deleteSession(s)
                            navController.popBackStack()
                        }
                    }
                }) { Text("Delete", color = MaterialTheme.colorScheme.error) }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) { Text("Cancel") }
            }
        )
    }

    val s = session
    Scaffold(
        containerColor = androidx.compose.ui.graphics.Color.Transparent,
        topBar = {
            TopAppBar(
                title = { Text(if (s != null) GameType.displayName(s.gameType) else "Session") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                },
                actions = {
                    if (s != null) {
                        IconButton(onClick = {
                            navController.navigate(Screen.EditSession.route(s.gameType, s.id))
                        }) { Icon(Icons.Default.Edit, "Edit") }
                        IconButton(onClick = { showDeleteDialog = true }) {
                            Icon(Icons.Default.Delete, "Delete", tint = MaterialTheme.colorScheme.error)
                        }
                    }
                }
            )
        }
    ) { padding ->
        if (s == null) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = androidx.compose.ui.Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(s.dateMillis.toDisplayDate(), style = MaterialTheme.typography.bodyLarge)
                    WinBadge(won = s.won)
                }
                HorizontalDivider()
                when (s.gameType) {
                    GameType.VOIDFALL -> VoidfallDetail(s.gameDataJson)
                    GameType.FINAL_GIRL -> FinalGirlDetail(s.gameDataJson)
                    GameType.SPIRIT_ISLAND -> SpiritIslandDetail(s.gameDataJson)
                }
            }
        }
    }
}

@Composable
private fun DetailRow(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, color = MaterialTheme.colorScheme.onSurfaceVariant, style = MaterialTheme.typography.bodyMedium)
        Text(value, style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable
private fun VoidfallDetail(json: String) {
    val data = runCatching { Json.decodeFromString<VoidfallData>(json) }.getOrNull() ?: return
    DetailRow("Difficulty", data.difficulty)
    DetailRow("House", "${data.house} (Side ${data.houseSide})")
    DetailRow("Scenario", data.map)
    DetailRow("House Score", "${data.houseScore}")
    DetailRow("Voidborn Score", "${data.voidbornScore}")

    val b = data.voidbornBreakdown
    if (b != null) {
        val base = VoidfallConstants.DIFFICULTY_BASE_SCORES[data.difficulty] ?: 100
        Spacer(Modifier.height(4.dp))
        Text("Voidborn Breakdown", color = MaterialTheme.colorScheme.onSurfaceVariant, style = MaterialTheme.typography.bodySmall)
        DetailRow("  Base (${data.difficulty})", "$base")
        if (b.rifts > 0)                 DetailRow("  Rifts (${b.rifts})",                        "${b.rifts * 30}")
        if (b.incompleteSafeHavens > 0)  DetailRow("  Incomplete Safe Havens (${b.incompleteSafeHavens})", "${b.incompleteSafeHavens * 20}")
        if (b.catastropheTokens > 0)     DetailRow("  Catastrophe tokens (${b.catastropheTokens})", "${b.catastropheTokens * 20}")
        if (b.harbingerTokens > 0)       DetailRow("  Harbinger tokens (${b.harbingerTokens})",   "${b.harbingerTokens * 10}")
        if (b.consumedTechnologies > 0)  DetailRow("  Consumed Technologies (${b.consumedTechnologies})", "${b.consumedTechnologies * 5}")
        if (b.ongoingCrisisCards > 0)    DetailRow("  Ongoing Crisis cards (${b.ongoingCrisisCards})", "${b.ongoingCrisisCards * 5}")
        if (b.fallenHouseCards > 0)      DetailRow("  Fallen House cards (${b.fallenHouseCards})", "${b.fallenHouseCards * 3}")
        if (b.corruptionMarkers > 0)     DetailRow("  Corruption markers (${b.corruptionMarkers})", "${b.corruptionMarkers * 2}")
        if (b.voidbornPopulation > 0)    DetailRow("  Voidborn Population (${b.voidbornPopulation})", "${b.voidbornPopulation}")
    }

    DetailRow("Delta (Δ)", "${if (data.deltaScore > 0) "+" else ""}${data.deltaScore}")
    if (data.feelOfGame.isNotBlank()) DetailRow("Feel of Game", data.feelOfGame)
    if (data.notes.isNotBlank()) {
        Spacer(Modifier.height(4.dp))
        Text("Notes", color = MaterialTheme.colorScheme.onSurfaceVariant, style = MaterialTheme.typography.bodySmall)
        Text(data.notes, style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable
private fun FinalGirlDetail(json: String) {
    val data = runCatching { Json.decodeFromString<FinalGirlData>(json) }.getOrNull() ?: return
    DetailRow("Killer", data.killer)
    DetailRow("Location", data.location)
    DetailRow("Final Girl", data.finalGirl)
    DetailRow("Survivors Remaining", "${data.survivorsRemaining}")
    DetailRow("Final Girl Survived", if (data.finalGirlSurvived) "Yes" else "No")
    if (data.feelOfGame.isNotBlank()) DetailRow("Feel of Game", data.feelOfGame)
    if (data.notes.isNotBlank()) {
        Spacer(Modifier.height(4.dp))
        Text("Notes", color = MaterialTheme.colorScheme.onSurfaceVariant, style = MaterialTheme.typography.bodySmall)
        Text(data.notes, style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable
private fun SpiritIslandDetail(json: String) {
    val data = runCatching { Json.decodeFromString<SpiritIslandData>(json) }.getOrNull() ?: return
    if (data.spirits.isNotEmpty()) {
        Text("Spirits", color = MaterialTheme.colorScheme.onSurfaceVariant, style = MaterialTheme.typography.bodySmall)
        data.spirits.forEach { Text("• $it", style = MaterialTheme.typography.bodyMedium) }
        Spacer(Modifier.height(4.dp))
    }
    DetailRow("Adversary", if (data.adversary == "None") "None" else "${data.adversary} (Level ${data.adversaryLevel})")
    DetailRow("Scenario", data.scenario)
    DetailRow("Difficulty", "${data.computedDifficulty}")
    DetailRow("Score", "${data.score}")
    if (data.feelOfGame.isNotBlank()) DetailRow("Feel of Game", data.feelOfGame)
    if (data.notes.isNotBlank()) {
        Spacer(Modifier.height(4.dp))
        Text("Notes", color = MaterialTheme.colorScheme.onSurfaceVariant, style = MaterialTheme.typography.bodySmall)
        Text(data.notes, style = MaterialTheme.typography.bodyMedium)
    }
}
