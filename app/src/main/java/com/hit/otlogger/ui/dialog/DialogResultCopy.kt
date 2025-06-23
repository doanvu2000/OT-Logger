package com.hit.otlogger.ui.dialog

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
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
    fun show(message: String, monthSelected: Int, yearSelected: Int, totalOTMinutes: Int) {
        dialog.setCancelable(true)
        binding.tvTitle.text = "OT Tháng $monthSelected/$yearSelected"
        binding.tvContent.text = message

        val totalHours = totalOTMinutes / 60
        val totalMinutes = totalOTMinutes - totalHours * 60
        val timeFloat = (totalHours + totalMinutes / 60.0f).floorOneNumber()

        val des = when (timeFloat) {
            in 0f..10f -> {
                "Chill chill thôi, không cần làm nhiều đâu!"
            }

            in 10.0f..15f -> {
                "Chill chill thôi, không cần làm nhiều đâu!"
            }

            in 15.0f..25f -> {
                "Làm ít thôi, nghỉ ngơi nhiều hơn!"
            }

            else -> {
                "Nhiều quá rồi đó bro, sắp xếp thời gian nghỉ ngơi đi nhé!"
            }
        }

        binding.tvDescription.text = des

        if (!isShowing()) {
            dialog.show()
        }

        binding.btnExit.clickWithAnimation { hide() }
    }
}