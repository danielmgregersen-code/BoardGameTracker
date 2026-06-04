package com.daniel.boardgametracker.ui.newsession

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.daniel.boardgametracker.data.constants.SpiritIslandConstants
import com.daniel.boardgametracker.ui.components.DropdownField
import com.daniel.boardgametracker.ui.components.NumberField
import com.daniel.boardgametracker.ui.components.SectionLabel

@Composable
fun SpiritIslandForm(vm: NewSessionViewModel) {
    var showSpiritPicker by remember { mutableStateOf(false) }

    if (showSpiritPicker) {
        SpiritPickerDialog(
            selected = vm.siSpirits,
            onDismiss = { showSpiritPicker = false },
            onConfirm = { vm.siSpirits = it; showSpiritPicker = false }
        )
    }

    SectionLabel("Spirits")

    OutlinedButton(
        onClick = { showSpiritPicker = true },
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            if (vm.siSpirits.isEmpty()) "Select Spirits"
            else "${vm.siSpirits.size} spirit${if (vm.siSpirits.size != 1) "s" else ""} selected"
        )
    }

    if (vm.siSpirits.isNotEmpty()) {
        vm.siSpirits.forEach { spirit ->
            Text(
                text = "• $spirit",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(start = 8.dp, top = 2.dp)
            )
        }
    }

    SectionLabel("Adversary & Scenario")

    DropdownField(
        label = "Adversary",
        options = SpiritIslandConstants.ADVERSARIES,
        selectedOption = vm.siAdversary,
        onOptionSelected = {
            vm.siAdversary = it
            if (it == "None") vm.siAdversaryLevel = 0
        }
    )
    Spacer(Modifier.height(8.dp))

    if (vm.siAdversary != "None") {
        Text(
            text = "Level: ${vm.siAdversaryLevel}",
            style = MaterialTheme.typography.bodyMedium
        )
        Slider(
            value = vm.siAdversaryLevel.toFloat(),
            onValueChange = { vm.siAdversaryLevel = it.toInt() },
            valueRange = 0f..6f,
            steps = 5
        )
    }

    Spacer(Modifier.height(4.dp))
    DropdownField(
        label = "Scenario",
        options = SpiritIslandConstants.SCENARIOS,
        selectedOption = vm.siScenario,
        onOptionSelected = { vm.siScenario = it }
    )
    Spacer(Modifier.height(8.dp))

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Row(
            modifier = Modifier.padding(12.dp).fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Computed Difficulty", style = MaterialTheme.typography.bodyLarge)
            Text(
                text = "${vm.siDifficulty}",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }

    SectionLabel("Outcome")

    NumberField(label = "Score (Dahan − Blight)", value = vm.siScore, onChange = { vm.siScore = it })
    Spacer(Modifier.height(12.dp))

    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        Text("Result:", style = MaterialTheme.typography.bodyLarge)
        Switch(checked = vm.siWon, onCheckedChange = { vm.siWon = it })
        Text(
            text = if (vm.siWon) "WIN" else "LOSS",
            style = MaterialTheme.typography.labelLarge,
            color = if (vm.siWon) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
        )
    }

    SectionLabel("Notes")

    OutlinedTextField(
        value = vm.siNotes,
        onValueChange = { vm.siNotes = it },
        label = { Text("Notes") },
        modifier = Modifier.fillMaxWidth(),
        maxLines = 4
    )
}

@Composable
private fun SpiritPickerDialog(
    selected: List<String>,
    onDismiss: () -> Unit,
    onConfirm: (List<String>) -> Unit
) {
    var currentSelection by remember { mutableStateOf(selected.toSet()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Select Spirits") },
        text = {
            LazyColumn(modifier = Modifier.heightIn(max = 400.dp)) {
                items(SpiritIslandConstants.SPIRITS) { spirit ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                currentSelection = if (spirit in currentSelection)
                                    currentSelection - spirit else currentSelection + spirit
                            }
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = spirit in currentSelection,
                            onCheckedChange = { checked ->
                                currentSelection = if (checked) currentSelection + spirit else currentSelection - spirit
                            }
                        )
                        Text(spirit, modifier = Modifier.padding(start = 8.dp), style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = { onConfirm(currentSelection.toList()) }) { Text("Done") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
