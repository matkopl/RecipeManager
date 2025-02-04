package hr.algebra.recipe.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import hr.algebra.recipe.adapter.EquipmentAdapter
import hr.algebra.recipe.adapter.StepAdapter
import hr.algebra.recipe.databinding.FragmentRecipeInstructionBinding
import hr.algebra.recipe.model.Equipment
import hr.algebra.recipe.model.Instruction
import hr.algebra.recipe.model.Step

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
            fetchInstructions()
        } else {
            binding.tvInstructionsHeader.text = "No instructions available."
        }

        binding.btnCloseFragment.setOnClickListener {
            parentFragmentManager.popBackStack()

            requireActivity().findViewById<View>(hr.algebra.recipe.R.id.mainContent).visibility = View.VISIBLE
            requireActivity().findViewById<View>(hr.algebra.recipe.R.id.fragment_container).visibility = View.GONE
        }

    }

    private fun fetchInstructions() {
        val instructionsJson = arguments?.getString("recipe_instructions") ?: "[]"

        val type = object : TypeToken<List<Instruction>>() {}.type
        val instructions: List<Instruction> = try {
            Gson().fromJson(instructionsJson, type) ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }


        val steps = if (instructions.isNotEmpty()) instructions.first().steps ?: emptyList() else emptyList()

        val allEquipment = steps.flatMap { it.equipment ?: emptyList() }
            .distinctBy { it.id }

        if (steps.isNotEmpty()) {
            setupRecyclerViews(steps, allEquipment)
        } else {
            binding.tvInstructionsHeader.text = "No instructions available."
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
            binding.rvEquipment.layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
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
