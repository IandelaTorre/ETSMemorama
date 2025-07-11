package com.example.memorama

data class Item(
    val imageResId: Int,
    val identifier: String,
    val isFlipped: Boolean = false,
    val isMatched: Boolean = false
)
