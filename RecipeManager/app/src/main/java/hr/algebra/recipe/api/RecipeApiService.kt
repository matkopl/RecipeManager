package hr.algebra.recipe.api

import hr.algebra.recipe.model.AutocompleteRecipe
import hr.algebra.recipe.model.Instruction
import hr.algebra.recipe.model.Recipe
import hr.algebra.recipe.model.RecipeResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface RecipeApiService {
    @GET("recipes/random")
    fun getRandomRecipes(
        @Query("number") number: Int
    ): Call<RecipeResponse>

    @GET("recipes/{id}/analyzedInstructions?stepBreakdown=true")
    fun getRecipeInstructions(
        @Path("id") recipeId: Int,
    ): Call<List<Instruction>>

    @GET("recipes/autocomplete")
    fun getRecipeAutocomplete(
        @Query("query") query: String,
        @Query("number") number: Int = 10
    ): Call<List<AutocompleteRecipe>>

    @GET("recipes/{id}/information?includeNutrition=false")
    fun getRecipeById(
        @Path("id") recipeId: Int
    ): Call<Recipe>
}
