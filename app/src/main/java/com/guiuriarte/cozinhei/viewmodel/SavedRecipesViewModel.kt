package com.guiuriarte.cozinhei.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.guiuriarte.cozinhei.data.repository.RecipeRepository
import com.guiuriarte.cozinhei.model.Recipe
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SavedRecipesViewModel @Inject constructor(
    private val repository: RecipeRepository
) : ViewModel() {

    val savedRecipes: StateFlow<List<Recipe>> = repository.getSavedRecipes()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val favoriteRecipes: StateFlow<List<Recipe>> = repository.getFavoriteRecipes()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun toggleFavorite(id: String, isFavorite: Boolean) {
        viewModelScope.launch {
            repository.toggleFavorite(id, !isFavorite)
        }
    }

    fun deleteRecipe(id: String) {
        viewModelScope.launch {
            repository.deleteRecipe(id)
        }
    }
}
