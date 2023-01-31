package com.tv9news.home.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.tv9news.R
import com.tv9news.models.masterHit.TopSubCategory

class TopSubcateogyItemAdapter(val list: List<TopSubCategory>, val context: Context) :
    RecyclerView.Adapter<TopSubcateogyItemAdapter.ViewHolder>() {

    inner class SimpleViewHolder(v: View) : ViewHolder(v) {
        val subCatLayout = v.findViewById<ConstraintLayout>(R.id.subCatLayout)
        val titleTv = v.findViewById<TextView>(R.id.titleTv)

        override fun getBind(position: Int, viewHolder: ViewHolder) {
            val data = list.get(position)
            titleTv.text = data.category_title

        }
    }

    abstract class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        abstract fun getBind(position: Int, viewHolder: ViewHolder)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(context)
                .inflate(R.layout.top_subcategory_item_view, parent, false);
        return SimpleViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.getBind(position, viewHolder = holder)
    }

    override fun getItemCount(): Int {
        return list.size
    }
}