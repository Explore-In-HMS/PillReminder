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
package com.hms.codelab.pillreminder.ui.map

import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.hms.codelab.pillreminder.BuildConfig
import com.hms.codelab.pillreminder.R
import com.hms.codelab.pillreminder.custom.Permission
import com.hms.codelab.pillreminder.custom.PermissionManager
import com.hms.codelab.pillreminder.databinding.FragmentMapBinding
import com.hms.codelab.pillreminder.service.LocationService
import com.hms.codelab.pillreminder.ui.base.BaseFragment
import com.hms.codelab.pillreminder.utils.Constant
import com.hms.codelab.pillreminder.utils.LocationUtil
import com.huawei.hms.location.*
import com.huawei.hms.maps.CameraUpdateFactory
import com.huawei.hms.maps.HuaweiMap
import com.huawei.hms.maps.OnMapReadyCallback
import com.huawei.hms.maps.model.LatLng
import com.huawei.hms.maps.model.MarkerOptions
import com.huawei.hms.site.api.SearchServiceFactory
import com.huawei.hms.site.api.model.LocationType
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_map.*
import java.net.URLEncoder

@AndroidEntryPoint
class MapFragment : BaseFragment<MapViewModel, FragmentMapBinding>(), OnMapReadyCallback {

    private val mapViewModel: MapViewModel by activityViewModels()

    override fun getViewModel(): MapViewModel = mapViewModel

    private lateinit var huaweiMap: HuaweiMap
    private val permissionManager = PermissionManager.from(this)

    private lateinit var locationRequest: LocationRequest
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var locationService: LocationService
    private lateinit var locationUtil: LocationUtil

    private lateinit var currentLatLng: LatLng

    override fun getFragmentViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentMapBinding {
        return FragmentMapBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        locationService = LocationService(requireContext())

        fusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(requireContext())
        locationRequest = LocationRequest.create()
        locationRequest.apply {
            interval = 500
            fastestInterval = 500
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }
    }

    override fun setupUi() {
        super.setupUi()

        mapViewModel.clearViewModel()

        val searchService by lazy {
            SearchServiceFactory.create(
                context,
                URLEncoder.encode(BuildConfig.API_KEY, "UTF-8")
            )
        }

        permissionManager
            .request(Permission.Location)
            .rationale("We need permission to access your location.")
            .checkPermission { granted ->
                if (granted) {
                    fragmentViewBinding.huaweiMap.apply {
                        onCreate(null)
                        getMapAsync(this@MapFragment)
                    }
                } else {
                    Log.i(Constant.TAG_MAP_FRAGMNET, "We couldn't access the camera.")
                    findNavController().navigate(R.id.allRemindersFragment)
                }
            }

        btnPharmacy.setOnClickListener {
            huaweiMap.clear()
            mapViewModel.nearbyPlaceSearch(
                searchService,
                currentLatLng.latitude,
                currentLatLng.longitude,
                LocationType.PHARMACY
            )
            btnPharmacy.setBackgroundResource(R.drawable.green_button)
            btnHospital.setBackgroundResource(R.drawable.button_background)
        }

        btnHospital.setOnClickListener {
            huaweiMap.clear()
            mapViewModel.nearbyPlaceSearch(
                searchService,
                currentLatLng.latitude,
                currentLatLng.longitude,
                LocationType.HOSPITAL
            )
            btnPharmacy.setBackgroundResource(R.drawable.button_background)
            btnHospital.setBackgroundResource(R.drawable.green_button)
        }
    }

    override fun setupObservers() {
        super.setupObservers()
        mapViewModel.lastKnownLocation.observe(viewLifecycleOwner) {
            it?.let { latLang ->
                huaweiMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLang, 18f))
                Log.i(
                    Constant.TAG_MAP_FRAGMNET,
                    "setupObservers Location : " + it.latitude + ", " + it.longitude
                )
                currentLatLng = LatLng(it.latitude, it.longitude)
            }
        }

        mapViewModel.getNearbySearchResult().observe(viewLifecycleOwner) {
            if (it != null) {
                for (i in it) {
                    addSiteMarker(
                        LatLng(i.location.lat, i.location.lng),
                        i.name
                    )
                    Log.i(
                        "TAG",
                        "Selected Site : " + i.name + "LatLng : " + i.location.lat + "," + i.location.lng
                    )
                }
                huaweiMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 12f))
            }
        }
    }

    override fun onMapReady(map: HuaweiMap) {
        huaweiMap = map
        setUpMapView()
    }

    private fun addSiteMarker(latLng: LatLng, title: String) {
        huaweiMap.apply {
            addMarker(
                MarkerOptions()
                    .position(latLng)
                    .draggable(false)
                    .title(title)
            )
        }
    }

    private fun setUpMapView() {
        huaweiMap.apply {
            isMyLocationEnabled = true
            uiSettings.isMyLocationButtonEnabled = true
            uiSettings.isZoomControlsEnabled = true
            mapType = HuaweiMap.MAP_TYPE_NORMAL
        }
        getLastKnownLocation()
    }

    private fun getLastKnownLocation() {
        locationUtil = LocationUtil(requireActivity())
        mapViewModel.locationService.getLastLocation { lastLocation ->
            if (lastLocation.latitude != 0.0) {
                mapViewModel.setLastKnownLocation(lastLocation)
            } else {
                if (!mapViewModel.locationService.isLocationEnabled()) {
                    locationUtil.checkLocationSettings {
                        startLocationUpdates()
                    }
                }
            }
        }
    }

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult?) {
            super.onLocationResult(locationResult)
            locationResult?.lastLocation?.let {
                print("Location : $it")
                mapViewModel.setLastKnownLocation(LatLng(it.latitude, it.longitude))
            }
        }
    }

    private fun startLocationUpdates() {
        fusedLocationProviderClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.getMainLooper()
        )
    }

    private fun stopLocationUpdates() {
        fusedLocationProviderClient.removeLocationUpdates(locationCallback)
    }

    override fun onStart() {
        super.onStart()
        fragmentViewBinding.huaweiMap.onStart()
    }

    override fun onStop() {
        super.onStop()
        fragmentViewBinding.huaweiMap.onStop()
    }

    override fun onResume() {
        super.onResume()
        fragmentViewBinding.huaweiMap.onResume()
    }

    override fun onDestroy() {
        super.onDestroy()
        fragmentViewBinding.huaweiMap.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        fragmentViewBinding.huaweiMap.onLowMemory()
    }
}