package com.example.myapplication.recordDB

import android.content.Context
import androidx.room.*

@Database(version =1, entities = [Record::class])
abstract class RecordDatabase :RoomDatabase(){

    abstract fun recordDao() : RecordDAO

    companion object{

        private var instance : RecordDatabase? = null

        @Synchronized
        fun getInstance(context : Context): RecordDatabase {
            if(instance == null){
                synchronized(RecordDatabase::class){
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        RecordDatabase::class.java,
                        "record-database"
                    ).build()
                }
            }

            return instance!!
        }
    }
}
