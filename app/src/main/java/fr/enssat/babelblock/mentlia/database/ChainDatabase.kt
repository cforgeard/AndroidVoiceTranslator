package fr.enssat.babelblock.mentlia.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = arrayOf(Chain::class), version = 1, exportSchema = false)
public abstract class ChainDatabase : RoomDatabase() {
    abstract fun chainDao(): ChainDAO

    companion object {
        // Singleton prevents multiple instances of database opening at the
        // same time.
        @Volatile
        private var INSTANCE: ChainDatabase? = null

        fun getDatabase(context: Context): ChainDatabase {
            // if the INSTANCE is not null, then return it,
            // if it is, then create the database
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ChainDatabase::class.java,
                    "ChainDatabase"
                ).build()
                INSTANCE = instance
                // return instance
                instance
            }
        }
    }

}
