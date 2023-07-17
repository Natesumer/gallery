package com.example.gallery.modul

import android.os.Parcelable

//这是一个数据类，存放从API访问得来的数据
data class Pixabay (
    val totalHits:Int,          //可通过 API 访问的图像数。默认情况下，API 限制为每个查询最多返回 500 张图像。
    val hits:Array<Photo>,      //Photo代表的是一个具体的图片的类
    val total: Int              //此次申请符合条件的图片总个数
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Pixabay

        if (totalHits != other.totalHits) return false
        if (!hits.contentEquals(other.hits)) return false
        if (total != other.total) return false

        return true
    }

    override fun hashCode(): Int {
        var result = totalHits
        result = 31 * result + hits.contentHashCode()
        result = 31 * result + total
        return result
    }
}


data class Photo(
    val webformatURL:String,    //低分辨率的url
    val id:Int,                 //图片的id
    val largeImageURL:String    //高分辨率的url
)