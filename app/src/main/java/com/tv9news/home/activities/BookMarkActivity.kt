package com.tv9news.home.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.databinding.ObservableChar
import androidx.lifecycle.Observer
import androidx.lifecycle.VIEW_MODEL_STORE_OWNER_KEY
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.tv9news.databinding.ActivityBookMarkBinding
import com.tv9news.details.activities.DetailsActivity
import com.tv9news.home.adapters.BookMarkAdapter
import com.tv9news.models.home.Articles
import com.tv9news.room.LocalRoomDatabase
import com.tv9news.room.dao.articles.LocalArticle
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class BookMarkActivity : AppCompatActivity() {
    private lateinit var bookmarkAdapter: BookMarkAdapter
    lateinit var binding : ActivityBookMarkBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,com.tv9news.R.layout.activity_book_mark)
        inilizeUI()
        setObserver()
    }

    private fun inilizeUI() {
        with(binding.bookmarkRecyclerview){
            bookmarkAdapter = BookMarkAdapter(this@BookMarkActivity, mutableListOf()
            ){ articleId, articleType ->
                openDetailScreen(articleId,articleType)

            }
            adapter=bookmarkAdapter
            layoutManager = LinearLayoutManager(this@BookMarkActivity)
        }
        binding.backImg.setOnClickListener {
            finish()
        }
    }

    private fun setObserver() {
        lifecycleScope.launch {
            LocalRoomDatabase.getDatabase(this@BookMarkActivity).ArticleDao().getAllArticlesLiveData()
                .observe(this@BookMarkActivity, Observer {
                    it?.let {
                        if(it.size>0){
                            showBookmarkEmptyScreen(false)
                            bookmarkAdapter.updateUI(it.toMutableList())
                        }
                    }?: kotlin.run {
                        showBookmarkEmptyScreen(true)
                    }
                })
        }
    }
    
    fun openDetailScreen(articleID:String ,articleType:String) {
        val intent = Intent(this, DetailsActivity::class.java)
            .putExtra("articleId", articleID)
            .putExtra("articleType", articleType)
        startActivity(intent)
    }

    fun showBookmarkEmptyScreen(isShow : Boolean){
        with(binding){
          bookmarkRecyclerview.showvisibility(!isShow)
          emptyLL.showvisibility(isShow)
            clearALL.showvisibility(!isShow)
        }

    }

    fun View.showvisibility(isShow : Boolean){
        this.visibility = if(isShow) View.VISIBLE else View.GONE
    }
}