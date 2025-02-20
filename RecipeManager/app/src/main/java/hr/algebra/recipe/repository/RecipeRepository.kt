package hr.algebra.recipe.repository

import hr.algebra.recipe.factory.RecipeApiFactory
import hr.algebra.recipe.model.AutocompleteRecipe
import hr.algebra.recipe.model.RecipeResponse
import hr.algebra.recipe.model.Instruction
import hr.algebra.recipe.model.Recipe
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RecipeRepository {

    fun getRandomRecipes(
        number: Int,
        onSuccess: (RecipeResponse) -> Unit,
        onError: (String) -> Unit
    ) {
        RecipeApiFactory.apiService.getRandomRecipes(number)
            .enqueue(object : Callback<RecipeResponse> {
                override fun onResponse(call: Call<RecipeResponse>, response: Response<RecipeResponse>) {
                    if (response.isSuccessful) {
                        response.body()?.let { onSuccess(it) }
                    } else {
                        onError("Error fetching recipes: ${response.message()}")
                    }
                }

                override fun onFailure(call: Call<RecipeResponse>, t: Throwable) {
                    onError("Network failure: ${t.localizedMessage}")
                }
            })
    }

    fun getRecipeInstructions(
        recipeId: Int,
        onSuccess: (List<Instruction>) -> Unit,
        onError: (String) -> Unit
    ) {
        RecipeApiFactory.apiService.getRecipeInstructions(recipeId)
            .enqueue(object : Callback<List<Instruction>> {
                override fun onResponse(call: Call<List<Instruction>>, response: Response<List<Instruction>>) {
                    if (response.isSuccessful) {
                        val instructions = response.body()

                        response.body()?.let { onSuccess(it) }
                    } else {
                        onError("Error fetching instructions: ${response.message()}")
                    }
                }

                override fun onFailure(call: Call<List<Instruction>>, t: Throwable) {
                    onError("Network failure: ${t.localizedMessage}")
                }
            })
    }

    fun getRecipeAutocomplete(
        query: String,
        number: Int,
        onSuccess: (List<AutocompleteRecipe>) -> Unit,
        onError: (String) -> Unit
    ) {
        RecipeApiFactory.apiService.getRecipeAutocomplete(query, number)
            .enqueue(object : Callback<List<AutocompleteRecipe>> {
                override fun onResponse(call: Call<List<AutocompleteRecipe>>, response: Response<List<AutocompleteRecipe>>) {
                    if (response.isSuccessful) {
                        response.body()?.let { onSuccess(it) }
                    } else {
                        onError("Error fetching search results: ${response.message()}")
                    }
                }

                override fun onFailure(call: Call<List<AutocompleteRecipe>>, t: Throwable) {
                    onError("Network failure: ${t.localizedMessage}")
                }
            })
    }

    fun getRecipeById(
        recipeId: Int,
        onSuccess: (Recipe) -> Unit,
        onError: (String) -> Unit
    ) {
        RecipeApiFactory.apiService.getRecipeById(recipeId)
            .enqueue(object : Callback<Recipe> {
                override fun onResponse(call: Call<Recipe>, response: Response<Recipe>) {
                    if (response.isSuccessful) {
                        response.body()?.let { onSuccess(it) }
                    } else {
                        onError("Error fetching recipe details: ${response.message()}")
                    }
                }

                override fun onFailure(call: Call<Recipe>, t: Throwable) {
                    onError("Network failure: ${t.localizedMessage}")
                }
            })
    }
}
