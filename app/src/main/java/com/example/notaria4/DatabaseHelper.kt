package com.example.notaria4.database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "nueva20.db"
        private const val DATABASE_VERSION = 3 // Incremented the version
        const val TABLE_USERS = "users"
        const val COLUMN_ID = "id"
        const val COLUMN_NAME = "name"
        const val COLUMN_EMAIL = "email"
        const val COLUMN_DATE = "date"
        const val COLUMN_TIME = "time"
        const val COLUMN_DESCRIPTION = "description"
        const val TABLE_NOTIFICATIONS = "notifications"
        const val COLUMN_TITLE = "title"
        const val COLUMN_CONTENT = "content"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createUserTable = ("CREATE TABLE $TABLE_USERS ("
                + "$COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "$COLUMN_NAME TEXT, "
                + "$COLUMN_EMAIL TEXT, "
                + "$COLUMN_DATE TEXT, "
                + "$COLUMN_TIME TEXT, "
                + "$COLUMN_DESCRIPTION TEXT)")
        db.execSQL(createUserTable)

        val createNotificationTable = ("CREATE TABLE $TABLE_NOTIFICATIONS ("
                + "$COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "$COLUMN_TITLE TEXT, "
                + "$COLUMN_CONTENT TEXT)")
        db.execSQL(createNotificationTable)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        if (oldVersion < 2) {
            val createNotificationTable = ("CREATE TABLE $TABLE_NOTIFICATIONS ("
                    + "$COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + "$COLUMN_TITLE TEXT, "
                    + "$COLUMN_CONTENT TEXT)")
            db.execSQL(createNotificationTable)
        }
        if (oldVersion < 3) {
            db.execSQL("ALTER TABLE $TABLE_USERS ADD COLUMN $COLUMN_DATE TEXT")
            db.execSQL("ALTER TABLE $TABLE_USERS ADD COLUMN $COLUMN_TIME TEXT")
            db.execSQL("ALTER TABLE $TABLE_USERS ADD COLUMN $COLUMN_DESCRIPTION TEXT")
        }
    }
}