package com.example.gallery.modul

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.Url

interface DefaultRequest {
    @GET("?key=37130449-60ed8d2c4c6351c8daa0ff662&")
    //这里接口的两个参数分别是图片的类型和一次返回的最大图片数量
    fun getPicture(@Query("q") param:String,@Query("per_page") num:Int,@Query("page") n: Int):Call<Pixabay>

    @GET("")
    //这个请求是我们对图片本体的请求，返回的就是二进制文件，用来让我们下载
    fun downLoad(@Url url:String):Call<ResponseBody>
}