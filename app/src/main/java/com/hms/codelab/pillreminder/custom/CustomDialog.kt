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
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import com.hms.codelab.pillreminder.databinding.CustomDialogBinding

class CustomDialog {
    private var builder: AlertDialog.Builder
    private lateinit var alertDialog: AlertDialog
    private val dialogBinding: CustomDialogBinding
    private val context: Context

    companion object {
        private lateinit var instance: CustomDialog
        fun getInstance(context: Context): CustomDialog {
            instance = CustomDialog(context)
            return instance
        }
    }

    private constructor(context: Context) {
        this.context = context
        dialogBinding = CustomDialogBinding.inflate(LayoutInflater.from(context))
        builder = AlertDialog.Builder(context).setCancelable(false)
    }

    fun setTitle(title: String): CustomDialog {
        dialogBinding.dialogTitle.apply {
            visibility = View.VISIBLE
            text = title
        }
        return this
    }

    fun setMessage(title: String): CustomDialog {
        dialogBinding.dialogMessage.apply {
            visibility = View.VISIBLE
            text = title
        }
        return this
    }

    fun setIcon(icon: Drawable?): CustomDialog {
        dialogBinding.dialogIcon.apply {
            visibility = View.VISIBLE
            setImageDrawable(icon)
        }
        return this
    }

    fun setCancelButton(negativeText: String): CustomDialog {
        dialogBinding.negativeButton.apply {
            visibility = View.VISIBLE
            text = negativeText
            setOnClickListener { dismissDialog() }
        }
        return this
    }

    fun setPositiveButton(
        positiveText: String,
        onClickListener: ICustomDialogClickListener
    ): CustomDialog {
        dialogBinding.positiveButton.apply {
            visibility = View.VISIBLE
            text = positiveText
            setOnClickListener {
                onClickListener.onClick()
                dismissDialog()
            }
        }
        return this
    }

    fun setNegativeButton(
        negativeText: String,
        onClickListener: ICustomDialogClickListener
    ): CustomDialog {
        dialogBinding.negativeButton.apply {
            visibility = View.VISIBLE
            text = negativeText
            setOnClickListener {
                onClickListener.onClick()
                dismissDialog()
            }
        }
        return this
    }

    fun createDialog(): CustomDialog {
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