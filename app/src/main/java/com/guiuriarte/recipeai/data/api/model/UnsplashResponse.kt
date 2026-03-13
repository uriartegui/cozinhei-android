package com.guiuriarte.recipeai.data.api.model

import com.google.gson.annotations.SerializedName

data class UnsplashResponse(
    val results: List<UnsplashPhoto>
)

data class UnsplashPhoto(
    val urls: UnsplashUrls
)

data class UnsplashUrls(
    val small: String,
    val regular: String
)
