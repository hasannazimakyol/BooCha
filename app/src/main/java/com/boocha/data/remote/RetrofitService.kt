package com.boocha.data.remote

import com.boocha.model.book.SearchResponse
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Query


interface RetrofitService {
    @GET("books/v1/volumes")
    fun searchBook(@Query("q") query: String, @Query("printType") printType: String = "books", @Query("orderBy") orderBy: String = "newest", @Query("key") key: String = "AIzaSyBim2couj_n0anwUNC4utY_7-7PwRg-JTw"): Observable<SearchResponse>
}