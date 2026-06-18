package com.daniel.boardgametracker.ui.helper

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.daniel.boardgametracker.data.model.ChapterProgress
import com.daniel.boardgametracker.data.model.FreeCompanySheet
import com.daniel.boardgametracker.data.model.OathswornCharacter
import com.daniel.boardgametracker.data.model.OathswornData
import com.daniel.boardgametracker.data.prefs.OathswornPrefs
import com.daniel.boardgametracker.ui.components.NumberField
import com.daniel.boardgametracker.ui.components.SectionLabel
import com.daniel.boardgametracker.ui.components.toDisplayDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OathswornHelperScreen(
    navController: NavController,
    prefs: OathswornPrefs
) {
    val vm: OathswornHelperViewModel = viewModel(initializer = { OathswornHelperViewModel(prefs) })
    var selectedTab by rememberSaveable { mutableIntStateOf(0) }

    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            TopAppBar(
                title = { Text("Oathsworn") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            TabRow(selectedTabIndex = selectedTab) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text("Free Company") }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("Characters") }
                )
            }
            when (selectedTab) {
                0 -> FreeCompanyTab(vm)
                else -> CharactersTab(vm)
            }
        }
    }
}

/* ----------------------------- Free Company ----------------------------- */

@Composable
private fun FreeCompanyTab(vm: OathswornHelperViewModel) {
    val fc = vm.state.freeCompany

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        OutlinedTextField(
            value = fc.name,
            onValueChange = { vm.updateFreeCompany(fc.copy(name = it)) },
            label = { Text("Company Name") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        SectionLabel("Keywords")
        OutlinedTextField(
            value = fc.keywords,
            onValueChange = { vm.updateFreeCompany(fc.copy(keywords = it)) },
            label = { Text("Keywords") },
            placeholder = { Text("Story keywords gained during the campaign…") },
            modifier = Modifier.fillMaxWidth(),
            minLines = 3
        )

        SectionLabel("Chapter Progress")
        ChapterTable(
            chapters = fc.chapters,
            onChange = { index, progress ->
                val list = fc.chapters.toMutableList()
                if (index in list.indices) list[index] = progress
                vm.updateFreeCompany(fc.copy(chapters = list))
            }
        )

        SectionLabel("Free Company Traits")
        OathswornData.TRAITS.forEach { trait ->
            val level = fc.traitLevels[trait.id] ?: 0
            Column(modifier = Modifier.padding(vertical = 6.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = trait.name,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.weight(1f)
                    )
                    LevelSelector(
                        level = level,
                        max = trait.maxLevel,
                        onChange = { newLevel ->
                            vm.updateFreeCompany(
                                fc.copy(traitLevels = fc.traitLevels + (trait.id to newLevel))
                            )
                        }
                    )
                }
                Text(
                    text = trait.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        SectionLabel("Knocked-Out Track")
        NumberField(
            label = "Skulls (0–${OathswornData.KNOCKED_OUT_MAX})",
            value = fc.knockedOut,
            onChange = { vm.updateFreeCompany(fc.copy(knockedOut = it.coerceIn(0, OathswornData.KNOCKED_OUT_MAX))) }
        )

        SectionLabel("Notes")
        OutlinedTextField(
            value = fc.notes,
            onValueChange = { vm.updateFreeCompany(fc.copy(notes = it)) },
            label = { Text("Notes") },
            modifier = Modifier.fillMaxWidth(),
            minLines = 3
        )
        Spacer(Modifier.height(24.dp))
    }
}

@Composable
private fun ChapterTable(
    chapters: List<ChapterProgress>,
    onChange: (Int, ChapterProgress) -> Unit
) {
    val headers = listOf("Story", "Ambush", "Rules", "Item", "Enc")
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            // Header row
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.width(28.dp))
                headers.forEach { h ->
                    Text(
                        text = h,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
            HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
            chapters.forEachIndexed { index, progress ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "${index + 1}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.width(28.dp)
                    )
                    CheckCell(progress.story, Modifier.weight(1f)) { onChange(index, progress.copy(story = it)) }
                    CheckCell(progress.ambushed, Modifier.weight(1f)) { onChange(index, progress.copy(ambushed = it)) }
                    CheckCell(progress.specialRules, Modifier.weight(1f)) { onChange(index, progress.copy(specialRules = it)) }
                    CheckCell(progress.uniqueItem, Modifier.weight(1f)) { onChange(index, progress.copy(uniqueItem = it)) }
                    CheckCell(progress.encounter, Modifier.weight(1f)) { onChange(index, progress.copy(encounter = it)) }
                }
            }
        }
    }
}

@Composable
private fun CheckCell(checked: Boolean, modifier: Modifier = Modifier, onChange: (Boolean) -> Unit) {
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Checkbox(checked = checked, onCheckedChange = onChange)
    }
}

/* ------------------------------ Characters ------------------------------ */

