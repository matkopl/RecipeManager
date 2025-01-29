package hr.algebra.recipe

import android.os.Bundle
import android.text.Html
import android.text.Spanned
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.squareup.picasso.Picasso
import hr.algebra.recipe.databinding.ActivityRecipeDetailBinding
import hr.algebra.recipe.fragment.RecipeInstructionFragment

class RecipeDetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRecipeDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRecipeDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)

        val recipeId = intent.getIntExtra("recipe_id", -1)
        if (recipeId == -1) {
            finish()
            return
        }

        val title = intent.getStringExtra("recipe_title") ?: "No Title Available"
        val summary = intent.getStringExtra("recipe_summary") ?: "No Description Available"
        val imageUrl = intent.getStringExtra("recipe_image") ?: ""
        val ingredients = intent.getStringExtra("recipe_ingredients") ?: "No ingredients listed"

        binding.tvRecipeTitle.text = title
        binding.tvRecipeDescription.text = formatDescription(summary)
        binding.tvRecipeIngredients.text = formatIngredients(ingredients)

        if (imageUrl.isNotEmpty()) {
            Picasso.get().load(imageUrl).into(binding.ivRecipeImage)
        }

        binding.btnViewInstructions.setOnClickListener {
            openInstructionsFragment(recipeId)
        }

        supportFragmentManager.addOnBackStackChangedListener {
            toggleViewVisibility()
        }
    }

    private fun openInstructionsFragment(recipeId: Int) {
        val fragment = RecipeInstructionFragment().apply {
            arguments = Bundle().apply { putInt("recipe_id", recipeId) }
        }

        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .addToBackStack("instructions_fragment")
            .commit()

        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
    }

    private fun toggleViewVisibility() {
        val isFragmentVisible = supportFragmentManager.backStackEntryCount > 0
        binding.mainContent.visibility = if (isFragmentVisible) View.GONE else View.VISIBLE
        binding.fragmentContainer.visibility = if (isFragmentVisible) View.VISIBLE else View.GONE
    }

    private fun formatDescription(description: String): Spanned {
        return Html.fromHtml(description.replace(Regex("<a.*?</a>"), "").trim(), Html.FROM_HTML_MODE_LEGACY)
    }

    private fun formatIngredients(ingredients: String): String {
        return ingredients.split("\n").joinToString("\n") { "• ${it.trim().removePrefix("-").trim()}" }
    }
}
