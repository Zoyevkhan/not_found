package com.tv9news.shorts.interfaces

import com.tv9news.models.home.Articles

interface ShortsItemClick {
    fun shortsClick(data: Articles, type: String, position: Int)
}