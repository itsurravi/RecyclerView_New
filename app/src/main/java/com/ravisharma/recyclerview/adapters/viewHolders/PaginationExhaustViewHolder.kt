package com.ravisharma.recyclerview.adapters.viewHolders

import androidx.recyclerview.widget.RecyclerView
import com.ravisharma.recyclerview.models.RecyclerViewModel
import com.ravisharma.recyclerview.databinding.CellPaginationExhaustBinding
import com.ravisharma.recyclerview.interfaces.Interaction

class PaginationExhaustViewHolder(
    private val binding: CellPaginationExhaustBinding,
): RecyclerView.ViewHolder(binding.root), PaginationExhaustViewHolderBind<RecyclerViewModel> {
    override fun bind(interaction: Interaction<RecyclerViewModel>) {
        binding.topButton.setOnClickListener { interaction.onExhaustButtonPressed() }
    }
}