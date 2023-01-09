package com.ravisharma.recyclerview.interfaces

interface Interaction<T> {
    fun onItemSelected(item: T)

    fun onErrorRefreshPressed()
    fun onExhaustButtonPressed()
}