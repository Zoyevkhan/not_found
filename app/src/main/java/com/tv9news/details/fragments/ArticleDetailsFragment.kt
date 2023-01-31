package com.tv9news.home.activities.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
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
import com.tv9news.details.adapters.AuthorAdapter
import com.tv9news.details.fragments.AuthorFragment
import com.tv9news.details.interfaces.DetailsItemClick
import com.tv9news.details.viewmodels.DetailsViewModel
import com.tv9news.home.activities.adapters.DetailNewsAdapter
import com.tv9news.models.utils.EncryptionData
import com.tv9news.room.LocalRoomDatabase
import com.tv9news.utils.helpers.AES
import com.tv9news.utils.helpers.Helper
import kotlinx.coroutines.launch


class ArticleDetailsFragment : Fragment(), DetailsItemClick {
    private var hasAleready: Boolean =false
    private lateinit var detailRecyclerView: RecyclerView
    private lateinit var shimmerFrameLayout: ShimmerFrameLayout
    private lateinit var noDataFound: TextView
    var articleId = ""
    val viewmodel: DetailsViewModel by activityViewModels<DetailsViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_article_details, container, false)
    }

    @SuppressLint("SuspiciousIndentation")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        articleId = arguments?.getString("articleId").toString()
        intiViews(view)
        observeData()
        observeEvents()
        callAPI(articleId)
    }

    private fun callAPI(articleId: String) {
        if (detailRecyclerView != null && detailRecyclerView.adapter != null) {
            (detailRecyclerView.adapter as DetailNewsAdapter).stopTextAudio()
        }
        val encryptionData = EncryptionData(lang_id = "1", article_id = articleId)
        val encryptionDataStr = Gson().toJson(encryptionData)
        val encryptionStr: String = AES.encrypt(encryptionDataStr)
        viewmodel.callDetailsApi(encryptionStr,articleId)
    }

    private fun intiViews(view: View) {
        detailRecyclerView = view.findViewById(R.id.detailRecyclerView)
        shimmerFrameLayout = view.findViewById(R.id.shimmerFrameLayout)
        noDataFound = view.findViewById(R.id.noDataFound)
        detailRecyclerView.setHasFixedSize(false)
    }

    private fun observeData() {
        lifecycleScope.launch {
            LocalRoomDatabase.getDatabase(requireContext()).ArticleDao().getAllArticlesIds()
                .observe(viewLifecycleOwner, Observer {
                    it?.let {
                        handleUI(it)
                    }
                })
        }
        viewmodel.detailsDataList.observe(viewLifecycleOwner, Observer { detailsDataList ->
            detailsDataList?.let { detailsDataList ->
                if (!detailsDataList.isNullOrEmpty()) {
                    detailRecyclerView.visibility = View.VISIBLE
                    detailRecyclerView.adapter = DetailNewsAdapter(detailsDataList, requireActivity(), this)
                }
            }
        })
    }
    fun handleUI(list : List<String>){
         hasAleready = list.any {
            it.equals(articleId)
        }
        (context as DetailsActivity).binding.bookmarkIV.setImageResource(if(hasAleready) R.drawable.bookmark_top_icon_active else R.drawable.bookmark_top_icon)

    }

    fun newInstance(articleId: String?): ArticleDetailsFragment {
        val fragmentHome = ArticleDetailsFragment()
        val args = Bundle()
        args.putString("articleId", articleId)
        fragmentHome.arguments = args
        return fragmentHome
    }

    private fun observeEvents() {
        lifecycleScope.launch {
            viewmodel.baseEvents.collect { UIEvent ->
                Helper.lifecycleScopeHandler(
                    requireContext(),
                    UIEvent,
                    shimmerFrameLayout,
                    detailRecyclerView,
                    noDataFound,
                    true
                )
            }
        }
    }

    override fun onResume() {
        if (!shimmerFrameLayout.isVisible) detailRecyclerView.visibility = View.VISIBLE
        super.onResume()
    }

    override fun BookmarkClick() {
        viewmodel.savearticleToRoom(articleId,hasAleready)
    }

    override fun AuthorClick(str: String?) {
        (activity as DetailsActivity).openAuthorFragment(AuthorFragment())
    }

    override fun itemDetailClick(str: String?) {
        str?.let { callAPI(it) }
    }
}