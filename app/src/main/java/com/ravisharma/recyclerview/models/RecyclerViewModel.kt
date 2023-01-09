package com.ravisharma.recyclerview.models

import java.util.Objects

data class RecyclerViewModel(
    var id: String,
    var content: String = "",
    var isLiked: Boolean = false
) {
    val text: String
        get() = "ID: $id"

    override fun equals(other: Any?): Boolean {
        if (this === other) {
            return true
        }
        if (other !is RecyclerViewModel) {
            return false
        }
        return other.id == id
    }

    override fun hashCode(): Int = Objects.hash(id)
}