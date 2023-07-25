package com.example.gallery

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.gallery.modul.dao.CollectionDao
import com.example.gallery.modul.dao.HistoryDao
import com.example.gallery.modul.entity.History
import com.example.gallery.modul.entity.Collection


@Database(entities = [History::class,Collection::class], version = 1, exportSchema = false)
abstract class DatabaseSingleton : RoomDatabase() {

    abstract fun getHistoryDao(): HistoryDao

    abstract fun getCollectionDao():CollectionDao

    companion object {
        @Volatile
        private var databaseINSTANCES: DatabaseSingleton? = null

        fun getDatabase(context: Context): DatabaseSingleton {
            databaseINSTANCES ?: synchronized(this) {
                databaseINSTANCES ?: Room.databaseBuilder(
                    context.applicationContext,
                    DatabaseSingleton::class.java,
                    "history_database"
                )
                    .build()
                    .also {
                        databaseINSTANCES = it
                    }
            }
            return databaseINSTANCES!!
        }
    }
}