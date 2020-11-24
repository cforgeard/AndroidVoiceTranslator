package fr.enssat.babelblock.mentlia

import android.app.Application
import timber.log.Timber
import timber.log.Timber.DebugTree

@Suppress("unused")
class Application : Application() {
    override fun onCreate() {
        super.onCreate()
        Timber.plant(DebugTree())
    }
}
