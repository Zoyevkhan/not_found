package com.tv9news.home.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.app.tv_nine.login.adapters.PreferenceCommonAdapter
import com.bumptech.glide.Glide
import com.tv9news.databinding.ItemBookmarkBinding
import com.tv9news.home.activities.MainActivity
import com.tv9news.login.activities.LoginActivity
import com.tv9news.room.dao.articles.LocalArticle

class BookMarkAdapter(val context : Context,
                      val bookmarkList : MutableList<LocalArticle>,
                      val callback : (String,String)->Unit
) : RecyclerView.Adapter<BookMarkAdapter.BookmarkViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        BookMarkAdapter.BookmarkViewHolder(
            DataBindingUtil.inflate<ItemBookmarkBinding>(
                LayoutInflater.from(parent.context),
                com.tv9news.R.layout.item_bookmark,
                parent,
                false
            )
        )

    override fun onBindViewHolder(holder: BookmarkViewHolder, position: Int) {
        with(holder.binding){
            root.setOnClickListener {
                callback.invoke(bookmarkList.get(position).articleID,bookmarkList[position].data.get(0).details.article_type)
            }
            titleTv.text = bookmarkList[position].data.get(0).details.article_description
            Glide.with(context)
                .load(bookmarkList[position].data.get(0).details.article_image)
                .into(imageView)
        }

    }

    fun updateUI(list : MutableList<LocalArticle>){
        this.bookmarkList.clear()
        this.bookmarkList.addAll(list)
        notifyDataSetChanged()
    }

    override fun getItemCount() = bookmarkList.size
    class BookmarkViewHolder(val binding : ItemBookmarkBinding ) : RecyclerView.ViewHolder(binding.root)
}