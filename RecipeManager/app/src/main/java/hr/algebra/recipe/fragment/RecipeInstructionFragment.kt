package hr.algebra.recipe.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import hr.algebra.recipe.adapter.EquipmentAdapter
import hr.algebra.recipe.adapter.StepAdapter
import hr.algebra.recipe.databinding.FragmentRecipeInstructionBinding
import hr.algebra.recipe.factory.RecipeApiFactory
import hr.algebra.recipe.model.Equipment
import hr.algebra.recipe.model.Step
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class RecipeInstructionFragment : Fragment() {

    private var _binding: FragmentRecipeInstructionBinding? = null
    private val binding get() = _binding!!

    private lateinit var stepAdapter: StepAdapter
    private lateinit var equipmentAdapter: EquipmentAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRecipeInstructionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recipeId = arguments?.getInt("recipe_id") ?: -1
        if (recipeId != -1) {
            fetchInstructions(recipeId)
        } else {
            binding.tvInstructionsHeader.text = "No instructions available."
        }

        binding.btnCloseFragment.setOnClickListener {
            requireActivity().supportFragmentManager.beginTransaction()
                .remove(this)
                .commit()

            requireActivity().findViewById<View>(hr.algebra.recipe.R.id.mainContent).visibility = View.VISIBLE
            requireActivity().findViewById<View>(hr.algebra.recipe.R.id.fragment_container).visibility = View.GONE
        }
    }

    private fun fetchInstructions(recipeId: Int) {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val response = RecipeApiFactory.apiService.getRecipeInstructions(recipeId)
                val instructionsList = response.execute().body()

                requireActivity().runOnUiThread {
                    if (!instructionsList.isNullOrEmpty()) {
                        val steps = instructionsList.first().steps ?: emptyList()

                        val allEquipment = steps.flatMap { it.equipment ?: emptyList() }
                            .distinctBy { it.id }

                        setupRecyclerViews(steps, allEquipment)
                    }
                }
            } catch (e: Exception) {
                requireActivity().runOnUiThread { binding.tvInstructionsHeader.text = "Failed to load instructions." }
            }
        }
    }

    private fun setupRecyclerViews(steps: List<Step>, equipment: List<Equipment>) {
        stepAdapter = StepAdapter(steps)
        binding.rvSteps.layoutManager = LinearLayoutManager(requireContext())
        binding.rvSteps.adapter = stepAdapter

        if (equipment.isNotEmpty()) {
            binding.tvEquipmentHeader.visibility = View.VISIBLE
            binding.rvEquipment.visibility = View.VISIBLE

            equipmentAdapter = EquipmentAdapter(equipment)
            binding.rvEquipment.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            binding.rvEquipment.adapter = equipmentAdapter
            equipmentAdapter.notifyDataSetChanged()
        } else {
            binding.tvEquipmentHeader.visibility = View.GONE
            binding.rvEquipment.visibility = View.GONE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
