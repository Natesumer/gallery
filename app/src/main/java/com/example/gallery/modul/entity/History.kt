package com.example.gallery.modul.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class History(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val record:String)
