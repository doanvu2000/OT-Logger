package com.hit.otlogger.ui.home

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import com.hit.otlogger.base.BaseAdapterRecyclerView
import com.hit.otlogger.data.model.OTModel
import com.hit.otlogger.databinding.ItemMonthTotalLoggerBinding
import com.hit.otlogger.util.CalendarUtil
import com.hit.otlogger.util.toDayFormat
import com.hit.otlogger.util.toHourMinuteFormat

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
            var totalTime = String.format("%.1f", hour + minutes.toFloat() / 60)

            totalTime = if (totalTime.endsWith(".0") || totalTime.endsWith(",0")) {
                "OT: ${totalTime.toFloat().toInt()} giờ"
            } else {
                "OT: $totalTime giờ"
            }

            binding.tvTimeOT.text =
                "$totalTime (${item.timeStart.toHourMinuteFormat()} - ${item.timeEnd.toHourMinuteFormat()})"
        }
    }
}