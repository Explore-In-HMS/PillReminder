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
import android.view.LayoutInflater
import android.view.View
import com.hms.codelab.pillreminder.databinding.LayoutDaysBinding

class DaysDialog  {
    private var builder: AlertDialog.Builder
    private lateinit var alertDialog: AlertDialog
    private val dialogBinding: LayoutDaysBinding
    private val context: Context

    companion object {
        private lateinit var instance: DaysDialog
        fun getInstance(context: Context): DaysDialog {
            instance = DaysDialog(context)
            return instance
        }
    }

    private constructor(context: Context) {
        this.context = context
        dialogBinding = LayoutDaysBinding.inflate(LayoutInflater.from(context))
        builder = AlertDialog.Builder(context).setCancelable(false)
    }

    fun setCancelButton(negativeText: String): DaysDialog {
        dialogBinding.btnCancelDays.apply {
            visibility = View.VISIBLE
            text = negativeText
            setOnClickListener { dismissDialog() }
        }
        return this
    }

    fun setPositiveButton(
        positiveText: String,
        onClickListener: ICustomDialogClickListener,
        resultList:(ArrayList<String>)-> Unit
    ): DaysDialog {
        dialogBinding.btnOkDays.apply {
            visibility = View.VISIBLE
            text = positiveText
            setOnClickListener {
                onClickListener.onClick()
                dismissDialog()
                val checkedDays: ArrayList<String> = ArrayList()
                if(dialogBinding.checkBoxMonday.isChecked){
                    checkedDays.add(dialogBinding.checkBoxMonday.text.toString())
                }
                if(dialogBinding.checkBoxTuesday.isChecked){
                    checkedDays.add(dialogBinding.checkBoxTuesday.text.toString())
                }
                if(dialogBinding.checkBoxWednesday.isChecked){
                    checkedDays.add(dialogBinding.checkBoxWednesday.text.toString())
                }
                if(dialogBinding.checkBoxThursday.isChecked){
                    checkedDays.add(dialogBinding.checkBoxThursday.text.toString())
                }
                if(dialogBinding.checkBoxFriday.isChecked){
                    checkedDays.add(dialogBinding.checkBoxFriday.text.toString())
                }
                if(dialogBinding.checkBoxSaturday.isChecked){
                    checkedDays.add(dialogBinding.checkBoxSaturday.text.toString())
                }
                if(dialogBinding.checkBoxSunday.isChecked){
                    checkedDays.add(dialogBinding.checkBoxSunday.text.toString())
                }
                resultList(checkedDays)
            }
        }
        return this
    }

    fun createDialog(): DaysDialog {
        builder.setView(dialogBinding.root)
        alertDialog = builder.create()
        alertDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
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