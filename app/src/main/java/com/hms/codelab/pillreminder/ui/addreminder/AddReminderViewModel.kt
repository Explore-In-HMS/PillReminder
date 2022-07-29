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
package com.hms.codelab.pillreminder.ui.addreminder

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import com.hms.codelab.pillreminder.ui.base.BaseViewModel
import com.hms.codelab.pillreminder.utils.Constant
import com.hms.codelab.pillreminder.utils.MyAlarmBroadcastReceiver
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.*
import javax.inject.Inject

@HiltViewModel
class AddReminderViewModel @Inject constructor(
    @ApplicationContext private val context: Context
) : BaseViewModel() {

    private val SUNDAY = 1
    private val MONDAY = 2
    private val TUESDAY = 3
    private val WEDNESDAY = 4
    private val THURSDAY = 5
    private val FRIDAY = 6
    private val SATURDAY = 7

    fun setAlarm(hour: Int, minute: Int, day: Int, context: Context){
        var calendar: Calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, hour)
        calendar.set(Calendar.MINUTE, minute)
        calendar.set(Calendar.DAY_OF_WEEK, day)

        runAlarm(calendar.timeInMillis, context)
        Log.i(Constant.TAG_ADDREMINDER_VIEWMODEL, "TIMEINMILISVALUE : " + calendar.timeInMillis)
    }

    fun runAlarm(timeInMillis: Long, context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, MyAlarmBroadcastReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0)
        alarmManager.setRepeating(
            AlarmManager.RTC,
            timeInMillis,
            AlarmManager.INTERVAL_DAY,
            pendingIntent
        )
    }
}