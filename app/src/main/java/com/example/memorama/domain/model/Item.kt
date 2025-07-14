package com.example.memorama.domain.model

data class Item(
    val imageResId: Int,
    val identifier: String,
    var isFlipped: Boolean = false,
    var isMatched: Boolean = false
)
