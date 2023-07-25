package com.example.gallery.modul

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.gallery.modul.entity.History
import com.example.gallery.modul.repository.HistoryRepository

class HistoryViewModel(application: Application) : AndroidViewModel(application)  {
    private val historyRepository:HistoryRepository

    init {
        historyRepository= HistoryRepository(application)
    }

    fun get()=historyRepository.getAllHistoryLive()

    fun insertHistory(history: History){
        historyRepository.insert(history)
    }

    fun deleteOneHistory(history: History){
        historyRepository.deleteOneHistory(history)
    }

    fun delete(){
        historyRepository.delete()
    }

    fun deleteSame(){
        historyRepository.deleteSame()
    }

}