package fr.enssat.babelblock.mentlia.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Chain")
class Chain(
    @PrimaryKey(autoGenerate = true) var id: Long,
    @ColumnInfo(name = "nom") var nom: String,
    @ColumnInfo(name = "favori") var favori: Int,
    @ColumnInfo(name = "json") var json: String
)