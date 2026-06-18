package com.daniel.boardgametracker.data.model

import kotlinx.serialization.Serializable
import java.util.UUID

/**
 * A single Oathsworn character sheet (the "Oathsworn Registration").
 *
 * Permanent token rows are stored as the number of tokens unlocked (0..3),
 * matching the 1 / 2 / 3 boxes printed on the physical sheet.
 */
@Serializable
data class OathswornCharacter(
    val id: String = UUID.randomUUID().toString(),
    val name: String = "",
    val hp: Int = 0,
    // Permanent Tokens (0..3 each)
    val defense: Int = 0,
    val animus: Int = 0,       // +2 Animus
    val redraw: Int = 0,
    val empowered: Int = 0,    // Empowered x3
    val battleflow: Int = 0,
    // Tracks
    val animusRegen: Int = 0,
    val maxAnimus: Int = 0,
    val createdAtMillis: Long = 0L,
    val retiredAtMillis: Long? = null
)

/** Per-chapter progress on the Free Company sheet. */
@Serializable
data class ChapterProgress(
    val story: Boolean = false,
    val ambushed: Boolean = false,
    val specialRules: Boolean = false,
    val uniqueItem: Boolean = false,
    val encounter: Boolean = false
)

/**
 * The shared Free Company sheet. Trait unlock levels are stored as a map of
 * trait id -> level unlocked (0..maxLevel) so that adding traits later stays
 * backwards compatible.
 */
@Serializable
data class FreeCompanySheet(
    val name: String = "",
    val keywords: String = "",
    val notes: String = "",
    val knockedOut: Int = 0,
    val chapters: List<ChapterProgress> = List(OathswornData.CHAPTER_COUNT) { ChapterProgress() },
    val traitLevels: Map<String, Int> = emptyMap()
)

/** Root persisted state for the Oathsworn helper. */
@Serializable
data class OathswornState(
    val freeCompany: FreeCompanySheet = FreeCompanySheet(),
    val characters: List<OathswornCharacter> = emptyList(),
    val retired: List<OathswornCharacter> = emptyList()
)

/** Static definition of a Free Company trait. */
data class OathswornTrait(
    val id: String,
    val name: String,
    val maxLevel: Int,
    val description: String
)

object OathswornData {
    const val CHAPTER_COUNT = 21
    const val CHARACTER_SLOTS = 4
    const val KNOCKED_OUT_MAX = 90
    const val MAX_TOKENS = 3

    val TRAITS = listOf(
        OathswornTrait("scavenger", "Scavenger", 1, "Loot 1 extra Common Item per chapter."),
        OathswornTrait("herbalist", "Herbalist", 1, "Gain 2 Curatives each time you purchase from an Apothecary."),
        OathswornTrait("tough_as_nails", "Tough As Nails", 1, "During Encounter setup, each Oathsworn rolls an unused Hit Point die. Whoever rolls highest gains 1 lost HP."),
        OathswornTrait("endurance", "Endurance", 1, "Your Backpack can hold up to 20 items."),
        OathswornTrait("true_grit", "True Grit", 1, "When you gain an injury, draw 2 Injury Cards, pick one and discard the other."),
        OathswornTrait("well_connected", "Well-Connected", 1, "'Sending a Runner' to a Banksmith or Apothecary no longer costs any additional iron."),
        OathswornTrait("field_medic", "Field Medic", 1, "Once per chapter, pay X iron (X = chapter number) to archive 1 Injury Card from one Oathsworn."),
        OathswornTrait("quartermaster", "Quartermaster", 1, "Gain 1 extra ration at the start of every Deepwood Journey."),
        OathswornTrait("bushcraft", "Bushcraft", 3, "1 / 2 / 3 redraws on Survival Checks."),
        OathswornTrait("keen_eyed", "Keen-Eyed", 3, "1 / 2 / 3 redraws on Spot / Search / Listen."),
        OathswornTrait("quick_witted", "Quick-Witted", 3, "1 / 2 / 3 redraws on Barter / Threat / Reason."),
        OathswornTrait("comradery", "Comradery", 3, "Allies enter play with 2 / 3 / 4 Redraw or Empowered x3 Tokens.")
    )
}
