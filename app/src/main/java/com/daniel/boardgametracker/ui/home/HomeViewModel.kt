package com.daniel.boardgametracker.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.daniel.boardgametracker.data.db.Session
import com.daniel.boardgametracker.data.repository.SessionRepository
import kotlinx.coroutines.flow.*

class HomeViewModel(repository: SessionRepository) : ViewModel() {
    val sessions: StateFlow<List<Session>> = repository.getAllSessions()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
}
