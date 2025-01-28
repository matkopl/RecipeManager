package hr.algebra.recipe.model

import com.google.gson.annotations.SerializedName

data class Recipe(
    @SerializedName("id")
    val id: Int,
    @SerializedName("title")
    val title: String,
    @SerializedName("summary")
    val summary: String,
    @SerializedName("image")
    val imageUrl: String?,
    @SerializedName("instructions")
    val instructions: String?,
    @SerializedName("extendedIngredients")
    val ingredients: List<Ingredient>
)