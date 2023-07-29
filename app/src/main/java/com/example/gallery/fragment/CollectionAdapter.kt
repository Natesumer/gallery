package com.example.gallery.fragment

import android.annotation.SuppressLint
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.example.gallery.R
import com.example.gallery.modul.entity.Collection
import io.supercharge.shimmerlayout.ShimmerLayout

class CollectionAdapter (private val collectionList:List<Collection>):
RecyclerView.Adapter<CollectionAdapter.ViewHolder>(){

    inner class ViewHolder(view: View):RecyclerView.ViewHolder(view){
        //这两个注释的控件不进行视图的绑定是因为在glide最后into的时候自动为我进行绑定过了
//        val photoImage:ImageView=view.findViewById(R.id.imageView)
        val photoViews: TextView =view.findViewById(R.id.photoViews)
        //        val userPhoto:ImageView=view.findViewById(R.id.user_imageView)
        val userName: TextView =view.findViewById(R.id.user_name)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view= LayoutInflater.from(parent.context)
            .inflate(R.layout.gallery_cell,parent,false)
        return ViewHolder(view)    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        //先获得shimmerLayout的实例，然后对其属性进行设置
        holder.itemView.findViewById<ShimmerLayout>(R.id.shimmerGellLayout).apply {
            //设置占位图片的颜色
            setShimmerColor(0x55FFFFFF)
            //设置闪动的角度
            setShimmerAngle(45)
            //开启闪动
            startShimmerAnimation()
        }
        //拿到此时该图片的数据
        val photo=collectionList[position]
        //使用Glide放置图片
        Glide.with(holder.itemView)
            .load(photo.webformatURL)
            //设置占位图片
            .placeholder(R.drawable.ic_grayphoto_24)
            //设置一个监听器，用于监测是否放置成功
            .listener(object : RequestListener<Drawable> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable>?,
                    isFirstResource: Boolean
                ): Boolean {
                    return false
                }
                //无论是否放置成功监听器的值必须返回false，return true图片就加载不出来了
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
                        //注意这里需要加一个对shimmerLayout的判空处理
                        //因为如果我们在图片加载完成之前从该页面离开，但是这个listener还是会执行
                        //就会遇见空指针异常，导致程序崩溃
                    }
                }
            })
            //加载到的控件的
            .into(holder.itemView.findViewById(R.id.imageView))

        holder.userName.text=photo.user
        holder.photoViews.text= ((photo.views)/10000).toString()+"W"
        Glide.with(holder.itemView)
            .load(photo.userImageURL)
            .placeholder(R.drawable.ic_grayphoto_24)
            .into(holder.itemView.findViewById(R.id.user_imageView))

        //下面这里是监听器的设置，由于这里我们不需要从Activity重获取数据
        //那我们直接采用这种最简单的监听器设置方法就好
        holder.itemView.setOnClickListener {
            //由于我们是采用Navigation的方式从一个fragment转跳到另外一个fragment当中去
            //这里数据的传输我们采用的是bundle的方式
            val bundle= Bundle()
            bundle.apply {
                putString("largeImageURL",photo.largeImageURL)
                putInt("id",photo.id)
                //转跳到图片详情的页面
                holder.itemView.findNavController().navigate(R.id.action_collectionFragment_to_photoFragment,bundle)
            }
        }
    }

    override fun getItemCount()=collectionList.size

}