package com.ml.shubham0204.facenet_android.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import org.koin.core.annotation.Single

@Database(
    entities = [PersonRecord::class],
    version = 1,
)
abstract class AppRoomDatabase : RoomDatabase() {
    abstract fun personRecordsDao(): PersonRecordDao
}

@Single
class AppDB(context: Context) {
    val db =
        Room.databaseBuilder(context, AppRoomDatabase::class.java, "app-database").build()
}