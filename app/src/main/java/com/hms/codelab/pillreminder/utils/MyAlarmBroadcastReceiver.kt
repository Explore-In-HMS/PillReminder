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

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.ActivityCompat
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

class MyAlarmBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(
        context: Context,
        intent: Intent
    ) {
        Log.d("Alarm Bell", "Alarm just fired")

        NotificationBuilderHelper.showNotification(
            context,
            "Alarm Time",
            "This is content"
        )
        val wearEngineClient: WearEngineClient
        val wearableNotificationBuilder: WearableNotificationBuilder = WearableNotificationBuilder()

        val permissionsStorage = arrayOf<String>(Manifest.permission.READ_EXTERNAL_STORAGE)
        val requestExternalStorage = 1
        val permission =
            ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE)

        val receiver: Receiver = Receiver { message ->
            Log.i("Wearable", String(message.data, StandardCharsets.UTF_8))

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

        wearEngineClient = HiWear.getWearEngineClient(context, serviceConnectionListener)
        wearEngineClient.registerServiceConnectionListener()
            .addOnSuccessListener {
                Log.i(
                    Constant.TAG_CONNECT_FRAGMNET,
                    "Registered service connection listener successfully."
                )
            }
            .addOnFailureListener {
                Log.i(
                    Constant.TAG_CONNECT_FRAGMNET,
                    "Failed to register service connection listener. "
                )
            }

        val deviceClient: DeviceClient = HiWear.getDeviceClient(context)
        val authClient: AuthClient = HiWear.getAuthClient(context)
        val p2pClient: P2pClient = HiWear.getP2pClient(context)
        val monitorClient: MonitorClient = HiWear.getMonitorClient(context)
        val notifyClient: NotifyClient = HiWear.getNotifyClient(context)

        wearableNotificationBuilder.init(
            deviceClient,
            authClient,
            p2pClient,
            monitorClient,
            notifyClient,
            wearEngineClient,
            receiver
        )
    }

}