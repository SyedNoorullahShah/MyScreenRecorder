package com.abdulwahabfaiz.myapplication

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.projection.MediaProjectionManager
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat


class MainActivity : AppCompatActivity() {
    private val REQUEST_MEDIA_PROJECTION: Int = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val manager = getSystemService(Context.MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.RECORD_AUDIO
            ) != PackageManager.PERMISSION_GRANTED
        ) {

            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE), 11);

        } else {

            startActivityForResult(manager.createScreenCaptureIntent(), REQUEST_MEDIA_PROJECTION)

        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        data!!.setClass(this, MediaService::class.java)
        data.putExtra("code", resultCode)

        ContextCompat.startForegroundService(this, data)
    }


}


/*

    private val TAG = "noor"

    private val PERMISSION_CODE = 1
    private val RESOLUTIONS: List<Resolution> = arrayListOf(
        Resolution(640, 360),
        Resolution(960, 540),
        Resolution(1366, 768),
        Resolution(1600, 900)
    )

    private var mScreenDensity = 0
    private var mProjectionManager: MediaProjectionManager? = null
    private var mDisplayWidth = 0
     var mDisplayHeight = 0
    private var mScreenSharing = false
    private var mMediaProjection: MediaProjection? = null
    private var mVirtualDisplay: VirtualDisplay? = null
    private var mSurface: Surface? = null
    private var mSurfaceView: SurfaceView? = null
    private var mToggle: ToggleButton? = null
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val metrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(metrics)
        mScreenDensity = metrics.densityDpi
        mSurfaceView = binding.surface
        mSurface = mSurfaceView!!.holder.surface
        mProjectionManager =
            getSystemService(Context.MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
        val arrayAdapter = ArrayAdapter(
            this, R.layout.simple_list_item_1, RESOLUTIONS
        )
        val s = binding.spinner
        s.adapter = arrayAdapter
        s.onItemSelectedListener = ResolutionSelector()
        s.setSelection(0)
        mToggle = binding.screenSharingToggle
        mToggle!!.isSaveEnabled = false
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode != PERMISSION_CODE) {
            Log.e(TAG, "Unknown request code: $requestCode")
            return
        }
        if (resultCode != RESULT_OK) {
            Toast.makeText(
                this,
                "User denied screen sharing permission", Toast.LENGTH_SHORT
            ).show()
            return
        }
        mMediaProjection = mProjectionManager!!.getMediaProjection(resultCode, data!!)
            .apply { registerCallback(MediaProjectionCallback(), null) }
        mVirtualDisplay = createVirtualDisplay()
    }

    fun onToggleScreenShare(view: View) {
        if ((view as ToggleButton).isChecked) {
            shareScreen()
        } else {
            stopScreenSharing()
        }
    }

    private fun shareScreen() {
        mScreenSharing = true
        if (mSurface == null) {
            return
        }
        if (mMediaProjection == null) {
            startActivityForResult(
                mProjectionManager!!.createScreenCaptureIntent(),
                PERMISSION_CODE
            )
            return
        }
        mVirtualDisplay = createVirtualDisplay()
    }

    private fun stopScreenSharing() {
        if (mToggle!!.isChecked) {
            mToggle!!.isChecked = false
        }
        mScreenSharing = false
        if (mVirtualDisplay != null) {
            mVirtualDisplay!!.release()
            mVirtualDisplay = null
        }
    }

    private fun createVirtualDisplay(): VirtualDisplay? {
        return mMediaProjection!!.createVirtualDisplay(
            "ScreenSharingDemo",
            mDisplayWidth, mDisplayHeight, mScreenDensity,
            DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
            mSurface, null Callbacks, null Handler
        )
    }

    }
*/


/*
EXTRA CODE


    private inner class MediaProjectionCallback : MediaProjection.Callback() {
        override fun onStop() {
            mMediaProjection = null
            stopScreenSharing()
        }
    }

    private inner class SurfaceCallbacks : SurfaceHolder.Callback {
        override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
            mDisplayWidth = width
            mDisplayHeight = height
            resizeVirtualDisplay()
        }

        override fun surfaceCreated(holder: SurfaceHolder) {
            mSurface = holder.surface
            if (mScreenSharing) {
                shareScreen()
            }
        }

        override fun surfaceDestroyed(holder: SurfaceHolder) {
            if (!mScreenSharing) {
                stopScreenSharing()
            }
        }
    }

    private class Resolution(var x: Int, var y: Int) {
        override fun toString(): String {
            return x.toString() + "x" + y
        }




    inner class ResolutionSelector : OnItemSelectedListener {
        override fun onItemSelected(parent: AdapterView<*>, v: View?, pos: Int, id: Long) {
            val r = parent.getItemAtPosition(pos) as Resolution
            val lp: ViewGroup.LayoutParams = mSurfaceView!!.layoutParams
            if (resources.configuration.orientation
                == Configuration.ORIENTATION_LANDSCAPE
            ) {
                mDisplayHeight = r.y
                mDisplayWidth = r.x
            } else {
                mDisplayHeight = r.x
                mDisplayWidth = r.y
            }
            lp.height = mDisplayHeight
            lp.width = mDisplayWidth
            mSurfaceView!!.layoutParams = lp
        }

        override fun onNothingSelected(parent: AdapterView<*>?) {  Ignore
        }
    }



    private fun resizeVirtualDisplay() {
        if (mVirtualDisplay == null) {
            return
        }
        mVirtualDisplay!!.resize(mDisplayWidth, mDisplayHeight, mScreenDensity)
    }


    override fun onStop() {
        stopScreenSharing()
        super.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (mMediaProjection != null) {
            mMediaProjection!!.stop()
            mMediaProjection = null
        }
    }





*/


