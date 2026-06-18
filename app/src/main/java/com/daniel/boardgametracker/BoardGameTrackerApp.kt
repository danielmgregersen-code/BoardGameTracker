package com.daniel.boardgametracker

import android.app.Application
import com.daniel.boardgametracker.data.db.AppDatabase
import com.daniel.boardgametracker.data.prefs.LastSessionPrefs
import com.daniel.boardgametracker.data.prefs.OathswornPrefs
import com.daniel.boardgametracker.data.repository.SessionRepository

class BoardGameTrackerApp : Application() {
    val database by lazy { AppDatabase.getDatabase(this) }
    val repository by lazy { SessionRepository(database) }
    val prefs by lazy { LastSessionPrefs(this) }
    val oathswornPrefs by lazy { OathswornPrefs(this) }
}
