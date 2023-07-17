package com.example.gallery

import android.content.Context
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class Singleton private constructor(){
    companion object{
        //创建Retrofit的单例
        private var retrofitINSTANCE:Retrofit?=null
        fun getInstance(): Retrofit? {
            retrofitINSTANCE?:
            synchronized(Singleton::class.java){
                //如果对象已经被创建，就无需同步，这就是所谓的避免不必要的同步
                //只有当对象尚未创建时，才需要进行同步并创建对象。
                //相比线程安全的懒汉模式，这里减少了同步的使用次数，提高了性能
                retrofitINSTANCE=Retrofit.Builder()
                    .baseUrl("https://pixabay.com/api/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()

            }
            return retrofitINSTANCE
        }

    }

}