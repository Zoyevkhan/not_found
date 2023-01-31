package com.tv9news.details.interfaces

import com.tv9news.models.home.DataHome

interface DetailsItemClick {
    fun BookmarkClick()
    fun AuthorClick(str: String?)
    fun itemDetailClick(str: String?)
}