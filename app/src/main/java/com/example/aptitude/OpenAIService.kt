package com.example.aptitude

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface OpenAIService {
  //  @POST("v1/engines/davinci-codex/completions")
    @POST("v1/chat/completions")
    suspend fun getCompletion(
        @Header("Authorization") authorization: String,
        @Body requestBody: CompletionRequest
    ): Response<CompletionResponse>
}