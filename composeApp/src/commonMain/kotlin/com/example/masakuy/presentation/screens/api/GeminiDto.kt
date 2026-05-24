package com.example.masakuy.presentation.screens.api

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

object GeminiDto {

    @Serializable
    data class GeminiRequest(
        val contents: List<Map<String, List<Map<String, String>>>>
    )

    @Serializable
    data class GeminiResponse(
        val candidates: List<Candidate>? = null
    )

    @Serializable
    data class Candidate(
        val content: Content? = null
    )

    @Serializable
    data class Content(
        val parts: List<Part>? = null
    )

    @Serializable
    data class Part(
        val text: String? = null
    )
}