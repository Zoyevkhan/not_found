package com.tv9news.room.dao.articles

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.tv9news.models.masterHit.Language
@Dao
interface ArticleDao {
    @Insert
    fun insertArticle(article: LocalArticle)

    @Query("SELECT * FROM LocalArticle")
    fun getAllArticles(): List<LocalArticle>

    @Query("SELECT * FROM LocalArticle")
    fun getAllArticlesLiveData(): LiveData<List<LocalArticle>>

    @Query("SELECT * FROM LocalArticle Where articleID=:articleid")
    fun getArticleById(articleid:String): LocalArticle

    @Query("SELECT articleID FROM LocalArticle")
     fun getAllArticlesIds() : LiveData<List<String>>

    @Delete
    fun deleteArticle(article: LocalArticle)

    @Query("DELETE FROM LocalArticle WHERE articleID = :id")
    fun deleteArticleById(id: Int)
}