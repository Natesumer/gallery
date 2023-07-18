package com.example.gallery

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

//这里使用的是Retrofit处理网络的请求
//并且在这里将其创建成单例的形式来节省自愿的利用
class Singleton private constructor(){
    companion object{
        //创建Retrofit的单例
        @Volatile
        private var retrofitINSTANCE:Retrofit?=null
        //这里采用了DLC双重判断的方式创建单例模式
        fun getInstance(): Retrofit? {
            //第一次判断
            retrofitINSTANCE?:
            synchronized(Singleton::class.java){
                //如果对象已经被创建，就无需同步，这就是所谓的避免不必要的同步
                //只有当对象尚未创建时，才需要进行同步并创建对象。
                //相比线程安全的懒汉模式，这里减少了同步的使用次数，提高了性能
                //第二次判断
                retrofitINSTANCE?:Retrofit.Builder()
                    .baseUrl("https://pixabay.com/api/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build().also { retrofitINSTANCE=it }
            }
            return retrofitINSTANCE
        }

    }

}