package com.tv9news.home.activities.fragments

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.gson.Gson
import com.tv9news.R
import com.tv9news.details.activities.DetailsActivity
import com.tv9news.details.activities.WebViewActivity
import com.tv9news.home.activities.adapters.DetailNewsAdapter
import com.tv9news.home.activities.adapters.MainNewsAdapter
import com.tv9news.home.activities.viewmodels.HomeViewModel
import com.tv9news.home.adapters.TopSubcateogryImageAdapter
import com.tv9news.home.adapters.TopSubcateogyItemAdapter
import com.tv9news.home.interfaces.HomeItemClick
import com.tv9news.models.home.Articles
import com.tv9news.models.home.SubCategory
import com.tv9news.models.masterHit.Menus
import com.tv9news.models.masterHit.TopSubCategory
import com.tv9news.models.utils.EncryptionData
import com.tv9news.utils.helpers.AES
import com.tv9news.utils.helpers.Constants
import com.tv9news.utils.helpers.Helper
import kotlinx.coroutines.launch


class HomeFragment : Fragment(), HomeItemClick {

    private lateinit var mainRecyclerView: RecyclerView
    private lateinit var topRecyclerView: RecyclerView
    val viewmodel: HomeViewModel by activityViewModels<HomeViewModel>()
    private lateinit var shimmerFrameLayout: ShimmerFrameLayout
    private lateinit var noDataFound: TextView
    private lateinit var menus: Menus

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    @SuppressLint("SuspiciousIndentation")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        menus = arguments?.getSerializable("menu") as Menus
        intiViews(view)
        observeData()
        observeEvents()
        callAPI()
    }

    fun callAPI() {
        if (menus.menu_action_element == "1") {
            val encryptionData = EncryptionData(lang_id = "1", state_id = "0", category_id = "0")
            val encryptionDataStr = Gson().toJson(encryptionData)
            val encryptionStr: String = AES.encrypt(encryptionDataStr)
            viewmodel.callHomeApi(encryptionStr, "home")
        } else {
            val subCatId = if (menus.sub_category.isNotEmpty()) {
                menus.sub_category.get(0).id
            } else {
                menus.id
            }
            val encryptionData =
                EncryptionData(lang_id = "1", state_id = "0", category_id = subCatId, page = "1")
            val encryptionDataStr = Gson().toJson(encryptionData)
            val encryptionStr: String = AES.encrypt(encryptionDataStr)
            viewmodel.callHomeApi(encryptionStr, "other")
            if (menus.sub_category.isNotEmpty()) {
                setTopRecyclerView(menus.sub_category)
            } else {
                topRecyclerView.visibility = View.GONE
            }
        }
    }

    private fun intiViews(view: View) {
        mainRecyclerView = view.findViewById(R.id.mainRecyclerView)
        topRecyclerView = view.findViewById(R.id.topRecyclerView)
        shimmerFrameLayout = view.findViewById(R.id.shimmerFrameLayout)
        noDataFound = view.findViewById(R.id.noDataFound)
        mainRecyclerView.setHasFixedSize(false)
    }

    private fun setTopRecyclerView(subCategory: List<TopSubCategory>) {
        if (subCategory[0].app_view_type == "0") {
            topRecyclerView.adapter = TopSubcateogyItemAdapter(subCategory, requireActivity())
        } else {
            topRecyclerView.adapter = TopSubcateogryImageAdapter(subCategory, requireActivity())
        }
        topRecyclerView.isNestedScrollingEnabled = false
        topRecyclerView.setHasFixedSize(true)
        topRecyclerView.visibility = View.VISIBLE
    }

    private fun observeData() {
        viewmodel.homeDataList.observe(viewLifecycleOwner, Observer { homeList ->
            homeList?.let { homeList ->
                if (!homeList.isNullOrEmpty()) {
                    mainRecyclerView.visibility = View.VISIBLE
                    mainRecyclerView.adapter = MainNewsAdapter(homeList, requireActivity(), this)
                    viewmodel._homeDataList.value = ArrayList()
                }
            }
        })
    }

    fun newInstance(menu: Menus): HomeFragment {
        val fragmentHome = HomeFragment()
        val args = Bundle()
        args.putSerializable("menu", menu)
        fragmentHome.arguments = args
        return fragmentHome
    }

    private fun observeEvents(){
        lifecycleScope.launch {
            viewmodel.baseEvents.collect { UIEvent ->
                Helper.lifecycleScopeHandler(
                    requireContext(),
                    UIEvent,
                    shimmerFrameLayout,
                    mainRecyclerView,
                    noDataFound,
                    true
                )
            }
        }
    }

    override fun itemHomeTopSubCatClick(data: TopSubCategory) {
        TODO("Not yet implemented")
    }

    override fun itemHomeSubCatClick(data: SubCategory) {
        TODO("Not yet implemented")
    }

    override fun itemHomeClick(data: Articles) {
        data.article_type?.let { Helper.showToast(requireActivity(), it) }
        when (data.article_type) {
            Constants.ARTICLE_DETAIL,
            Constants.PHOTO_DETAIL,
            Constants.VIDEO_DETAIL,
            Constants.PODCAST_DETAIL -> {
                openDetailScreen(data)
            }
            Constants.SHORTS_DETAIL -> {

            }
            Constants.LIVE_BLOG_DETAIL -> {

            }
            Constants.WEBSTORY_DETAIL -> {
                val intent =
                    Intent(context, WebViewActivity::class.java).putExtra("url", data.media_url)
                startActivity(intent)
            }
            else -> {

            }
        }
    }

    fun openDetailScreen(data: Articles) {
        val intent = Intent(context, DetailsActivity::class.java)
            .putExtra("articleId", data.article_id)
            .putExtra("articleType", data.article_type)
        startActivity(intent)
    }
}