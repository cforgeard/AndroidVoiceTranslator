package fr.enssat.babelblock.mentlia

import android.app.Application
import androidx.viewbinding.BuildConfig
import timber.log.Timber
import timber.log.Timber.DebugTree

class Application() : Application() {
    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            Timber.plant(DebugTree())
        }
    }
}
