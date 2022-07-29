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

import android.app.Activity
import android.util.Log
import com.hms.codelab.pillreminder.utils.Constant
import com.huawei.hmf.tasks.OnFailureListener
import com.huawei.hmf.tasks.OnSuccessListener
import com.huawei.wearengine.HiWear
import com.huawei.wearengine.device.Device
import com.huawei.wearengine.p2p.Receiver
import com.huawei.wearengine.auth.AuthCallback
import com.huawei.wearengine.client.ServiceConnectionListener
import com.huawei.wearengine.client.WearEngineClient
import com.huawei.wearengine.monitor.MonitorItem
import com.huawei.wearengine.monitor.MonitorListener
import com.huawei.wearengine.notify.Action
import com.huawei.wearengine.notify.NotificationConstants
import com.huawei.wearengine.notify.NotificationTemplate
import com.hms.codelab.pillreminder.ui.base.BaseViewModel
import com.huawei.wearengine.auth.AuthClient
import com.huawei.wearengine.device.DeviceClient
import com.huawei.wearengine.monitor.MonitorClient
import com.huawei.wearengine.notify.NotifyClient
import com.huawei.wearengine.p2p.P2pClient
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ConnectDeviceViewModel @Inject constructor(
) : BaseViewModel() {

    private lateinit var deviceClient: DeviceClient
    private lateinit var authClient: AuthClient
    private lateinit var p2pClient: P2pClient
    private lateinit var monitorClient: MonitorClient
    private lateinit var notifyClient: NotifyClient
    private lateinit var wearEngineClient: WearEngineClient

    private var nBuilder = com.huawei.wearengine.notify.Notification.Builder()

    private var isDeviceAvailable: Boolean = false
    private var isDeviceManagerPermissionGranted: Boolean = false

    private val deviceList: ArrayList<Device> = ArrayList()
    private var connectedDevice: Device? = null
    private var devicePkgName: String = "com.wearable.pillreminder"
    private lateinit var receiver: Receiver

    fun init(deviceClient1: DeviceClient, authClient1: AuthClient, p2pClient1: P2pClient, monitorClient1: MonitorClient, notifyClient1: NotifyClient, wearEngineClient1: WearEngineClient, receiver1: Receiver) {
        deviceClient = deviceClient1
        authClient = authClient1
        p2pClient = p2pClient1
        monitorClient = monitorClient1
        notifyClient = notifyClient1
        wearEngineClient = wearEngineClient1
        receiver = receiver1

        checkAvailableDevices()
    }

    private fun checkAvailableDevices(){
        deviceClient.hasAvailableDevices().addOnSuccessListener {
            isDeviceAvailable = true
            checkMultiplePermissions()
        }.addOnFailureListener {
            isDeviceAvailable = true
            Log.i(Constant.TAG_CONNECT_VIEWMODEL,"Available Device Error " + it.message)
        }
    }

    private fun checkDeviceManagerPermission(){
        authClient.checkPermission(com.huawei.wearengine.auth.Permission.DEVICE_MANAGER).addOnSuccessListener {
            isDeviceManagerPermissionGranted = it
            if (isDeviceManagerPermissionGranted) {
                Log.i(Constant.TAG_CONNECT_VIEWMODEL, "Device Manager permission is granted.")
                getBondedDevicesFromDeviceClient()
            } else {
                requestDeviceManagerPermission()
            }
        }.addOnFailureListener {
            isDeviceManagerPermissionGranted = false
            Log.i(Constant.TAG_CONNECT_VIEWMODEL, "Failed get Auth Permission. ${it.message}")
        }
    }

    private fun checkMultiplePermissions() {
        val permissions = arrayOf(com.huawei.wearengine.auth.Permission.DEVICE_MANAGER, com.huawei.wearengine.auth.Permission.NOTIFY)
        authClient.checkPermissions(permissions).addOnSuccessListener {
            Log.i(Constant.TAG_CONNECT_VIEWMODEL, "Permissions: $permissions Check: $it")
            var isPermissionsGranted = true
            for (permission in it) {
                if (!permission) {
                    isPermissionsGranted = false
                }
            }
            if (isPermissionsGranted) {
                getBondedDevicesFromDeviceClient()
            } else {
                requestDeviceManagerPermission()
            }
        }.addOnFailureListener {
            Log.i(Constant.TAG_CONNECT_VIEWMODEL, "Failed to get permissions. ${it.message}")
        }
    }

    private fun requestDeviceManagerPermission() {
        val authCallback: AuthCallback = object : AuthCallback {
            override fun onOk(permissions: Array<com.huawei.wearengine.auth.Permission>) {
                Log.i(Constant.TAG_CONNECT_VIEWMODEL, "Permissions: $permissions")
                isDeviceManagerPermissionGranted = true
                getBondedDevicesFromDeviceClient()
            }

            override fun onCancel() {
                Log.i(Constant.TAG_CONNECT_VIEWMODEL, "User canceled permission process.")
            }
        }

        authClient.requestPermission(authCallback, com.huawei.wearengine.auth.Permission.DEVICE_MANAGER, com.huawei.wearengine.auth.Permission.NOTIFY)
            .addOnSuccessListener {
                Log.i(Constant.TAG_CONNECT_VIEWMODEL, "Permission process success.")
            }
            .addOnFailureListener {
                Log.i(Constant.TAG_CONNECT_VIEWMODEL, "Permission process failed. ${it.message}")
                isDeviceManagerPermissionGranted = false
            }
    }

    private fun getBondedDevicesFromDeviceClient() {
        deviceClient.bondedDevices
            .addOnSuccessListener { devices ->
                deviceList.addAll(devices)
                if (deviceList.isNotEmpty()) {
                    for (device in deviceList) {
                        Log.i(Constant.TAG_CONNECT_VIEWMODEL, "Device: $device")
                        if (device.isConnected) {
                            connectedDevice = device
                        }else{
                            Log.i(Constant.TAG_CONNECT_VIEWMODEL, "Device Not Founded")
                        }
                    }
                    if (connectedDevice != null) {
                        checkApplicationHasInstalled()
                        Log.i(Constant.TAG_CONNECT_VIEWMODEL, "Connected Device: $connectedDevice")
                        obtainClientApiLevel()
                        checkDeviceConnectionWithMonitor()
                    }
                }
            }
            .addOnFailureListener {
                Log.i(Constant.TAG_CONNECT_VIEWMODEL, "Failed to get bonded devices. ${it.message}")
            }
    }

    private fun checkApplicationHasInstalled() {
        if (connectedDevice != null && connectedDevice!!.isConnected) {
            p2pClient.isAppInstalled(connectedDevice, devicePkgName)
                .addOnSuccessListener {
                    if (it) {
                        checkApplicationIsRunning()
                        Log.i(Constant.TAG_CONNECT_VIEWMODEL, "App is installed already.")
                    } else {
                        Log.i(Constant.TAG_CONNECT_VIEWMODEL, "Application is not installed. Install it.")
                    }
                }
                .addOnFailureListener {
                    Log.i(Constant.TAG_CONNECT_VIEWMODEL, "Failed to check application install status. ${it.message}")
                }
        }
    }

    private fun checkApplicationIsRunning() {
        if (connectedDevice != null && connectedDevice!!.isConnected) {
            p2pClient.ping(connectedDevice) {
                Log.i(Constant.TAG_CONNECT_VIEWMODEL, "Result: $it")
            }.addOnSuccessListener {
                Log.i(Constant.TAG_CONNECT_VIEWMODEL, "Pinged successfully.")
                receiveMessageFromWearable()
            }.addOnFailureListener {
                Log.i(Constant.TAG_CONNECT_VIEWMODEL, "Ping failed.")
            }
        }
    }

    private fun receiveMessageFromWearable() {
        p2pClient.setPeerPkgName(devicePkgName)
        //TODO
        val peerFingerPrint = "ADD YOUR WEARABLE DEVICE FINGER PRINT"

        p2pClient.setPeerFingerPrint(peerFingerPrint)

        if (connectedDevice != null && connectedDevice!!.isConnected) {
            p2pClient.registerReceiver(connectedDevice, receiver)
                .addOnFailureListener {
                    Log.i(Constant.TAG_CONNECT_VIEWMODEL, "Message receive failed.")
                }
                .addOnSuccessListener {
                    Log.i(Constant.TAG_CONNECT_VIEWMODEL, "Messages received successfully.")
                }
        }
    }

    private fun obtainClientApiLevel() {
        wearEngineClient.clientApiLevel
            .addOnSuccessListener(OnSuccessListener<Int> { integer ->
                Log.i(Constant.TAG_CONNECT_VIEWMODEL, "sdk apiLevel: $integer")
            })
            .addOnFailureListener(OnFailureListener { e ->
                Log.e(Constant.TAG_CONNECT_VIEWMODEL, "sdk apiLevel e: " + e.message)
            })
    }

    private fun checkDeviceConnectionWithMonitor() {
        val monitorListener = MonitorListener { errorCode, monitorItem, monitorData ->
            if (MonitorItem.MONITOR_ITEM_CONNECTION.name == monitorItem.name) {
                val connectionStatus = monitorData.asInt()
                if (connectionStatus == 3) {
                    Log.i(Constant.TAG_CONNECT_VIEWMODEL, "Device is disconnected.")
                } else if (connectionStatus == 2) {
                    Log.i(Constant.TAG_CONNECT_VIEWMODEL, "Device is disconnected.")
                }
            }
        }

        if (connectedDevice != null && connectedDevice!!.isConnected) {
            monitorClient.register(connectedDevice, MonitorItem.MONITOR_ITEM_CONNECTION, monitorListener)
                .addOnSuccessListener {
                    Log.i(Constant.TAG_CONNECT_VIEWMODEL, "Monitor Client registered successfully.")
                }
                .addOnFailureListener {
                    Log.i(Constant.TAG_CONNECT_VIEWMODEL, "Monitor Client register failed.")
                }
        }
    }

    fun sendNotification() {
        nBuilder.setTemplateId(NotificationTemplate.NOTIFICATION_TEMPLATE_TWO_BUTTONS)
        val packageName = "com.wearable.pillreminder"
        nBuilder.setPackageName(packageName)
        val title = "Lite Wearable Demo"
        nBuilder.setTitle(title)
        val text = "You are seeing a notification right now."
        nBuilder.setText(text)
        val buttonContents = HashMap<Int, String>()
        buttonContents[NotificationConstants.BUTTON_ONE_CONTENT_KEY] = "Yes"
        buttonContents[NotificationConstants.BUTTON_TWO_CONTENT_KEY] = "No"
        nBuilder.setButtonContents(buttonContents)
        val action: Action = object : Action {
            override fun onResult(notification: com.huawei.wearengine.notify.Notification?, feedback: Int) {
                var feedbackMsg = "Empty"
                when (feedback) {
                    0 -> feedbackMsg = "Exit or turn off the screen by pressing the home button"
                    1 -> feedbackMsg = "Delete a message"
                    2 -> feedbackMsg = "Touches the Yes button"
                    3 -> feedbackMsg = "Touches the No button"
                }
                Log.i(Constant.TAG_CONNECT_VIEWMODEL, "User pressed. Feedback Number: $feedback. Msg: $feedbackMsg")
            }

            override fun onError(notification: com.huawei.wearengine.notify.Notification?, errorCode: Int, errorMsg: String?) {
                Log.i(Constant.TAG_CONNECT_VIEWMODEL, "Error Code: $errorCode - Error Msg: $errorMsg")
            }
        }
        nBuilder.setAction(action)
        val notification = nBuilder.build()

        if (connectedDevice != null && connectedDevice!!.isConnected) {
            notifyClient.notify(connectedDevice, notification).addOnSuccessListener {
                Log.i(Constant.TAG_CONNECT_VIEWMODEL, "Notification sent successfully.")
            }.addOnFailureListener {
                Log.i(Constant.TAG_CONNECT_VIEWMODEL, "Failed to send notification. Msg: ${it.message}")
            }
        }
    }
}