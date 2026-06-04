package com.daniel.boardgametracker.ui.newsession

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.daniel.boardgametracker.data.model.GameType
import com.daniel.boardgametracker.data.prefs.LastSessionPrefs
import com.daniel.boardgametracker.data.repository.SessionRepository
import com.daniel.boardgametracker.ui.components.toDisplayDate
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewSessionScreen(
    gameType: String,
    editSessionId: Long?,
    navController: NavController,
    repository: SessionRepository,
    prefs: LastSessionPrefs
) {
    val vm: NewSessionViewModel = viewModel(
        key = "new_session_${gameType}_${editSessionId}",
        initializer = { NewSessionViewModel(gameType, repository, prefs, editSessionId) }
    )

    LaunchedEffect(vm.saveSuccess) {
        if (vm.saveSuccess) navController.popBackStack()
    }

    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = vm.dateMillis
    )

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        // Adjust to local midnight to avoid UTC-offset date shifts
                        val cal = Calendar.getInstance().apply { timeInMillis = millis }
                        val local = Calendar.getInstance().apply {
                            set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH), 12, 0, 0)
                            set(Calendar.MILLISECOND, 0)
                        }
                        vm.dateMillis = local.timeInMillis
                    }
                    showDatePicker = false
                }) { Text("OK") }
            },
            dismissButton = { TextButton(onClick = { showDatePicker = false }) { Text("Cancel") } }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    val title = if (editSessionId != null && editSessionId > 0L)
        "Edit ${GameType.displayName(gameType)}"
    else
        "New ${GameType.displayName(gameType)} Session"

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(title) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            OutlinedButton(
                onClick = { showDatePicker = true },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Date: ${vm.dateMillis.toDisplayDate()}")
            }
            Spacer(Modifier.height(8.dp))

            when (gameType) {
                GameType.VOIDFALL       -> VoidfallForm(vm)
                GameType.FINAL_GIRL     -> FinalGirlForm(vm)
                GameType.SPIRIT_ISLAND  -> SpiritIslandForm(vm)
            }

            Spacer(Modifier.height(24.dp))

            vm.saveError?.let { error ->
                Text(error, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(bottom = 8.dp))
            }

            Button(
                onClick = { vm.save() },
                enabled = !vm.isSaving,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (vm.isSaving) CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
                else Text("Save Session")
            }

            Spacer(Modifier.height(16.dp))
        }
    }
}
