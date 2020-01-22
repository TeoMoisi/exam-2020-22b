package com.example.marketapp.networking

import com.example.marketapp.models.Product
import io.reactivex.Completable
import io.reactivex.Observable
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*

interface ApiClient {
    @GET("old") fun getElements(): Observable<List<Product>> // Observable<SongEmbedded>
    @GET("types") fun getTypes(): Observable<List<String>>
    @GET("robots/{type}") fun getBookmarks(@Path("type") genre: String): Observable<List<Product>>
    @POST("robot") fun addElement(@Body element: Product): Completable
    @DELETE("bookmark/{id}") fun deleteElement(@Path("id") id: Int) : Completable
    @POST("height") fun updateHeight(@Body product: Product) : Completable
    @POST("age") fun updateAge(@Body product: Product) : Completable
    @POST("rate/{id}") fun rateElement(@Path("id") id: Int) : Completable
    companion object {

        fun create(): ApiClient {

            val retrofit = Retrofit.Builder()
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl("http://10.0.2.2:2202/")
                .build()

            return retrofit.create(ApiClient::class.java)
        }
    }
}
