package com.example.gallery.fragment

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.gallery.R
import com.example.gallery.modul.HistoryViewModel
import com.example.gallery.modul.entity.History

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [SearchFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SearchFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private val TAG:String="lkj"

    private lateinit var historyViewModel: HistoryViewModel
    private lateinit var searchView: SearchView
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: HistoryAdapter
    private lateinit var textView: TextView

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
        return inflater.inflate(R.layout.fragment_search, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        historyViewModel=ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory(requireActivity().application))[HistoryViewModel::class.java]
        searchView=view.findViewById(R.id.searchView)
        recyclerView=view.findViewById(R.id.history_recyclerView)
        textView=view.findViewById(R.id.delete_history)

        val layoutManager=LinearLayoutManager(view.context)
        recyclerView.layoutManager=layoutManager


        historyViewModel.get().observe(viewLifecycleOwner){
            adapter= HistoryAdapter(it)
            adapter.setItemClickListener(object : HistoryAdapter.OnItemLongClickListener {
                override fun onItemLongClick(position: Int) {

                    AlertDialog.Builder(context).apply {
                        setTitle("tip:")
                        setMessage("\t\tDo you want to delete this record?")
                        setCancelable(false)
                        setPositiveButton("Yes"){ dialog, which ->
                            val s= historyViewModel.get().value!![position].id
                            Log.d(TAG, "onItemLongClick: id is $s")
                            val history=History(id =s,"")
                            historyViewModel.deleteOneHistory(history)
                        }
                        setNegativeButton("Cancel"){ dialog, which ->

                        }
                        show()
                    }
                }
            })
            recyclerView.adapter=adapter
        }

        searchView.setOnQueryTextListener(object :SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query != null) {
                    val bundle=Bundle()
                    bundle.apply {
                        putString("key",query)
                        Log.d(TAG, "onQueryTextSubmit: the key is $query")
                    }
                    if(  findNavController().currentDestination?.id == R.id.searchFragment){
                        val history=History(record = query)
                        historyViewModel.insertHistory(history)
                        historyViewModel.deleteSame()
                        findNavController().navigate(R.id.action_searchFragment_to_resultFragment,bundle)
                    }

                }
                else {
                    Toast.makeText(requireContext(),"please enter the right key",Toast.LENGTH_SHORT).show()
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }

        })

        textView.setOnClickListener {
            historyViewModel.delete()
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment SearchFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            SearchFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}