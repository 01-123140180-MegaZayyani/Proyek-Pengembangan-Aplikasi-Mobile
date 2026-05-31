package com.example.masakuy.presentation.screens.api

import kotlinx.serialization.Serializable

object GeminiDto {

    // ✅ Fix: pakai typed class bukan List<Map<String, List<Map<String, String>>>>
    // supaya serialization pasti benar ke format yang Gemini API butuhkan
    @Serializable
    data class GeminiRequest(
        val contents: List<ContentRequest>
    )

    @Serializable
    data class ContentRequest(
        val parts: List<PartRequest>
    )

    @Serializable
    data class PartRequest(
        val text: String
    )

    // Response classes tetap sama
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