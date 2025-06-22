package com.hit.otlogger.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.hit.otlogger.base.BaseFragment
import com.hit.otlogger.data.database.OTViewModel
import com.hit.otlogger.data.database.getViewModelFactory
import com.hit.otlogger.databinding.FragmentHomeBinding
import com.hit.otlogger.util.clickWithAnimation
import com.hit.otlogger.util.launchOnStarted
import com.hit.otlogger.util.setLinearLayoutManager

class HomeFragment : BaseFragment<FragmentHomeBinding>() {
    override fun inflateLayout(
        inflater: LayoutInflater, container: ViewGroup?
    ): FragmentHomeBinding {
        return FragmentHomeBinding.inflate(inflater, container, false)
    }

    private val viewModel: OTViewModel by viewModels { getViewModelFactory() }

    private val otAdapter by lazy {
        OTAdapter()
    }

    override fun initView() {
        binding.rcvOTLogger.setLinearLayoutManager(requireContext(), otAdapter)

        launchOnStarted {
            viewModel.event.collect { event ->
                when (event) {
                    OTViewModel.Event.ClickAdd -> {
                        clickAdd()
                    }
                }
            }
        }
    }

    override fun initData() {
        viewModel.getAllData()
        launchOnStarted {
            viewModel.allData.collect { data ->
                otAdapter.setDataList(data)
            }
        }
    }

    override fun initListener() {
        binding.btnAdd.clickWithAnimation {
            viewModel.clickAdd()
        }
    }

    private fun clickAdd() {
        //show dialog to add new OT
    }
}