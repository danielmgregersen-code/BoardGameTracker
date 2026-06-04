package com.daniel.boardgametracker.ui.newsession

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.daniel.boardgametracker.data.constants.FinalGirlConstants
import com.daniel.boardgametracker.ui.components.DropdownField
import com.daniel.boardgametracker.ui.components.NumberField
import com.daniel.boardgametracker.ui.components.SectionLabel

@Composable
fun FinalGirlForm(vm: NewSessionViewModel) {
    SectionLabel("Feature Film")

    DropdownField(
        label = "Killer",
        options = FinalGirlConstants.KILLERS,
        selectedOption = vm.fgKiller.ifEmpty { FinalGirlConstants.KILLERS[0] },
        onOptionSelected = { vm.fgKiller = it }
    )
    Spacer(Modifier.height(8.dp))

    DropdownField(
        label = "Location",
        options = FinalGirlConstants.LOCATIONS,
        selectedOption = vm.fgLocation.ifEmpty { FinalGirlConstants.LOCATIONS[0] },
        onOptionSelected = { vm.fgLocation = it }
    )
    Spacer(Modifier.height(8.dp))

    DropdownField(
        label = "Final Girl",
        options = FinalGirlConstants.FINAL_GIRLS,
        selectedOption = vm.fgFinalGirl.ifEmpty { FinalGirlConstants.FINAL_GIRLS[0] },
        onOptionSelected = { vm.fgFinalGirl = it }
    )

    SectionLabel("Outcome")

    NumberField(label = "Survivors Remaining", value = vm.fgSurvivors, onChange = { vm.fgSurvivors = it })
    Spacer(Modifier.height(12.dp))

    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        Text("Final Girl Survived:", style = MaterialTheme.typography.bodyLarge)
        Switch(checked = vm.fgSurvived, onCheckedChange = { vm.fgSurvived = it })
        Text(
            text = if (vm.fgSurvived) "YES — WIN" else "NO — LOSS",
            style = MaterialTheme.typography.labelLarge,
            color = if (vm.fgSurvived) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
        )
    }

    SectionLabel("Notes")

    OutlinedTextField(
        value = vm.fgFeelOfGame,
        onValueChange = { vm.fgFeelOfGame = it },
        label = { Text("Feel of Game") },
        placeholder = { Text("Tense, one-sided, exciting…") },
        modifier = Modifier.fillMaxWidth(),
        maxLines = 2
    )
    Spacer(Modifier.height(8.dp))
    OutlinedTextField(
        value = vm.fgNotes,
        onValueChange = { vm.fgNotes = it },
        label = { Text("Notes") },
        modifier = Modifier.fillMaxWidth(),
        maxLines = 4
    )
}
