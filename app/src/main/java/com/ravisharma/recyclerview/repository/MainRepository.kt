package com.ravisharma.recyclerview.repository

import com.ravisharma.recyclerview.utils.NetworkResponse
import com.ravisharma.recyclerview.utils.Operation
import com.ravisharma.recyclerview.utils.OperationEnum
import com.ravisharma.recyclerview.models.RecyclerViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import java.util.UUID

const val PAGE_SIZE = 50

class MainRepository {

    private val tempList = arrayListOf<RecyclerViewModel>().apply {
        for (i in 0..PAGE_SIZE) {
            add(RecyclerViewModel(id = UUID.randomUUID().toString(), content = "Content: $i"))
        }
    }

    fun fetchData(page: Int): Flow<NetworkResponse<ArrayList<RecyclerViewModel>>> = flow {
        emit(NetworkResponse.Loading(page != 1))

        delay(2000L)

        try {
            if (page == 1) {
                emit(NetworkResponse.Success(tempList.toList() as ArrayList<RecyclerViewModel>))
            } else {
                val tempPaginationList = arrayListOf<RecyclerViewModel>().apply {
                    for (i in 0..PAGE_SIZE) {
                        add(
                            RecyclerViewModel(
                                id = UUID.randomUUID().toString(),
                                content = "Content: ${i * 2}"
                            )
                        )
                    }
                }

                if (page < 4) {
                    emit(
                        NetworkResponse.Success(
                            tempPaginationList,
                            isPaginationData = true
                        )
                    )
                } else {
                    emit(
                        NetworkResponse.Failure(
                            "Pagination Failed",
                            isPaginationError = true
                        )
                    )
                }
            }
        } catch (e: Exception) {
            emit(
                NetworkResponse.Failure(
                    errorMessage = e.message ?: e.toString(),
                    isPaginationError = page != 1
                )
            )
        }
    }.flowOn(Dispatchers.IO)

    fun deleteData(item: RecyclerViewModel): Flow<NetworkResponse<Operation<RecyclerViewModel>>> =
        flow {
            delay(1000L)

            try {
                emit(NetworkResponse.Success(Operation(item, OperationEnum.Delete)))
            } catch (e: Exception) {
                emit(NetworkResponse.Failure(e.message ?: e.toString()))
            }
        }.flowOn(Dispatchers.IO)

    fun updateData(item: RecyclerViewModel): Flow<NetworkResponse<Operation<RecyclerViewModel>>> =
        flow {
            delay(1000L)

            try {
                item.content = "Update Content ${(0..10).random()}"
                emit(NetworkResponse.Success(Operation(item, OperationEnum.Update)))
            } catch (e: Exception) {
                emit(NetworkResponse.Failure(e.message ?: e.toString()))
            }
        }.flowOn(Dispatchers.IO)

    fun toggleLikeData(item: RecyclerViewModel): Flow<NetworkResponse<Operation<RecyclerViewModel>>> =
        flow {
            delay(1000L)

            try {
                item.isLiked = !item.isLiked
                emit(NetworkResponse.Success(Operation(item, OperationEnum.Update)))
            } catch (e: Exception) {
                emit(NetworkResponse.Failure(e.message ?: e.toString()))
            }
        }.flowOn(Dispatchers.IO)

    fun insertData(item: RecyclerViewModel): Flow<NetworkResponse<Operation<RecyclerViewModel>>> =
        flow {
            emit(NetworkResponse.Loading())
            delay(1000L)

            try {
                emit(NetworkResponse.Success(Operation(item, OperationEnum.Insert)))
            } catch (e: Exception) {
                emit(NetworkResponse.Failure(e.message ?: e.toString()))
            }
        }.flowOn(Dispatchers.IO)
}