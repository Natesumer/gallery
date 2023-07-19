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
import kotlin.random.Random

//这里的是我们采用的是ViewModel来存储数据的
//优点是可以观察数据的变化进而对UI进行改变
class GalleryViewModel(application: Application) : AndroidViewModel(application) {

    //各个变量对象
    private val TAG:String="zxr"
    //图片网络请求的标签
    private val keyWords = arrayOf("backgrounds","fashion","nature","science",
        "education","feelings","health","people",
        "religion","places","animals","industry",
        "computer","food","sports","transportation",
        "travel","buildings","business","music")
    private var num:Int= Random.nextInt(0, 20)
    //使用单例方式创建的Retrofit对象
    private val service=getInstance()!!. create(defaultRequest::class.java)
    //内部对象存放的数据，不可被外部访问，但可以在内部进行修改
    private var _photoListLiveData=MutableLiveData<List<Photo>>()
    //对外开放的数据，只能被外部访问，不能被外部修改
    val photoListLiveData:LiveData<List<Photo>>
        get() =_photoListLiveData
    //这样写的好处是我们不能对photoListLiveData进行赋值，只能重新进行读取


    //其一次启动app的首页加载
    init {
        Log.d(TAG, "GalleryViewModel Constructor '_photoListLiveData' is : ${_photoListLiveData.value}")
        //如果没有数据就进行网络请求
        if (_photoListLiveData.value==null){
            thread {
                Log.d(TAG, "GalleryViewModel Constructor is going ")
                //从上面随机选取标签进行网络请求
                service.getPicture(getURL(),100).enqueue(object :Callback<Pixabay>{
                    override fun onResponse(call: Call<Pixabay>, response: Response<Pixabay>) {
                        if (response.body()==null){
                            Log.d(TAG, "GalleryViewModel Constructor onResponse: response is null")
                        }else if (response.body()!!.totalHits==0){
                            Toast.makeText(getApplication(), "GalleryViewModel Constructor There are no images that match the filter", Toast.LENGTH_SHORT).show()
                            Log.d(TAG, "GalleryViewModel Constructor onResponse: no images that match the filter in this response")
                        }else{
                            //由于MutableLiveData类型并不是线程安全的类型，所以这里在非UI线程中进行网络请求后就需要使用postValue对数据进行修改
                            _photoListLiveData.postValue(response.body()!!.hits.toList())
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

    //当用户主动刷新图片
    fun refreshPhoto(){
        Log.d(TAG, "refreshPhoto: refresh is going")
        thread {
            //操作内容基本同上
            //getURL()->key
            service.getPicture(getURL(),100).enqueue(object :Callback<Pixabay>{
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

    private fun getURL():String{
        var temple=keyWords[num]
        num += 1
        if (num==20){
            num=0
        }
        return temple
    }

}