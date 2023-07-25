package com.example.gallery.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.core.view.MenuProvider
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.gallery.R
import com.example.gallery.modul.GalleryViewModel

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [GalleryFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
//这个fragment的主要功能就是：
//采用recyclerView来展示图片流的
class GalleryFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private val TAG:String="zxr"

    //所有需要的控件
    private lateinit var recyclerView:RecyclerView
    private lateinit var adapter: GalleryAdapter
    private lateinit var galleryViewModel:GalleryViewModel
    private lateinit var swipeRefresh: SwipeRefreshLayout



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    @SuppressLint("ResourceAsColor")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view=inflater.inflate(R.layout.fragment_gallery, container, false)
        //ViewModel的创建
        galleryViewModel= ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory(requireActivity().application))[GalleryViewModel::class.java]

        //SwipeRefreshLayout的实例创建
        swipeRefresh=view.findViewById<SwipeRefreshLayout?>(R.id.swipeLayoutGallery).apply {
            setColorSchemeColors(R.color.purple_700)
        }
        //recyclerView的相关配置
        recyclerView=view.findViewById(R.id.recyclerView)
        val layoutManager=StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL)
        recyclerView.layoutManager=layoutManager

        //数据观察者，将新的数据加载到recyclerView当中
        galleryViewModel.photoListLiveData.observe(viewLifecycleOwner){
            adapter=GalleryAdapter(it)
            recyclerView.adapter=adapter
            //如果是通过shimmerLayout进行刷新的话，需要在刷新完成后关闭它
            swipeRefresh.isRefreshing=false
        }

        //设置SwipeRefreshLayout监听器，监听刷新请求
        swipeRefresh.setOnRefreshListener{
            Log.d(TAG, "onCreateView: refresh is going")
            //调用刷新的方法
            galleryViewModel.refreshPhoto()
            Log.d(TAG, "onCreateView: refresh is over")
        }
        return view
    }


    //由于不是所以人都知道下拉刷新。所以为了人性化考虑，添加一个按钮刷新的功能
    //这里是对菜单刷新的完成
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        //考虑到重写成员方法onCreateOptionsMenu和onMenuItemSelected已弃用
        //这里使用的另一种方法
        val menuHost=requireActivity()
        menuHost.addMenuProvider(object :MenuProvider{
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.menu,menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when(menuItem.itemId){
                    R.id.refresh_button -> {
                        swipeRefresh.isRefreshing=true
                        Handler(Looper.getMainLooper()).postDelayed({
                            galleryViewModel.refreshPhoto()
                        },700)
                        true
                    }
                    R.id.search_button->{
                        findNavController().navigate(R.id.action_galleryFragment_to_searchFragment)
                        true
                    }
                    R.id.my_collection->{
                        findNavController().navigate(R.id.action_galleryFragment_to_collectionFragment)
                        true
                    }
                    else->false
                }
            }
        },viewLifecycleOwner,Lifecycle.State.RESUMED)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment GalleryFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            GalleryFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}