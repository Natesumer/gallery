package com.example.gallery.modul.repository

import android.content.Context
import androidx.lifecycle.LiveData
import com.example.gallery.DatabaseSingleton
import com.example.gallery.modul.dao.CollectionDao
import com.example.gallery.modul.entity.Collection
import kotlin.concurrent.thread

class CollectionRepository(context: Context) {

    private val allCollectionLiveData:LiveData<List<Collection>>
    private val collectionDao:CollectionDao

    init {
        collectionDao=DatabaseSingleton.getDatabase(context.applicationContext).getCollectionDao()
        allCollectionLiveData=collectionDao.getAllCollectionLive()
    }

    fun getAllCollection()=allCollectionLiveData

    fun insert(collection: Collection){
        thread {
            collectionDao.insert(collection)
        }
    }

    fun delete(collection: Collection){
        thread {
            collectionDao.deleteOneCollection(collection)
        }
    }

    fun deleteSame(){
        thread {
                collectionDao.deleteDuplicates()
        }
    }

}