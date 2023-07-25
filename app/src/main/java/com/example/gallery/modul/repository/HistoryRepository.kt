package com.example.gallery.modul.repository

import android.content.Context
import androidx.lifecycle.LiveData
import com.example.gallery.DatabaseSingleton
import com.example.gallery.modul.dao.HistoryDao
import com.example.gallery.modul.entity.History
import kotlin.concurrent.thread

class HistoryRepository(context: Context) {
    private val allHistoryLiveData:LiveData<List<History>>
    private val historyDao:HistoryDao

    init {
        historyDao=DatabaseSingleton.getDatabase(context.applicationContext).getHistoryDao()
        allHistoryLiveData=historyDao.getAllHistoryLive()
    }

    fun getAllHistoryLive()=allHistoryLiveData

    fun insert(history: History){
        thread {
            historyDao.insert(history)
        }
    }

    fun deleteOneHistory(history: History){
        thread {
            historyDao.deleteOneHistory(history)
        }
    }

    fun delete(){
        thread {
            historyDao.deleteAllHistory()
        }
    }

    fun deleteSame(){
        thread {
            historyDao.deleteDuplicates()
        }
    }

}