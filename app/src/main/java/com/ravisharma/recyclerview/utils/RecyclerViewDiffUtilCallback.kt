package com.ravisharma.recyclerview.utils

import androidx.recyclerview.widget.DiffUtil
import com.ravisharma.recyclerview.models.RecyclerViewModel

class RecyclerViewDiffUtilCallback(
    private val oldList: List<RecyclerViewModel>,
    private val newList: List<RecyclerViewModel>,
): DiffUtil.Callback() {

    override fun getOldListSize(): Int = oldList.size

    override fun getNewListSize(): Int = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].id == newList[newItemPosition].id
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return when {
            oldList[oldItemPosition].id != newList[newItemPosition].id -> false
            oldList[oldItemPosition].content != newList[newItemPosition].content -> false
            oldList[oldItemPosition].isLiked != newList[newItemPosition].isLiked -> false
            else -> true
        }
    }
}