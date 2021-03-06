package fr.enssat.babelblock.mentlia.database

import android.content.Context
import androidx.room.*
import kotlinx.coroutines.CoroutineScope
import org.json.JSONArray

@Database(entities = [Chain::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class ChainDatabase : RoomDatabase() {
    abstract fun chainDao(): ChainDAO

    companion object {
        // Singleton prevents multiple instances of database opening at the
        // same time.
        @Volatile
        private var INSTANCE: ChainDatabase? = null

        fun getDatabase(
            context: Context,
            @Suppress("UNUSED_PARAMETER") scope: CoroutineScope
        ): ChainDatabase {
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

class Converters {
    @TypeConverter
    fun fromJSON(string: String?): JSONArray? {
        return string?.let { JSONArray(it) }
    }

    @TypeConverter
    fun jsonToBlob(json: JSONArray?): String? {
        return json?.toString()
    }

}

