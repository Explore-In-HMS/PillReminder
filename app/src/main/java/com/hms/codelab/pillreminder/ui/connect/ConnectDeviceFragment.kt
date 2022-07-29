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
package com.hms.codelab.pillreminder.ui.connect

import android.Manifest
import android.content.pm.PackageManager
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.fragment.app.activityViewModels
import com.hms.codelab.pillreminder.databinding.FragmentConnectDeviceBinding
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
import java.nio.charset.StandardCharsets
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_connect_device.*

@AndroidEntryPoint
class ConnectDeviceFragment : BaseFragment<ConnectDeviceViewModel, FragmentConnectDeviceBinding>() {

    private val connectDeviceViewModel: ConnectDeviceViewModel by activityViewModels()

    override fun getViewModel(): ConnectDeviceViewModel = connectDeviceViewModel

    private lateinit var deviceClient: DeviceClient
    private lateinit var authClient: AuthClient
    private lateinit var p2pClient: P2pClient
    private lateinit var monitorClient: MonitorClient
    private lateinit var notifyClient: NotifyClient
    private lateinit var wearEngineClient: WearEngineClient
    private lateinit var receiver: Receiver

    override fun getFragmentViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentConnectDeviceBinding {
        return FragmentConnectDeviceBinding.inflate(inflater, container, false)
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
            Log.i("LiteWearable", String(message.data, StandardCharsets.UTF_8));
            requireActivity().runOnUiThread {
                textView2.text = String(message.data, StandardCharsets.UTF_8)
            }
        }


        val serviceConnectionListener: ServiceConnectionListener = object :
            ServiceConnectionListener {
            override fun onServiceConnect() {
                Log.i(Constant.TAG_CONNECT_FRAGMNET, "Service Connection Listener is successful.")
            }

            override fun onServiceDisconnect() {
                Log.i(Constant.TAG_CONNECT_FRAGMNET, "Service Connection Listener failed.")
            }
        }

        wearEngineClient = HiWear.getWearEngineClient(requireActivity(), serviceConnectionListener)
        wearEngineClient.registerServiceConnectionListener()
            .addOnSuccessListener {
                Log.i(Constant.TAG_CONNECT_FRAGMNET, "Registered service connection listener successfully.")
            }
            .addOnFailureListener {
                Log.i(Constant.TAG_CONNECT_FRAGMNET, "Failed to register service connection listener. ")
            }

        deviceClient = HiWear.getDeviceClient(requireActivity())
        authClient = HiWear.getAuthClient(requireActivity())
        p2pClient = HiWear.getP2pClient(requireActivity())
        monitorClient = HiWear.getMonitorClient(requireActivity())
        notifyClient = HiWear.getNotifyClient(requireActivity())

        connectDeviceViewModel.init(deviceClient,authClient,p2pClient,monitorClient,notifyClient, wearEngineClient, receiver)

        btn_sendNotification.setOnClickListener {
            connectDeviceViewModel.sendNotification()
        }
    }
}
