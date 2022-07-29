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

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.hms.codelab.pillreminder.R
import com.hms.codelab.pillreminder.custom.CustomDialog
import com.hms.codelab.pillreminder.custom.DaysDialog
import com.hms.codelab.pillreminder.custom.DosageDialog
import com.hms.codelab.pillreminder.custom.TimeDialog
import com.hms.codelab.pillreminder.data.room.Reminder
import com.hms.codelab.pillreminder.data.room.ReminderDatabase
import com.hms.codelab.pillreminder.databinding.FragmentAddReminderBinding
import com.hms.codelab.pillreminder.ui.base.BaseFragment
import com.hms.codelab.pillreminder.utils.Constant
import com.hms.codelab.pillreminder.utils.toast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_add_reminder.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import java.util.*
import kotlin.system.measureTimeMillis

@AndroidEntryPoint
class AddReminderFragment : BaseFragment<AddReminderViewModel, FragmentAddReminderBinding>() {

    private val addReminderViewModel: AddReminderViewModel by activityViewModels()

    override fun getViewModel(): AddReminderViewModel = addReminderViewModel

    var selectedFrequently: String = ""
    val selectedDays: MutableList<Int> = ArrayList()
    var selectedAction: String? = null
    var selectedReminderId: String? = null
    var selectedHour: Int? = null
    var selectedMinute: Int? = null
    var sharedIdValue: String? = null
    var listSize: Int = 0
    var reminder: Reminder? = null

