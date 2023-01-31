package com.tv9news.room.dao.articles

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.tv9news.models.home.DataHome

@Entity
data class LocalArticle(
    @PrimaryKey(autoGenerate = false)
    val articleID:String,
    val data: List<DataHome>
)
