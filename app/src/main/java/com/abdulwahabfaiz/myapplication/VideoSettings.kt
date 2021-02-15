package com.abdulwahabfaiz.myapplication

import android.content.Context
import android.os.Build
import android.text.TextUtils
import android.util.DisplayMetrics
import android.util.Log
import android.view.Display
import android.view.WindowManager
import android.widget.RadioButton
import android.widget.RadioGroup
import java.util.*
import kotlin.math.roundToInt

object VideoSettings : RadioGroup.OnCheckedChangeListener {
    //Resolution settings
    val currentMetrics = DisplayMetrics()
    val selectedMetrics = DisplayMetrics()
    private var aspectRatio: Float = 0.0f

    //Orientation settings
    var orientation = -1   //auto
        private set

    fun setMetrics(ctx: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val display: Display? = ctx.display
            display!!.getRealMetrics(currentMetrics)

        } else {
            val windowManager =
                ctx.applicationContext.getSystemService(Context.WINDOW_SERVICE) as WindowManager
            windowManager.defaultDisplay.getMetrics(currentMetrics)
        }
        if (selectedMetrics.widthPixels == 0 && selectedMetrics.heightPixels == 0) selectedMetrics.setTo(
            currentMetrics
        )   //initially

        aspectRatio = currentMetrics.widthPixels.toFloat() / currentMetrics.heightPixels.toFloat()
    }


    override fun onCheckedChanged(group: RadioGroup, checkedId: Int) {
        when (group.id) {
            R.id.resolution_options -> setResolutions(group, checkedId)
            R.id.orientation_options -> setOrientation(group, checkedId)
        }
    }

    private fun setOrientation(group: RadioGroup, checkedId: Int) {
        val selectedOrientation =
            group.findViewById<RadioButton>(checkedId).text.toString().toLowerCase(Locale.ROOT)
        orientation = when (selectedOrientation) {
            "portrait" -> {
                selectedMetrics.setTo(currentMetrics)
                0
            }
            "landscape" -> {
                selectedMetrics.apply {
                    heightPixels = currentMetrics.widthPixels
                    widthPixels = currentMetrics.heightPixels
                }
                90
            }
            else -> {
                selectedMetrics.setTo(currentMetrics)
                -1
            }
        }
        Log.d("noor", "setOrientation: $orientation")
    }

    private fun setResolutions(group: RadioGroup, checkedId: Int) {
        val resolution =
            group.findViewById<RadioButton>(checkedId).text.toString()

        if (TextUtils.isEmpty(resolution) || resolution.toLowerCase(Locale.ROOT) == "auto") selectedMetrics.setTo(
            currentMetrics
        )
        else {
            val userChosenWidth = resolution.substringBefore("P").toInt()
            selectedMetrics.apply {
                widthPixels = userChosenWidth
                heightPixels = (widthPixels.toFloat() / aspectRatio).roundToInt()
                densityDpi = currentMetrics.densityDpi
            }
        }

        Log.d(
            "noor",
            "onItemSelected: ${selectedMetrics.widthPixels}......${selectedMetrics.heightPixels}......${selectedMetrics.densityDpi}"
        )
    }
}
