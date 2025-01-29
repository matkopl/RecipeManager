package hr.algebra.recipe.model

import com.google.gson.annotations.SerializedName

data class Equipment (
    @SerializedName("id")
    val id: Int,
    @SerializedName("name")
    val name: String,
    @SerializedName("image")
    val image: String
)
