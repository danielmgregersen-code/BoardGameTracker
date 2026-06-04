package com.daniel.boardgametracker.ui.stats

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.daniel.boardgametracker.data.db.Session
import com.daniel.boardgametracker.data.model.GameType
import com.daniel.boardgametracker.data.model.SpiritIslandData
import com.daniel.boardgametracker.data.repository.SessionRepository
import kotlinx.coroutines.flow.*
import kotlinx.serialization.json.Json

data class GameStats(
    val gameType: String,
    val totalSessions: Int,
    val wins: Int,
    val winRate: Float,
    val currentStreak: Int,
    val mostPlayedSpirit: String? = null
)

class StatsViewModel(repository: SessionRepository) : ViewModel() {
    val sessions: StateFlow<List<Session>> = repository.getAllSessions()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val stats: StateFlow<List<GameStats>> = sessions.map { all ->
        listOf(GameType.VOIDFALL, GameType.FINAL_GIRL, GameType.SPIRIT_ISLAND).map { type ->
            computeStats(type, all.filter { it.gameType == type })
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private fun computeStats(gameType: String, sessions: List<Session>): GameStats {
        val total = sessions.size
        val wins = sessions.count { it.won }
        val winRate = if (total > 0) wins.toFloat() / total else 0f

        var streak = 0
        if (sessions.isNotEmpty()) {
            val last = sessions.first()
            val streakWon = last.won
            for (s in sessions) {
                if (s.won == streakWon) streak++ else break
            }
            if (!streakWon) streak = -streak
        }

        val mostPlayedSpirit = if (gameType == GameType.SPIRIT_ISLAND) {
            sessions.flatMap { session ->
                runCatching { Json.decodeFromString<SpiritIslandData>(session.gameDataJson).spirits }
                    .getOrDefault(emptyList())
            }
                .groupingBy { it }
                .eachCount()
                .maxByOrNull { it.value }
                ?.key
        } else null

        return GameStats(gameType, total, wins, winRate, streak, mostPlayedSpirit)
    }
}
