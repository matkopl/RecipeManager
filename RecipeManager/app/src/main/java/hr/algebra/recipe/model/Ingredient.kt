package hr.algebra.recipe.model

import com.google.gson.annotations.SerializedName

data class Ingredient(
    @SerializedName("original") // random recipes
    val original: String
)