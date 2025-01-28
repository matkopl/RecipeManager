package hr.algebra.recipe.repository

import hr.algebra.recipe.api.RecipeApiService
import hr.algebra.recipe.factory.RecipeApiFactory
import hr.algebra.recipe.model.RecipeResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RecipeRepository {

    private val apiService: RecipeApiService = RecipeApiFactory.apiService

    fun getRandomRecipes(number: Int, onSuccess: (RecipeResponse) -> Unit, onError: (String) -> Unit) {
        val call = apiService.getRandomRecipes(number)
        call.enqueue(object : Callback<RecipeResponse> {
            override fun onResponse(call: Call<RecipeResponse>, response: Response<RecipeResponse>) {
                if (response.isSuccessful && response.body() != null) {
                    response.body()?.let {
                        onSuccess(it)
                    }
                } else {
                    onError("Error: ${response.code()} ${response.message()}")
                }
            }

            override fun onFailure(call: Call<RecipeResponse>, t: Throwable) {
                onError("Failure: ${t.message}")
            }
        })
    }
}
