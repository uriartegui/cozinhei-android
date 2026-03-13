package com.guiuriarte.recipeai.ui.screens.fridge

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

val fridgeIngredients = listOf(
    "Arroz", "Feijão", "Frango", "Carne", "Ovo",
    "Tomate", "Cebola", "Alho", "Queijo", "Batata",
    "Macarrão", "Cenoura", "Pimentão", "Leite", "Manteiga"
)

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun FridgeModeScreen(
    onNavigateToHome: (List<String>) -> Unit
) {
    var checkedIngredients by remember { mutableStateOf(setOf<String>()) }
    var customIngredient by remember { mutableStateOf("") }
    var extraIngredients by remember { mutableStateOf(listOf<String>()) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("🧊 Minha Geladeira") },
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
                text = "Marque o que você tem disponível:",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                (fridgeIngredients + extraIngredients).forEach { ingredient ->
                    FilterChip(
                        selected = ingredient in checkedIngredients,
                        onClick = {
                            checkedIngredients = if (ingredient in checkedIngredients) {
                                checkedIngredients - ingredient
                            } else {
                                checkedIngredients + ingredient
                            }
                        },
                        label = { Text(ingredient) }
                    )
                }
            }

            HorizontalDivider()

            Text(
                text = "Adicionar ingrediente",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = customIngredient,
                    onValueChange = { customIngredient = it },
                    label = { Text("Ex: Abóbora") },
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )
                IconButton(
                    onClick = {
                        if (customIngredient.isNotBlank()) {
                            extraIngredients = extraIngredients + customIngredient.trim()
                            checkedIngredients = checkedIngredients + customIngredient.trim()
                            customIngredient = ""
                        }
                    }
                ) {
                    Icon(
                        Icons.Default.Add,
                        contentDescription = "Adicionar",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }

            if (checkedIngredients.isNotEmpty()) {
                Text(
                    text = "${checkedIngredients.size} ingrediente(s) selecionado(s)",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Button(
                onClick = { onNavigateToHome(checkedIngredients.toList()) },
                modifier = Modifier.fillMaxWidth(),
                enabled = checkedIngredients.isNotEmpty()
            ) {
                Text("O que posso cozinhar? 🍳")
            }
        }
    }
}
