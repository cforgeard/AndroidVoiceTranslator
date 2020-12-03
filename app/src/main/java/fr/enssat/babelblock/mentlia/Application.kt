package fr.enssat.babelblock.mentlia

import android.app.Application
import android.content.pm.ApplicationInfo
import timber.log.Timber
import timber.log.Timber.DebugTree

@Suppress("unused")
class Application : Application() {
    override fun onCreate() {
        super.onCreate()
        val isDebuggable = 0 != applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE
        if (isDebuggable) {
            Timber.plant(DebugTree())
        }
    }
}
