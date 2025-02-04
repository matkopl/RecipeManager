package hr.algebra.recipe.framework

import android.app.IntentService
import android.content.Intent
import android.util.Log
import hr.algebra.recipe.RECIPE_PROVIDER_CONTENT_URI

private const val TAG = "RecipeService"

class RecipeService : IntentService("RecipeService") {

    override fun onHandleIntent(intent: Intent?) {
        Log.d(TAG, "RecipeService started")

        try {
            val cursor = contentResolver.query(RECIPE_PROVIDER_CONTENT_URI, null, null, null, null)
            cursor?.use {
                if (it.count > 0) {
                    Log.d(TAG, "Favorite recipes found: ${it.count}")
                } else {
                    Log.d(TAG, "No favorite recipes found in DB")
                }
            }

            sendBroadcast(Intent("hr.algebra.recipe.FAVORITE_RECIPES_UPDATED"))

        } catch (e: Exception) {
            Log.e(TAG, "Error fetching favorite recipes", e)
        }
    }
}
