package com.example.gallery.fragment

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.example.gallery.R
import com.example.gallery.modul.Photo
import io.supercharge.shimmerlayout.ShimmerLayout
import uk.co.senab.photoview.PhotoView

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
    private val TAG:String="zxr"

    //主要用到的两个控件
    private lateinit var shimmerLayoutPhoto: ShimmerLayout
    private lateinit var photoView: PhotoView


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
        var view= inflater.inflate(R.layout.fragment_photo, container, false)

        //先从之前的fragment当中取出传来的数据url
        val url=arguments?.getString("largeImageURL")
        Log.d(TAG, "PhotoFragment onCreateView: $url")

        //为我们的控件创建实例
        photoView=view.findViewById(R.id.photoView)
        shimmerLayoutPhoto=view.findViewById<ShimmerLayout?>(R.id.shimmerlLayoutPhoto).apply {
            setShimmerColor(0x55FFFFFF)
            setShimmerAngle(45)
            startShimmerAnimation()
        }

        //shimmerLayout和Glide图片加载的使用与适配器当中写的差不多
        //有一个值得注意的点就是我们使用Glide最后into到的控件是PhotoView而不是写适配器时的imageview
        Glide.with(requireContext())
            .load(url)
            .placeholder(R.drawable.ic_grayphoto_24)
            .listener(object :RequestListener<Drawable>{
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

        return view
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