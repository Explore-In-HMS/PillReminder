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
package com.hms.codelab.pillreminder.ui.allreminders

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.hms.codelab.pillreminder.R
import com.hms.codelab.pillreminder.adapter.PillAdapter
import com.hms.codelab.pillreminder.data.room.Reminder
import com.hms.codelab.pillreminder.data.room.ReminderDatabase
import com.hms.codelab.pillreminder.databinding.FragmentAllRemindersBinding
import com.hms.codelab.pillreminder.ui.base.BaseFragment
import com.hms.codelab.pillreminder.utils.Constant
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_all_reminders.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import kotlin.system.measureTimeMillis
import androidx.navigation.fragment.findNavController

@AndroidEntryPoint
class AllRemindersFragment : BaseFragment<AllRemindersViewModel, FragmentAllRemindersBinding>(){

    private val allRemindersViewModel: AllRemindersViewModel by activityViewModels()

    var pillAdapter: PillAdapter? = null
    var listSize: Int = 0
    var remindersArrayList: ArrayList<Reminder> = ArrayList<Reminder>()

    override fun getViewModel(): AllRemindersViewModel = allRemindersViewModel

    override fun getFragmentViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentAllRemindersBinding {
        return FragmentAllRemindersBinding.inflate(inflater, container, false)
    }

    override fun setupListeners() {
        fragmentViewBinding.btnAdd.setOnClickListener {
            findNavController().navigate(R.id.action_allRemindersFragment_to_addReminderFragment)
        }
    }

    override fun setupUi() {
        super.setupUi()
        cleanSharedPrefs()
        checkReminderList()
    }

    fun getReminderList(context: Context) = GlobalScope.async {
        val reminderDatabase: ReminderDatabase = ReminderDatabase.getReminderDatabase(context)!!
        val reminderList = reminderDatabase.getReminderDao().getAllReminders()
        if (reminderList.isNotEmpty()) {
            remindersArrayList.clear()
            for (it in reminderList) {
                listSize = reminderList.size
                remindersArrayList.add(Reminder(it.id, it.name, it.frequently, it.dosage, it.time, it.days))
                Log.i(
                    Constant.TAG_ALL_REMINDERS_FRAGMNET,
                    "REMINDERSSS : " + it.id + it.name + it.frequently + it.dosage + it.time + it.days
                )
            }
        } else {
            listSize = 0
        }
    }

    fun checkReminderList(){
        measureTimeMillis {
            val one = getReminderList(requireContext())
            runBlocking {
                println("The answer is ${one.await()}")
                if(listSize == 0){
                    imageView.visibility = View.VISIBLE
                    tvDesc.visibility = View.VISIBLE
                    btnAdd.visibility = View.VISIBLE
                    rvReminders.visibility = View.GONE
                }else{
                    imageView.visibility = View.GONE
                    tvDesc.visibility = View.GONE
                    btnAdd.visibility = View.GONE
                    rvReminders.visibility = View.VISIBLE

                    pillAdapter = PillAdapter(remindersArrayList, requireContext(), "AllReminders")
                    pillAdapter!!.notifyDataSetChanged()
                    rvReminders!!.layoutManager = LinearLayoutManager(requireContext())
                    rvReminders!!.adapter = pillAdapter

                }
            }
        }
    }

    fun cleanSharedPrefs(){
        val sharedPreferences: SharedPreferences = requireContext()!!.getSharedPreferences("APP_PREF_1", Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor = sharedPreferences.edit()
        editor.clear()
        editor.apply()
    }
}