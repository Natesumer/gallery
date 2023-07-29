package com.example.gallery.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.gallery.R
import com.example.gallery.modul.SearchViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ResultFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ResultFragment : Fragment(), SearchAdapter.OnItemClickListener {


    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private val TAG: String = "zxr"

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: SearchAdapter
    private lateinit var floatingButton: FloatingActionButton
    private lateinit var key: String
    private val searchViewModel by lazy {
        //懒加载的形式创建searchViewModel
        ViewModelProvider(
            this,
            ViewModelProvider.AndroidViewModelFactory(requireActivity().application)
        )[SearchViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //得到搜索的关键字
        arguments?.let {
            key = it.getString("key").toString()
            searchViewModel.searchPhoto(key)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_result, container, false)

        //配置recyclerView
        recyclerView = view.findViewById(R.id.result_recyclerView)
        val layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        recyclerView.layoutManager = layoutManager

        //创建悬浮按钮实例
        floatingButton = view.findViewById(R.id.floating_buttons)


        searchViewModel.dataStatusLiveData.observe(viewLifecycleOwner) {

            adapter.footerViewStatus = it
            adapter.notifyItemChanged(adapter.itemCount - 1)
        }

        searchViewModel.resultList.observe(viewLifecycleOwner) {

            //进行搜索操作
            if (searchViewModel.resultList.value!!.isEmpty()) {
                Toast.makeText(
                    requireContext(),
                    "If you can't find the content, try to change the keywords and remember to use English",
                    Toast.LENGTH_SHORT
                ).show()
                return@observe
            }

            adapter = SearchAdapter(searchViewModel.resultList.value!!, this)

            recyclerView.adapter = adapter

            if (searchViewModel.needBackBefore) {

                recyclerView.scrollToPosition(searchViewModel.nowPosition - 5)
                searchViewModel.needBackBefore = false
            }


        }

        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val intArray = IntArray(2)
                val mLayoutManager = recyclerView.layoutManager as StaggeredGridLayoutManager

                //当第一个元素即将要被可见时
                mLayoutManager.findFirstVisibleItemPositions(intArray)

                //当最上面的item的position为0时
                //也就是第一条的item的时候
                if (intArray[0] == 0) {

                    //如果第一个item可见就让floatingButton不可见
                    floatingButton.visibility = View.GONE
                } else {

                    //如果第一个item不可见就让floatingButton可见
                    floatingButton.visibility = View.VISIBLE
                }

                //如果是上滑，我们就可以直接返回了
                //因为这样不会触发加载更多
                if (dy < 0) return

                //如果是下滑
                //当最后一个元素即将要被可见时
                mLayoutManager.findLastVisibleItemPositions(intArray)
                if (intArray[0] == adapter.itemCount - 1) {

                    //我们就要进行数据追加
                    searchViewModel.getMorePhoto()
                }
            }
        })

        floatingButton.setOnClickListener {

            //点击就直接回到recyclerView的最顶端
            recyclerView.smoothScrollToPosition(0)


        }

        return view
    }


    override fun onItemClick(position: Int) {
        searchViewModel.getMorePhoto()
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ResultFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ResultFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}