    override fun getFragmentViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentAddReminderBinding {
        return FragmentAddReminderBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    override fun setupUi() {
        super.setupUi()

        if(CheckSharedPrefs()){
            selectedAction = "Edit"
            checkReminderList()
        }else{
            selectedAction = "Insert"
            selectedReminderId = createRandomId()
            btnDelete.visibility = View.GONE
        }

        setSpinner()

        buttonDosage.setOnClickListener {
            dosageDialogCreate()
        }

        buttonTime.setOnClickListener {
            timeDialogCreate()
        }

        buttonDays.setOnClickListener {
            daysDialogCreate()
        }

        btnSave.setOnClickListener {
            if (isInputsCorrect()){
                createNewReminder()
            }
        }

        btnDelete.setOnClickListener {
            deleteSelectedReminder()
        }
    }

    private fun isInputsCorrect(): Boolean {
        if (etPillName.text?.trim().isNullOrBlank()){
            toast(R.string.please_enter_pill_name)
            return false
        }

        if (buttonDosage.text.toString().trim().isEmpty() || buttonDosage.text.toString().trim().toInt()<1){
            toast(R.string.please_enter_valid_dosage)
            return false
        }

        if (selectedHour == null){
            toast(R.string.please_select_hour)
            return false
        }

        if (selectedMinute == null){
            toast(R.string.please_select_minute)
            return false
        }

        return true
    }

    fun getReminderList(context: Context) = GlobalScope.async {
        val reminderDatabase: ReminderDatabase = ReminderDatabase.getReminderDatabase(context)!!
        val reminderList = reminderDatabase.getReminderDao().getAllReminders()
        if (reminderList.isNotEmpty()) {
            for (it in reminderList) {
                if(it.id == sharedIdValue)
                    reminder = Reminder(it.id, it.name, it.frequently, it.dosage, it.time, it.days)
                Log.i(
                    Constant.TAG_ADD_REMINDER_FRAGMNET,
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
                etPillName.setText(reminder!!.name)
                buttonDosage.setText(reminder!!.dosage)
                buttonTime.setText(reminder!!.time)
                selectedReminderId = reminder!!.id
            }
        }
    }

    fun setSpinner() {
        ArrayAdapter.createFromResource(
            requireContext(),
            R.array.FrequentlyList,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerFrequently.adapter = adapter
        }
        spinnerFrequently.setOnItemSelectedListener(object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View,
                position: Int,
                id: Long
            ) {
                selectedFrequently = parent.getItemAtPosition(position) as String
                if(selectedFrequently == "Specific Days"){
                    linearLayoutDays.visibility = View.VISIBLE
                }else if(selectedFrequently == "Every Day"){
                    linearLayoutDays.visibility = View.GONE
                    selectedDays.clear()
                    selectedDays.add(1)
                    selectedDays.add(2)
                    selectedDays.add(3)
                    selectedDays.add(4)
                    selectedDays.add(5)
                    selectedDays.add(6)
                    selectedDays.add(7)
                }else if(selectedFrequently == "Every Week"){
                    linearLayoutDays.visibility = View.GONE
                    val calendar = Calendar.getInstance()
                    val day = calendar[Calendar.DAY_OF_WEEK]
                    selectedDays.clear()
                    when (day) {
                        Calendar.SUNDAY -> {
                            selectedDays.add(1)
                        }
                        Calendar.MONDAY -> {
                            selectedDays.add(2)
                        }
                        Calendar.TUESDAY -> {
                            selectedDays.add(3)
                        }
                        Calendar.WEDNESDAY ->{
                            selectedDays.add(4)
                        }
                        Calendar.THURSDAY ->{
                            selectedDays.add(5)
                        }
                        Calendar.FRIDAY ->{
                            selectedDays.add(6)
                        }
                        Calendar.SATURDAY ->{
                            selectedDays.add(7)
                        }
                    }
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        })
    }

    fun dosageDialogCreate() {
        val customDialog = DosageDialog.getInstance(requireContext())
            .setPlusButton()
            .setMinusButton()
            .setPositiveButton(
                "Ok",
                object : DosageDialog.ICustomDialogClickListener {
                    override fun onClick() {
                    }
                }) {
                buttonDosage.text = it
            }
            .setCancelButton("Cancel")
            .createDialog()
        customDialog.showDialog()
    }

    fun timeDialogCreate() {
        val customDialog = TimeDialog.getInstance(requireContext())
            .setPositiveButton(
                "Ok",
                object : TimeDialog.ICustomDialogClickListener {
                    override fun onClick() {
                    }
                }) {
                buttonTime.text = it.get(0).toString() + ":" + it.get(1).toString()
                selectedHour = it.get(0)
                selectedMinute = it.get(1)
            }
            .setCancelButton("Cancel")
            .createDialog()
        customDialog.showDialog()
    }

    fun daysDialogCreate() {
        val customDialog = DaysDialog.getInstance(requireContext())
            .setPositiveButton(
                "Ok",
                object : DaysDialog.ICustomDialogClickListener {
                    override fun onClick() {
                    }
                }) {
                buttonDays.text = it.toString()
                selectedDays.clear()
                for(i in it){
                    if(i == "Sunday"){
                        selectedDays.add(1)
                    }else if(i == "Monday"){
                        selectedDays.add(2)
                    }else if(i == "Tuesday"){
                        selectedDays.add(3)
                    }else if(i == "Wednesday"){
                        selectedDays.add(4)
                    }else if(i == "Thursday"){
                        selectedDays.add(5)
                    }else if(i == "Friday"){
                        selectedDays.add(6)
                    }else if(i == "Saturday"){
                        selectedDays.add(7)
                    }
                }
            }
            .setCancelButton("Cancel")
            .createDialog()
        customDialog.showDialog()
    }

    fun createNewReminder() {
        measureTimeMillis {
            val one = addReminderToRoomDB(
                selectedReminderId?:createRandomId(),
                etPillName.text.toString(),
                selectedFrequently,
                buttonDosage.text.toString(),
                buttonTime.text.toString(),
                selectedDays.toString(),
                requireContext()
            )
            runBlocking {
                println("The answer is ${one.await()}")
                for(i in selectedDays){
                    addReminderViewModel.setAlarm(selectedHour?:return@runBlocking,selectedMinute?:return@runBlocking,i, requireContext())
                }
                showCustomDialog()
            }
        }
    }

    fun addReminderToRoomDB(
        id: String,
        name: String,
        frequently: String,
        dosage: String,
        time: String,
        days: String,
        context: Context
    ) = GlobalScope.async {
        val reminderDatabase: ReminderDatabase = ReminderDatabase.getReminderDatabase(context)!!
        reminderDatabase.getReminderDao().addReminder(Reminder(id, name, frequently, dosage, time, days))
        Log.i(Constant.TAG_ADD_REMINDER_FRAGMNET, "RoomDBInserted!")
    }

    private fun showCustomDialog() {
        val customDialog = CustomDialog.getInstance(requireContext())
            .setTitle("Wearable Pill Reminder")
            .setMessage("Reminder saved successfully.")
            .setIcon(ContextCompat.getDrawable(requireContext(), R.drawable.ic_custom_dialog))
            .setPositiveButton(
                "Ok",
                object : CustomDialog.ICustomDialogClickListener {
                    override fun onClick() {
                        findNavController().navigate(R.id.action_addReminderFragment_to_allRemindersFragment)
                    }
                })
            .createDialog()
        customDialog.showDialog()
    }

    fun createRandomId(): String{
        val randomNumber: Int = Random().nextInt(1000000000)
        return randomNumber.toString()
    }

    fun CheckSharedPrefs(): Boolean{
        val sharedPreferences: SharedPreferences = requireContext()!!.getSharedPreferences("APP_PREF_1", Context.MODE_PRIVATE)
        sharedIdValue = sharedPreferences.getString("reminderId","0")
        return sharedIdValue != "0"
    }

    fun deleteReminder(context: Context) = GlobalScope.async {
        val userDatabase: ReminderDatabase = ReminderDatabase.getReminderDatabase(context)!!
        userDatabase.getReminderDao().deleteReminderById(selectedReminderId!!)
    }

    fun deleteSelectedReminder(){
        measureTimeMillis {
            val one = deleteReminder(requireContext())
            runBlocking {
                findNavController().navigate(R.id.action_addReminderFragment_to_allRemindersFragment)
            }
        }
    }
}