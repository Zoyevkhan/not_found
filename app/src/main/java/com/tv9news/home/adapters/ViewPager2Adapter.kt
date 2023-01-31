package com.tv9news.home.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.tv9news.models.masterHit.Menus


class ViewPager2Adapter(fragmentManager: FragmentManager, lifecycle: Lifecycle) :
    FragmentStateAdapter(fragmentManager, lifecycle) {
    private val fragmentList: ArrayList<Fragment> = ArrayList()
    private lateinit var top_menu: List<Menus>
    override fun createFragment(position: Int): Fragment {
        return fragmentList[position]
    }

    fun addFragment(fragment: Fragment, topMenu: List<Menus>) {
        fragmentList.add(fragment)
        top_menu = topMenu
    }

    override fun getItemCount(): Int {
        return fragmentList.size
    }

    fun getPageTitle(position: Int): CharSequence? {
        return top_menu.get(position).title
    }
}