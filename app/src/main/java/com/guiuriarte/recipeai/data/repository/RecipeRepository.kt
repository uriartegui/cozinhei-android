package com.guiuriarte.recipeai.data.repository

import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.guiuriarte.recipeai.BuildConfig
import com.guiuriarte.recipeai.data.api.AiService
import com.guiuriarte.recipeai.data.api.model.ChatRequest
import com.guiuriarte.recipeai.data.api.model.Message
import com.guiuriarte.recipeai.data.api.model.RecipeDto
import com.guiuriarte.recipeai.data.database.RecipeDao
import com.guiuriarte.recipeai.data.database.entity.RecipeEntity
import com.guiuriarte.recipeai.model.Recipe
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RecipeRepository @Inject constructor(
    private val aiService: AiService,
    private val recipeDao: RecipeDao
) {
    private val gson = Gson()

    suspend fun generateRecipe(ingredients: String): Result<Recipe> {
        return try {
            val prompt = """
    Crie uma receita detalhada usando os seguintes ingredientes: $ingredients
    Responda APENAS com JSON válido neste formato exato, sem texto adicional:
    {
      "name": "Nome da receita",
      "description": "Descrição curta e apetitosa",
      "ingredients": ["200g de arroz", "2 dentes de alho picados", "100g de queijo ralado"],
      "steps": ["Passo 1: Descrição detalhada do que fazer...", "Passo 2: ..."],
      "cookingTime": "30 minutos",
      "servings": "4 porções"
    }
    Regras importantes:
    - Cada ingrediente DEVE ter quantidade exata (gramas, xícaras, colheres, unidades, etc.)
    - Cada passo deve ser detalhado e claro, com temperatura e tempo quando necessário
    - Mínimo de 4 passos na receita
    - A receita deve ser realista e saborosa
""".trimIndent()


            val response = aiService.generateRecipe(
                ChatRequest(
                    messages = listOf(Message(role = "user", content = prompt))
                )
            )

            val content = response.choices.first().message.content
            val json = content
                .removePrefix("```json")
                .removePrefix("```")
                .removeSuffix("```")
                .trim()

            val dto = gson.fromJson(json, RecipeDto::class.java)
            Result.success(dto.toDomain())
        } catch (e: JsonSyntaxException) {
            Result.failure(Exception("A IA retornou um formato inesperado. Tente novamente."))
        } catch (e: Exception) {
            Result.failure(Exception("Erro ao gerar receita: ${e.message}"))
        }
    }

    suspend fun saveRecipe(recipe: Recipe) {
        recipeDao.insertRecipe(recipe.toEntity())
    }

    fun getSavedRecipes(): Flow<List<Recipe>> =
        recipeDao.getAllRecipes().map { list -> list.map { it.toDomain() } }

    fun getFavoriteRecipes(): Flow<List<Recipe>> =
        recipeDao.getFavorites().map { list -> list.map { it.toDomain() } }

    suspend fun toggleFavorite(id: String, isFavorite: Boolean) =
        recipeDao.toggleFavorite(id, isFavorite)

    suspend fun deleteRecipe(id: String) =
        recipeDao.deleteRecipe(id)

    suspend fun getById(id: String): Recipe? =
        recipeDao.getById(id)?.toDomain()

    private fun RecipeDto.toDomain() = Recipe(
        id = UUID.randomUUID().toString(),
        name = name,
        description = description,
        ingredients = ingredients,
        steps = steps,
        cookingTime = cookingTime,
        servings = servings
    )

    private fun Recipe.toEntity() = RecipeEntity(
        id = id,
        name = name,
        description = description,
        ingredients = gson.toJson(ingredients),
        steps = gson.toJson(steps),
        cookingTime = cookingTime,
        servings = servings,
        isFavorite = isFavorite,
        createdAt = createdAt
    )

    private fun RecipeEntity.toDomain() = Recipe(
        id = id,
        name = name,
        description = description,
        ingredients = gson.fromJson(ingredients, Array<String>::class.java).toList(),
        steps = gson.fromJson(steps, Array<String>::class.java).toList(),
        cookingTime = cookingTime,
        servings = servings,
        isFavorite = isFavorite,
        createdAt = createdAt
    )
}
