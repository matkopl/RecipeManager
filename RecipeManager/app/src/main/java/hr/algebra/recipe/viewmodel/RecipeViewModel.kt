package hr.algebra.recipe.viewmodel

import androidx.lifecycle.ViewModel
import hr.algebra.recipe.model.RecipeResponse
import hr.algebra.recipe.repository.RecipeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class RecipeViewModel : ViewModel() {

    private val recipeRepository = RecipeRepository()

    private val _recipes = MutableStateFlow<RecipeResponse?>(null)
    val recipes: StateFlow<RecipeResponse?> = _recipes

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    fun fetchRandomRecipes(number: Int) {
        _isLoading.value = true
        recipeRepository.getRandomRecipes(
            number,
            onSuccess = { response ->
                _recipes.value = response
                _isLoading.value = false
            },
            onError = { error ->
                _errorMessage.value = error
                _isLoading.value = false
            }
        )
    }
}
