package fr.enssat.babelblock.mentlia.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ChainDAO {
    @Query("SELECT * FROM Chain ORDER BY nom ASC")
    fun getChain(): Flow<List<Chain>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(chain: Chain)

    @Query("DELETE FROM Chain WHERE id = id")
    suspend fun deleteId(id: Int)

    @Query("DELETE FROM Chain")
    suspend fun deleteAll()
}