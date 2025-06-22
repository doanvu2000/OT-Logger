package com.hit.otlogger.ui.home

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import com.hit.otlogger.base.BaseAdapterRecyclerView
import com.hit.otlogger.data.model.OTModel
import com.hit.otlogger.databinding.ItemMonthTotalLoggerBinding
import com.hit.otlogger.util.CalendarUtil
import com.hit.otlogger.util.toDayFormat

class OTAdapter : BaseAdapterRecyclerView<OTModel, ItemMonthTotalLoggerBinding>() {
    override fun inflateBinding(
        inflater: LayoutInflater, parent: ViewGroup
    ): ItemMonthTotalLoggerBinding {
        return ItemMonthTotalLoggerBinding.inflate(inflater, parent, false)
    }

    @SuppressLint("SetTextI18n")
    override fun bindData(
        binding: ItemMonthTotalLoggerBinding, item: OTModel, position: Int
    ) {
        binding.tvDate.text = "Ngày ${item.date.toDayFormat()}"

        CalendarUtil.diffTime(item.timeStart, item.timeEnd) { hour, minutes ->
            val totalTime = hour + minutes.toFloat() / 60

            binding.tvDate.text = "Tổng thời gian: $totalTime giờ"
        }
    }
}