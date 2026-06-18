package com.daniel.boardgametracker.ui.helper

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.daniel.boardgametracker.data.model.GameType
import com.daniel.boardgametracker.navigation.Screen

private data class HelperEntry(
    val title: String,
    val subtitle: String,
    val route: String,
    val accent: Color
)

private val helperEntries = listOf(
    HelperEntry(
        title = GameType.displayName(GameType.OATHSWORN),
        subtitle = "Free Company & character sheets",
        route = Screen.OathswornHelper.route,
        accent = Color(0xFF5A3E1B)
    )
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HelperScreen(navController: NavController) {
    Scaffold(
        containerColor = Color.Transparent,
        topBar = { TopAppBar(title = { Text("Helper") }) }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(helperEntries) { entry ->
                HelperCard(entry = entry, onClick = { navController.navigate(entry.route) })
            }
        }
    }
}

@Composable
private fun HelperCard(entry: HelperEntry, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = entry.title,
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = entry.subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Icon(
                Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
}
