package com.example.gallery.modul.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Collection (
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,            //主键
    val webformatURL:String,    //低分辨率的url
    val largeImageURL:String,    //高分辨率的url
    val views:Int,              //浏览数量
    val user:String,            //发布者昵称
    val userImageURL:String     //发布者头像
    )