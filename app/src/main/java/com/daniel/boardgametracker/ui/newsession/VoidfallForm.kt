package com.daniel.boardgametracker.ui.newsession

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.daniel.boardgametracker.data.constants.VoidfallConstants
import com.daniel.boardgametracker.ui.components.DropdownField
import com.daniel.boardgametracker.ui.components.NumberField
import com.daniel.boardgametracker.ui.components.SectionLabel

@Composable
fun VoidfallForm(vm: NewSessionViewModel) {
    SectionLabel("Setup")

    DropdownField(
        label = "Difficulty",
        options = VoidfallConstants.DIFFICULTIES,
        selectedOption = vm.vfDifficulty,
        onOptionSelected = { vm.vfDifficulty = it }
    )
    Spacer(Modifier.height(8.dp))

    DropdownField(
        label = "House",
        options = VoidfallConstants.HOUSES,
        selectedOption = vm.vfHouse.ifEmpty { VoidfallConstants.HOUSES[0] },
        onOptionSelected = { vm.vfHouse = it }
    )
    Spacer(Modifier.height(8.dp))

    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        Text("Side:", style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(end = 4.dp))
        VoidfallConstants.HOUSE_SIDES.forEach { side ->
            FilterChip(
                selected = vm.vfHouseSide == side,
                onClick = { vm.vfHouseSide = side },
                label = { Text(side) }
            )
        }
    }
    Spacer(Modifier.height(8.dp))

    DropdownField(
        label = "Scenario",
        options = VoidfallConstants.MAP_NAMES,
        selectedOption = vm.vfMap.ifEmpty { VoidfallConstants.MAP_NAMES[0] },
        onOptionSelected = { vm.vfMap = it }
    )

    SectionLabel("Scores")

    NumberField(label = "House Score", value = vm.vfHouseScore, onChange = { vm.vfHouseScore = it })
    Spacer(Modifier.height(12.dp))

    // Voidborn mode toggle
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text("Voidborn Score:", style = MaterialTheme.typography.bodyMedium)
        FilterChip(
            selected = !vm.vfUseBreakdown,
            onClick = { vm.vfUseBreakdown = false },
            label = { Text("Simple") }
        )
        FilterChip(
            selected = vm.vfUseBreakdown,
            onClick = { vm.vfUseBreakdown = true },
            label = { Text("Breakdown") }
        )
    }
    Spacer(Modifier.height(8.dp))

    if (!vm.vfUseBreakdown) {
        NumberField(
            label = "Voidborn Score",
            value = vm.vfVoidbornScoreManual,
            onChange = { vm.vfVoidbornScoreManual = it }
        )
    } else {
        VoidbornBreakdownPanel(vm)
    }

    Spacer(Modifier.height(8.dp))

    SectionLabel("Notes")

    OutlinedTextField(
        value = vm.vfFeelOfGame,
        onValueChange = { vm.vfFeelOfGame = it },
        label = { Text("Feel of Game") },
        placeholder = { Text("Tense, one-sided, exciting…") },
        modifier = Modifier.fillMaxWidth(),
        maxLines = 2
    )
    Spacer(Modifier.height(8.dp))
    OutlinedTextField(
        value = vm.vfNotes,
        onValueChange = { vm.vfNotes = it },
        label = { Text("Notes") },
        modifier = Modifier.fillMaxWidth(),
        maxLines = 4
    )
    Spacer(Modifier.height(8.dp))

    val delta = vm.vfDeltaScore
    val deltaColor = when {
        delta > 0 -> MaterialTheme.colorScheme.primary
        delta < 0 -> MaterialTheme.colorScheme.error
        else -> MaterialTheme.colorScheme.onSurfaceVariant
    }
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Row(
            modifier = Modifier.padding(12.dp).fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Delta (Δ)", style = MaterialTheme.typography.bodyLarge)
            Text(
                text = "${if (delta > 0) "+" else ""}$delta",
                style = MaterialTheme.typography.titleLarge,
                color = deltaColor
            )
            Text(
                text = if (delta > 0) "WIN" else "LOSS",
                style = MaterialTheme.typography.labelLarge,
                color = deltaColor
            )
        }
    }
}

@Composable
private fun VoidbornBreakdownPanel(vm: NewSessionViewModel) {
    val baseScore = VoidfallConstants.DIFFICULTY_BASE_SCORES[vm.vfDifficulty] ?: 100

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            // Read-only base score row
            BreakdownReadOnlyRow(label = "Base (${vm.vfDifficulty})", subtotal = baseScore)
            HorizontalDivider()

            BreakdownRow("Rifts", vm.vfRifts, 30) { vm.vfRifts = it }
            BreakdownRow("Incomplete Safe Havens", vm.vfSafeHavens, 20) { vm.vfSafeHavens = it }
            BreakdownRow("Catastrophe tokens", vm.vfCatastrophe, 20) { vm.vfCatastrophe = it }
            BreakdownRow("Harbinger tokens", vm.vfHarbinger, 10) { vm.vfHarbinger = it }
            BreakdownRow("Consumed Technologies", vm.vfConsumedTech, 5) { vm.vfConsumedTech = it }
            BreakdownRow("Ongoing Crisis cards", vm.vfOngoingCrisis, 5) { vm.vfOngoingCrisis = it }
            BreakdownRow("Fallen House cards", vm.vfFallenHouse, 3) { vm.vfFallenHouse = it }
            BreakdownRow("Corruption markers", vm.vfCorruption, 2) { vm.vfCorruption = it }
            BreakdownRow("Voidborn Population", vm.vfPopulation, 1) { vm.vfPopulation = it }

            HorizontalDivider()
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Total Voidborn", style = MaterialTheme.typography.titleSmall)
                Text(
                    text = "${vm.vfVoidbornScore}",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
private fun BreakdownRow(
    label: String,
    count: Int,
    rate: Int,
    onCountChange: (Int) -> Unit
) {
    var text by remember(count) { mutableStateOf(if (count == 0) "" else count.toString()) }

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(label, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        OutlinedTextField(
            value = text,
            onValueChange = { s ->
                text = s
                val parsed = s.toIntOrNull()
                if (parsed != null) onCountChange(parsed) else if (s.isEmpty()) onCountChange(0)
            },
            modifier = Modifier.width(64.dp),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            textStyle = MaterialTheme.typography.bodyMedium.copy(textAlign = TextAlign.Center),
            label = null
        )
        Text(
            text = "×$rate",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.width(32.dp)
        )
        Text(
            text = "${count * rate}",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.width(40.dp),
            textAlign = TextAlign.End
        )
    }
}

@Composable
private fun BreakdownReadOnlyRow(label: String, subtotal: Int) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text("$subtotal", style = MaterialTheme.typography.bodyMedium)
    }
}
