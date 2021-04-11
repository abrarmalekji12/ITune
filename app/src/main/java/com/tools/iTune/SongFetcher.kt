package com.tools.iTune

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query


interface SongFetcher {
    @GET("search")
    fun getSongs(@Query("term")  search: String): Call<ITuneSongs?>?
}