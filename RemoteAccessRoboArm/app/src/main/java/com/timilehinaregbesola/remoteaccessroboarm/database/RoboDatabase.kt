package com.timilehinaregbesola.remoteaccessroboarm.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.timilehinaregbesola.remoteaccessroboarm.model.DataConverter
import com.timilehinaregbesola.remoteaccessroboarm.model.Recording

@Database(entities = [Recording::class], version = 1, exportSchema = false)
@TypeConverters(DataConverter::class)
abstract class RoboDatabase : RoomDatabase() {
    abstract val recordingDatabaseDao: RoboDao
}
