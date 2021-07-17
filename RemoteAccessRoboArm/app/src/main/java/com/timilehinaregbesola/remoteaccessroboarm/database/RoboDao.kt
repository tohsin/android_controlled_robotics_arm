package com.timilehinaregbesola.remoteaccessroboarm.database

import androidx.room.*
import com.timilehinaregbesola.remoteaccessroboarm.model.Recording

@Dao
interface RoboDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addRecording(recording: Recording?): Long

    @Update
    suspend fun updateRecording(recording: Recording?)

    @Delete
    suspend fun deleteRecording(recording: Recording?)

    @Query("DELETE FROM recordings")
    suspend fun clear()

    @Query("SELECT * FROM recordings WHERE recordingId = :recordingId LIMIT 1")
    suspend fun getRecording(recordingId: Long?): Recording?

    @Query("SELECT * FROM recordings ORDER BY recordingId DESC LIMIT 1")
    suspend fun getLastRecording(): Recording?

    @Query("SELECT * FROM recordings ORDER BY recordingId DESC")
    suspend fun getRecordings(): List<Recording>

}