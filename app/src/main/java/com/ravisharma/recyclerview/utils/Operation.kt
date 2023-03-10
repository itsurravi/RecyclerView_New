package com.ravisharma.recyclerview.utils

data class Operation<out T>(
    val data: T,
    val operationEnum: OperationEnum
)

enum class OperationEnum {
    Insert,
    Delete,
    Update
}