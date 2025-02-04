package hr.algebra.recipe.model

import android.health.connect.datatypes.units.Length
import com.google.gson.annotations.SerializedName

data class Step(
    @SerializedName("number")
    val number: Int,
    @SerializedName("step")
    val step: String,
    @SerializedName("equipment")
    val equipment: List<Equipment>?
)
