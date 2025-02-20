package com.carriez.flutter_hbb

import android.accessibilityservice.AccessibilityService
import android.app.Service
import android.content.Intent
import android.graphics.Color
import android.graphics.PixelFormat
import android.os.Build
import android.os.IBinder
import android.view.LayoutInflater
import android.view.SurfaceView
import android.view.View
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.WindowManager
import android.view.WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
import android.view.WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
import android.view.WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
import android.view.WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
import android.view.WindowManager.LayoutParams.FLAG_SECURE
import android.view.WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_NEVER
import android.view.WindowManager.LayoutParams.TYPE_ACCESSIBILITY_OVERLAY
import android.view.accessibility.AccessibilityEvent

class MyFloatService : Service() {
    private var windowManager: WindowManager? = null
    private var overlayView: SurfaceView? = null

    override fun onCreate() {
        super.onCreate()

        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager

        // 创建透明覆盖层
        overlayView = SurfaceView(this).apply {
            setBackgroundColor(Color.BLACK)
            alpha = 0.5f // 透明度设置
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
            format = PixelFormat.RGB_888 // 半透明格式
        }

        windowManager?.addView(overlayView, params)
//        overlayView?.setZOrderOnTop(true)
    }

    override fun onDestroy() {
        super.onDestroy()
        try {
            windowManager?.removeView(overlayView) // 安全移除视图
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
