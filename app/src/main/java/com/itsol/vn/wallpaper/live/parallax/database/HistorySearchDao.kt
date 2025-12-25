package com.itsol.vn.wallpaper.live.parallax.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.itsol.vn.wallpaper.live.parallax.model.HistorySearchModel

@Dao
interface HistorySearchDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(historySearchModel: HistorySearchModel)

    @Query("SELECT * FROM history_search ORDER BY id DESC LIMIT 5")
    fun getHistoryModel():List<HistorySearchModel>
}