package com.example.marketapp.local_db

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.marketapp.models.EntityContract

private const val SQL_CREATE_ENTRIES =
    """CREATE TABLE IF NOT EXISTS ${EntityContract.TaskEntry.DB_TABLE} (
            ${EntityContract.TaskEntry.COLUMN_ID} INTEGER PRIMARY KEY,
            ${EntityContract.TaskEntry.COLUMN_NAME} TEXT,
            ${EntityContract.TaskEntry.COLUMN_SPECS} TEXT,
            ${EntityContract.TaskEntry.COLUMN_HEIGHT} INTEGER,
            ${EntityContract.TaskEntry.COLUMN_TYPE} TEXT,
            ${EntityContract.TaskEntry.COLUMN_AGE} INTEGER
            );
        """

private const val SQL_CREATE_TYPES =
    """CREATE TABLE IF NOT EXISTS ${EntityContract.TypeEntry.DB_TABLE} (
            ${EntityContract.TaskEntry.COLUMN_TYPE} TEXT
            );
        """

private const val SQL_DELETE_ENTRIES =
    "DROP TABLE IF EXISTS ${EntityContract.TaskEntry.DB_TABLE}"

private const val SQL_DELETE_TYPES =
    "DROP TABLE IF EXISTS ${EntityContract.TypeEntry.DB_TABLE}"

class DatabaseHelper(context: Context) :
    SQLiteOpenHelper(
        context,
        EntityContract.DB_NAME,
        null,
        EntityContract.DB_VERSION
    ) {

    var context: Context? = context

    override fun onCreate(db: SQLiteDatabase?) {
        db!!.execSQL(SQL_CREATE_ENTRIES)
        db!!.execSQL(SQL_CREATE_TYPES)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db!!.execSQL(SQL_DELETE_ENTRIES)
        db!!.execSQL(SQL_DELETE_TYPES)
    }
}