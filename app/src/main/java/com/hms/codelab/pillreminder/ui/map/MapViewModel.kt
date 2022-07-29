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

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.hms.codelab.pillreminder.service.LocationService
import com.hms.codelab.pillreminder.ui.base.BaseViewModel
import com.hms.codelab.pillreminder.utils.Constant
import com.huawei.hms.maps.model.LatLng
import com.huawei.hms.site.api.SearchResultListener
import com.huawei.hms.site.api.SearchService
import com.huawei.hms.site.api.model.*
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MapViewModel @Inject constructor(
    val locationService: LocationService
) : BaseViewModel() {

    val nearbySearchResultLiveData = MutableLiveData<List<Site>?>()

    private val _lastKnownLocation = MutableLiveData<LatLng?>()
    val lastKnownLocation: LiveData<LatLng?>
        get() = _lastKnownLocation

    fun setLastKnownLocation(latLng: LatLng?) {
        _lastKnownLocation.value = latLng
    }

    fun nearbyPlaceSearch(
        searchService: SearchService,
        lat: Double,
        lng: Double,
        locationType: LocationType
    ) {
        val request = NearbySearchRequest()
        val location = Coordinate(lat, lng)
        request.location = location
        request.radius = 1000
        request.poiType = locationType
        request.language = "tr"
        request.pageIndex = 1
        request.pageSize = 20
        request.strictBounds = false
        val resultListener: SearchResultListener<NearbySearchResponse> =
            object : SearchResultListener<NearbySearchResponse> {
                override fun onSearchResult(results: NearbySearchResponse?) {
                    if (results == null || results.totalCount <= 0) {
                        return
                    }
                    val sites: List<Site>? = results.sites
                    if (sites == null || sites.isEmpty()) {
                        return
                    }
                    nearbySearchResultLiveData.postValue(sites)
                    for (site in sites) {
                        Log.i(
                            Constant.TAG_MAP_VIEWMODEL,
                            "siteId: ${site.siteId}, name: ${site.name}"
                        )
                    }
                }

                override fun onSearchError(status: SearchStatus) {
                    Log.i(
                        Constant.TAG_MAP_VIEWMODEL,
                        "Error : ${status.errorCode}  ${status.errorMessage}"
                    )
                }
            }
        searchService.nearbySearch(request, resultListener)
    }

    fun getNearbySearchResult() = nearbySearchResultLiveData

    fun clearViewModel() {
        setLastKnownLocation(null)
    }
}