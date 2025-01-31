package com.example.aptitude

data class CompletionRequest(
    val prompt: String,
    val max_tokens: Int
)

data class CompletionResponse(
    val id: String,
    val choices: List<Choice>
)

data class Choice(
    val text: String
)

