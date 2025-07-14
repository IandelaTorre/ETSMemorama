package com.example.memorama.data.db

import android.content.ContentValues
import android.content.Context
import java.util.*

class GameStatsRepository(context: Context) {
    private val dbHelper = GameStatsDatabaseHelper(context)

    fun insertGameStat(
        name: String,
        theme: String,
        sizeMap: String,
        time: String,
        movements: String,
        endGame: Boolean,
        winGame: Boolean
    ) {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put("uuid", UUID.randomUUID().toString())
            put("name", name)
            put("theme", theme)
            put("size_map", sizeMap)
            put("time", time)
            put("movements", movements)
            put("end_game", if (endGame) 1 else 0)
            put("win_game", if (winGame) 1 else 0)
        }
        db.insert(GameStatsDatabaseHelper.TABLE_NAME, null, values)
        db.close()
    }
}
