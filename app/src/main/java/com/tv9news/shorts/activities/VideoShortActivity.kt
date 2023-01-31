package com.tv9news.shorts.activities

import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import com.tv9news.R
import com.tv9news.models.home.Articles
import com.tv9news.shorts.adapters.ViewPagerAdapter


class VideoShortActivity : AppCompatActivity() {
    private lateinit var viewPager2: ViewPager2
    private lateinit var closeImg: ImageView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video_short)
        viewPager2 = findViewById(R.id.viewpager)
        closeImg = findViewById(R.id.closeImg)

        val shortList: ArrayList<Articles> = intent.extras?.getParcelableArrayList<Articles>("shortList") as ArrayList<Articles>
        val position = intent.getIntExtra("position", 0)

        viewPager2.adapter = ViewPagerAdapter(shortList, this)
        viewPager2.registerOnPageChangeCallback(object : OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
            }
        })
        viewPager2.setCurrentItem(position, true)

        closeImg.setOnClickListener {
            finish()
        }
    }

    override fun onDestroy() {
        viewPager2.adapter = null
        super.onDestroy()
    }

    override fun onBackPressed() {
        finish()
    }
}