package com.example.gallery.modul

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface defaultRequest {
    @GET("?key=37130449-60ed8d2c4c6351c8daa0ff662&")
    fun getPicture(@Query("q") param:String,@Query("per_page") num:Int):Call<Pixabay>
}