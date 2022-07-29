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
package com.hms.codelab.pillreminder.ui.home

import android.Manifest
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.hms.codelab.pillreminder.adapter.PillAdapter
import com.hms.codelab.pillreminder.data.room.Reminder
import com.hms.codelab.pillreminder.data.room.ReminderDatabase
import com.hms.codelab.pillreminder.databinding.FragmentHomeBinding
import com.hms.codelab.pillreminder.ui.base.BaseFragment
import com.hms.codelab.pillreminder.utils.Constant
import com.huawei.wearengine.HiWear
import com.huawei.wearengine.auth.AuthClient
import com.huawei.wearengine.client.ServiceConnectionListener
import com.huawei.wearengine.client.WearEngineClient
import com.huawei.wearengine.device.DeviceClient
import com.huawei.wearengine.monitor.MonitorClient
import com.huawei.wearengine.notify.NotifyClient
import com.huawei.wearengine.p2p.P2pClient
import com.huawei.wearengine.p2p.Receiver
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_connect_device.*
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import java.nio.charset.StandardCharsets
import java.text.SimpleDateFormat
import java.util.*
import kotlin.system.measureTimeMillis

@AndroidEntryPoint
class HomeFragment : BaseFragment<HomeViewModel, FragmentHomeBinding>() {

    private val homeViewModel: HomeViewModel by activityViewModels()

    var remindersArrayList: ArrayList<Reminder> = ArrayList<Reminder>()
    var listSize: Int = 0
    var pillAdapter: PillAdapter? = null

    private lateinit var deviceClient: DeviceClient
    private lateinit var authClient: AuthClient
    private lateinit var p2pClient: P2pClient
    private lateinit var monitorClient: MonitorClient
    private lateinit var notifyClient: NotifyClient
    private lateinit var wearEngineClient: WearEngineClient
    private lateinit var receiver: Receiver

    override fun getViewModel(): HomeViewModel = homeViewModel

    override fun getFragmentViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentHomeBinding {
        return FragmentHomeBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    override fun setupUi() {
        super.setupUi()

        val permissionsStorage = arrayOf<String>(Manifest.permission.READ_EXTERNAL_STORAGE)
        val requestExternalStorage = 1
        val permission = ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE)
        if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), permissionsStorage, requestExternalStorage)
        }

        receiver = Receiver { message ->
            Log.i(Constant.TAG_HOME_FRAGMNET, String(message.data, StandardCharsets.UTF_8));
            requireActivity().runOnUiThread {
                textView2.text = String(message.data, StandardCharsets.UTF_8)
            }
        }

        val serviceConnectionListener: ServiceConnectionListener = object :
            ServiceConnectionListener {
            override fun onServiceConnect() {
                Log.i(Constant.TAG_HOME_FRAGMNET, "Service Connection Listener is successful.")
            }

            override fun onServiceDisconnect() {
                Log.i(Constant.TAG_HOME_FRAGMNET, "Service Connection Listener failed.")
            }
        }

        wearEngineClient = HiWear.getWearEngineClient(requireActivity(), serviceConnectionListener)
        wearEngineClient.registerServiceConnectionListener()
            .addOnSuccessListener {
                Log.i(Constant.TAG_HOME_FRAGMNET, "Registered service connection listener successfully.")
            }
            .addOnFailureListener {
                Log.i(Constant.TAG_HOME_FRAGMNET, "Failed to register service connection listener. ")
            }

        deviceClient = HiWear.getDeviceClient(requireActivity())
        authClient = HiWear.getAuthClient(requireActivity())
        p2pClient = HiWear.getP2pClient(requireActivity())
        monitorClient = HiWear.getMonitorClient(requireActivity())
        notifyClient = HiWear.getNotifyClient(requireActivity())

        homeViewModel.init(deviceClient,authClient,p2pClient,monitorClient,notifyClient, wearEngineClient, receiver)

        cleanSharedPrefs()

        checkReminderList()

        tv_empty_desc.setOnClickListener {
            cleanDatabase()
        }

        tvDailyTitle.setOnClickListener {
            cleanDatabase()
        }
    }

    fun cleanDatabase(){
        Thread {
            val userDatabase: ReminderDatabase = ReminderDatabase.getReminderDatabase(this.requireContext())!!
            userDatabase.getReminderDao().deleteAllReminders()
        }.start()
    }

    fun getCurrentReminderList(context: Context) = GlobalScope.async {
        val reminderDatabase: ReminderDatabase = ReminderDatabase.getReminderDatabase(context)!!
        val reminderList = reminderDatabase.getReminderDao().getAllReminders()
        if (reminderList.isNotEmpty()) {
            remindersArrayList.clear()
            for (it in reminderList) {
                listSize = reminderList.size
                val currentTime: String = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())
                Log.i("TAg", "CURRENTTIME : " + currentTime + " DBTIME : " + it.time)
                if (currentTime < it.time) {
                    remindersArrayList.add(Reminder(it.id, it.name, it.frequently, it.dosage, it.time, it.days))
                    Log.i(
                        Constant.TAG_ALL_REMINDERS_FRAGMNET,
                        "CURRENTREMINDERSSS : " + it.id + it.name + it.frequently + it.dosage + it.time + it.days
                    )
                }
            }
        } else {
            listSize = 0
        }
    }

    fun checkReminderList(){
        measureTimeMillis {
            val one = getCurrentReminderList(requireContext())
            runBlocking {
                println("The answer is ${one.await()}")
                if(listSize == 0){
                    iv_empty.visibility = View.VISIBLE
                    tv_empty_desc.visibility = View.VISIBLE
                    btn_add.visibility = View.VISIBLE
                    rvCurrentReminders.visibility = View.GONE
                }else{
                    iv_empty.visibility = View.GONE
                    tv_empty_desc.visibility = View.GONE
                    btn_add.visibility = View.GONE
                    rvCurrentReminders.visibility = View.VISIBLE

                    pillAdapter = PillAdapter(remindersArrayList, requireContext(), "Today")
                    pillAdapter!!.notifyDataSetChanged()
                    rvCurrentReminders!!.layoutManager = LinearLayoutManager(requireContext())
                    rvCurrentReminders!!.adapter = pillAdapter
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

