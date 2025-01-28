package hr.algebra.recipe.fragment

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.lifecycle.lifecycleScope
import hr.algebra.recipe.adapter.RecipeAdapter
import hr.algebra.recipe.databinding.ActivityHostBinding
import hr.algebra.recipe.R
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

        setSupportActionBar(binding.toolbar)

        setupRecyclerView()
        setupFabFavorites()

        observeViewModel()

        fetchRecipes()
    }

    private fun setupRecyclerView() {
        recipeAdapter = RecipeAdapter(emptyList()) { recipe ->
            Toast.makeText(this, "Clicked on: ${recipe.title}", Toast.LENGTH_SHORT).show()
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
        // Observe recipes from the ViewModel
        lifecycleScope.launch {
            recipeViewModel.recipes.collect { response ->
                response?.let {
                    updateUI(it)
                }
            }
        }

        // Observe loading state from the ViewModel
        lifecycleScope.launch {
            recipeViewModel.isLoading.collect { isLoading ->
                showLoading(isLoading)
            }
        }

        // Observe error message from the ViewModel
        lifecycleScope.launch {
            recipeViewModel.errorMessage.collect { errorMessage ->
                errorMessage?.let {
                    showError(it)
                }
            }
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
            Toast.makeText(this, "Clicked on: ${recipe.title}", Toast.LENGTH_SHORT).show()
        }
        binding.recyclerView.adapter = recipeAdapter
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.progressBar.visibility = View.VISIBLE
            binding.recyclerView.visibility = View.GONE
        } else {
            binding.progressBar.visibility = View.GONE
            binding.recyclerView.visibility = View.VISIBLE
        }
    }

    private fun showError(message: String) {
        Toast.makeText(this, "Error: $message", Toast.LENGTH_SHORT).show()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_host_activity, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_settings -> {
                Toast.makeText(this, "Settings clicked", Toast.LENGTH_SHORT).show()
                return true
            }
            R.id.action_search -> {
                Toast.makeText(this, "Search clicked", Toast.LENGTH_SHORT).show()
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }
}
