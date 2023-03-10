package com.ravisharma.recyclerview.ui.main

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ravisharma.recyclerview.R
import com.ravisharma.recyclerview.adapters.RecyclerViewAdapter
import com.ravisharma.recyclerview.adapters.viewHolders.RecyclerViewInteraction
import com.ravisharma.recyclerview.databinding.FragmentMainBinding
import com.ravisharma.recyclerview.interfaces.Interaction
import com.ravisharma.recyclerview.models.RecyclerViewModel
import com.ravisharma.recyclerview.repository.PAGE_SIZE
import com.ravisharma.recyclerview.ui.BaseFragment
import com.ravisharma.recyclerview.ui.GenericBaseFragment
import com.ravisharma.recyclerview.utils.NetworkResponse
import com.ravisharma.recyclerview.utils.quickScrollToTop
import com.ravisharma.recyclerview.viewModels.MainViewModel
import kotlinx.coroutines.launch
import java.util.UUID

class MainFragment : GenericBaseFragment<FragmentMainBinding>(
    FragmentMainBinding::inflate
) {

    companion object {
        fun newInstance() = MainFragment()
    }

    private lateinit var viewModel: MainViewModel
    private var recyclerViewAdapter: RecyclerViewAdapter? = null
    private var loadingDialog: Dialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this)[MainViewModel::class.java]
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setDialog(view.context)
        setListeners()
        setRecyclerView()
        setObservers()
    }

    private fun setDialog(context: Context) {
        loadingDialog = Dialog(context)
        loadingDialog?.setCancelable(false)
        loadingDialog?.setContentView(R.layout.dialog_loading)
        loadingDialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }

    private fun setListeners() {
        binding.swipeRefreshLayout.setOnRefreshListener {
            viewModel.refreshData()

            binding.swipeRefreshLayout.isRefreshing = false
        }

        binding.errorButton.setOnClickListener {
            viewModel.throwError()
        }

        binding.appendButton.setOnClickListener {
            if (recyclerViewAdapter?.canPaginate == true && recyclerViewAdapter?.isPaginating == false) {
                viewModel.fetchData()
            }

            binding.mainRV.smoothScrollToPosition(recyclerViewAdapter?.itemCount ?: 0)
        }

        binding.insertButton.setOnClickListener {
            viewModel.insertData(RecyclerViewModel(UUID.randomUUID().toString()))
        }

        binding.paginateErrorButton.setOnClickListener {
            viewModel.exhaustPagination()
        }

        binding.fab.setOnClickListener {
            viewLifecycleOwner.lifecycleScope.launch {
                binding.mainRV.quickScrollToTop()
            }
        }
    }

    private fun setObservers() {
        viewModel.rvList.observe(viewLifecycleOwner) { response ->
            binding.swipeRefreshLayout.isEnabled = when (response) {
                is NetworkResponse.Success -> {
                    true
                }
                is NetworkResponse.Failure -> {
                    response.isPaginationError
                }
                else -> false
            }

            when (response) {
                is NetworkResponse.Failure -> {
                    recyclerViewAdapter?.setErrorView(
                        response.errorMessage,
                        response.isPaginationError
                    )
                }
                is NetworkResponse.Loading -> {
                    recyclerViewAdapter?.setLoadingView(response.isPaginating)
                }
                is NetworkResponse.Success -> {
                    recyclerViewAdapter?.setData(response.data, response.isPaginationData)
                }
            }
        }

        viewModel.rvOperation.observe(viewLifecycleOwner) { response ->
            when (response) {
                is NetworkResponse.Failure -> {
                    if (loadingDialog?.isShowing == true)
                        loadingDialog?.dismiss()
                }
                is NetworkResponse.Loading -> {
                    if (recyclerViewAdapter?.isLoading == false)
                        loadingDialog?.show()
                }
                is NetworkResponse.Success -> {
                    if (loadingDialog?.isShowing == true)
                        loadingDialog?.dismiss()
                    recyclerViewAdapter?.handleOperation(response.data)
                }
            }
        }
    }

    private fun setRecyclerView() {
        binding.mainRV.apply {
            val linearLayoutManager = LinearLayoutManager(context)
            layoutManager = linearLayoutManager

            addItemDecoration(DividerItemDecoration(context, linearLayoutManager.orientation))

            recyclerViewAdapter =
                RecyclerViewAdapter(interaction = object : Interaction<RecyclerViewModel> {
                    override fun onItemSelected(item: RecyclerViewModel) {
                        Toast.makeText(context, "Item ${item.content}", Toast.LENGTH_SHORT).show()
                    }

                    override fun onErrorRefreshPressed() {
                        viewModel.refreshData()
                    }

                    override fun onExhaustButtonPressed() {
                        viewLifecycleOwner.lifecycleScope.launch {
                            quickScrollToTop()
                        }
                    }
                }, extraInteraction = object : RecyclerViewInteraction {
                    override fun onUpdatePressed(item: RecyclerViewModel) {
                        viewModel.updateData(item.copy())
                    }

                    override fun onDeletePressed(item: RecyclerViewModel) {
                        viewModel.deleteData(item)
                    }

                    override fun onLikePressed(item: RecyclerViewModel) {
                        viewModel.toggleLikeData(item.copy())
                    }
                })

            adapter = recyclerViewAdapter

            var isScrolling = false

            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)
                    isScrolling = newState != AbsListView.OnScrollListener.SCROLL_STATE_IDLE
                }

                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)

                    val itemCount = linearLayoutManager.itemCount
                    val lastVisibleItemPosition = linearLayoutManager.findLastVisibleItemPosition()

                    if (lastVisibleItemPosition > PAGE_SIZE.plus(PAGE_SIZE.div(2)) && dy <= -75) {
                        binding.fab.show()
                    } else if (lastVisibleItemPosition <= PAGE_SIZE.plus(PAGE_SIZE.div(2)) || dy >= 60) {
                        binding.fab.hide()
                    }

                    recyclerViewAdapter?.let {
                        if (
                            isScrolling &&
                            lastVisibleItemPosition >= itemCount.minus(5) &&
                            it.canPaginate &&
                            !it.isPaginating
                        ) {
                            viewModel.fetchData()
                        }
                    }
                }
            })
        }
    }

    override fun onDestroyView() {
        if (loadingDialog?.isShowing == true) {
            loadingDialog?.dismiss()
        }
        loadingDialog = null
        super.onDestroyView()
    }
}