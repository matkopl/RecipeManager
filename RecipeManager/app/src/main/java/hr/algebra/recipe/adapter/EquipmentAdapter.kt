package hr.algebra.recipe.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.compose.ui.text.capitalize
import androidx.compose.ui.text.toUpperCase
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import hr.algebra.recipe.R
import hr.algebra.recipe.databinding.EquipmentItemBinding
import hr.algebra.recipe.model.Equipment

class EquipmentAdapter(private val equipmentList: List<Equipment>) :
    RecyclerView.Adapter<EquipmentAdapter.EquipmentViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EquipmentViewHolder {
        val binding = EquipmentItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return EquipmentViewHolder(binding)
    }

    override fun onBindViewHolder(holder: EquipmentViewHolder, position: Int) {
        val equipment = equipmentList[position]

        holder.binding.tvEquipmentName.text = equipment.name.replaceFirstChar { it.uppercase() }

        Picasso.get()
            .load(equipment.image)
            .placeholder(R.drawable.ic_placeholder)
            .error(R.drawable.error_image)
            .into(holder.binding.ivEquipmentImage)
    }

    override fun getItemCount() = equipmentList.size

    class EquipmentViewHolder(val binding: EquipmentItemBinding) : RecyclerView.ViewHolder(binding.root)
}
