package com.example.gallery.modul

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface defaultRequest {
    @GET("?key=37130449-60ed8d2c4c6351c8daa0ff662&")
    //这里接口的两个参数分别是图片的类型和一次返回的最大图片数量
    fun getPicture(@Query("q") param:String,@Query("per_page") num:Int):Call<Pixabay>


}