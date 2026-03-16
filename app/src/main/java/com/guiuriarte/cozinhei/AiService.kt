package com.guiuriarte.cozinhei.data.api

import com.guiuriarte.cozinhei.data.api.model.ChatRequest
import com.guiuriarte.cozinhei.data.api.model.ChatResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface AiService {

    @POST("v1/chat/completions")
    suspend fun generateRecipe(
        @Body request: ChatRequest
    ): ChatResponse
}
