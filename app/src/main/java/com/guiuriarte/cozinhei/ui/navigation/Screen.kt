package com.guiuriarte.cozinhei.ui.navigation

sealed class Screen(val route: String) {
    object Home : Screen("home?ingredients={ingredients}") {
        fun createRoute(ingredients: String = "") =
            if (ingredients.isBlank()) "home?ingredients=" else "home?ingredients=$ingredients"
    }

    object Fridge : Screen("fridge")
    object SavedRecipes : Screen("saved_recipes")
    object RecipeDetail : Screen("recipe_detail/{recipeId}") {
        fun createRoute(recipeId: String) = "recipe_detail/$recipeId"
    }
    object CookingMode : Screen("cooking_mode/{recipeId}") {
        fun createRoute(recipeId: String) = "cooking_mode/$recipeId"
    }
}
