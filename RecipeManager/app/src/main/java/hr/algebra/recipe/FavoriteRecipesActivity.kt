package hr.algebra.recipe

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import hr.algebra.recipe.adapter.RecipeAdapter
import hr.algebra.recipe.databinding.ActivityFavoriteRecipesBinding
import hr.algebra.recipe.model.Recipe
import hr.algebra.recipe.repository.RecipeSqlHelper

class FavoriteRecipesActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFavoriteRecipesBinding
    private lateinit var recipeAdapter: RecipeAdapter
    private lateinit var dbHelper: RecipeSqlHelper
    private val gson = Gson()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFavoriteRecipesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dbHelper = RecipeSqlHelper(this)
        setupRecyclerView()
        loadFavorites()
    }

    private fun setupRecyclerView() {
        recipeAdapter = RecipeAdapter(emptyList()) { recipe ->
            val intent = Intent(this, RecipeDetailActivity::class.java).apply {
                putExtra("recipe_id", recipe.id)
                putExtra("recipe_title", recipe.title)
                putExtra("recipe_summary", recipe.summary)
                putExtra("recipe_image", recipe.imageUrl)
                putExtra("recipe_ingredients", Gson().toJson(recipe.ingredients))

                val instructionsJson = Gson().toJson(recipe.analyzedInstructions)
                putExtra("recipe_instructions", instructionsJson)
            }
            startActivity(intent)
        }

        binding.rvFavorites.layoutManager = LinearLayoutManager(this)
        binding.rvFavorites.adapter = recipeAdapter
    }


    private fun loadFavorites() {
        val favoriteRecipes = dbHelper.getAllRecipes()
        if (favoriteRecipes.isEmpty()) {
            binding.tvNoFavorites.visibility = View.VISIBLE
            binding.rvFavorites.visibility = View.GONE
        } else {
            binding.tvNoFavorites.visibility = View.GONE
            binding.rvFavorites.visibility = View.VISIBLE
            recipeAdapter.updateRecipes(favoriteRecipes)
        }
    }
}
