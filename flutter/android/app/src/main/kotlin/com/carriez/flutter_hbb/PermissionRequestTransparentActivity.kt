package com.carriez.flutter_hbb

import android.app.Activity
import android.content.Intent
import android.media.projection.MediaProjectionManager
import android.os.Build
import android.os.Bundle
import android.util.Log

import android.graphics.Color
import android.graphics.PixelFormat
import android.view.Gravity
import android.view.SurfaceView
import android.view.WindowManager
import android.view.WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
import android.view.WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
import android.view.WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE

class PermissionRequestTransparentActivity: Activity() {
    private val logTag = "permissionRequest"

    private var windowManager: WindowManager? = null
    private var blackOverlay: SurfaceView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(logTag, "onCreate PermissionRequestTransparentActivity: intent.action: ${intent.action}")

        when (intent.action) {
            ACT_REQUEST_MEDIA_PROJECTION -> {
                val mediaProjectionManager =
                    getSystemService(MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
                val intent = mediaProjectionManager.createScreenCaptureIntent()
//                addBlackOverlay()
                startActivityForResult(intent, REQ_REQUEST_MEDIA_PROJECTION)
            }
            else -> finish()
        }
    }

    private fun addBlackOverlay() {
        // 初始化 WindowManager
        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager

        // 创建黑色 SurfaceView
        blackOverlay = SurfaceView(this).apply {
            setBackgroundColor(Color.BLACK)
        }

        // 设置窗口参数
        val params = WindowManager.LayoutParams().apply {
            width = WindowManager.LayoutParams.MATCH_PARENT
            height = WindowManager.LayoutParams.MATCH_PARENT
            type = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            } else {
                WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY
            }
            flags =
                FLAG_NOT_FOCUSABLE or FLAG_LAYOUT_IN_SCREEN or FLAG_NOT_TOUCHABLE
            format = PixelFormat.TRANSPARENT // 半透明格式
        }
        params.gravity = Gravity.TOP or Gravity.START

        // 添加遮挡层到屏幕
        windowManager?.addView(blackOverlay, params)
    }

    private fun removeFloatLayer() {
        // 移除悬浮按钮
        if (blackOverlay != null) {
            windowManager?.removeView(blackOverlay)
            blackOverlay = null
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQ_REQUEST_MEDIA_PROJECTION) {
            if (resultCode == RESULT_OK && data != null) {
                launchService(data)
            } else {
                setResult(RES_FAILED)
            }
        }

        finish()
    }

    private fun launchService(mediaProjectionResultIntent: Intent) {
        Log.d(logTag, "Launch MainService")
        val serviceIntent = Intent(this, MainService::class.java)
        serviceIntent.action = ACT_INIT_MEDIA_PROJECTION_AND_SERVICE
        serviceIntent.putExtra(EXT_MEDIA_PROJECTION_RES_INTENT, mediaProjectionResultIntent)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(serviceIntent)
        } else {
            startService(serviceIntent)
        }

//        removeFloatLayer()
    }

}