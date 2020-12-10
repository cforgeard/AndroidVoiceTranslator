package fr.enssat.babelblock.mentlia.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import org.json.JSONArray

@Entity(tableName = "Chain")
class Chain(
    @ColumnInfo(name = "nom") var nom: String,
    @ColumnInfo(name = "favori") var favori: Int,
    @ColumnInfo(name = "json") var json: JSONArray
){
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0
}