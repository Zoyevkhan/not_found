package com.tv9news.home.activities.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.tv9news.R
import com.tv9news.home.interfaces.HomeItemClick
import com.tv9news.models.home.Articles
import com.tv9news.utils.helpers.Helper

class ForYouVerticalItemAdapter(
    val list: List<Articles>,
    val context: Context,
    val homeItemClick: HomeItemClick
) :
    RecyclerView.Adapter<ForYouVerticalItemAdapter.ViewHolder>() {

    inner class SimpleViewHolder(v: View) : ViewHolder(v) {
        val titleTv = v.findViewById<TextView>(R.id.titleTv)
        val politicsTv = v.findViewById<TextView>(R.id.politicsTv)
        val timeTv = v.findViewById<TextView>(R.id.timeTv)
        val imageView = v.findViewById<ImageView>(R.id.imageView)
        val itemContainer = v.findViewById<RelativeLayout>(R.id.itemContainer)

        override fun getBind(position: Int, viewHolder: ViewHolder) {
            val data = list.get(position)
            titleTv.text = data.article_title
            politicsTv.text = data.category_name
            if (data.article_image != null && !data.article_image.equals("null")) {
                Helper.loadImage(imageView, data.article_image!!, "")
            }

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
                .inflate(R.layout.vertical_for_you_child_view, parent, false);
        return SimpleViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.getBind(position, viewHolder = holder)
    }

    override fun getItemCount(): Int {
        return list.size
    }
}