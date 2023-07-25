package com.example.gallery.modul

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.gallery.modul.entity.Collection
import com.example.gallery.modul.repository.CollectionRepository

class CollectionViewModel(application: Application) : AndroidViewModel(application) {
    private val collectionRepository:CollectionRepository

    init {
        collectionRepository= CollectionRepository(application)
    }

    fun get()=collectionRepository.getAllCollection()

    fun insertCollection(collection: Collection){
        collectionRepository.insert(collection)
    }

    fun delete(collection: Collection){
        collectionRepository.delete(collection)
    }

    fun deleteSame(){
        collectionRepository.deleteSame()
    }
}