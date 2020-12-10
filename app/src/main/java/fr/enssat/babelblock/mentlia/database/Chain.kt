package fr.enssat.babelblock.mentlia.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import org.json.JSONArray

@Entity(tableName = "Chain")
class Chain(
    @ColumnInfo(name = "name") var name: String,
    @ColumnInfo(name = "favorite") var favorite: Int,
    @ColumnInfo(name = "json") var json: JSONArray
){
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0
}