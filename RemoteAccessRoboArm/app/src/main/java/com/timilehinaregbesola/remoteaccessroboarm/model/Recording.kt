package com.timilehinaregbesola.remoteaccessroboarm.model

import androidx.room.*
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.*

@Entity(tableName = "recordings")
data class Recording(
    @PrimaryKey(autoGenerate = true)
    val recordingId: Long = 0L,
    @ColumnInfo(name = "title")
    val title: String,
    var tasks: List<Task> = listOf()
)

data class Task(
    val id: String = UUID.randomUUID().toString(),
    var angle: Int,
    var actuator: Int,
    val timing: Int = 0
)

class DataConverter {
    @TypeConverter
    fun fromTaskList(value: List<Task>): String {
        val gson = Gson()
        val type = object : TypeToken<List<Task>>() {}.type
        return gson.toJson(value, type)
    }

    @TypeConverter
    fun toTaskList(value: String): List<Task> {
        val gson = Gson()
        val type = object : TypeToken<List<Task>>() {}.type
        return gson.fromJson(value, type)
    }
}
