package hr.algebra.recipe.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import hr.algebra.recipe.databinding.SearchItemBinding
import hr.algebra.recipe.model.AutocompleteRecipe

class SearchAdapter(
    private var searchResults: List<AutocompleteRecipe>,
    private val onRecipeClick: (Int) -> Unit
) : RecyclerView.Adapter<SearchAdapter.SearchViewHolder>() {

    inner class SearchViewHolder(private val binding: SearchItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(recipe: AutocompleteRecipe) {
            binding.tvRecipeTitle.text = recipe.title

            Picasso.get()
                .load(recipe.imageUrl)
                .placeholder(hr.algebra.recipe.R.drawable.ic_placeholder)
                .error(hr.algebra.recipe.R.drawable.ic_error)
                .into(binding.ivRecipeImage)

            binding.root.setOnClickListener {
                onRecipeClick(recipe.id)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchViewHolder {
        val binding = SearchItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SearchViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SearchViewHolder, position: Int) {
        holder.bind(searchResults[position])
    }

    override fun getItemCount() = searchResults.size

    fun updateData(newResults: List<AutocompleteRecipe>) {
        searchResults = newResults
        notifyDataSetChanged()
    }
}
