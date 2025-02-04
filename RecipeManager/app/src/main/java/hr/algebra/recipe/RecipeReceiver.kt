package hr.algebra.recipe.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import hr.algebra.recipe.framework.startRecipeService

class RecipeReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        context?.startRecipeService()
    }
}
