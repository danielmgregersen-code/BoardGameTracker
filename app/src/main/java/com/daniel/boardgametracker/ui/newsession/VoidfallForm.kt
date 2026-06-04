package com.daniel.boardgametracker.ui.newsession

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
    Spacer(Modifier.height(8.dp))
    NumberField(label = "Voidborn Score", value = vm.vfVoidbornScore, onChange = { vm.vfVoidbornScore = it })
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
