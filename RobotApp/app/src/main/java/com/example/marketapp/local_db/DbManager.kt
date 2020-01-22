package com.example.marketapp.local_db

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import com.example.marketapp.models.EntityContract

class DbManager(context: Context) {
    private val dbHelper: DatabaseHelper = DatabaseHelper(context)
    private val db: SQLiteDatabase by lazy { dbHelper.writableDatabase }

    fun insert(values: ContentValues): Long {
        return db.insert(EntityContract.TaskEntry.DB_TABLE, "", values)
    }

    fun add(values: ContentValues): Long {
        return db.insert(EntityContract.TypeEntry.DB_TABLE, "", values)
    }

    fun queryAll(): Cursor {
        return db.rawQuery("select * from ${EntityContract.TaskEntry.DB_TABLE}" , null)
    }

    fun queryAllTypes(): Cursor {
        return db.rawQuery("select * from ${EntityContract.TypeEntry.DB_TABLE}", null)
    }

    fun delete(selection: String, selectionArgs: Array<String>): Int {
        return db.delete(EntityContract.TaskEntry.DB_TABLE, selection, selectionArgs)
    }

    fun deleteAllTypes() {
        return db.execSQL("delete from ${EntityContract.TypeEntry.DB_TABLE}")
    }

    fun deleteWhereType(type: String) {
        return db.execSQL("delete from ${EntityContract.TaskEntry.DB_TABLE} where ${EntityContract.TaskEntry.COLUMN_TYPE} = type")
    }

    fun update(values: ContentValues, selection: String, selectionArgs: Array<String>): Int {
        return db.update(EntityContract.TaskEntry.DB_TABLE, values, selection, selectionArgs)
    }

    fun close() {
        db.close()
    }
}