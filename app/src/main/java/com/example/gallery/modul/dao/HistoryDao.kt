package com.example.gallery.modul.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.gallery.modul.entity.History

@Dao
interface HistoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(history: History)

    @Delete
    fun deleteOneHistory(history: History)

    @Query("DELETE FROM HISTORY")
    fun deleteAllHistory()

    @Query("SELECT * FROM HISTORY ORDER BY ID DESC")
    fun getAllHistoryLive():LiveData<List<History>>

    @Query("DELETE FROM HISTORY WHERE id NOT IN (SELECT MIN(id) FROM HISTORY GROUP BY record)")
    fun deleteDuplicates()
}