package com.hit.otlogger.ui.home

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.hit.otlogger.base.BaseFragment
import com.hit.otlogger.data.database.OTViewModel
import com.hit.otlogger.data.database.getViewModelFactory
import com.hit.otlogger.data.model.OTModel
import com.hit.otlogger.databinding.FragmentHomeBinding
import com.hit.otlogger.ui.dialog.AddOTBottomDialog
import com.hit.otlogger.ui.dialog.DialogPickMonthYear
import com.hit.otlogger.ui.dialog.DialogResultCopy
import com.hit.otlogger.util.clickWithAnimation
import com.hit.otlogger.util.launchOnStarted
import com.hit.otlogger.util.setLinearLayoutManager
import com.hit.otlogger.util.showToast

class HomeFragment : BaseFragment<FragmentHomeBinding>() {
    override fun inflateLayout(
        inflater: LayoutInflater, container: ViewGroup?
    ): FragmentHomeBinding {
        return FragmentHomeBinding.inflate(inflater, container, false)
    }

    private val viewModel: OTViewModel by viewModels { getViewModelFactory() }

    private var isFilter = false
    private var yearSelected = 0
    private var monthSelected = 0
    private var allData = mutableListOf<OTModel>()
    private val otAdapter by lazy {
        OTAdapter()
    }

    private val dialogResult by lazy {
        DialogResultCopy(requireContext())
    }

    private val dialogPickMonthYear by lazy {
        DialogPickMonthYear(requireContext())
    }

    override fun initView() {
        binding.rcvOTLogger.setLinearLayoutManager(requireContext(), otAdapter)

        launchOnStarted {
            viewModel.event.collect { event ->
                when (event) {
                    OTViewModel.Event.ClickAdd -> {
                        clickAdd()
                    }

                    OTViewModel.Event.ClickChooseMonth -> {
                        clickChooseMonth()
                    }

                    OTViewModel.Event.ClickCopy -> {
                        clickCopy()
                    }
                }
            }
        }

        // Setup swipe to delete
        setupSwipeToDelete()
    }

    private fun setupSwipeToDelete() {
        val itemTouchHelperCallback = object : ItemTouchHelper.SimpleCallback(
            0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(
                viewHolder: RecyclerView.ViewHolder, direction: Int
            ) {
                val position = viewHolder.bindingAdapterPosition
                val itemToDelete = otAdapter.dataList[position]

                // Delete from database
                viewModel.deleteData(itemToDelete)
                showToast("Item deleted")
            }
        }

        val itemTouchHelper = ItemTouchHelper(itemTouchHelperCallback)
        itemTouchHelper.attachToRecyclerView(binding.rcvOTLogger)
    }

    @SuppressLint("DefaultLocale", "SetTextI18n")
    override fun initData() {
        launchOnStarted {
            viewModel.allData.collect { data ->
                val dataSorted = data.sortedBy { it.year }.sortedBy { it.month }.sortedBy { it.day }
                allData.clear()
                allData.addAll(dataSorted)
                if (isFilter) {
                    reloadData()
                } else {
                    otAdapter.setDataList(allData)

                    viewModel.calculateTotalOT(dataSorted) { hour, minutes, total ->
                        binding.tvTotalTime.text = "OT: $hour giờ $minutes phút"
                    }
                }
            }
        }
    }

    override fun initListener() {
        binding.btnAdd.clickWithAnimation {
            viewModel.clickAdd()
        }

        binding.btnChooseMonth.clickWithAnimation {
            viewModel.clickChooseMonth()
        }

        binding.btnCopy.clickWithAnimation {
            viewModel.clickCopy()
        }
    }

    private fun clickAdd() {
        //show dialog to add new OT
        val dialog = AddOTBottomDialog()
        dialog.show(childFragmentManager, null)
        dialog.setOnTimeSelectedListener { startDay, startMonth, startYear, startHour, startMinutes, endHour, endMinutes ->
            // Handle the selected time
            viewModel.insertData(
                OTModel(
                    0,
                    day = startDay,
                    month = startMonth,
                    year = startYear,
                    hourStart = startHour,
                    minutesStart = startMinutes,
                    hourEnd = endHour,
                    minutesEnd = endMinutes
                )
            )
            showToast("OT added successfully")
        }
    }

    @SuppressLint("DefaultLocale")
    private fun clickChooseMonth() {

        launchCoroutineMain {
            dialogPickMonthYear.show { month, year ->
                monthSelected = month
                yearSelected = year
                isFilter = true
                reloadData()

                binding.btnChooseMonth.text =
                    String.format("Tháng %02d/%d", monthSelected, yearSelected)
            }
        }
    }

    @SuppressLint("DefaultLocale", "SetTextI18n")
    private fun reloadData() {
        val filteredList =
            allData.sortedBy { it.year }.sortedBy { it.month }.sortedBy { it.day }.filter { item ->
                item.year == yearSelected && item.month == monthSelected
            }
        otAdapter.setDataList(filteredList)

        viewModel.calculateTotalOT(filteredList) { hour, minutes, total ->
            binding.tvTotalTime.text = "OT: $hour giờ $minutes phút"
        }
    }

    private fun clickCopy() {
        if (!isFilter) {
            showToast("Chọn tháng để sao chép")
            return
        }

        val content =
            viewModel.getTotalFormat(allData, monthSelected, yearSelected) { msg, time, avg ->
                dialogResult.show(msg, monthSelected, yearSelected, time, avg)
            }
        requireContext().getSystemService(android.content.ClipboardManager::class.java)
            .setPrimaryClip(
                android.content.ClipData.newPlainText(
                    "Giờ OT tháng $monthSelected", content
                )
            )



        showToast("Đã sao chép giờ OT tháng $monthSelected vào clipboard")
    }
}