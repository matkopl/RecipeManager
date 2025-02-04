package hr.algebra.recipe

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import hr.algebra.recipe.adapter.RecipeAdapter
import hr.algebra.recipe.databinding.ActivityHostBinding
import hr.algebra.recipe.model.Recipe
import hr.algebra.recipe.viewmodel.RecipeViewModel
import kotlinx.coroutines.launch

class HostActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHostBinding
    private lateinit var recipeAdapter: RecipeAdapter
    private val recipeViewModel: RecipeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHostBinding.inflate(layoutInflater)
        setContentView(binding.root)

        overridePendingTransition(R.anim.fade_in, R.anim.fade_out)

        setSupportActionBar(binding.toolbar)

        setupRecyclerView()
        setupFabFavorites()
        observeViewModel()
        fetchRecipes()
    }

    private fun setupRecyclerView() {
        recipeAdapter = RecipeAdapter(emptyList()) { recipe ->
            openRecipeDetail(recipe)
        }

        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = recipeAdapter
    }

    private fun setupFabFavorites() {
        binding.fabFavorites.setOnClickListener {
            val intent = Intent(this, FavoriteRecipesActivity::class.java)
            startActivity(intent)
        }
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            recipeViewModel.recipes.collect { response ->
                response?.let { updateUI(it.recipes) }
            }
        }

        lifecycleScope.launch {
            recipeViewModel.isLoading.collect { showLoading(it) }
        }

        lifecycleScope.launch {
            recipeViewModel.errorMessage.collect { it?.let { showError(it) } }
        }
    }

    private fun fetchRecipes() {
        recipeViewModel.fetchRandomRecipes(10)
    }

    private fun updateUI(recipes: List<Recipe>) {
        recipeAdapter.updateRecipes(recipes)
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.recyclerView.visibility = if (isLoading) View.GONE else View.VISIBLE
    }

    private fun showError(message: String) {
        Toast.makeText(this, "Error: $message", Toast.LENGTH_SHORT).show()
    }

    private fun openRecipeDetail(recipe: Recipe) {
        val intent = Intent(this, RecipeDetailActivity::class.java).apply {
            putExtra("recipe_id", recipe.id)
            putExtra("recipe_title", recipe.title)
            putExtra("recipe_summary", recipe.summary)
            putExtra("recipe_image", recipe.imageUrl)
            putExtra("recipe_ingredients", Gson().toJson(recipe.ingredients))
            putExtra("recipe_instructions", Gson().toJson(recipe.analyzedInstructions))
        }
        startActivity(intent)
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_host_activity, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_settings -> {
                Toast.makeText(this, "Settings clicked", Toast.LENGTH_SHORT).show()
                true
            }
            R.id.action_search -> {
                Toast.makeText(this, "Search clicked", Toast.LENGTH_SHORT).show()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
