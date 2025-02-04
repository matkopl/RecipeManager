package hr.algebra.recipe

import android.os.Bundle
import android.text.Html
import android.text.Spanned
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.squareup.picasso.Picasso
import hr.algebra.recipe.databinding.ActivityRecipeDetailBinding
import hr.algebra.recipe.fragment.RecipeInstructionFragment
import hr.algebra.recipe.model.Ingredient
import hr.algebra.recipe.model.Instruction
import hr.algebra.recipe.model.Recipe
import hr.algebra.recipe.repository.RecipeSqlHelper

class RecipeDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRecipeDetailBinding
    private lateinit var dbHelper: RecipeSqlHelper
    private var isFavorite = false
    private var recipeId: Int = -1
    private var imageUrl: String = ""
    private var ingredients: List<Ingredient> = emptyList()
    private var instructions: List<Instruction> = emptyList()
    private val gson = Gson()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRecipeDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dbHelper = RecipeSqlHelper(this)

        recipeId = intent.getIntExtra("recipe_id", -1)
        if (recipeId == -1) {
            finish()
            return
        }

        val title = intent.getStringExtra("recipe_title") ?: "No Title Available"
        val summary = intent.getStringExtra("recipe_summary") ?: "No Description Available"
        imageUrl = intent.getStringExtra("recipe_image") ?: ""

        val ingredientsJson = intent.getStringExtra("recipe_ingredients") ?: "[]"
        val instructionsJson = intent.getStringExtra("recipe_instructions") ?: "[]"

        val typeIngredients = object : TypeToken<List<Ingredient>>() {}.type
        val typeInstructions = object : TypeToken<List<Instruction>>() {}.type

        ingredients = try {
            gson.fromJson(ingredientsJson, typeIngredients) ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }

        instructions = try {
            gson.fromJson(instructionsJson, typeInstructions) ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }

        binding.tvRecipeTitle.text = title
        binding.tvRecipeDescription.text = formatDescription(summary)
        binding.tvRecipeIngredients.text = formatIngredients(ingredients)

        if (imageUrl.isNotEmpty()) {
            Picasso.get().load(imageUrl).into(binding.ivRecipeImage)
        }

        isFavorite = dbHelper.isRecipeFavorite(recipeId)
        updateFavoriteIcon()

        binding.fabFavorite.setOnClickListener {
            toggleFavorite(title, summary, ingredients, instructions)
        }

        binding.btnViewInstructions.setOnClickListener {
            openInstructionsFragment()
        }
    }

    private fun toggleFavorite(
        title: String,
        summary: String,
        ingredients: List<Ingredient>,
        instructions: List<Instruction>
    ) {
        val recipe = Recipe(
            id = recipeId,
            title = title,
            summary = summary,
            imageUrl = imageUrl,
            ingredients = ingredients,
            analyzedInstructions = instructions
        )

        if (isFavorite) {
            dbHelper.deleteRecipe(recipe.id)
            isFavorite = false
        } else {
            dbHelper.addRecipe(recipe)
            isFavorite = true
        }

        updateFavoriteIcon()
    }

    private fun updateFavoriteIcon() {
        val icon = if (isFavorite) R.drawable.ic_favorite_filled else R.drawable.ic_favorite
        binding.fabFavorite.setImageResource(icon)
    }

    private fun openInstructionsFragment() {
        val instructionsJson = intent.getStringExtra("recipe_instructions") ?: "[]"

        if (instructionsJson == "[]") {
            return
        }

        val fragment = RecipeInstructionFragment().apply {
            arguments = Bundle().apply {
                putString("recipe_instructions", instructionsJson)
            }
        }

        binding.fragmentContainer.visibility = View.VISIBLE
        binding.mainContent.visibility = View.GONE

        supportFragmentManager.beginTransaction()
            .add(R.id.fragment_container, fragment, "INSTRUCTIONS_FRAGMENT")
            .addToBackStack(null)
            .commit()
    }


    private fun formatDescription(description: String): Spanned {
        return Html.fromHtml(description.replace(Regex("<a.*?</a>"), "").trim(), Html.FROM_HTML_MODE_LEGACY)
    }

    private fun formatIngredients(ingredients: List<Ingredient>): String {
        return if (ingredients.isEmpty()) {
            "No ingredients available."
        } else {
            ingredients.joinToString("\n") { "• ${it.original}" }
        }
    }
}
