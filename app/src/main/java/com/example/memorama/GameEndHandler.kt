package com.example.memorama

interface GameEndHandler {
    fun onGameEndEarly()
    fun onRevealSolution()
}