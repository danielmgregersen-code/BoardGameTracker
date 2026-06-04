package com.daniel.boardgametracker.data.constants

object SpiritIslandConstants {
    val SPIRITS = listOf(
        "Lightning's Swift Strike",
        "River Surges in Sunlight",
        "Shadows Flicker Like Flame",
        "Vital Strength of the Earth",
        "A Spread of Rampant Green",
        "Thunderspeaker",
        "Bringer of Dreams and Nightmares",
        "Ocean's Hungry Grasp",
        "Keeper of the Forbidden Wilds",
        "Sharp Fangs Behind the Leaves",
        "Heart of the Wildfire",
        "Serpent Slumbering Beneath the Island",
        "Grinning Trickster Stirs Up Trouble",
        "Lure of the Deep Wilderness",
        "Many Minds Move as One",
        "Shifting Memory of Ages",
        "Stone's Unyielding Defiance",
        "Volcano Looming High",
        "Shroud of Silent Mist",
        "Vengeance as a Burning Plague",
        "Fractured Days Split the Sky",
        "Starlight Seeks Its Form",
        "Downpour Drenches the World",
        "Finder of Paths Unseen",
        "Devouring Teeth Lurk Underfoot",
        "Eyes Watch from the Trees",
        "Fathomless Mud of the Swamp",
        "Rising Heat of Stone and Sand",
        "Sun-Bright Whirlwind",
        "Ember-Eyed Behemoth",
        "Hearth-Vigil",
        "Towering Roots of the Jungle",
        "Breath of Darkness Down Your Spine",
        "Relentless Gaze of the Sun",
        "Wandering Voice Keens Delirium",
        "Wounded Waters Bleeding",
        "Dances Up Earthquakes"
    )

    val ADVERSARIES = listOf(
        "None",
        "Brandenburg-Prussia",
        "England",
        "Sweden",
        "France (Plantation Colony)",
        "Habsburg Monarchy (Livestock Colony)",
        "Russia",
        "Scotland",
        "Habsburg Mining Expedition"
    )

    // Index 0 = base/L0, indices 1-6 = levels 1-6.
    // "None" has only level 0 (difficulty 0).
    val ADVERSARY_DIFFICULTIES: Map<String, List<Int>> = mapOf(
        "None"                               to listOf(0),
        "Brandenburg-Prussia"                to listOf(1, 2, 4, 6, 7, 9, 10),
        "England"                            to listOf(1, 3, 4, 6, 7, 9, 11),
        "Sweden"                             to listOf(1, 2, 3, 5, 6, 7, 8),
        "France (Plantation Colony)"         to listOf(2, 3, 5, 7, 8, 9, 10),
        "Habsburg Monarchy (Livestock Colony)" to listOf(2, 3, 5, 6, 8, 9, 10),
        "Russia"                             to listOf(1, 3, 4, 6, 7, 9, 11),
        "Scotland"                           to listOf(1, 3, 4, 6, 7, 8, 10),
        "Habsburg Mining Expedition"         to listOf(1, 3, 4, 5, 7, 9, 10)
    )

    val SCENARIOS = listOf(
        "None",
        "Blitz",
        "Guard the Isle's Heart",
        "A Diversity of Spirits",
        "Destiny Unfolds",
        "Second Wave",
        "Powers Long Forgotten",
        "Elemental Invocation",
        "Ward the Shores",
        "Despicable Theft",
        "Varied Terrains",
        "Rituals of Terror",
        "Rituals of the Destroying Flame",
        "The Great River",
        "Dahan Insurrection",
        "Surges of Colonization"
    )

    val SCENARIO_DIFFICULTIES: Map<String, Int> = mapOf(
        "None"                          to 0,
        "Blitz"                         to 0,
        "Guard the Isle's Heart"        to 0,
        "A Diversity of Spirits"        to 0,
        "Destiny Unfolds"               to -1,
        "Second Wave"                   to 0,
        "Powers Long Forgotten"         to 1,
        "Elemental Invocation"          to 1,
        "Ward the Shores"               to 2,
        "Despicable Theft"              to 2,
        "Varied Terrains"               to 2,
        "Rituals of Terror"             to 3,
        "Rituals of the Destroying Flame" to 3,
        "The Great River"               to 3,
        "Dahan Insurrection"            to 4,
        "Surges of Colonization"        to 2
    )

    fun computeDifficulty(adversary: String, level: Int, scenario: String): Int {
        val adversaryDiff = ADVERSARY_DIFFICULTIES[adversary]?.getOrElse(level) { 0 } ?: 0
        val scenarioDiff = SCENARIO_DIFFICULTIES[scenario] ?: 0
        return adversaryDiff + scenarioDiff
    }

    fun maxLevel(adversary: String): Int =
        if (adversary == "None") 0 else 6
}
