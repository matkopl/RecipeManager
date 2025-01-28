package hr.algebra.recipe.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import hr.algebra.recipe.R
import hr.algebra.recipe.databinding.RecipeItemBinding
import hr.algebra.recipe.model.Recipe

class RecipeAdapter(
    private val recipes: List<Recipe>,
    private val onRecipeClick: (Recipe) -> Unit
) : RecyclerView.Adapter<RecipeAdapter.RecipeViewHolder>() {

    class RecipeViewHolder(private val binding: RecipeItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(recipe: Recipe, onRecipeClick: (Recipe) -> Unit) {
            binding.tvRecipeTitle.text = recipe.title
            binding.tvRecipeDescription.text = recipe.summary

            Picasso.get()
                .load(recipe.imageUrl)
                .placeholder(R.drawable.ic_placeholder)
                .into(binding.ivRecipeImage)

            binding.root.setOnClickListener {
                onRecipeClick(recipe)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipeViewHolder {
        val binding = RecipeItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RecipeViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecipeViewHolder, position: Int) {
        holder.bind(recipes[position], onRecipeClick)
    }

    override fun getItemCount(): Int = recipes.size
}

