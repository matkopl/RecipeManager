package hr.algebra.recipe.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import hr.algebra.recipe.model.Step
import hr.algebra.recipe.R

class StepAdapter(private val steps: List<Step>) : RecyclerView.Adapter<StepAdapter.StepViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StepViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.step_item, parent, false)
        return StepViewHolder(view)
    }

    override fun onBindViewHolder(holder: StepViewHolder, position: Int) {
        val step = steps[position]

        holder.stepNumber.text = "Step ${step.number}"
        holder.stepText.text = step.step
    }

    override fun getItemCount(): Int = steps.size

    class StepViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val stepNumber: TextView = view.findViewById(R.id.tvStepNumber)
        val stepText: TextView = view.findViewById(R.id.tvStepDescription)
    }
}

