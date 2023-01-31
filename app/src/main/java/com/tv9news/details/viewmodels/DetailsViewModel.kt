package com.tv9news.details.viewmodels

import android.annotation.SuppressLint
import android.app.Application
import androidx.annotation.NonNull
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.tv9news.R
import com.tv9news.models.home.DataHome
import com.tv9news.models.home.HomeApi
import com.tv9news.room.LocalRoomDatabase
import com.tv9news.room.dao.articles.LocalArticle
import com.tv9news.utils.base.BaseViewModel
import com.tv9news.utils.helpers.AES
import com.tv9news.utils.helpers.UIEvent
import com.tv9news.utils.networks.ResponseValidator
import com.tv9news.utils.networks.RetrofitFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject

class DetailsViewModel(
    @NonNull application: Application
) : BaseViewModel(application) {

    fun callDetailsApi(encryptionStr: String,articleid: String) {
        getArticleFromLocal(articleid){
           if(it!=null && it.data!=null){
               _detailsDataList.value = it.data
           }
           else{
                viewModelScope.launch(baseExceptionHandler) {
                    sendSameEvents(UIEvent.onLoading)

                    val webservice = RetrofitFactory.getInstance(context)
                    val response = webservice.getDetails(encryptionStr)
                    ResponseValidator.processResponse<String>(response) { status, msg, result ->
                        if (status && result != null) {
                            sendSameEvents(UIEvent.Success(msg))
                            var jsonObject: JSONObject? = null
                            try {
                                val decryptdata =
                                    AES.decrypt(result, AES.generatekeyAPI(), AES.generateVectorAPI())
                                jsonObject = JSONObject(decryptdata)
                                if (jsonObject.getBoolean("status")) {
                                    val detailsData: HomeApi =
                                        Gson().fromJson(jsonObject.toString(), HomeApi::class.java)
                                    _detailsDataList.value = detailsData.data
                                } else {
                                    sendSameEvents(UIEvent.OnError(jsonObject.getString("message")))
                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        } else {
                            sendSameEvents(UIEvent.OnError(context.getString(R.string.no_data_found)))
                        }
                    }
                }
            }

        }

    }

    //details Success Live data
    var _detailsDataList = MutableLiveData(emptyList<DataHome>())
    val detailsDataList: LiveData<List<DataHome>> get() = _detailsDataList

    override fun onCleared() {
        super.onCleared()
    }
    fun savearticleToRoom(articleID:String,hasAlready : Boolean) {
        _detailsDataList.value?.let { data->
            val localArticle = LocalArticle(articleID, data)
            viewModelScope.launch(Dispatchers.IO){
                var articleDB = LocalRoomDatabase.getDatabase(context).ArticleDao()
                if(hasAlready)
                    articleDB.deleteArticle(localArticle)
                else
                  articleDB.insertArticle(localArticle)
            }
        }
    }
    @SuppressLint("SuspiciousIndentation")
    fun getArticleFromLocal(
        articleid:String,
       callback : (LocalArticle?)->Unit
    ){
        viewModelScope.launch (Dispatchers.IO){
          var article=  LocalRoomDatabase.getDatabase(context).ArticleDao().getArticleById(articleid)
            withContext(Dispatchers.Main){
                callback.invoke(article)
            }

        }

    }
}