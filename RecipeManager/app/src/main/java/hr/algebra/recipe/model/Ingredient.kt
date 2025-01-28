package hr.algebra.recipe.model

import com.google.gson.annotations.SerializedName

data class Ingredient(
    @SerializedName("original")
    val name: String
)