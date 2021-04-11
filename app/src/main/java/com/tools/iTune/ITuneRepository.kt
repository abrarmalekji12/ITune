package com.tools.iTune

import android.app.Application

class ITuneRepository(application: Application)  {
    private var itunedao: ITuneDao

//    private var allNotes: LiveData<List<ITuneSong>>

    init {
        val database: ITuneRoomDatabase = ITuneRoomDatabase.getInstance(
            application.applicationContext
        )!!
        itunedao = database.iTuneDao()
    }


    fun searchFor(keyword:String):List<ITuneSong>{
        return  itunedao.searchFor(keyword)
    }
    fun getImage(name:String) : ByteArray?{
        return itunedao.getImage(name)
    }

    fun insert(song: ITuneSong) {
itunedao.insert(song)
    }
}
