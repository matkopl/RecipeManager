package hr.algebra.recipe.framework

import android.content.Context
import android.content.Intent

fun Context.startRecipeService() {
    Intent(this, RecipeService::class.java).also { intent ->
        startService(intent)
    }
}
