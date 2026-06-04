package com.daniel.boardgametracker.ui.newsession

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.daniel.boardgametracker.data.constants.SpiritIslandConstants
import com.daniel.boardgametracker.data.constants.VoidfallConstants
import com.daniel.boardgametracker.data.db.Session
import com.daniel.boardgametracker.data.model.*
import com.daniel.boardgametracker.data.prefs.LastSessionPrefs
import com.daniel.boardgametracker.data.repository.SessionRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class NewSessionViewModel(
    private val gameType: String,
    private val repository: SessionRepository,
    private val prefs: LastSessionPrefs,
    private val editSessionId: Long? = null
) : ViewModel() {

    var dateMillis by mutableLongStateOf(System.currentTimeMillis())

    // --- Voidfall ---
    var vfDifficulty by mutableStateOf(VoidfallConstants.DIFFICULTIES[1])
    var vfHouse by mutableStateOf("")
    var vfHouseSide by mutableStateOf("A")
    var vfMap by mutableStateOf("")
    var vfHouseScore by mutableIntStateOf(0)
    var vfVoidbornScore by mutableIntStateOf(0)
    val vfDeltaScore: Int get() = vfHouseScore - vfVoidbornScore

    // --- Final Girl ---
    var fgKiller by mutableStateOf("")
    var fgLocation by mutableStateOf("")
    var fgFinalGirl by mutableStateOf("")
    var fgSurvivors by mutableIntStateOf(0)
    var fgSurvived by mutableStateOf(false)
    var fgFeelOfGame by mutableStateOf("")
    var fgNotes by mutableStateOf("")

    // --- Spirit Island ---
    var siSpirits by mutableStateOf(emptyList<String>())
    var siAdversary by mutableStateOf("None")
    var siAdversaryLevel by mutableIntStateOf(0)
    var siScenario by mutableStateOf("None")
    val siDifficulty: Int get() = SpiritIslandConstants.computeDifficulty(siAdversary, siAdversaryLevel, siScenario)
    var siScore by mutableIntStateOf(0)
    var siWon by mutableStateOf(false)
    var siNotes by mutableStateOf("")

    var isSaving by mutableStateOf(false)
    var saveSuccess by mutableStateOf(false)
    var saveError by mutableStateOf<String?>(null)

    init {
        viewModelScope.launch {
            if (editSessionId != null && editSessionId > 0L) {
                loadExistingSession(editSessionId)
            } else {
                loadPrefs()
            }
        }
    }

    private suspend fun loadPrefs() {
        when (gameType) {
            GameType.VOIDFALL -> {
                prefs.getLastVoidfall().first()?.let { json ->
                    runCatching { Json.decodeFromString<VoidfallData>(json) }.onSuccess {
                        vfDifficulty = it.difficulty
                        vfHouse = it.house
                        vfHouseSide = it.houseSide
                        vfMap = it.map
                    }
                }
            }
            GameType.FINAL_GIRL -> {
                prefs.getLastFinalGirl().first()?.let { json ->
                    runCatching { Json.decodeFromString<FinalGirlData>(json) }.onSuccess {
                        fgKiller = it.killer
                        fgLocation = it.location
                        fgFinalGirl = it.finalGirl
                    }
                }
            }
            GameType.SPIRIT_ISLAND -> {
                prefs.getLastSpiritIsland().first()?.let { json ->
                    runCatching { Json.decodeFromString<SpiritIslandData>(json) }.onSuccess {
                        siSpirits = it.spirits
                        siAdversary = it.adversary
                        siAdversaryLevel = it.adversaryLevel
                        siScenario = it.scenario
                    }
                }
            }
        }
    }

    private suspend fun loadExistingSession(id: Long) {
        val session = repository.getSessionById(id) ?: return
        dateMillis = session.dateMillis
        when (gameType) {
            GameType.VOIDFALL -> runCatching { Json.decodeFromString<VoidfallData>(session.gameDataJson) }.onSuccess {
                vfDifficulty = it.difficulty; vfHouse = it.house; vfHouseSide = it.houseSide
                vfMap = it.map; vfHouseScore = it.houseScore; vfVoidbornScore = it.voidbornScore
            }
            GameType.FINAL_GIRL -> runCatching { Json.decodeFromString<FinalGirlData>(session.gameDataJson) }.onSuccess {
                fgKiller = it.killer; fgLocation = it.location; fgFinalGirl = it.finalGirl
                fgSurvivors = it.survivorsRemaining; fgSurvived = it.finalGirlSurvived
                fgFeelOfGame = it.feelOfGame; fgNotes = it.notes
            }
            GameType.SPIRIT_ISLAND -> runCatching { Json.decodeFromString<SpiritIslandData>(session.gameDataJson) }.onSuccess {
                siSpirits = it.spirits; siAdversary = it.adversary; siAdversaryLevel = it.adversaryLevel
                siScenario = it.scenario; siScore = it.score; siWon = it.won; siNotes = it.notes
            }
        }
    }

    fun save() {
        viewModelScope.launch {
            isSaving = true
            saveError = null
            runCatching {
                val (json, won) = buildSessionData()
                val session = Session(
                    id = if (editSessionId != null && editSessionId > 0L) editSessionId else 0L,
                    gameType = gameType,
                    dateMillis = dateMillis,
                    won = won,
                    gameDataJson = json
                )
                if (editSessionId != null && editSessionId > 0L) repository.updateSession(session)
                else repository.insertSession(session)
                saveSuccess = true
            }.onFailure { saveError = it.message }
            isSaving = false
        }
    }

    private suspend fun buildSessionData(): Pair<String, Boolean> = when (gameType) {
        GameType.VOIDFALL -> {
            val data = VoidfallData(vfDifficulty, vfHouse, vfHouseSide, vfMap, vfHouseScore, vfVoidbornScore, vfDeltaScore)
            prefs.saveLastVoidfall(Json.encodeToString(data))
            Json.encodeToString(data) to (vfDeltaScore > 0)
        }
        GameType.FINAL_GIRL -> {
            val data = FinalGirlData(fgKiller, fgLocation, fgFinalGirl, fgSurvivors, fgSurvived, fgFeelOfGame, fgNotes)
            prefs.saveLastFinalGirl(Json.encodeToString(data))
            Json.encodeToString(data) to fgSurvived
        }
        GameType.SPIRIT_ISLAND -> {
            val data = SpiritIslandData(siSpirits, siAdversary, siAdversaryLevel, siScenario, siDifficulty, siScore, siWon, siNotes)
            prefs.saveLastSpiritIsland(Json.encodeToString(data))
            Json.encodeToString(data) to siWon
        }
        else -> throw IllegalArgumentException("Unknown game type: $gameType")
    }
}
