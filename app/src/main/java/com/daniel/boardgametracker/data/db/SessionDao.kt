package com.daniel.boardgametracker.data.db

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface SessionDao {
    @Query("SELECT * FROM sessions ORDER BY dateMillis DESC")
    fun getAllSessions(): Flow<List<Session>>

    @Query("SELECT * FROM sessions WHERE gameType = :gameType ORDER BY dateMillis DESC")
    fun getSessionsByGameType(gameType: String): Flow<List<Session>>

    @Query("SELECT * FROM sessions WHERE id = :id LIMIT 1")
    suspend fun getSessionById(id: Long): Session?

    @Query("SELECT * FROM sessions WHERE gameType = :gameType ORDER BY dateMillis DESC LIMIT 1")
    suspend fun getLatestSessionForGame(gameType: String): Session?

    @Insert
    suspend fun insert(session: Session): Long

    @Update
    suspend fun update(session: Session)

    @Delete
    suspend fun delete(session: Session)
}
