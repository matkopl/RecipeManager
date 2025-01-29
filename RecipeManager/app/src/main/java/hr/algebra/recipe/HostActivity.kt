package hr.algebra.recipe

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import hr.algebra.recipe.adapter.RecipeAdapter
import hr.algebra.recipe.databinding.ActivityHostBinding
import hr.algebra.recipe.model.RecipeResponse
import hr.algebra.recipe.utils.NetworkUtils
import hr.algebra.recipe.viewmodel.RecipeViewModel
import kotlinx.coroutines.launch
import android.view.View

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
            val intent = Intent(this, RecipeDetailActivity::class.java).apply {
                putExtra("recipe_id", recipe.id)
                putExtra("recipe_title", recipe.title)
                putExtra("recipe_summary", recipe.summary)
                putExtra("recipe_image", recipe.imageUrl)

                val ingredients = recipe.ingredients?.joinToString("\n") { "- ${it.original}" } ?: "No ingredients available"
                putExtra("recipe_ingredients", ingredients)

                val instructions = recipe.analyzedInstructions?.firstOrNull()?.steps
                    ?.joinToString("\n") { "${it.number}. ${it.step}" }
                    ?: "No instructions available"
                putExtra("recipe_instructions", instructions)
            }

            startActivity(intent)
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
        }

        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = recipeAdapter
    }


    private fun setupFabFavorites() {
        binding.fabFavorites.setOnClickListener {
            Toast.makeText(this, "Favorites clicked!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            recipeViewModel.recipes.collect { response ->
                response?.let { updateUI(it) }
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
        if (NetworkUtils.isNetworkAvailable(this)) {
            recipeViewModel.fetchRandomRecipes(10)
        } else {
            showLoading(false)
            showError("No internet connection")
        }
    }

    private fun updateUI(response: RecipeResponse) {
        val recipes = response.recipes
        recipeAdapter = RecipeAdapter(recipes) { recipe ->
            val intent = Intent(this, RecipeDetailActivity::class.java).apply {
                putExtra("recipe_id", recipe.id)
                putExtra("recipe_title", recipe.title)
                putExtra("recipe_summary", recipe.summary)
                putExtra("recipe_image", recipe.imageUrl)

                val ingredients = recipe.ingredients?.joinToString("\n") { "- ${it.original}" } ?: "No ingredients available"
                putExtra("recipe_ingredients", ingredients)

                val instructions = recipe.analyzedInstructions?.firstOrNull()?.steps
                    ?.joinToString("\n") { "${it.number}. ${it.step}" }
                    ?: "No instructions available"
                putExtra("recipe_instructions", instructions)
            }

            startActivity(intent)
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
        }
        binding.recyclerView.adapter = recipeAdapter
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.recyclerView.visibility = if (isLoading) View.GONE else View.VISIBLE
    }

    private fun showError(message: String) {
        Toast.makeText(this, "Error: $message", Toast.LENGTH_SHORT).show()
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
