package com.itsol.vn.wallpaper.live.parallax.model

import androidx.compose.runtime.Immutable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "history_search")
data class HistorySearchModel(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo("query") var query: String,
    @ColumnInfo("isSelected") var isSelected: Boolean = false
)
