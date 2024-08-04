package com.ebookfrenzy.galleryapp02.data.rest

import com.ebookfrenzy.galleryapp02.data.model.Gallery
import com.ebookfrenzy.galleryapp02.data.model.Painting
import com.ebookfrenzy.galleryapp02.data.model.LocationModel
import com.ebookfrenzy.galleryapp02.data.model.RoomModel
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface FirebaseApiService {
    @GET("galleries")
    fun getGalleries(): Call<List<Gallery>>

    @GET("galleriesMaps/{id}")
    fun getGalleryMaps(@Path("id") id: String): Call<LocationModel>

    @GET("painting/{id}")
    fun getPainting(@Path("id") id: String): Call<Painting>

    @GET("paintings")
    fun getAllPaintings(): Call<List<Painting>>

    @GET("rooms")
    fun getRoom(): Call<List<RoomModel>>
}