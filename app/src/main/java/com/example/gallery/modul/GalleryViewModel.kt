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
import kotlin.math.ceil
import kotlin.random.Random

//还可以继续加载
const val DATA_STATUS_CAN_LOAD_MORE = 0
//已经全部加载完毕
const val DATA_STATUS_NO_MORE = 1
//网络故障，请求失败
const val DATA_STATUS_NETWORK_ERROR = 2

//这里的是我们采用的是ViewModel来存储数据的
//优点是可以观察数据的变化进而对UI进行改变
class GalleryViewModel(application: Application) : AndroidViewModel(application) {

    //各个变量对象
    //图片网络请求的标签
    private val keyWords = arrayOf(
        "backgrounds", "fashion", "nature", "science",
        "education", "feelings", "health", "people",
        "religion", "places", "animals", "industry",
        "computer", "food", "sports", "transportation",
        "travel", "buildings", "business", "music"
    )
    private var num: Int = Random.nextInt(0, 20)

    //使用单例方式创建的Retrofit对象
    private val service = getRetrofit().create(DefaultRequest::class.java)

    //创建一个可观察对象，用来表示请求数据的状态
    private val _dataStatusLiveData = MutableLiveData<Int>()
    //存取格式和下面一样
    val dataStatusLiveData: LiveData<Int>
        get() = _dataStatusLiveData

    //内部对象存放的数据，不可被外部访问，但可以在内部进行修改
    private var _photoListLiveData = MutableLiveData<List<Pixbay>>()

    //对外开放的数据，只能被外部访问，不能被外部修改
    val photoListLiveData: LiveData<List<Pixbay>>
        get() = _photoListLiveData

    //这样写的好处是我们不能对photoListLiveData进行赋值，只能重新进行读取
    //这两个参数是用来返回刷新前的位置
    //这个标记位表示是否需要回到原来的位置
    var needBackBefore = false
    //这个标记位用来标记当前的位置
    var nowPosition = 0

    //这个变量记录当前的页码
    private var currentPage = 1
    //这个标记位记录总共的页数
    private var totalPage = 1
    //这个标记位用来记录此次页面的关键词
    private var currentKey = ""
    //这个标识位表示是否是一次新的刷新请求
    private var isNew = true

    //防止在下拉刷新的时候同时也请求加载更多，这样会导致请求碰撞
    //所以这个变量是为了我们在同一时间内只能有一个请求在执行
    private var isLoading = false

    //先拼接一下标签
    private fun getURL(): String {
        val temple = keyWords[num]
        num += 1
        if (num == 20) {
            num = 0
        }
        return temple
    }

    //第一次启动app的首页加载
    init {
        refreshPhoto()
    }


    //如果是一次新的刷新就执行该方法
    fun refreshPhoto() {
        //先对某一关键字的刷新进行初始化
        currentPage = 1
        totalPage = 1
        //得到本次刷新的标签
        currentKey = getURL()
        //标记为一次新的刷新
        isNew = true
        //进行网络请求的部分
        getMore()
    }


    //当需要加载更多的照片
    fun getMore() {

        //如果正在请求更多，不能进行下拉刷新
        //直接退出
        if (isLoading) return

        //如果当前页码大于总页数
        //改变加载状态，表示已经加载完毕
        //直接退出
        if (currentPage > totalPage) {
            _dataStatusLiveData.value = DATA_STATUS_NO_MORE
            return
        }

        //标记当前正在加载
        //此时如果有下拉刷新就无法进行请求
        //直接退出
        isLoading = true

        thread {
            //真正进行网络请求
            service.getPicture(currentKey, 100, currentPage).enqueue(object : Callback<Pixabay> {
                override fun onResponse(call: Call<Pixabay>, response: Response<Pixabay>) {

                    //如果返回的数组是空的
                    //说明没有我们所需要的关键字内容的图片
                    if (response.body()!!.totalHits == 0) {
                        Toast.makeText(
                            getApplication(),
                            "There are no images that you want",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {

                        //如果返回的内容不为空
                        with(response.body()!!) {

                            //我们先计算出总的页数
                            totalPage = ceil(totalHits.toDouble() / 100).toInt()

                            //然后进行判断是不是一次新的刷新请求
                            if (isNew) {

                                //如果是一次新的请求刷新，就直接将新的内容覆盖先前的数据就好了
                                _photoListLiveData.value = hits.toList()
                            } else {

                                //如果不是一次新的刷新请求
                                //而是一次追加请求
                                //我们就需要先将归位标记位置位true
                                needBackBefore = true
                                Log.d("aaa", "onResponse: $needBackBefore")

                                //然后记录当前的位置
                                nowPosition = _photoListLiveData.value!!.size
                                Log.d("aaa", "onResponse: $nowPosition")
                                //然后将新请求的数据追加到先前数据的后面
                                _photoListLiveData.value =
                                    arrayListOf(_photoListLiveData.value!!, hits.toList()).flatten()

                            }
                        }

                        //然后设置数据状态，还可以继续加载
                        _dataStatusLiveData.value = DATA_STATUS_CAN_LOAD_MORE
                        //加载完毕，归位正在加载的标记位
                        isLoading = false
                        //归位是不是新请求的标记位
                        isNew = false
                        //将当前的页面加一
                        currentPage++
                        //如果当前的页面大于总的页面
                        //就将数据状态位设置为加载完毕
                        if (currentPage > totalPage) _dataStatusLiveData.value = DATA_STATUS_NO_MORE

                    }
                }

                override fun onFailure(call: Call<Pixabay>, t: Throwable) {

                    //如果加载失败
                    //我们就将数据状态位设置位网络故障
                    _dataStatusLiveData.value = DATA_STATUS_NETWORK_ERROR
                    //并且归位正在加载的标记位
                    isLoading = false
                }
            })
        }
    }

}