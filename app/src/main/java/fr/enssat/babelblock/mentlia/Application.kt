package fr.enssat.babelblock.mentlia

import android.app.Application
import android.content.pm.ApplicationInfo
import fr.enssat.babelblock.mentlia.database.ChainDatabase
import fr.enssat.babelblock.mentlia.database.ChainRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import timber.log.Timber
import timber.log.Timber.DebugTree

@Suppress("unused")
class Application : Application() {

    private val applicationScope = CoroutineScope(SupervisorJob())

    // Using by lazy so the database and the repository are only created when they're needed
    // rather than when the application starts
    val database by lazy { ChainDatabase.getDatabase(this,applicationScope) }
    val repository by lazy { ChainRepository(database.chainDao()) }

    override fun onCreate() {
        super.onCreate()
        val isDebuggable = 0 != applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE
        if (isDebuggable) {
            Timber.plant(DebugTree())
        }
    }
}
