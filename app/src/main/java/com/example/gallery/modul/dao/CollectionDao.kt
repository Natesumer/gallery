package com.example.gallery.modul.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.gallery.modul.entity.Collection

@Dao
interface CollectionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(collection:Collection)

    @Delete
    fun deleteOneCollection(collection:Collection)

    @Query("SELECT * FROM COLLECTION ORDER BY ID DESC")
    fun getAllCollectionLive(): LiveData<List<Collection>>

    @Query("DELETE FROM COLLECTION WHERE id NOT IN (SELECT MIN(id) FROM COLLECTION GROUP BY webformatURL, largeImageURL, views, user, userImageURL)")
    fun deleteDuplicates()

}