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
package com.hms.codelab.pillreminder.ui.main

import android.graphics.Color
import android.view.LayoutInflater
import android.widget.LinearLayout
import android.widget.TextView
import androidx.navigation.NavGraph
import androidx.navigation.NavInflater
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.color.MaterialColors
import com.hms.codelab.pillreminder.R
import com.hms.codelab.pillreminder.databinding.ActivityMainBinding
import com.hms.codelab.pillreminder.ui.base.BaseActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : BaseActivity<ActivityMainBinding>() {
    override fun getActivityViewBinding(inflater: LayoutInflater): ActivityMainBinding {
        return ActivityMainBinding.inflate(layoutInflater)
    }

    override fun setup() {
        super.setup()
        navigateUser()
    }

    private fun navigateUser(){
        val navHostFragment =
            (supportFragmentManager.findFragmentById(R.id.navHostFragment) as NavHostFragment)
        val inflater = navHostFragment.navController.navInflater
        navHostFragment.navController.graph = getNavGraph(inflater)

        val todayButton = findViewById<LinearLayout>(R.id.mainBottomToday)
        val addButton = findViewById<LinearLayout>(R.id.mainBottomHome)
        val allButton = findViewById<LinearLayout>(R.id.mainBottomAll)

        val tvToday = findViewById<TextView>(R.id.tvToday)
        val tvAll = findViewById<TextView>(R.id.tvAll)

        tvToday.setTextColor(Color.parseColor("#ED8C72"))

        todayButton.setOnClickListener {
            navHostFragment.findNavController().navigate(R.id.allRemindersFragment)
            changeTextViewColors()
            tvToday.setTextColor(Color.parseColor("#ED8C72"))
        }

        allButton.setOnClickListener {
            navHostFragment.findNavController().navigate(R.id.mapFragment)
            changeTextViewColors()
            tvAll.setTextColor(Color.parseColor("#ED8C72"))
        }

        addButton.setOnClickListener {
            navHostFragment.findNavController().navigate(R.id.addReminderFragment)
            changeTextViewColors()
        }
    }

    private fun getNavGraph(inflater: NavInflater): NavGraph {
        return inflater.inflate(R.navigation.app_nav_graph)
    }

    private fun changeTextViewColors(){
        val defaultColor =  MaterialColors.getColor(this, R.attr.colorOnSurface, Color.BLACK)

        val tvToday = findViewById<TextView>(R.id.tvToday)
        val tvAll = findViewById<TextView>(R.id.tvAll)

        tvToday.setTextColor(defaultColor)
        tvAll.setTextColor(defaultColor)
    }
}