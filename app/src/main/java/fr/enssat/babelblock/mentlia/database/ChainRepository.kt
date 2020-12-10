package fr.enssat.babelblock.mentlia.database

import androidx.annotation.WorkerThread
import kotlinx.coroutines.flow.Flow

class ChainRepository (private val chainDAO: ChainDAO) {
    // Room executes all queries on a separate thread.
    // Observed Flow will notify the observer when the data has changed.
    val allChain: Flow<List<Chain>> = chainDAO.getChain()

    // By default Room runs suspend queries off the main thread, therefore, we don't need to
    // implement anything else to ensure we're not doing long running database work
    // off the main thread.
    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insert(chain: Chain) {
        chainDAO.insert(chain)
    }
    // By default Room runs suspend queries off the main thread, therefore, we don't need to
    // implement anything else to ensure we're not doing long running database work
    // off the main thread.
    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun deleteId(chain: Chain) {
        chainDAO.deleteId(chain.id)
    }

}