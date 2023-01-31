package com.tv9news.home.interfaces

import com.tv9news.models.home.Articles
import com.tv9news.models.home.SubCategory
import com.tv9news.models.masterHit.TopSubCategory

interface HomeItemClick {
    fun itemHomeTopSubCatClick(data: TopSubCategory)
    fun itemHomeSubCatClick(data: SubCategory)
    fun itemHomeClick(data: Articles)
}