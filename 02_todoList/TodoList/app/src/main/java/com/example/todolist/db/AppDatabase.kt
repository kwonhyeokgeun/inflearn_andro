package com.example.todolist.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [TodoEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun getTodo() : TodoDao

    companion object{
        val databaseName = "db_todo"
        var appDatabase : AppDatabase? = null

        @Synchronized
        fun getInstance(context : Context) : AppDatabase?{
            if(appDatabase == null){
                synchronized(AppDatabase::class){
                    appDatabase = Room.databaseBuilder(context,
                        AppDatabase::class.java,
                        databaseName
                    ).build()
                }
            }
            return appDatabase
        }
    }
}