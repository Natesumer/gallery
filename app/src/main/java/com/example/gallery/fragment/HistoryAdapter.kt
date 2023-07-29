package com.example.gallery.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.gallery.R
import com.example.gallery.modul.entity.History

class HistoryAdapter(
    private val historyList: List<History>,
    private val onItemLongClickListener: OnItemLongClickListener
) : RecyclerView.Adapter<HistoryAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        init {
            itemView.setOnLongClickListener {
                onItemLongClickListener.onItemLongClick(bindingAdapterPosition)
                true
            }
        }

        val history: TextView = view.findViewById(R.id.history)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.history_cell, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val history = historyList[position].record
        holder.history.text = history
        holder.itemView.setOnClickListener {
            val bundle= Bundle()
            bundle.apply {
                putString("key",history)
                holder.itemView.findNavController().navigate(R.id.action_searchFragment_to_resultFragment,bundle)
            }
        }
    }

    override fun getItemCount() = historyList.size

    interface OnItemLongClickListener {
        fun onItemLongClick(position: Int)
    }
}