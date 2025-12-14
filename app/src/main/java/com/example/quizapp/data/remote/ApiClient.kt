package com.example.quizapp.data.remote

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {

    // Emulador: 10.0.2.2
    // Celular físico: "http://IP_DE_TU_PC:8080/"
    private const val BASE_URL = "http://10.0.2.2:8080/"

    private val logging = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val client = OkHttpClient.Builder()
        .addInterceptor(logging)
        .build()

    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .client(client)
        .build()

    val authApi: AuthApi = retrofit.create(AuthApi::class.java)

    // 🔹 microservicio de preguntas (quiz-service)
    val quizApi: QuizApi = retrofit.create(QuizApi::class.java)

    // 🔹 microservicio de partidas (game-service)
    val gameApi: GameApi = retrofit.create(GameApi::class.java)

    // 🔹 microservicio de feedback (feedback-service)
    val feedbackApi: FeedbackApi = retrofit.create(FeedbackApi::class.java)
}
