package com.example.myapplication.recordDB

import androidx.room.*

@Dao
interface RecordDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(record : Record)

    @Delete
    fun delete(record : Record)

    @Query("SELECT count FROM Record WHERE date = :date AND subject = :subject")
    fun getCount(date : String, subject : String) : Int

    @Query("SELECT * FROM Record WHERE date = :date")
    fun getRecord(date : String) : List<Record>

    @Query("SELECT DISTINCT date FROM Record")
    fun getDate() : List<String>
}