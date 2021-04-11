package com.tools.iTune

import android.app.Application
import android.graphics.BitmapFactory
import androidx.lifecycle.AndroidViewModel
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ITuneViewModel(application: Application) : AndroidViewModel(application)  {
    private var repository: ITuneRepository =
        ITuneRepository(application)

    private var jsonPlaceHolderApi: SongFetcher
    init {
        val retrofit = Retrofit.Builder()
            .baseUrl(" https://itunes.apple.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        jsonPlaceHolderApi =
            retrofit.create(SongFetcher::class.java)
    //   https://itunes.apple.com/search?term=[word]

    }
    private fun insert(song: ITuneSong) {
        repository.insert(song)
    }

    fun getAllSongs(keyword:String) :SearchResult?{
        val call: Call<ITuneSongs?>? = jsonPlaceHolderApi.getSongs(keyword)
        var response:Response<ITuneSongs?>?=null
        try {
           response= call!!.execute()
        }
        catch (e:Exception){
     // Network Problem
        }
        if(keyword!=MainActivity.lastSearched)
            return null
            if(response!=null&& response.isSuccessful) {
              val post: ITuneSongs = response.body()!!
               val  list= mutableListOf<Song>()
                for(it in post.results){
                    val tmp= ITuneSong(
                        it.trackName,
                      it.artistName,
                   null,
                       it.trackPrice,
                    )
                      insert(tmp)
                    val image=   repository.getImage(it.trackName)
                    val bitmap =  if(image!=null)
                        BitmapFactory.decodeByteArray(image,0, image.size)
                    else
                        null
                list.add(Song(
                    it.trackName ,
                    it.artistName,
                    it.artworkUrl100,
                    bitmap,
                    it.trackPrice,
                    tmp.id
                ))

              }
              return SearchResult(true,list)
          }else{
              val words=keyword.split(" ")
                println("words $words")
               val set= mutableSetOf<ITuneSong>()
               for(word in words)
                   if(word.isNotEmpty())
                set.addAll(repository.searchFor(word))
               val list= set.toList().map {
                    val bitmap
                            =  if(it.image!=null)
                        BitmapFactory.decodeByteArray(it.image,0,it.image!!.size)
                    else
                        null
                    return@map Song(it.name,it.artistName,null,bitmap,it.price,it.id)
                }
                return SearchResult(false,list)
          }
    }
}
class SearchResult(val online:Boolean,val songs:List<Song>)