@Composable
private fun CharactersTab(vm: OathswornHelperViewModel) {
    val characters = vm.state.characters
    var selectedSlot by rememberSaveable { mutableIntStateOf(0) }
    var showRetireDialog by remember { mutableStateOf(false) }

    if (characters.isEmpty()) return
    val slot = selectedSlot.coerceIn(0, characters.lastIndex)
    val character = characters[slot]

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        ScrollableTabRow(
            selectedTabIndex = slot,
            edgePadding = 0.dp,
            containerColor = Color.Transparent
        ) {
            characters.forEachIndexed { index, c ->
                Tab(
                    selected = slot == index,
                    onClick = { selectedSlot = index },
                    text = { Text(if (c.name.isBlank()) "Slot ${index + 1}" else c.name) }
                )
            }
        }

        Spacer(Modifier.height(8.dp))
        CharacterSheet(
            character = character,
            onChange = { vm.updateCharacter(slot, it) }
        )

        Spacer(Modifier.height(16.dp))
        OutlinedButton(
            onClick = { showRetireDialog = true },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error)
        ) {
            Text("Retire & Start New Sheet")
        }

        if (vm.state.retired.isNotEmpty()) {
            SectionLabel("Retired Characters")
            vm.state.retired.forEach { retired ->
                RetiredRow(retired, onDelete = { vm.deleteRetired(retired.id) })
            }
        }
        Spacer(Modifier.height(24.dp))
    }

    if (showRetireDialog) {
        val displayName = character.name.ifBlank { "Slot ${slot + 1}" }
        AlertDialog(
            onDismissRequest = { showRetireDialog = false },
            title = { Text("Retire $displayName?") },
            text = { Text("This sheet will be archived under Retired Characters and replaced with a fresh, empty sheet.") },
            confirmButton = {
                TextButton(onClick = {
                    vm.retireCharacter(slot)
                    showRetireDialog = false
                }) { Text("Retire") }
            },
            dismissButton = {
                TextButton(onClick = { showRetireDialog = false }) { Text("Cancel") }
            }
        )
    }
}

@Composable
private fun CharacterSheet(
    character: OathswornCharacter,
    onChange: (OathswornCharacter) -> Unit
) {
    OutlinedTextField(
        value = character.name,
        onValueChange = { onChange(character.copy(name = it)) },
        label = { Text("Name") },
        singleLine = true,
        modifier = Modifier.fillMaxWidth()
    )
    Spacer(Modifier.height(12.dp))
    NumberField(label = "HP", value = character.hp, onChange = { onChange(character.copy(hp = it)) })

    SectionLabel("Permanent Tokens")
    TokenRow("Defense", character.defense) { onChange(character.copy(defense = it)) }
    TokenRow("+2 Animus", character.animus) { onChange(character.copy(animus = it)) }
    TokenRow("Redraw", character.redraw) { onChange(character.copy(redraw = it)) }
    TokenRow("Empowered x3", character.empowered) { onChange(character.copy(empowered = it)) }
    TokenRow("Battleflow", character.battleflow) { onChange(character.copy(battleflow = it)) }

    SectionLabel("Animus")
    NumberField(label = "Animus Regen", value = character.animusRegen, onChange = { onChange(character.copy(animusRegen = it)) })
    Spacer(Modifier.height(12.dp))
    NumberField(label = "Max Animus", value = character.maxAnimus, onChange = { onChange(character.copy(maxAnimus = it)) })
}

@Composable
private fun TokenRow(label: String, level: Int, onChange: (Int) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, style = MaterialTheme.typography.bodyLarge)
        LevelSelector(level = level, max = OathswornData.MAX_TOKENS, onChange = onChange)
    }
}

@Composable
private fun RetiredRow(character: OathswornCharacter, onDelete: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(start = 16.dp, end = 8.dp, top = 8.dp, bottom = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = character.name.ifBlank { "Unnamed" },
                    style = MaterialTheme.typography.bodyLarge
                )
                val retiredAt = character.retiredAtMillis
                Text(
                    text = buildString {
                        append("HP ${character.hp}")
                        if (retiredAt != null) append("  •  Retired ${retiredAt.toDisplayDate()}")
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "Delete retired character")
            }
        }
    }
}

/* ------------------------------ Shared bits ----------------------------- */

/**
 * A row of [max] tappable pips. Tapping a pip sets the level to that pip's
 * position; tapping the currently-highest pip clears it back down by one.
 */
@Composable
private fun LevelSelector(level: Int, max: Int, onChange: (Int) -> Unit) {
    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        for (i in 1..max) {
            val filled = i <= level
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(
                        if (filled) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.surfaceVariant
                    )
                    .border(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.outline,
                        shape = CircleShape
                    )
                    .clickable { onChange(if (level == i) i - 1 else i) },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "$i",
                    style = MaterialTheme.typography.labelMedium,
                    color = if (filled) MaterialTheme.colorScheme.onPrimary
                    else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
