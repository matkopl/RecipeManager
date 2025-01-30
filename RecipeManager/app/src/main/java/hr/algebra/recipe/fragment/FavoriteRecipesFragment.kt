package hr.algebra.recipe.fragment

import android.database.Cursor
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import hr.algebra.recipe.R
import hr.algebra.recipe.RECIPE_PROVIDER_CONTENT_URI
import hr.algebra.recipe.adapter.RecipeAdapter
import hr.algebra.recipe.databinding.FragmentFavoriteRecipesBinding
import hr.algebra.recipe.model.Recipe

class FavoriteRecipesFragment : Fragment() {
    private var _binding: FragmentFavoriteRecipesBinding? = null
    private val binding get() = _binding!!
    private lateinit var favoritesAdapter: RecipeAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentFavoriteRecipesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        favoritesAdapter = RecipeAdapter(emptyList()) { /* Handle click */ }
        binding.rvFavorites.layoutManager = LinearLayoutManager(requireContext())
        binding.rvFavorites.adapter = favoritesAdapter

        loadFavorites()
    }

    private fun loadFavorites() {
        val cursor: Cursor? = requireActivity().contentResolver.query(
            RECIPE_PROVIDER_CONTENT_URI,
            null, null, null, null
        )

        val favoritesList = mutableListOf<RecipeEntity>()

        cursor?.use {
            while (it.moveToNext()) {
                favoritesList.add(RecipeEntity.fromCursor(it))
            }
        }

        favoritesAdapter.updateRecipes(favoritesList.map { it.toRecipe() })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}