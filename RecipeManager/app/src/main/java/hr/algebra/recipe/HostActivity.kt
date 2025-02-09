package hr.algebra.recipe

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.ActionMenuView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import hr.algebra.recipe.adapter.RecipeAdapter
import hr.algebra.recipe.adapter.SearchAdapter
import hr.algebra.recipe.databinding.ActivityHostBinding
import hr.algebra.recipe.fragment.SettingsFragment
import hr.algebra.recipe.model.Recipe
import hr.algebra.recipe.repository.RecipeRepository
import hr.algebra.recipe.viewmodel.RecipeViewModel
import kotlinx.coroutines.launch

class HostActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHostBinding
    private lateinit var recipeAdapter: RecipeAdapter
    private lateinit var searchAdapter: SearchAdapter
    private val recipeViewModel: RecipeViewModel by viewModels()
    private val repository = RecipeRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHostBinding.inflate(layoutInflater)
        setContentView(binding.root)

        overridePendingTransition(R.anim.fade_in, R.anim.fade_out)

        setSupportActionBar(binding.toolbar)

        setupRecyclerView()
        setupSearchRecyclerView()
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

    private fun setupSearchRecyclerView() {
        searchAdapter = SearchAdapter(emptyList()) { recipeId ->
            fetchRecipeById(recipeId)
        }
        binding.searchRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.searchRecyclerView.adapter = searchAdapter
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

        val searchItem = menu?.findItem(R.id.action_search)
        val searchView = searchItem?.actionView as? SearchView

        searchView?.queryHint = "Search for recipes..."
        searchView?.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                Log.d("HostActivity", "Search query changed: $newText")
                if (!newText.isNullOrEmpty()) {
                    fetchSearchResults(newText)
                } else {
                    binding.recyclerView.visibility = View.VISIBLE
                    binding.searchRecyclerView.visibility = View.GONE
                }
                return true
            }

        })

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_settings -> {
                Log.d("HostActivity", "Settings clicked!") // Debug log
                openSettingsFragment()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount > 0) {
            supportFragmentManager.popBackStack()

            binding.toolbar.visibility = View.VISIBLE
            binding.fragmentContainer.visibility = View.GONE
            binding.recyclerView.visibility = View.VISIBLE
            binding.searchRecyclerView.visibility = View.GONE

        } else {
            super.onBackPressed()
        }
    }

    private fun fetchSearchResults(query: String) {
        Log.d("HostActivity", "Fetching results for: $query") // Debug log

        repository.getRecipeAutocomplete(query, 10,
            onSuccess = { results ->
                runOnUiThread {
                    Log.d("HostActivity", "Search results received: ${results.size}") // Debug log
                    searchAdapter.updateData(results)
                    binding.searchRecyclerView.visibility = if (results.isNotEmpty()) View.VISIBLE else View.GONE
                    binding.recyclerView.visibility = if (results.isEmpty()) View.VISIBLE else View.GONE
                }
            },
            onError = { message ->
                Log.e("HostActivity", "Error fetching search results: $message")
                runOnUiThread {
                    Toast.makeText(this, "Error fetching search results: $message", Toast.LENGTH_SHORT).show()
                }
            }
        )
    }

    private fun fetchRecipeById(recipeId: Int) {
        repository.getRecipeById(recipeId,
            onSuccess = { recipe ->
                runOnUiThread { openRecipeDetail(recipe) }
            },
            onError = { message ->
                runOnUiThread {
                    Toast.makeText(this, "Error fetching recipe: $message", Toast.LENGTH_SHORT).show()
                }
            }
        )
    }

    private fun openSettingsFragment() {
        Log.d("HostActivity", "Opening Settings Fragment...")

        val fragment = SettingsFragment()
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment, "SETTINGS_FRAGMENT")
            .addToBackStack(null)
            .commit()

        binding.toolbar.visibility = View.GONE

        binding.fragmentContainer.visibility = View.VISIBLE
        binding.recyclerView.visibility = View.GONE
        binding.searchRecyclerView.visibility = View.GONE
    }

    fun updateRecipeCount(count: Int) {
        recipeViewModel.fetchRandomRecipes(count);
    }

    fun showToolbar(visible: Boolean) {
        binding.toolbar.visibility = if (visible) View.VISIBLE else View.GONE
    }
}
