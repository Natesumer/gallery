package com.example.gallery.fragment

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.core.view.MenuProvider
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
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
class GalleryFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private val TAG:String="zxr"

    private lateinit var recyclerView:RecyclerView
    private lateinit var adapter: GalleryAdapter
    private lateinit var galleryViewModel:GalleryViewModel
    private lateinit var shimmerLayoutPhoto: SwipeRefreshLayout



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

        galleryViewModel= ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory(requireActivity().application))[GalleryViewModel::class.java]

        var view=inflater.inflate(R.layout.fragment_gallery, container, false)
        val layoutManager=StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL)

        shimmerLayoutPhoto=view.findViewById(R.id.swipeLayoutGallery)
        recyclerView=view.findViewById(R.id.recyclerView)
        recyclerView.layoutManager=layoutManager

        galleryViewModel.photoListLiveData.observe(viewLifecycleOwner){
            adapter=GalleryAdapter(it)
            adapter.setOnItemClickListener(object :GalleryAdapter.OnItemClickListener{
                override fun onItemClick(position: Int) {

                }
            })
            recyclerView.adapter=adapter
            shimmerLayoutPhoto.isRefreshing=false
        }

        shimmerLayoutPhoto.setOnRefreshListener{
            Log.d(TAG, "onCreateView: refresh is going")
            galleryViewModel.refreshPhoto()
            Log.d(TAG, "onCreateView: refresh is over")
        }
        return view
    }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val menuHost=requireActivity()
        menuHost.addMenuProvider(object :MenuProvider{
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.menu,menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when(menuItem.itemId){
                    R.id.swipIndicator -> {
                        shimmerLayoutPhoto.isRefreshing=true
                        Handler(Looper.getMainLooper()).postDelayed({
                            galleryViewModel.refreshPhoto()
                        },700)
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