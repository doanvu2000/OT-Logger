package com.hit.otlogger.ui.dialog

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import com.hit.otlogger.R
import com.hit.otlogger.databinding.DialogEditDescriptionItemBinding

class DialogEditDescriptionItem(private val context: Context) {
    private val binding by lazy {
        DialogEditDescriptionItemBinding.inflate(
            LayoutInflater.from(context)
        )
    }

    private val dialog by lazy {
        AlertDialog.Builder(context, R.style.dialog_transparent_width).setView(binding.root)
            .create()
    }

    private fun isShowing(): Boolean {
        return dialog.isShowing
    }

    private fun hide() {
        dialog.dismiss()
    }

    fun show(
        title: String, subTitle: String, description: String = "", onConfirm: (String) -> Unit
    ) {
        if (isShowing()) return
        binding.apply {
            tvTitle.text = title
            tvSubTitle.text = subTitle
            binding.edtDescription.setText(description)
            if (description.isNotEmpty()) {
                binding.edtDescription.setSelection(description.length)
            }
            binding.btnDone.setOnClickListener {
                val newDescription = binding.edtDescription.text.toString()
//                if (newDescription.isEmpty()) {
//                    binding.edtDescription.error = "Mô tả không được để trống"
//                    return@setOnClickListener
//                }
                onConfirm(newDescription)
                hide()
            }
        }
        dialog.show()
    }
}