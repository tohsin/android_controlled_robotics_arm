package com.timilehinaregbesola.remoteaccessroboarm.app

import android.app.Application
import com.timilehinaregbesola.remoteaccessroboarm.app.di.databaseModule
import com.timilehinaregbesola.remoteaccessroboarm.app.di.repositoryModule
import com.timilehinaregbesola.remoteaccessroboarm.app.di.viewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

class RoboApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger(Level.ERROR)
            androidContext(this@RoboApplication)
            modules(listOf(repositoryModule, databaseModule, viewModelModule))
        }
    }
}
