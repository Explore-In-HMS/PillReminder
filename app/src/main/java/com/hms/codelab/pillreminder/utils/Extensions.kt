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
package com.hms.codelab.pillreminder.utils

import android.content.Context
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment


fun Fragment.toast(@StringRes resId: Int, length: Int = Toast.LENGTH_SHORT) {
    context?.toast(resId, length)
}

fun Fragment.toast(message: String, length: Int = Toast.LENGTH_SHORT) {
    context?.toast(message, length)
}

fun Context.toast(message: String, length: Int) {
    Toast.makeText(this, message, length).show()
}

fun Context.toast(@StringRes resId: Int, length: Int) {
    Toast.makeText(this, getString(resId), length).show()
}