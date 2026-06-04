package com.daniel.boardgametracker.data.db

import android.content.Context
import androidx.room.*

@Database(entities = [Session::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun sessionDao(): SessionDao

    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase =
            INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "boardgame_tracker_db"
                ).build().also { INSTANCE = it }
            }
    }
}
