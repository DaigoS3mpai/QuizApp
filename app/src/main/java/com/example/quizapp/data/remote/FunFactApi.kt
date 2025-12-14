package com.example.quizapp.data.remote

import com.example.quizapp.data.remote.dto.FunFactResponse
import retrofit2.http.GET

interface FunFactApi {

    @GET("random.json?language=es")
    suspend fun getRandomFact(): FunFactResponse
}
