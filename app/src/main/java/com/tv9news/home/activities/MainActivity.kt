package com.tv9news.home.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.tv9news.R
import com.tv9news.databinding.ActivityMainBinding
import com.tv9news.home.activities.adapters.BottomNavAdapter
import com.tv9news.home.activities.adapters.DrawerChannelItemAdapter
import com.tv9news.home.activities.adapters.DrawerItemAdapter
import com.tv9news.home.activities.fragments.HomeFragment
import com.tv9news.home.activities.interfaces.CustomNavClick
import com.tv9news.home.adapters.ViewPager2Adapter
import com.tv9news.models.masterHit.Menu
import com.tv9news.models.masterHit.Menus
import com.tv9news.models.masterHit.TopSubCategory
import com.tv9news.room.LocalRoomDatabase
import com.tv9news.shorts.activities.ShortsActivity
import com.tv9news.utils.helpers.Constants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class MainActivity : AppCompatActivity(), CustomNavClick {
    private var fabOpenClose: Animation? = null
    private var isOpen = false
    private lateinit var binding: ActivityMainBinding
    private lateinit var customNavDrawer: FrameLayout
    private lateinit var drawerControl: ImageView
    private lateinit var bottomRecyclerView: RecyclerView
    private lateinit var tabViewpager: ViewPager2
    private lateinit var myAdapter: ViewPager2Adapter
    private lateinit var tabTablayout: TabLayout
    private lateinit var menuData: Menu
    private lateinit var settingLL: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        setSupportActionBar(binding.appBarMain.toolbar)
        tabViewpager = findViewById(R.id.tab_viewpager)
        tabTablayout = findViewById(R.id.tab_tablayout)
        settingLL = findViewById(R.id.settingLL)
        customNavDrawer = findViewById(R.id.custom_nav_drawer)
        val close = findViewById<ImageView>(R.id.closeIcon)
        customNavDrawer.visibility = View.GONE
        close.setOnClickListener { customNavDrawer.visibility = View.GONE }
        drawerControl = findViewById(R.id.drawer_control)
        drawerControl.setOnClickListener {
            customNavDrawer.visibility = View.VISIBLE
        }
        getMenuDataFromRoom { menudata ->
            getMasterData(menudata)
        }
        settingLL.setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }
        findViewById<LinearLayout>(R.id.saved).setOnClickListener {
           startActivity(Intent(this,BookMarkActivity::class.java))
        }

    }

    private fun getMasterData(menu: Menu) {
        menuData = menu
        setSideMenu(menuData.left_menu.toMutableList())
        setBottomNav(menuData.bottom_menu.toMutableList())
        setChannelLeft(menuData.left_top_menu.toMutableList())

        //viewPager
        tabViewpager.setOrientation(ViewPager2.ORIENTATION_HORIZONTAL)
        myAdapter = ViewPager2Adapter(supportFragmentManager, lifecycle)

        for (topMenu in menuData.top_menu){
            myAdapter.addFragment(HomeFragment().newInstance(topMenu), menuData.top_menu)
        }
        tabViewpager.adapter =myAdapter

        TabLayoutMediator(tabTablayout, tabViewpager) { tab, position ->
            tab.text = myAdapter.getPageTitle(position)
            tabViewpager.setCurrentItem(tab.position, true)
        }.attach()

        tabViewpager.registerOnPageChangeCallback(object : OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
               // myAdapter.notifyItemChanged(position);
            }
        })
    }

    private fun setChannelLeft(list: List<Menus>) {
        val channelRecyclerView = findViewById<RecyclerView>(R.id.topRecyclerView)
        channelRecyclerView.adapter = DrawerChannelItemAdapter(list, this)
    }

    private fun setSideMenu(list: List<Menus>) {
        val drawerRecyclerView = findViewById<RecyclerView>(R.id.nav_custom_cat_list)
        val layoutManager = LinearLayoutManager(this)
        val dividerItemDecoration = DividerItemDecoration(
            drawerRecyclerView.context,
            layoutManager.orientation
        )
        drawerRecyclerView.addItemDecoration(dividerItemDecoration)
        drawerRecyclerView.adapter =
            DrawerItemAdapter(list, this)
    }

    private fun setBottomNav(list: MutableList<Menus>) {
        bottomRecyclerView = findViewById(R.id.bottomRecyclerView)
        if (list.size == 6) {
            list.add(
                3,
                Menus("", "center", "", "", "", "", "", "", "", arrayListOf<TopSubCategory>())
            )
        } else if (list.size == 4) {
            list.add(
                2,
                Menus("", "center", "", "", "", "", "", "", "", arrayListOf<TopSubCategory>())
            )
        }
        bottomRecyclerView.adapter = BottomNavAdapter(list, this, this)
    }

    override fun getCategoris(cat: String) {

    }

    override fun getBottomClick(cat: String, fab: View) {
        Log.e("getBottomClick", isOpen.toString() + "-" + cat)
        if (cat == "center") {
            animateFab(fab as ImageView)
        } else {

        }
    }

    private fun animateFab(fab: ImageView) {
        handleShortsClicks(fab)
        fabOpenClose = AnimationUtils.loadAnimation(this, R.anim.fab_open_close)
        isOpen = if (isOpen) {
            fab.startAnimation(fabOpenClose)
            fab.setImageResource(R.drawable.tv_nine_icon)
            binding.appBarMain.bottomSheet.shortLayout.visibility = View.GONE
            false
        } else {
            fab.startAnimation(fabOpenClose)
            fab.setImageResource(R.drawable.fab_close_icon)
            binding.appBarMain.bottomSheet.shortLayout.visibility = View.VISIBLE
            true
        }
    }

    private fun handleShortsClicks(fab: ImageView) {
        binding.appBarMain.bottomSheet.shortLayout.setOnClickListener {
            animateFab(fab as ImageView)
        }
        binding.appBarMain.bottomSheet.articleShorts.setOnClickListener {
            animateFab(fab as ImageView)
            startActivity(Intent(this@MainActivity, ShortsActivity::class.java).putExtra("type", Constants.ARTICLES_CONTENT_TYPE))
        }
        binding.appBarMain.bottomSheet.videoShorts.setOnClickListener {
            animateFab(fab as ImageView)
            startActivity(Intent(this@MainActivity, ShortsActivity::class.java).putExtra("type", Constants.VIDEOS_CONTENT_TYPE))
        }
        binding.appBarMain.bottomSheet.podcastShorts.setOnClickListener {
            startActivity(Intent(this@MainActivity, ShortsActivity::class.java).putExtra("type", Constants.PODCAST_CONTENT_TYPE))
            animateFab(fab as ImageView)
        }
    }
    fun getMenuDataFromRoom(
        callback : (Menu)->Unit
    ){
        lifecycleScope.launch {
            var job = lifecycleScope.async(Dispatchers.IO) {
                LocalRoomDatabase.getDatabase(this@MainActivity).languageDao().getLanguage()
            }
            withContext(Dispatchers.Main){
                    callback.invoke(job.await().get(0).menu)
            }
        }


    }
}