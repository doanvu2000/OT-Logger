package com.hit.otlogger.ui.home

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import com.hit.otlogger.base.BaseAdapterRecyclerView
import com.hit.otlogger.data.model.OTModel
import com.hit.otlogger.databinding.ItemMonthTotalLoggerBinding
import com.hit.otlogger.util.CalendarUtil
import com.hit.otlogger.util.showOrGone
import com.hit.otlogger.util.toTimeFormat

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
        binding.tvDate.text =
            "Ngày ${item.day.toTimeFormat()}/${item.month.toTimeFormat()}/${item.year.toTimeFormat()}"

        binding.tvDescription.text = item.description

        binding.tvDescription.showOrGone(item.description.isNotEmpty())

        CalendarUtil.diffTime(
            item.hourStart, item.minutesStart, item.hourEnd, item.minutesEnd
        ) { hour, minutes ->
            val rs = StringBuilder("")
            rs.append("${item.getTimeStart()} - ${item.getTimeEnd()}")
            rs.append(
                if (minutes > 0) {
                    "($hour giờ $minutes phút)"
                } else {
                    "($hour giờ)"
                }
            )
            binding.tvTimeOT.text = rs.toString()
        }
    }
}