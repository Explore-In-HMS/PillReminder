/**
 * Copyright 2022. Huawei Technologies Co., Ltd. All rights reserved.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * https://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.hms.codelab.pillreminder.custom

import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import com.hms.codelab.pillreminder.databinding.LayoutTimeBinding
import com.hms.codelab.pillreminder.utils.Constant

class TimeDialog  {
    private var builder: AlertDialog.Builder
    private lateinit var alertDialog: AlertDialog
    private val dialogBinding: LayoutTimeBinding
    private val context: Context

    companion object {
        private lateinit var instance: TimeDialog
        fun getInstance(context: Context): TimeDialog {
            instance = TimeDialog(context)
            return instance
        }
    }

    private constructor(context: Context) {
        this.context = context
        dialogBinding = LayoutTimeBinding.inflate(LayoutInflater.from(context))
        builder = AlertDialog.Builder(context).setCancelable(false)
    }

    fun setCancelButton(negativeText: String): TimeDialog {
        dialogBinding.btnCancelDosage.apply {
            visibility = View.VISIBLE
            text = negativeText
            setOnClickListener { dismissDialog() }
        }
        return this
    }

    fun setPositiveButton(
        positiveText: String,
        onClickListener: ICustomDialogClickListener,
        resultList:(ArrayList<Int>)-> Unit
    ): TimeDialog {
        dialogBinding.btnOkDosage.apply {
            visibility = View.VISIBLE
            text = positiveText
            setOnClickListener {
                val selectedTime: ArrayList<Int> = ArrayList()
                onClickListener.onClick()
                dismissDialog()
                val time = checkDigit(dialogBinding.timePicker.hour) + ":" + checkDigit(dialogBinding.timePicker.minute)
                selectedTime.add(dialogBinding.timePicker.currentHour)
                selectedTime.add(dialogBinding.timePicker.currentMinute)
                Log.i(Constant.TAG_TIME_DIALOG, "SELECTED TIME : " + dialogBinding.timePicker.currentHour + ":" + dialogBinding.timePicker.currentMinute)
                resultList(selectedTime)
            }
        }
        return this
    }

    private fun checkDigit(number: Int): String {
        return if (number <= 9) "0$number" else number.toString()
    }

    fun createDialog(): TimeDialog {
        builder.setView(dialogBinding.root)
        alertDialog = builder.create()
        alertDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialogBinding.timePicker.setIs24HourView(true)
        return this
    }

    fun showDialog() {
        if (!this::alertDialog.isInitialized) {
            createDialog()
        }
        alertDialog.show()
    }

    fun dismissDialog() = alertDialog.dismiss()

    interface ICustomDialogClickListener {
        fun onClick()
    }
}