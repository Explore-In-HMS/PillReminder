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
package com.hms.codelab.pillreminder.adapter

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.view.animation.LayoutAnimationController
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.hms.codelab.pillreminder.R
import com.hms.codelab.pillreminder.data.room.Reminder
import com.hms.codelab.pillreminder.utils.Constant
import kotlinx.android.synthetic.main.item_reminder.view.*

class PillAdapter(var reminderList: List<Reminder>, var context: Context?, currentFragment: String) :
    RecyclerView.Adapter<PillAdapter.ViewHolder>() {

    var currentFragment: String? = currentFragment

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): ViewHolder {
        val view: View = LayoutInflater.from(context)
            .inflate(R.layout.item_reminder, viewGroup, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, i: Int) {
        val animation: LayoutAnimationController =
            AnimationUtils.loadLayoutAnimation(context, R.anim.item_anim)
        viewHolder.tvPillTitle.text = reminderList[i].name
        viewHolder.tvPillFrequently.text = reminderList[i].frequently + reminderList[i].days
        viewHolder.tvPillAlarm.text = reminderList[i].time
        viewHolder.rl1.layoutAnimation = animation
        viewHolder.cardView.setOnClickListener {
            getItemId(i)
            Log.i(
                Constant.TAG_PILL_ADAPTER,
                "ITEM CLICK ID : " + reminderList[i].name + " CurrentFragment : "
            )
        }
        viewHolder.itemView.cardReminder.setOnClickListener { view ->
            val sharedPreferences: SharedPreferences =
                context!!.getSharedPreferences("APP_PREF_1", Context.MODE_PRIVATE)
            val editor: SharedPreferences.Editor = sharedPreferences.edit()
            editor.putString("reminderId", reminderList[i].id)
            editor.apply()
            editor.commit()
            if (currentFragment == "Today") {
                view.findNavController().navigate(R.id.action_homeFragment_to_addReminderFragment)
            } else {
                view.findNavController()
                    .navigate(R.id.action_allRemindersFragment_to_addReminderFragment)
            }
        }
    }

    override fun getItemCount(): Int {
        return reminderList.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tvPillTitle: TextView
        var tvPillFrequently: TextView
        var tvPillAlarm: TextView
        var rl1: RelativeLayout
        var cardView: CardView

        init {
            tvPillTitle = itemView.findViewById(R.id.tvPillTitle)
            tvPillFrequently = itemView.findViewById(R.id.tvPillFrequently)
            tvPillAlarm = itemView.findViewById(R.id.tvPillAlarm)
            rl1 = itemView.findViewById(R.id.rl1)
            cardView = itemView.findViewById(R.id.cardReminder)
        }
    }
}