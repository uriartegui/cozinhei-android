package com.guiuriarte.recipeai.ui.screens.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.guiuriarte.recipeai.viewmodel.HomeUiState
import com.guiuriarte.recipeai.viewmodel.HomeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToRecipeDetail: (String) -> Unit,
    initialIngredients: List<String> = emptyList(),
    viewModel: HomeViewModel = hiltViewModel()
) {
    LaunchedEffect(initialIngredients) {
        if (initialIngredients.isNotEmpty()) viewModel.setIngredients(initialIngredients)
    }

    val uiState by viewModel.uiState.collectAsState()
    val query by viewModel.query.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("🍳 RecipeAI") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "O que você quer cozinhar?",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            OutlinedTextField(
                value = query,
                onValueChange = viewModel::onQueryChange,
                label = { Text("Ex: leite, ovo, macarrão...") },
                modifier = Modifier
                    .fillMaxWidth()
                    .defaultMinSize(minHeight = 120.dp),
                maxLines = 5
            )

            Button(
                onClick = viewModel::generateRecipe,
                modifier = Modifier.fillMaxWidth(),
                enabled = query.isNotBlank() && uiState !is HomeUiState.Loading,
                contentPadding = PaddingValues(vertical = 14.dp)
            ) {
                Text("Gerar Receita", style = MaterialTheme.typography.titleMedium)
            }

            when (val state = uiState) {
                is HomeUiState.Loading -> {
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                            Text("Gerando receita...", style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                        }
                    }
                }

                is HomeUiState.Error -> {
                    Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)) {
                        Text(
                            text = state.message,
                            modifier = Modifier.padding(16.dp),
                            color = MaterialTheme.colorScheme.onErrorContainer
                        )
                    }
                }

                is HomeUiState.Success -> {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        elevation = CardDefaults.cardElevation(4.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Surface(
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text(
                                    text = state.recipe.name,
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onPrimary
                                )
                                Text(
                                    text = state.recipe.description,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.85f)
                                )
                            }
                        }

                        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                SuggestionChip(onClick = {}, label = { Text("⏱ ${state.recipe.cookingTime}") })
                                SuggestionChip(onClick = {}, label = { Text("🍽 ${state.recipe.servings}") })
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                OutlinedButton(
                                    onClick = viewModel::clearAll,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text("Limpar")
                                }
                                Button(
                                    onClick = {
                                        viewModel.saveCurrentRecipe()
                                        onNavigateToRecipeDetail(state.recipe.id)
                                    },
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text("Ver Receita")
                                }
                            }
                        }
                    }
                }

                else -> {}
            }
        }
    }
}
