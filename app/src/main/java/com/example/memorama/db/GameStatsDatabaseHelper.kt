package com.example.memorama.db

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class GameStatsDatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        const val DATABASE_NAME = "memoramagame.db"
        const val DATABASE_VERSION = 1
        const val TABLE_NAME = "game_stats"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createTable = """
            CREATE TABLE $TABLE_NAME (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                uuid TEXT NOT NULL,
                name TEXT NOT NULL,
                theme TEXT NOT NULL,
                size_map TEXT NOT NULL,
                time TEXT NOT NULL,
                movements TEXT NOT NULL,
                end_game INTEGER NOT NULL,
                win_game INTEGER NOT NULL
            )
        """.trimIndent()

        db.execSQL(createTable)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }
}
