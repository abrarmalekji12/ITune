package com.tools.iTune

import android.content.Context
import androidx.room.*
@Database(entities = [ITuneSong::class], version = 1)
abstract  class ITuneRoomDatabase : RoomDatabase() {

    abstract fun iTuneDao():ITuneDao
    companion object{
        private var instance: ITuneRoomDatabase? = null
        fun getInstance(context: Context): ITuneRoomDatabase? {
            if (instance == null) {
                synchronized(ITuneRoomDatabase::class) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        ITuneRoomDatabase::class.java, "iTune_database"
                    )
                        .fallbackToDestructiveMigration()
                        .build()
                }
            }
            return instance
        }
    }

}