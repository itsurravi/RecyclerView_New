package com.ravisharma.recyclerview.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.ravisharma.recyclerview.utils.RecyclerViewDiffUtilCallback
import com.ravisharma.recyclerview.utils.RecyclerViewEnum
import com.ravisharma.recyclerview.models.RecyclerViewModel
import com.ravisharma.recyclerview.adapters.viewHolders.*
import com.ravisharma.recyclerview.databinding.CellEmptyBinding
import com.ravisharma.recyclerview.databinding.CellErrorBinding
import com.ravisharma.recyclerview.databinding.CellItemBinding
import com.ravisharma.recyclerview.databinding.CellLoadingBinding
import com.ravisharma.recyclerview.databinding.CellPaginationExhaustBinding
import com.ravisharma.recyclerview.databinding.CellPaginationLoadingBinding
import com.ravisharma.recyclerview.interfaces.Interaction

class RecyclerViewAdapter(
    override val interaction: Interaction<RecyclerViewModel>,
    private val extraInteraction: RecyclerViewInteraction
) : BaseAdapter<RecyclerViewModel>(interaction) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            RecyclerViewEnum.View.value -> {
                ItemViewHolder(CellItemBinding.inflate(LayoutInflater.from(parent.context), parent, false), extraInteraction)
            }
            RecyclerViewEnum.Loading.value -> {
                LoadingViewHolder(CellLoadingBinding.inflate(LayoutInflater.from(parent.context), parent, false))
            }
            RecyclerViewEnum.PaginationLoading.value -> {
                PaginationLoadingViewHolder(CellPaginationLoadingBinding.inflate(LayoutInflater.from(parent.context), parent, false))
            }
            RecyclerViewEnum.PaginationExhaust.value -> {
                PaginationExhaustViewHolder(CellPaginationExhaustBinding.inflate(LayoutInflater.from(parent.context), parent, false))
            }
            RecyclerViewEnum.Error.value -> {
                ErrorItemViewHolder(CellErrorBinding.inflate(LayoutInflater.from(parent.context), parent, false))
            }
            else -> {
                EmptyViewHolder(CellEmptyBinding.inflate(LayoutInflater.from(parent.context), parent, false))
            }
        }
    }

    override fun handleDiffUtil(newList: ArrayList<RecyclerViewModel>) {
        val diffUtil = RecyclerViewDiffUtilCallback(
            arrayList,
            newList
        )
        val diffResults = DiffUtil.calculateDiff(diffUtil, true)
        arrayList = newList.toList() as ArrayList<RecyclerViewModel>

        diffResults.dispatchUpdatesTo(this)
    }
}