package com.timilehinaregbesola.remoteaccessroboarm

import com.timilehinaregbesola.remoteaccessroboarm.model.Recording

class RoboRepository(private val dataSource: RoomRecordingDataSource) {
    suspend fun addRecording(recording: Recording) = dataSource.addRecording(recording)

    suspend fun deleteRecording(recording: Recording) = dataSource.deleteRecording(recording)

    suspend fun updateRecording(recording: Recording) = dataSource.updateRecording(recording)

    suspend fun getRecordings() = dataSource.getRecordings()

    suspend fun getLatestRecordingFromDatabase() = dataSource.getLatestRecordingFromDatabase()

    suspend fun findRecording(id: Long) = dataSource.findRecording(id)

    suspend fun clear() = dataSource.clear()
}
