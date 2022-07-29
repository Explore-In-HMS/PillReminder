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
import com.hms.codelab.pillreminder.databinding.LayoutDosageBinding

class DosageDialog  {
    private var builder: AlertDialog.Builder
    private lateinit var alertDialog: AlertDialog
    private val dialogBinding: LayoutDosageBinding
    private val context: Context

    companion object {
        private lateinit var instance: DosageDialog
        fun getInstance(context: Context): DosageDialog {
            instance = DosageDialog(context)
            return instance
        }
    }

    private constructor(context: Context) {
        this.context = context
        dialogBinding = LayoutDosageBinding.inflate(LayoutInflater.from(context))
        builder = AlertDialog.Builder(context).setCancelable(false)
    }

    fun setPlusButton(): DosageDialog {
        dialogBinding.btnPlus.setOnClickListener{
                dialogBinding.etDosage.text = (dialogBinding.etDosage.text.toString().toInt() + 1).toString()
        }
        return this
    }

    fun setMinusButton(): DosageDialog {
        dialogBinding.btnMinus.setOnClickListener {
                dialogBinding.etDosage.text = (dialogBinding.etDosage.text.toString().toInt() - 1).toString()
        }
        return this
    }

    fun setCancelButton(negativeText: String): DosageDialog {
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
        resultList:(String)-> Unit
    ): DosageDialog {
        dialogBinding.btnOkDosage.apply {
            visibility = View.VISIBLE
            text = positiveText
            setOnClickListener {
                onClickListener.onClick()
                dismissDialog()
                resultList(dialogBinding.etDosage.text.toString())
            }
        }
        return this
    }

    fun createDialog(): DosageDialog {
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