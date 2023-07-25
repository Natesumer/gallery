package com.example.gallery.fragment

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.gallery.R
import com.example.gallery.modul.entity.History

class HistoryAdapter(private val historyList: List<History>):RecyclerView.Adapter<HistoryAdapter.ViewHolder>() {

    inner class ViewHolder(view: View):RecyclerView.ViewHolder(view) {
        val history:TextView=view.findViewById(R.id.history)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view= LayoutInflater.from(parent.context)
            .inflate(R.layout.history_cell,parent,false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val history=historyList[position].record
        holder.history.text=history
        holder.history.setOnClickListener {
            mOnItemLongClickListener?.onItemLongClick(position)
        }
    }

    override fun getItemCount()=historyList.size

    private var mOnItemLongClickListener:OnItemLongClickListener? = null

    interface OnItemLongClickListener{
        fun onItemLongClick(position: Int)
    }

    fun setItemClickListener(onItemLongClickListener: OnItemLongClickListener){
        mOnItemLongClickListener=onItemLongClickListener
    }
}