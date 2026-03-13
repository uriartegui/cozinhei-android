package com.guiuriarte.recipeai.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.guiuriarte.recipeai.data.database.entity.RecipeEntity

@Database(entities = [RecipeEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun recipeDao(): RecipeDao
}
