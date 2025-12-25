package com.itsol.vn.wallpaper.live.parallax.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.itsol.vn.wallpaper.live.parallax.model.CategoryModel

@Dao
interface CategoryDao {

    @Query("select * from CategoryTable")
    fun getAllCategory(): List<CategoryModel>

    @Query("select count() from CategoryTable")
    fun getCountCategory(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertCategory(categoryModel: CategoryModel)

    @Query("delete from CategoryTable")
    fun deleteCategoryTable()
}