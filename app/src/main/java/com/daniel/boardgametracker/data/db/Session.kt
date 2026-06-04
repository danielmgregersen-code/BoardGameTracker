package com.daniel.boardgametracker.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sessions")
data class Session(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val gameType: String,
    val dateMillis: Long,
    val won: Boolean,
    val gameDataJson: String
)
