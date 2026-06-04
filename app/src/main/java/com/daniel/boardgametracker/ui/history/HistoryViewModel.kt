package com.daniel.boardgametracker.ui.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.daniel.boardgametracker.data.db.Session
import com.daniel.boardgametracker.data.repository.SessionRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class HistoryViewModel(private val repository: SessionRepository) : ViewModel() {
    private val filterFlow = MutableStateFlow("ALL")

    var selectedFilter: String
        get() = filterFlow.value
        set(value) { filterFlow.value = value }

    val filteredSessions: StateFlow<List<Session>> = repository.getAllSessions()
        .combine(filterFlow) { sessions, filter ->
            if (filter == "ALL") sessions else sessions.filter { it.gameType == filter }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun deleteSession(session: Session) {
        viewModelScope.launch { repository.deleteSession(session) }
    }
}
