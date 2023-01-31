package com.tv9news.home.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.imageview.ShapeableImageView
import com.tv9news.R
import com.tv9news.home.interfaces.HomeItemClick
import com.tv9news.models.home.Articles
import com.tv9news.utils.helpers.Helper

class ViralPhotoGalleryItemAdapter(
    val list: List<Articles>,
    val context: Context,
    val homeItemClick: HomeItemClick
) :
    RecyclerView.Adapter<ViralPhotoGalleryItemAdapter.ViewHolder>() {

    inner class SimpleViewHolder(v: View) : ViewHolder(v) {
        val titleTv = v.findViewById<TextView>(R.id.titleTv)
        val categoryTv = v.findViewById<TextView>(R.id.categoryTv)
        val videoDurationTv = v.findViewById<TextView>(R.id.videoDurationTv)
        val mainImageView = v.findViewById<ShapeableImageView>(R.id.mainImageView)
        val itemContainer = v.findViewById<ConstraintLayout>(R.id.itemContainer)

        override fun getBind(position: Int, viewHolder: ViewHolder) {
            val data = list.get(position)
            titleTv.text = data.article_title
            if (data.article_image != null && !data.article_image.equals("null")) {
                Helper.loadImage(mainImageView, data.article_image!!, "")
            }
            titleTv.text = data.article_title
//                videoDurationTv.text = item.

            itemContainer.setOnClickListener {
                if (null != homeItemClick) {
                    homeItemClick.itemHomeClick(data)
                }
            }
        }
    }

    abstract class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        abstract fun getBind(position: Int, viewHolder: ViewHolder)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(context)
                .inflate(R.layout.viral_photo_gallery_item_child, parent, false);
        return SimpleViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.getBind(position, viewHolder = holder)
    }

    override fun getItemCount(): Int {
        return list.size
    }
}
