package com.example.todolist.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface TodoDao {
    @Query("select * from TodoEntity")
    fun getAll() : List<TodoEntity>

    @Insert
    fun insert(todo : TodoEntity)

    @Delete
    fun delete(todo : TodoEntity)
}