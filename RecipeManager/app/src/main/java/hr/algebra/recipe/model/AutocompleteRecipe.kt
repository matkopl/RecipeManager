package hr.algebra.recipe.model

import com.google.gson.annotations.SerializedName

data class AutocompleteRecipe (
    @SerializedName("id")
    val id: Int,
    @SerializedName("title")
    val title: String,
    @SerializedName("imageType")
    val imageType: String
) {
    val imageUrl: String
        get() = "https://spoonacular.com/recipeImages/${id}-312x231.${imageType}"
}
