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
package com.hms.codelab.pillreminder.service


import android.content.Context
import android.location.Location
import android.location.LocationManager
import android.util.Log
import com.hms.codelab.pillreminder.utils.Constant
import com.huawei.hmf.tasks.Task
import com.huawei.hms.location.FusedLocationProviderClient
import com.huawei.hms.location.LocationServices
import com.huawei.hms.maps.model.LatLng
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class LocationService @Inject constructor(
    @ApplicationContext private val context: Context
) {

    fun isLocationEnabled(): Boolean {
        val lm = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        var gpsEnabled = false
        var networkEnabled = false

        try {
            gpsEnabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER)
        } catch (ex: Exception) {
            print(ex.message)
        }

        try {
            networkEnabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
        } catch (ex: Exception) {
            print(ex.message)
        }

        return !(!gpsEnabled && !networkEnabled)
    }

    fun getLastLocation(lastKnownLocation: (LatLng) -> Unit) {
        var mFusedLocationProviderClient: FusedLocationProviderClient? = null
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)

        var latLng: LatLng
        try {
            val lastLocation: Task<Location> = mFusedLocationProviderClient!!.lastLocation
            lastLocation.addOnSuccessListener {
                if (it == null) {
                    latLng = LatLng(0.0, 0.0)
                    lastKnownLocation(latLng)
                } else {
                    latLng = LatLng(it.latitude, it.longitude)
                    lastKnownLocation(latLng)
                }
            }.addOnFailureListener {
                Log.e(Constant.TAG_LOCATION_SERVICE, "getLastLocation exception:" + it.message)
                latLng = LatLng(0.0, 0.0)
            }
        } catch (e: Exception) {
            Log.e(Constant.TAG_LOCATION_SERVICE, "getLastLocation exception:" + e.message)
            latLng = LatLng(0.0, 0.0)
        }
    }
}