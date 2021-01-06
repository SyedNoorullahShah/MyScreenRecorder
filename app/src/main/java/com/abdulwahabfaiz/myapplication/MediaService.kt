package com.abdulwahabfaiz.myapplication

import android.app.Service
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.hardware.display.DisplayManager
import android.hardware.display.VirtualDisplay
import android.media.CamcorderProfile
import android.media.MediaRecorder
import android.media.projection.MediaProjection
import android.media.projection.MediaProjectionManager
import android.os.Build
import android.os.Environment
import android.os.IBinder
import android.provider.MediaStore
import com.abdulwahabfaiz.myapplication.NotificationUtils.Companion.notification
import java.io.File
import java.util.*
import kotlin.concurrent.timerTask


class MediaService : Service() {
    private lateinit var mVirtualDisplay: VirtualDisplay
    private lateinit var mMediaRecorder: MediaRecorder
    private lateinit var mMediaProjection: MediaProjection

    companion object {
        var isRecording = false
            private set
        const val BROADCAST_ACTION = "updateUI"
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        isRecording = true
        sendBroadcast(broadcastIntent())
    }

    private fun broadcastIntent() =
        Intent(BROADCAST_ACTION).apply { putExtra("isRecording", isRecording) }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startForeground()

        val mResultCode = intent!!.getIntExtra("code", -1)
        val mScreenWidth = intent.getIntExtra("width", 720)
        val mScreenHeight = intent.getIntExtra("height", 1280)
        val mScreenDensity = intent.getIntExtra("density", 1)

        mMediaProjection = createMediaProjection(mResultCode, intent);
        mMediaRecorder = createMediaRecorder(mScreenWidth, mScreenHeight);
        mVirtualDisplay = createVirtualDisplay(
            mMediaProjection,
            mMediaRecorder,
            mScreenDensity,
            mScreenWidth,
            mScreenHeight
        )
        mMediaRecorder.start()
        startTimer()

        return START_STICKY
    }

    private fun startTimer() {
        Timer().schedule(timerTask {
            stopSelf()
        }, 10_000)
    }

    override fun onDestroy() {
        isRecording = false
        mMediaRecorder.stop()
        mMediaProjection.stop()
        mVirtualDisplay.release()
        sendBroadcast(broadcastIntent())

        super.onDestroy()
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
        profile.videoFrameWidth = mScreenWidth
        profile.videoFrameHeight = mScreenHeight
        mMediaRecorder.setProfile(profile)
        setOutputFile(mMediaRecorder)
        mMediaRecorder.prepare()
        return mMediaRecorder
    }

    private fun setOutputFile(mediaRecorder: MediaRecorder) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val contentVals = ContentValues()
            contentVals.put(MediaStore.MediaColumns.DISPLAY_NAME, "Record_${System.currentTimeMillis()}")
            contentVals.put(MediaStore.MediaColumns.MIME_TYPE, "video/mp4")
            contentVals.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_MOVIES)
            val uri =
                contentResolver.insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, contentVals)
            val fileDescriptor = contentResolver.openFileDescriptor(uri!!, "w")
            mediaRecorder.setOutputFile(fileDescriptor!!.fileDescriptor)
        } else {
            val fileDir =
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES)
            val file = File(fileDir, "Record_${System.currentTimeMillis()}.mp4")
            mediaRecorder.setOutputFile(file.path)
        }
    }

    private fun createMediaProjection(resultCode: Int, data: Intent?): MediaProjection {
        val manager = getSystemService(Context.MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
        return manager.getMediaProjection(resultCode, data!!) as MediaProjection
    }

/*val mFolder = File("${filesDir}/sample")
        val recFile = File(mFolder.absolutePath.toString() + "/some.mp4")
        if (!mFolder.exists()) mFolder.mkdir()
        if (!recFile.exists()) recFile.createNewFile()*/
}