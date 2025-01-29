package hr.algebra.recipe.model

import com.google.gson.annotations.SerializedName

data class Instruction (
    @SerializedName("name")
    val name: String?,
    @SerializedName("steps")
    val steps: List<Step>?
)
