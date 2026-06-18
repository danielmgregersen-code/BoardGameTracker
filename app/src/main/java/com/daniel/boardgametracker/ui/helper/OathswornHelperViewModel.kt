package com.daniel.boardgametracker.ui.helper

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.daniel.boardgametracker.data.model.FreeCompanySheet
import com.daniel.boardgametracker.data.model.OathswornCharacter
import com.daniel.boardgametracker.data.model.OathswornData
import com.daniel.boardgametracker.data.model.OathswornState
import com.daniel.boardgametracker.data.prefs.OathswornPrefs
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class OathswornHelperViewModel(
    private val prefs: OathswornPrefs
) : ViewModel() {

    var state by mutableStateOf(OathswornState())
        private set

    /** Guards persistence until the initial load is complete. */
    private var loaded = false

    init {
        viewModelScope.launch {
            state = ensureRoster(prefs.getState().first())
            loaded = true
        }
    }

    /** Guarantees there are always [OathswornData.CHARACTER_SLOTS] active sheets. */
    private fun ensureRoster(s: OathswornState): OathswornState {
        if (s.characters.size >= OathswornData.CHARACTER_SLOTS) return s
        val now = System.currentTimeMillis()
        val chars = s.characters.toMutableList()
        while (chars.size < OathswornData.CHARACTER_SLOTS) {
            chars.add(OathswornCharacter(createdAtMillis = now))
        }
        return s.copy(characters = chars)
    }

    private fun update(transform: (OathswornState) -> OathswornState) {
        state = transform(state)
        if (loaded) {
            val snapshot = state
            viewModelScope.launch { prefs.saveState(snapshot) }
        }
    }

    fun updateFreeCompany(fc: FreeCompanySheet) = update { it.copy(freeCompany = fc) }

    fun updateCharacter(index: Int, character: OathswornCharacter) = update { st ->
        val list = st.characters.toMutableList()
        if (index in list.indices) list[index] = character
        st.copy(characters = list)
    }

    /** Retires the active sheet at [index], archiving it and opening a fresh blank slot. */
    fun retireCharacter(index: Int) = update { st ->
        val list = st.characters.toMutableList()
        if (index !in list.indices) return@update st
        val retiring = list[index].copy(retiredAtMillis = System.currentTimeMillis())
        list[index] = OathswornCharacter(createdAtMillis = System.currentTimeMillis())
        st.copy(characters = list, retired = listOf(retiring) + st.retired)
    }

    fun deleteRetired(id: String) = update { st ->
        st.copy(retired = st.retired.filterNot { it.id == id })
    }
}
