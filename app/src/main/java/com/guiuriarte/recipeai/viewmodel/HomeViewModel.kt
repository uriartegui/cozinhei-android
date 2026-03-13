package com.guiuriarte.recipeai.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.guiuriarte.recipeai.BuildConfig
import com.guiuriarte.recipeai.data.repository.RecipeRepository
import com.guiuriarte.recipeai.model.Recipe
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class HomeUiState {
    object Idle : HomeUiState()
    object Loading : HomeUiState()
    data class Success(val recipe: Recipe) : HomeUiState()
    data class Error(val message: String) : HomeUiState()
}

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: RecipeRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Idle)
    val uiState: StateFlow<HomeUiState> = _uiState

    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query

    fun onQueryChange(value: String) {
        _query.value = value
    }

    fun setIngredients(ingredients: List<String>) {
        _query.value = ingredients.joinToString(", ")
    }

    fun generateRecipe() {
        val q = _query.value.trim()
        if (q.isBlank()) return

        viewModelScope.launch {
            _uiState.value = HomeUiState.Loading
            val result = repository.generateRecipe(q)
            _uiState.value = result.fold(
                onSuccess = { HomeUiState.Success(it) },
                onFailure = { HomeUiState.Error(it.message ?: "Erro desconhecido") }
            )
        }
    }

    fun saveCurrentRecipe() {
        val state = _uiState.value
        if (state is HomeUiState.Success) {
            viewModelScope.launch { repository.saveRecipe(state.recipe) }
        }
    }

    fun clearAll() {
        _uiState.value = HomeUiState.Idle
        _query.value = ""
    }
}
