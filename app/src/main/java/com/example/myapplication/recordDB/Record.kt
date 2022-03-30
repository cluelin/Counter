package com.example.myapplication.recordDB

import androidx.room.Entity

@Entity(tableName = "Record", primaryKeys = ["date", "subject"])
data class Record(
    var date : String,
    var subject : String,
    var count : Int
)
