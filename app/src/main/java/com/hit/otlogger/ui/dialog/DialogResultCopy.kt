package com.hit.otlogger.ui.dialog

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import androidx.core.graphics.toColorInt
import com.hit.otlogger.R
import com.hit.otlogger.databinding.DialogResultCopyBinding
import com.hit.otlogger.util.clickWithAnimation
import com.hit.otlogger.util.floorOneNumber

class DialogResultCopy(private val context: Context) {
    private val binding by lazy {
        DialogResultCopyBinding.inflate(LayoutInflater.from(context))
    }

    private val dialog by lazy {
        AlertDialog.Builder(context, R.style.dialog_transparent_width).setView(binding.root)
            .create()
    }

    fun isShowing() = dialog.isShowing
    fun hide() {
        dialog.dismiss()
    }

    @SuppressLint("SetTextI18n")
    fun show(
        message: String,
        monthSelected: Int,
        yearSelected: Int,
        totalOTMinutes: Int,
        avg: Int
    ) {
        dialog.setCancelable(true)
        binding.tvTitle.text = "OT Tháng $monthSelected/$yearSelected"

        val totalHours = totalOTMinutes / 60
        val totalMinutes = totalOTMinutes - totalHours * 60
        val timeFloat = (totalHours + totalMinutes / 60.0f).floorOneNumber()

        val avgHours = avg / 60
        val avgMinutes = avg - avgHours * 60
        val avgFloat = (avgHours + avgMinutes / 60.0f).floorOneNumber()


        var textColorDescription = "#000000".toColorInt()

        val des = when (avgFloat) {
            in 0f..0.5f -> {
                "Chill chill thôi, không cần làm nhiều đâu!"
            }

            in 0.51f..0.9f -> {
                textColorDescription = "#e68f25".toColorInt()
                "Làm ít thôi, nghỉ ngơi nhiều hơn!"
            }

            else -> {
                textColorDescription = "#de1010".toColorInt()
                "Nhiều quá rồi đó bro, sắp xếp thời gian nghỉ ngơi đi nhé!"
            }
        }


        val result = StringBuilder(message)
        result.append("\n\nTrung bình một ngày: $avgFloat giờ ($avgHours giờ $avgMinutes phút)")
        binding.tvContent.text = result

        binding.tvDescription.setTextColor(textColorDescription)
        binding.tvDescription.text = des

        if (!isShowing()) {
            dialog.show()
        }

        binding.btnExit.clickWithAnimation { hide() }
    }
}