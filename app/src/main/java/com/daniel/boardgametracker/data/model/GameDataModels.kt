package com.daniel.boardgametracker.data.model

import kotlinx.serialization.Serializable

@Serializable
data class VoidfallData(
    val difficulty: String = "Normal",
    val house: String = "",
    val houseSide: String = "A",
    val map: String = "",
    val houseScore: Int = 0,
    val voidbornScore: Int = 0,
    val deltaScore: Int = 0
)

@Serializable
data class FinalGirlData(
    val killer: String = "",
    val location: String = "",
    val finalGirl: String = "",
    val survivorsRemaining: Int = 0,
    val finalGirlSurvived: Boolean = false,
    val feelOfGame: String = "",
    val notes: String = ""
)

@Serializable
data class SpiritIslandData(
    val spirits: List<String> = emptyList(),
    val adversary: String = "None",
    val adversaryLevel: Int = 0,
    val scenario: String = "None",
    val computedDifficulty: Int = 0,
    val score: Int = 0,
    val won: Boolean = false,
    val notes: String = ""
)

object GameType {
    const val VOIDFALL = "VOIDFALL"
    const val FINAL_GIRL = "FINAL_GIRL"
    const val SPIRIT_ISLAND = "SPIRIT_ISLAND"

    fun displayName(gameType: String) = when (gameType) {
        VOIDFALL -> "Voidfall"
        FINAL_GIRL -> "Final Girl"
        SPIRIT_ISLAND -> "Spirit Island"
        else -> gameType
    }
}
