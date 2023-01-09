package com.ravisharma.recyclerview.adapters.viewHolders

import androidx.recyclerview.widget.RecyclerView
import com.ravisharma.recyclerview.models.RecyclerViewModel
import com.ravisharma.recyclerview.databinding.CellErrorBinding
import com.ravisharma.recyclerview.interfaces.Interaction

class ErrorItemViewHolder(
    private val binding: CellErrorBinding,
) : RecyclerView.ViewHolder(binding.root), ErrorViewHolderBind<RecyclerViewModel> {
    override fun bind(errorMessage: String?, interaction: Interaction<RecyclerViewModel>) {
        binding.errorText.text = errorMessage

        binding.refreshButton.setOnClickListener { interaction.onErrorRefreshPressed() }
    }
}