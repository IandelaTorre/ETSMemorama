package com.example.memorama

data class Item(
    val imageResId: Int,
    val identifier: String,
    var isFlipped: Boolean = false,
    var isMatched: Boolean = false
)
