package com.example.gallery.fragment

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView.OnItemClickListener
import android.widget.ImageView
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.example.gallery.R
import com.example.gallery.modul.Photo
import io.supercharge.shimmerlayout.ShimmerLayout

class GalleryAdapter(private val photoList:List<Photo>) :RecyclerView.Adapter<GalleryAdapter.ViewHolder>(){
    inner class ViewHolder(view:View):RecyclerView.ViewHolder(view){
        val photoImage:ImageView=view.findViewById(R.id.imageView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view=LayoutInflater.from(parent.context)
            .inflate(R.layout.gallery_cell,parent,false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        //先获得shimmer的实例，然后对其属性进行设置
        holder.itemView.findViewById<ShimmerLayout>(R.id.shimmerGellLayout).apply {
            setShimmerColor(0x55FFFFFF)
            setShimmerAngle(0)
            startShimmerAnimation()
        }
        //拿到图片的数据
        val photo=photoList[position]
        //使用Glide放置图片
        Glide.with(holder.itemView)
            .load(photo.webformatURL)
            .placeholder(R.drawable.ic_grayphoto_24)
            .listener(object :RequestListener<Drawable>{        //这里设置一个监听器，用于监测是否放置成功
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable>?,
                    isFirstResource: Boolean
                ): Boolean {
                    return false
                }
                //无论是否放置成功监听器的值必须返回false
                override fun onResourceReady(
                    resource: Drawable?,
                    model: Any?,
                    target: Target<Drawable>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean
                ): Boolean {
                    return false.also {
                        //放置成功后关闭shimmer
                        holder.itemView.findViewById<ShimmerLayout>(R.id.shimmerGellLayout)
                            ?.stopShimmerAnimation()
                    }
                }
            })
            .into(holder.itemView.findViewById(R.id.imageView))

        holder.itemView.setOnClickListener {
            val bundle=Bundle()
            bundle.apply {
                putString("largeImageURL",photo.largeImageURL)
                holder.itemView.findNavController().navigate(R.id.action_galleryFragment_to_photoFragment,bundle)
            }
        }
    }

    override fun getItemCount(): Int =photoList.size

    interface OnItemClickListener{
        fun onItemClick(position: Int)
    }

    private var mOnItemClickListener:OnItemClickListener?=null

    public fun setOnItemClickListener(mOnItemClickListener:OnItemClickListener?){
        this.mOnItemClickListener =mOnItemClickListener
    }
}