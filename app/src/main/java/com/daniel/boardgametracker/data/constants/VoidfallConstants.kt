package com.daniel.boardgametracker.data.constants

import com.daniel.boardgametracker.data.model.VoidbornBreakdown

object VoidfallConstants {
    val DIFFICULTIES = listOf("Easy", "Normal", "Hard")

    val HOUSES = listOf(
        "Belitan", "Cortozaar", "Dunlork", "Valnis",
        "Marqualos", "Novaris", "Yarvek", "Zenor",
        "Astoran", "Fenrax", "Nervo", "Thegwyn",
        "Kradmor", "Shiveus"
    )

    val HOUSE_SIDES = listOf("A", "B")

    data class VoidfallMap(val code: String, val name: String, val complexity: Int) {
        val displayName: String get() = "$code – $name"
    }

    val MAPS = listOf(
        VoidfallMap("C011", "First Stand", 1),
        VoidfallMap("C021", "And One For All", 1),
        VoidfallMap("C031", "Darkest Hour", 2),
        VoidfallMap("C041", "Ancient Secrets", 2),
        VoidfallMap("C051", "Devil's Triangle", 3),
        VoidfallMap("C061", "When Darkness Fades", 3),
        VoidfallMap("C071", "Today is Not the Day", 4),
        VoidfallMap("C081", "Fall of Civilization", 4)
    )

    val MAP_NAMES: List<String> = MAPS.map { it.displayName }

    val DIFFICULTY_BASE_SCORES = mapOf("Easy" to 60, "Normal" to 100, "Hard" to 140)

    fun computeVoidbornScore(difficulty: String, b: VoidbornBreakdown): Int =
        (DIFFICULTY_BASE_SCORES[difficulty] ?: 100) +
        b.rifts * 30 +
        b.incompleteSafeHavens * 20 +
        b.catastropheTokens * 20 +
        b.harbingerTokens * 10 +
        b.consumedTechnologies * 5 +
        b.ongoingCrisisCards * 5 +
        b.fallenHouseCards * 3 +
        b.corruptionMarkers * 2 +
        b.voidbornPopulation
}
