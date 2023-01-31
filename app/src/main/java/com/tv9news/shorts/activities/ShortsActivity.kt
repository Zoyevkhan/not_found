package com.tv9news.shorts.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.viewpager.widget.ViewPager
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.gson.Gson
import com.tv9news.R
import com.tv9news.details.activities.DetailsActivity
import com.tv9news.models.home.Articles
import com.tv9news.models.utils.EncryptionData
import com.tv9news.shorts.adapters.PagerAdapter
import com.tv9news.shorts.interfaces.ShortsItemClick
import com.tv9news.shorts.viewmodels.ShortsViewModel
import com.tv9news.utils.helpers.AES
import com.tv9news.utils.helpers.Constants.ARTICLES_CONTENT_TYPE
import com.tv9news.utils.helpers.Constants.ARTICLE_DETAIL
import com.tv9news.utils.helpers.Constants.PODCAST_CONTENT_TYPE
import com.tv9news.utils.helpers.Constants.PODCAST_DETAIL
import com.tv9news.utils.helpers.Constants.SHORTS
import com.tv9news.utils.helpers.Constants.VIDEOS_CONTENT_TYPE
import com.tv9news.utils.helpers.Helper
import kotlinx.coroutines.launch

class ShortsActivity : AppCompatActivity(), ShortsItemClick {

    private lateinit var viewPager: ViewPager
    private lateinit var closeImg: ImageView
    private lateinit var allTv: TextView
    private lateinit var countTv: TextView
    private lateinit var titleTv: TextView
    private lateinit var shimmerFrameLayout: ShimmerFrameLayout
    private lateinit var noDataFound: TextView
    val viewmodel: ShortsViewModel by viewModels<ShortsViewModel>()
    private var shortList: ArrayList<Articles> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shorts)
        initViews()
    }

    private fun initViews() {
        viewPager = findViewById(R.id.view_pager)
        closeImg = findViewById(R.id.closeImg)
        countTv = findViewById(R.id.countTv)
        titleTv = findViewById(R.id.titleTv)
        allTv = findViewById(R.id.allTv)
        shimmerFrameLayout = findViewById(R.id.shimmerFrameLayout)
        noDataFound = findViewById(R.id.noDataFound)
        if (intent.getStringExtra("type").equals(ARTICLES_CONTENT_TYPE)){
            titleTv.text = resources.getString(R.string.article_shorts)
        }else if (intent.getStringExtra("type").equals(VIDEOS_CONTENT_TYPE)){
            titleTv.text = resources.getString(R.string.video_shorts)
        }else{
            titleTv.text = resources.getString(R.string.podcast_shorts)
        }
        observeData()
        observeEvents()
        val encryptionData = EncryptionData(lang_id = "1", article_type = SHORTS, page = "1", content_type = intent.getStringExtra("type"))
        val encryptionDataStr = Gson().toJson(encryptionData)
        val encryptionStr: String = AES.encrypt(encryptionDataStr)
        viewmodel.callShortsApi(encryptionStr)

        viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
            }

            override fun onPageSelected(position: Int) {
                countTv.text = (position + 1).toString()
            }

            override fun onPageScrollStateChanged(state: Int) {


            }
        })
        closeImg.setOnClickListener {
            onBackPressed()
        }
    }

    private fun observeData() {
        viewmodel.shortsDataList.observe(this, Observer { detailsDataList ->
            detailsDataList?.let { detailsDataList ->
                if (detailsDataList.isNotEmpty()) {
                    shortList = detailsDataList[0].list as ArrayList<Articles>
                    allTv.text = "/"+detailsDataList[0].list.size.toString()
                    allTv.visibility = View.VISIBLE
                    countTv.visibility = View.VISIBLE
                    viewPager.adapter = PagerAdapter(this, this, intent.getStringExtra("type"), detailsDataList[0].list)
                }
            }
        })
    }

    private fun observeEvents() {
        lifecycleScope.launch {
            viewmodel.baseEvents.collect { UIEvent ->
                Helper.lifecycleScopeHandler(
                    this@ShortsActivity,
                    UIEvent,
                    shimmerFrameLayout,
                    viewPager,
                    noDataFound,
                    true
                )
            }
        }
    }

    fun openDetailScreen(data: Articles) {
        val intent = Intent(this, DetailsActivity::class.java)
            .putExtra("articleId", data.article_id)
            .putExtra("articleType", ARTICLE_DETAIL)
        Log.e("articleId", data.article_id.toString())
        startActivity(intent)
    }

    override fun shortsClick(data: Articles, type: String, position: Int) {
        if (type == ARTICLES_CONTENT_TYPE){
            openDetailScreen(data)
        }else if (type == PODCAST_CONTENT_TYPE){
            openPodCastDetailScreen(data)
        }else {
            val intent = Intent(this, VideoShortActivity::class.java)
            val bundle = Bundle()
            bundle.putParcelableArrayList("shortList", shortList)
            intent.putExtras(bundle)
            intent.putExtra("position", position)
            startActivity(intent)
        }
    }

    private fun openPodCastDetailScreen(data: Articles) {
        val intent = Intent(this, DetailsActivity::class.java)
            .putExtra("articleId", data.article_id)
            .putExtra("articleType", PODCAST_DETAIL)
        startActivity(intent)
    }

}