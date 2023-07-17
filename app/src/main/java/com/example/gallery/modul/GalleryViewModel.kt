package com.example.gallery.modul

import android.app.Application
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.gallery.Singleton.Companion.getInstance
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.concurrent.thread

class GalleryViewModel(application: Application) : AndroidViewModel(application) {
    private val TAG:String="zxr"
    private val keyWords = arrayOf("cat","dog","car","beauty","phone","computer","flower","animal")
    private val service=getInstance()!!. create(defaultRequest::class.java)
    private var _photoListLiveData=MutableLiveData<List<Photo>>()
    val photoListLiveData:LiveData<List<Photo>>
        get() =_photoListLiveData
    //这样写的好处是我们不能对photoListLiveData进行赋值，只能重新进行读取

    init {
        Log.d(TAG, "GalleryViewModel Constructor '_photoListLiveData' is : ${_photoListLiveData.value}")
        if (_photoListLiveData.value==null){
            thread {
                Log.d(TAG, "GalleryViewModel Constructor is going ")
                service.getPicture(keyWords.random(),100).enqueue(object :Callback<Pixabay>{
                    override fun onResponse(call: Call<Pixabay>, response: Response<Pixabay>) {
                        if (response.body()==null){
                            Log.d(TAG, "GalleryViewModel Constructor onResponse: response is null")
                        }else if (response.body()!!.totalHits==0){
                            Toast.makeText(getApplication(), "GalleryViewModel Constructor There are no images that match the filter", Toast.LENGTH_SHORT).show()
                            Log.d(TAG, "GalleryViewModel Constructor onResponse: no images that match the filter in this response")
                        }else{
                            //由于MutableLiveData类型并不是线程安全的类型，所以这里在非UI线程中进行网络请求后就需要使用postValue对数据进行修改
                            _photoListLiveData.postValue(response.body()!!.hits.toList())
                            Log.d(TAG, "onResponse: ${keyWords.random()}")
                            Log.d(TAG, "GalleryViewModel Constructor onResponse: Success! Response is ${response.body()!!.hits.toList()}")
                        }
                    }
                    override fun onFailure(call: Call<Pixabay>, t: Throwable) {
                        Log.d(TAG, "GalleryViewModel Constructor onFailure: ${t.printStackTrace()}")
                    }
                })
            }

        }
    }

    fun refreshPhoto(){
        Log.d(TAG, "refreshPhoto: refresh is going")
        thread {
            service.getPicture(keyWords.random(),100).enqueue(object :Callback<Pixabay>{
                override fun onResponse(call: Call<Pixabay>, response: Response<Pixabay>) {
                    if (response.body()==null){
                        Log.d(TAG, "onResponse: response is null")
                    }else if (response.body()!!.totalHits==0){
                        Toast.makeText(getApplication(), "There are no images that match the filter", Toast.LENGTH_SHORT).show()
                        Log.d(TAG, "refreshPhoto onResponse: no images that match the filter in this response")
                    }else{
                        Log.d(TAG, "onResponse: ${keyWords.random()}")
                        _photoListLiveData.value= response.body()!!.hits.toList()
                        Log.d(TAG, "refreshPhoto onResponse: Success! Response is ${response.body()!!.hits.toList()}")
                    }
                }
                override fun onFailure(call: Call<Pixabay>, t: Throwable) {
                    Log.d(TAG, "onFailure: ${t.printStackTrace()}")
                }
            })
        }
    }
}