package com.example.gallery.modul

import android.app.Application
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.gallery.RetrofitSingleton.Companion.getRetrofit
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.concurrent.thread


class SearchViewModel(application: Application):AndroidViewModel(application) {

    private val TAG:String="zxr"

    private val service= getRetrofit().create(DefaultRequest::class.java)
    private var _resultList=MutableLiveData<List<Pixbay>>()
    val resultList:LiveData<List<Pixbay>>
        get() = _resultList

    fun searchPhoto(key:String){
        Log.d(TAG, "searchPhoto: is going")
        if (resultList.value==null){
            thread {
                Log.d(TAG, "searchPhoto: is into thread and key is $key")
                service.getPicture(key,100).enqueue(object :Callback<Pixabay>{
                    override fun onResponse(call: Call<Pixabay>, response: Response<Pixabay>) {
                        Log.d(TAG, "searchPhoto onResponse: is going")
                        if (response.body()==null){
                            Log.d(TAG, "searchPhoto onResponse: Sorry, we have the image you are searching for, try it with a different keyword.")
                            Toast.makeText(getApplication(),"Sorry, we have the image you are searching for, try it with a different keyword.",Toast.LENGTH_SHORT).show()
                        }else{
                            Log.d(TAG, "searchPhoto onResponse: is ${response.body()}")
                            _resultList.postValue(response.body()!!.hits.toList())
                            Log.d(TAG, "searchOnResponse: ${resultList.value}")
                        }
                    }

                    override fun onFailure(call: Call<Pixabay>, t: Throwable) {
                        Log.d(TAG, "onFailure: this request is fail")
                        Toast.makeText(getApplication(),"There seems to be something wrong with your network.",Toast.LENGTH_SHORT).show()
                    }
                })

            }
        }
    }
}