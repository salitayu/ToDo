package com.example.todo

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DBHelper(context: Context, factory: SQLiteDatabase.CursorFactory?) :
    SQLiteOpenHelper(context, DATABASE_NAME, factory, DATABASE_VERSION) {
        override fun onCreate(db: SQLiteDatabase) {
            val query = ("CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " ("
                    + ID_COL + " INTEGER PRIMARY KEY, " +
                    TASK_COL + " TEXT," +
                    DUE_COL + " TEXT," +
                    DONE_COL + " BOOLEAN," +
                    CATEGORY_COL + " TEXT" + ")")
            db.execSQL(query)
        }

    override fun onUpgrade(db: SQLiteDatabase, p1: Int, p2: Int) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME)
        onCreate(db)
    }

    fun addTask(task: String, due: String, done: Boolean, category: String) {
        val values = ContentValues()
        values.put(TASK_COL, task)
        values.put(DUE_COL, due)
        values.put(DONE_COL, done)
        values.put(CATEGORY_COL, category)
        val db = this.writableDatabase
        db.insert(TABLE_NAME, null, values)
        db.close()
    }

    fun getTasks(): Cursor? {
        val db = this.readableDatabase
        return db.rawQuery("SELECT * FROM " + TABLE_NAME, null)
    }

    fun editTaskById(id: Int, task: Task) {
        val db = this.readableDatabase
        val cv = ContentValues()
        cv.put(TASK_COL, task.task)
        cv.put(DUE_COL, task.due)
        cv.put(CATEGORY_COL, task.category)
        cv.put(DONE_COL, task.done)
        val idstr = "id = $id"
        db.update(TABLE_NAME, cv, idstr, null)
    }

    fun deleteTaskById(id: Int): Int {
        val db = this.readableDatabase
        return db.delete(TABLE_NAME, "$ID_COL=$id", null)
    }

    fun removeAllTasks() {
        val db = this.readableDatabase
        db.delete(TABLE_NAME, null, null)
    }

    companion object {
        private val DATABASE_NAME = "db"
        private val DATABASE_VERSION = 1
        val TABLE_NAME = "todos_table"
        val ID_COL = "id"
        val TASK_COL = "task"
        val DUE_COL = "due"
        val DONE_COL = "done"
        val CATEGORY_COL = "category"
    }
}