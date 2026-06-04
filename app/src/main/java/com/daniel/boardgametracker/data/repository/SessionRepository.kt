package com.daniel.boardgametracker.data.repository

import com.daniel.boardgametracker.data.db.AppDatabase
import com.daniel.boardgametracker.data.db.Session
import kotlinx.coroutines.flow.Flow

class SessionRepository(private val db: AppDatabase) {
    fun getAllSessions(): Flow<List<Session>> = db.sessionDao().getAllSessions()
    fun getSessionsByGameType(gameType: String): Flow<List<Session>> = db.sessionDao().getSessionsByGameType(gameType)
    suspend fun getSessionById(id: Long): Session? = db.sessionDao().getSessionById(id)
    suspend fun getLatestSessionForGame(gameType: String): Session? = db.sessionDao().getLatestSessionForGame(gameType)
    suspend fun insertSession(session: Session): Long = db.sessionDao().insert(session)
    suspend fun updateSession(session: Session) = db.sessionDao().update(session)
    suspend fun deleteSession(session: Session) = db.sessionDao().delete(session)
}
