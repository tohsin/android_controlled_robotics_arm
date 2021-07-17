package com.timilehinaregbesola.remoteaccessroboarm

import com.timilehinaregbesola.remoteaccessroboarm.database.RoboDao
import com.timilehinaregbesola.remoteaccessroboarm.model.Recording

class RoomRecordingDataSource(private val roboDao: RoboDao) {

    suspend fun addRecording(recording: Recording?): Long {
        return roboDao.addRecording(recording)
    }

    suspend fun deleteRecording(recording: Recording) =
        roboDao.deleteRecording(recording)

    suspend fun updateRecording(recording: Recording) =
        roboDao.updateRecording(recording)

    suspend fun getRecordings(): List<Recording> = roboDao.getRecordings()

    suspend fun getLatestRecordingFromDatabase(): Recording? = roboDao.getLastRecording()

    suspend fun findRecording(id: Long): Recording? = roboDao.getRecording(id)

    suspend fun clear() = roboDao.clear()
}
