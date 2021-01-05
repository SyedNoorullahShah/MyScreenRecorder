package com.abdulwahabfaiz.myapplication

import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.hardware.display.DisplayManager
import android.media.CamcorderProfile
import android.media.MediaRecorder
import android.media.projection.MediaProjection
import android.media.projection.MediaProjectionManager
import android.os.Build
import android.os.Environment
import android.os.IBinder
import com.abdulwahabfaiz.myapplication.NotificationUtils.Companion.notification
import java.io.File
import java.util.*
import kotlin.concurrent.timerTask

class MediaService : Service() {
    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startForeground()

        val mResultCode = intent!!.getIntExtra("code", -1)
        val mScreenWidth = intent.getIntExtra("width", 720)
        val mScreenHeight = intent.getIntExtra("height", 1280)
        val mScreenDensity = intent.getIntExtra("density", 1)

        val mMediaProjection = createMediaProjection(mResultCode, intent);
        val mMediaRecorder = createMediaRecorder(mScreenWidth, mScreenHeight);
        val mVirtualDisplay = createVirtualDisplay(
            mMediaProjection,
            mMediaRecorder,
            mScreenDensity,
            mScreenWidth,
            mScreenHeight
        )
        mMediaRecorder.start()
        Timer().schedule(timerTask {
            mMediaRecorder.stop()
            mMediaProjection.stop()
            mVirtualDisplay.release()
            stopSelf()
        }, 10_000)
        return START_STICKY

    }

    private fun startForeground() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            startForeground(
                1,
                notification(this),
                ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PROJECTION
            )
        } else {
            startForeground(1, notification(this));
        }
    }

    private fun createVirtualDisplay(
        mMediaProjection: MediaProjection,
        mMediaRecorder: MediaRecorder,
        screenDensity: Int,
        mScreenWidth: Int,
        mScreenHeight: Int
    ) =
        mMediaProjection.createVirtualDisplay(
            "MY DISPLAY",
            mScreenWidth, mScreenHeight, screenDensity,
            DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
            mMediaRecorder.surface, null, null
        )


    private fun createMediaRecorder(mScreenWidth: Int, mScreenHeight: Int): MediaRecorder {
        val mMediaRecorder = MediaRecorder()
        mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.DEFAULT)
        mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.SURFACE)
        val profile: CamcorderProfile = CamcorderProfile.get(CamcorderProfile.QUALITY_HIGH)
        profile.videoFrameHeight = mScreenHeight
        profile.videoFrameWidth = mScreenWidth
        mMediaRecorder.setProfile(profile)

        mMediaRecorder.setOutputFile(getFileName())
        mMediaRecorder.prepare()
        return mMediaRecorder
    }

    private fun getFileName(): String {
        val mFolder = File("${filesDir}/sample")
        val recFile = File(mFolder.absolutePath.toString() + "/some.mp4")
        if (!mFolder.exists()) mFolder.mkdir()
        if (!recFile.exists()) recFile.createNewFile()
        return recFile.path
    }

    private fun createMediaProjection(resultCode: Int, data: Intent?): MediaProjection {
        val manager = getSystemService(Context.MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
        return manager.getMediaProjection(resultCode, data!!) as MediaProjection
    }


}