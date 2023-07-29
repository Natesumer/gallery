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
import com.example.gallery.modul.DATA_STATUS_NETWORK_ERROR
import com.example.gallery.modul.GalleryViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlin.math.log

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
class GalleryFragment : Fragment(),GalleryAdapter.OnItemClickListener {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private val TAG: String = "zxr"

    //所有需要的控件
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: GalleryAdapter
    private lateinit var galleryViewModel: GalleryViewModel
    private lateinit var swipeRefresh: SwipeRefreshLayout
    private lateinit var floatingButton:FloatingActionButton


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("qwe", "onCreate: arrived")
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }



    @SuppressLint("ResourceAsColor", "ClickableViewAccessibility")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d("qwe", "onCreateView: arrived")
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_gallery, container, false)
        //ViewModel的创建
        galleryViewModel = ViewModelProvider(
            this,
            ViewModelProvider.AndroidViewModelFactory(requireActivity().application)
        )[GalleryViewModel::class.java]

        //FloatingActionButton的实例创建
        floatingButton=view.findViewById(R.id.floating_button)
        //因为recyclerView是重最前面开始展示的
        //所以一开始我们需要让悬浮按钮被隐藏
        floatingButton.visibility=View.GONE

        //SwipeRefreshLayout的实例创建
        swipeRefresh = view.findViewById<SwipeRefreshLayout?>(R.id.swipeLayoutGallery).apply {
            setColorSchemeColors(R.color.purple_500)
        }
        swipeRefresh.isEnabled = true

        //recyclerView的相关配置
        recyclerView = view.findViewById(R.id.recyclerView)
        val layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        recyclerView.layoutManager = layoutManager

        //数据观察者，将新的数据加载到recyclerView当中
        //自动更新recyclerView中的视图内容
        galleryViewModel.photoListLiveData.observe(viewLifecycleOwner) {

            //并且将自定义的监听器传入
            adapter = GalleryAdapter(it,this)


            recyclerView.adapter = adapter

            Log.d("aaa", "onCreateView: ${galleryViewModel.needBackBefore}")
            //如果需要回到之前的位置
            if (galleryViewModel.needBackBefore) {
                //返回刷新前的原位
                Log.d("sss", "onCreateView: ${galleryViewModel.nowPosition}")
                recyclerView.scrollToPosition(galleryViewModel.nowPosition - 5)
                //归位返回位置的标记位
                galleryViewModel.needBackBefore = false
            }

            //如果是通过SwipeRefreshLayout进行刷新的话，需要在刷新完成后关闭它
            if (swipeRefresh.isRefreshing) {
                swipeRefresh.isRefreshing = false
            }

        }

        //这是一个recyclerView的滑动监听
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                //这是一个精确的监测回调
                //我们不滑动newState等于0
                //我们缓慢的滑动newState等于1
                //我们快速的滑动newState等于2
                //不过我暂时永不到
                Log.d(TAG, "onScrollStateChanged: $newState")
            }

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                //这是一个粗略的滑动监听，x，y分别为水平和竖直滑动的距离
                super.onScrolled(recyclerView, dx, dy)

                //因为是双列的recyclerView
                //所以这里我们拿出他的layoutManager将其转型成StaggeredGridLayoutManager
                //用来存放最上面，或者最下面的两个item的position
                val intArray = IntArray(2)
                val mLayoutManager = recyclerView.layoutManager as StaggeredGridLayoutManager

                //当第一个元素即将要被可见时
                mLayoutManager.findFirstVisibleItemPositions(intArray)

                //当最上面的item的position为0时
                //也就是第一条的item的时候
                if (intArray[0]==0){

                    //如果第一个item可见就让floatingButton不可见
                    floatingButton.visibility=View.GONE
                }else{

                    //如果第一个item不可见就让floatingButton可见
                    floatingButton.visibility=View.VISIBLE
                }

                //如果是上滑，我们就可以直接返回了
                //因为这样不会触发加载更多
                if (dy < 0) return

                //如果是下滑
                //当最后一个元素即将要被可见时
//                mLayoutManager.findLastVisibleItemPositions(intArray)
//                if (intArray[0]==adapter.itemCount-1){
//
//                    //我们就要进行数据追加
//                    galleryViewModel.getMore()
//                }

                //这里这样写的原因是为了我们方便测试，检查加载前后的位置
                mLayoutManager.findLastCompletelyVisibleItemPositions(intArray)
                if (intArray[0] == adapter.itemCount - 1) {
                    Handler(Looper.getMainLooper()).postDelayed({
                        galleryViewModel.getMore()

                    }, 3000)
                }
            }
        })

        //这个观察者是在观察我们我们数据的状态
        galleryViewModel.dataStatusLiveData.observe(viewLifecycleOwner) {

            //先将数据的状态传递到adapter
            adapter.footerViewStatus = it

            //提醒观察者最后一个数据有变化，做一次异步回调
            adapter.notifyItemChanged(adapter.itemCount-1)

            //如果数据状态为网络错误
            if (it == DATA_STATUS_NETWORK_ERROR) {
                //如果swipeFresh还在刷新，就关闭它
                if (swipeRefresh.isRefreshing) {
                    swipeRefresh.isRefreshing = false
                }
            }
        }

        //设置SwipeRefreshLayout监听器，监听刷新请求
        swipeRefresh.setOnRefreshListener {

            //刷新，请求新的图片
            galleryViewModel.refreshPhoto()
        }


        //悬浮按钮的监听器
        floatingButton.setOnClickListener {

            //点击就直接回到recyclerView的最顶端
            recyclerView.smoothScrollToPosition(0)
            Log.d("asd", "onCreateView setOnClickListener: you click the button")
        }

        return view
    }







    //由于不是所以人都知道下拉刷新。所以为了人性化考虑，添加一个按钮刷新的功能
    //这里是对菜单刷新的完成
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Log.d("qwe", "onViewCreated: arrived")
        //考虑到重写成员方法onCreateOptionsMenu和onMenuItemSelected已弃用
        //这里使用的另一种方法
        val menuHost = requireActivity()
        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.menu, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.refresh_button -> {
                        //这里联动一下swipeRefresh
                        swipeRefresh.isRefreshing = true
                        Handler(Looper.getMainLooper()).postDelayed({
                            galleryViewModel.refreshPhoto()
                        }, 700)
                        true
                    }
                    R.id.search_button -> {
                        findNavController().navigate(R.id.action_galleryFragment_to_searchFragment)
                        true
                    }
                    R.id.my_collection -> {
                        findNavController().navigate(R.id.action_galleryFragment_to_collectionFragment)
                        true
                    }
                    else -> false
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
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

    //防止swipeRefresh内存泄漏
    override fun onStop() {
        super.onStop()
        Log.d("qwe", "onStop: arrived")
        swipeRefresh.isRefreshing = false
        swipeRefresh.clearAnimation() //I tried with/without this
        swipeRefresh.isEnabled = false
    }

    override fun onResume() {
        super.onResume()
        Log.d("qwe", "onResume: arrived")
        swipeRefresh.isEnabled = true
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Log.d("qwe", "onDestroyView: arrived")
        swipeRefresh.isRefreshing = false
        swipeRefresh.setOnRefreshListener(null)
        swipeRefresh.clearAnimation()
        swipeRefresh.isEnabled = false
        recyclerView.adapter = null
    }

    override fun onItemClick(position: Int) {
        galleryViewModel.getMore()
    }

}