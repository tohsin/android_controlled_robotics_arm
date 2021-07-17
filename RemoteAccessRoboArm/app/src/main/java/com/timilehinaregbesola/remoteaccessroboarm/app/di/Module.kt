package com.timilehinaregbesola.remoteaccessroboarm.app.di

import android.app.Application
import androidx.room.Room
import com.timilehinaregbesola.remoteaccessroboarm.recording.AutomateActivityViewModel
import com.timilehinaregbesola.remoteaccessroboarm.RoboRepository
import com.timilehinaregbesola.remoteaccessroboarm.RoomRecordingDataSource
import com.timilehinaregbesola.remoteaccessroboarm.database.RoboDao
import com.timilehinaregbesola.remoteaccessroboarm.database.RoboDatabase
import com.timilehinaregbesola.remoteaccessroboarm.task.TaskViewModel
import org.koin.android.ext.koin.androidApplication
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel { AutomateActivityViewModel(get()) }
    viewModel { TaskViewModel(get()) }
}

val databaseModule = module {
    fun provideDatabase(application: Application): RoboDatabase {
        return Room.databaseBuilder(
            application,
            RoboDatabase::class.java,
            "recording_history_database"
        ).build()
    }

    fun provideAlarmDao(database: RoboDatabase): RoboDao {
        return database.recordingDatabaseDao
    }

    single { provideDatabase(androidApplication()) }
    single { provideAlarmDao(get()) }
}

val repositoryModule = module {
    fun provideDataSource(roboDao: RoboDao): RoomRecordingDataSource {
        return RoomRecordingDataSource(roboDao)
    }

    fun provideRepository(recordingDataSource: RoomRecordingDataSource): RoboRepository {
        return RoboRepository(recordingDataSource)
    }

    single { provideDataSource(get()) }
    single { provideRepository(get()) }
}
