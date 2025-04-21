package com.ismanieji.parkingosystema

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.DELETE

interface ApiService {
    @POST("users/login/")
    fun loginUser(@Body loginRequest: LoginRequest): Call<LoginResponse>

    @POST("users/register/")
    fun registerUser(@Body registerRequest: RegisterRequest): Call<RegisterResponse>

    @GET("users/profile/")
    fun getProfile(@Header("Authorization") token: String): Call<ProfileResponse>

    @POST("users/logout/")  // Adjust the endpoint based on your Django backend
    fun logoutUser(@Header("Authorization") token: String): Call<Void>
}


