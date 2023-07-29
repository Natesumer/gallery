package com.example.gallery.fragment

import android.app.AlertDialog
import android.content.ContentValues
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.util.Log
import com.example.gallery.modul.entity.Collection
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.example.gallery.R
import com.example.gallery.RetrofitSingleton
import com.example.gallery.modul.CollectionViewModel
import com.example.gallery.modul.DefaultRequest
import com.example.gallery.modul.Pixbay
import io.supercharge.shimmerlayout.ShimmerLayout
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import uk.co.senab.photoview.PhotoView
import java.io.IOException
import kotlin.concurrent.thread

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [PhotoFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
//这个fragment的则主要采用了PhotoView控件来对图片进行详细的展示
class PhotoFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private val TAG: String = "zxr"

    //主要用到的两个控件
    private lateinit var shimmerLayoutPhoto: ShimmerLayout
    private lateinit var photoView: PhotoView
    private lateinit var collectionViewModel: CollectionViewModel
    private lateinit var downLoad: ImageView
    private lateinit var collection: ImageView
    private lateinit var URL: String
    private val handler=Handler(Looper.getMainLooper())
    private var id: Int? = null
    private var photo: Pixbay? = null
    //这个标志用来表示当前图片是否处于被收藏状态
    private var collectionFlag:Boolean?=false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_photo, container, false)
        collectionViewModel = ViewModelProvider(
            this,
            ViewModelProvider.AndroidViewModelFactory(requireActivity().application)
        )[CollectionViewModel::class.java]
        downLoad = view.findViewById(R.id.downLoad_image)
        collection = view.findViewById(R.id.collection_image)


        //先从之前的fragment当中取出传来的数据
        //注意看：这里取数据取的key是"photo"
        //如果"photo"的value为null说明他是从GalleryFragment或者ResultFragment转跳过来的
        photo = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requireArguments().getSerializable("photo", Pixbay::class.java)
        } else {
            requireArguments().getSerializable("photo")?.let { it as Pixbay }
        }

        //如果"photo"的value为null说明他是从CollectionFragment转跳过来的
        if (photo == null) {
            URL = requireArguments().getString("largeImageURL")!!
            //将状态改为收藏状态，并且切换图片
            collectionFlag=true
            collection.setImageResource(R.drawable.ic_baseline_star_24)
            id = requireArguments().getInt("id")
        } else {
            URL = photo!!.largeImageURL
            id = photo!!.id
        }


        //为我们的控件创建实例
        photoView = view.findViewById(R.id.photoView)
        shimmerLayoutPhoto = view.findViewById<ShimmerLayout?>(R.id.shimmerLayoutPhoto).apply {
            setShimmerColor(0x55FFFFFF)
            setShimmerAngle(45)
            startShimmerAnimation()
        }

        downLoad.setOnClickListener {
            if (Build.VERSION.SDK_INT < 29 && ContextCompat.checkSelfPermission(
                    requireContext(),
                    "android.permission.WRITE_EXTERNAL_STORAGE"
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                //申请本地文件写入的权限权限
                startLocationPermissionRequest()
            } else {
                savePhoto()
            }

        }

        //shimmerLayout和Glide图片加载的使用与适配器当中写的差不多
        //有一个值得注意的点就是我们使用Glide最后into到的控件是PhotoView而不是写适配器时的imageview
        Glide.with(requireContext())
            .load(URL)
            .placeholder(R.drawable.ic_grayphoto_24)
            .listener(object : RequestListener<Drawable> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable>?,
                    isFirstResource: Boolean
                ): Boolean {
                    return false
                }

                override fun onResourceReady(
                    resource: Drawable?,
                    model: Any?,
                    target: Target<Drawable>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean
                ): Boolean {
                    return false.also { shimmerLayoutPhoto.stopShimmerAnimation() }
                }
            })
            .into(photoView)

        collection.setOnClickListener {


            //如果photo不为null，说明我们是从GalleryFragment或者ResultFragment转跳过来的
            //在这里我们点击收藏就会导致将其保存数据到本地
            if (photo != null) {

                if (collectionFlag==true){
                    //再次点击了收藏图标
                    //意思是取消之前的收藏
                    collectionViewModel.delete(Collection(id = id!!, "", "", 0, "", ""))
                    collectionFlag=false
                    collection.setImageResource(R.drawable.ic_baseline_star_border_24)

                }else{
                    val c = Collection(
                        id = photo!!.id,
                        webformatURL = photo!!.webformatURL,
                        largeImageURL = photo!!.largeImageURL,
                        views = photo!!.views,
                        user = photo!!.user,
                        userImageURL = photo!!.userImageURL
                    )
                    Log.d("xxx", "onCreateView: ${photo!!.id}")
                    //数据保存
                    collectionViewModel.insertCollection(c)
                    collectionViewModel.deleteSame()
                    collectionFlag=true
                    //切换成已收藏的图标
                    collection.setImageResource(R.drawable.ic_baseline_star_24)
                }

            } else {

                if (collectionFlag==true){

                    //警告框弹出
                    AlertDialog.Builder(context).apply {
                        setTitle("tip:")
                        setMessage("\t\tDo you want to unbookmark this image?")
                        setCancelable(false)
                        setPositiveButton("Yes"){ dialog, which ->
                            //如果photo为null，说明我们是从CollectionFragment转跳过来的
                            //此时，点击收藏的意思是取消收藏
                            //所以我们将其移出数据库
                            collectionViewModel.delete(Collection(id = id!!, "", "", 0, "", ""))
                            //并将图片切换回未收藏的状态
                            collectionFlag=false
                            collection.setImageResource(R.drawable.ic_baseline_star_border_24)
                            findNavController().navigateUp()
                        }
                        setNegativeButton("Cancel"){ dialog, which ->

                        }
                        show()
                    }

                }
            }

        }


        return view
    }

    //下载图片方法的入口
    private fun savePhoto() {
        thread {

            //先通过网络请求得到response
            val service = RetrofitSingleton.getRetrofit().create(DefaultRequest::class.java)

            service.downLoad(URL).enqueue(object : Callback<ResponseBody> {
                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {
                    //自动生成文件名
                    val fileName = URL.substring(URL.length - 10, URL.length)
                    Log.d(TAG, "onResponse: $fileName")
                    Log.d(TAG, "onResponse: ${response.isSuccessful}")
                    if (response.isSuccessful) {
                        Log.d(TAG, "onResponse: response isSuccessful")
                        val responseBody = response.body()
                        if (responseBody != null) {
                            Log.d(TAG, "onResponse: response is not null")
                            //创建数据流对象
                            val inputStream = responseBody.byteStream()
                            //将其转化为bitmap格式
                            val bitmap = BitmapFactory.decodeStream(inputStream)
                            //将其保存到相册
                            saveImageToGallery(bitmap, fileName)
                        }else{
                            handler.post {
                                Toast.makeText(requireActivity().applicationContext, "Download failed", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }else{
                        handler.post {
                            Toast.makeText(requireActivity().applicationContext, "Download failed", Toast.LENGTH_SHORT).show()
                        }
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    Log.d(TAG, "onFailure: downLoad fail")
                    handler.post {
                        Toast.makeText(requireActivity().applicationContext, "Download failed", Toast.LENGTH_SHORT).show()
                    }
                }
            })

        }
    }

    private fun saveImageToGallery(bitmap: Bitmap, filename: String) {

        Log.d(TAG, "saveImageToGallery: is going")
        //配置
        val contentValues = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, filename)
            put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
        }
        //
        val resolver = requireContext().contentResolver
        val uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

        uri?.let {
            try {
                val outputStream = resolver.openOutputStream(uri)
                outputStream?.use { stream ->
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
                    stream.flush()

                }
            } catch (e: IOException) {
                Log.d(TAG, "saveImageToGallery: load fail")
            }
        }
        Log.d(TAG, "saveImageToGallery: downLoad success")
        handler.post {
            Toast.makeText(requireActivity().applicationContext, "Download is successful", Toast.LENGTH_SHORT).show()
        }
    }

    // Ex. android.permission.WRITE_EXTERNAL_STORAGE.
    private fun startLocationPermissionRequest() {
        //launch方法用于启动权限请求，并将权限请求的结果作为回调传递给注册的ActivityResultCallback。
        //launch方法用触发权限请求，并将权限请求的结果递给回调函数进行处理。
        requestPermissionLauncher.launch("android.permission.WRITE_EXTERNAL_STORAGE")
    }

    //registerForActivityResult方法用于注册一个用于处理活动结果的回调函数在这种情况下，
    //我们使用ActivityResultContracts.RequestPermission()作为活动结果合同，
    //该合同负责处理权限请求的结果。
    //当权限请求完成时，ActivityResultCallback会调用，并将权限是否被授的信息传递给回调函数中的isGranted参数。
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            // PERMISSION GRANTED
            savePhoto()
        } else {
            // PERMISSION NOT GRANTED
            Toast.makeText(
                requireContext(),
                "You declined the permission request, you can't save it",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment PhotoFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            PhotoFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}