package com.ravisharma.recyclerview.adapters.viewHolders

import com.ravisharma.recyclerview.interfaces.Interaction


interface ItemViewHolderBind<T> {
    fun bind(item: T, position: Int, interaction: Interaction<T>)
}

interface ErrorViewHolderBind<T> {
    fun bind(errorMessage: String?, interaction: Interaction<T>)
}

interface PaginationExhaustViewHolderBind<T> {
    fun bind(interaction: Interaction<T>)
}