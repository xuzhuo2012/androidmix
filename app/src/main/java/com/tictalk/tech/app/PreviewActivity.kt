package com.tictalk.tech.app

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.ColorSpace
import android.hardware.camera2.CameraCaptureSession
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraDevice
import android.hardware.camera2.CameraManager
import android.hardware.camera2.CaptureRequest
import android.hardware.camera2.params.ColorSpaceProfiles
import android.hardware.camera2.params.OutputConfiguration
import android.hardware.camera2.params.SessionConfiguration
import android.os.Bundle
import android.os.Handler
import android.os.HandlerThread
import android.util.Log
import android.util.Range
import android.view.Surface
import android.view.SurfaceHolder
import android.view.SurfaceView
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import java.util.concurrent.Executor
import java.util.concurrent.RejectedExecutionException
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class PreviewActivity : BaseActivity() {

    private lateinit var mSurfaceView: SurfaceView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_preview)
        mSurfaceView = findViewById(R.id.camera_preview)
        mSurfaceView.holder.addCallback(object : SurfaceHolder.Callback {
            override fun surfaceCreated(holder: SurfaceHolder) {
                // To ensure that size is set, initialize camera in the view's thread
                mSurfaceView.post {
                    initializeCamera()
                }
            }

            override fun surfaceChanged(
                holder: SurfaceHolder,
                format: Int,
                width: Int,
                height: Int
            ) {
            }

            override fun surfaceDestroyed(holder: SurfaceHolder) {
            }
        })
    }

    private class HandlerExecutor(handler: Handler) : Executor {
        private val mHandler = handler

        override fun execute(command: Runnable) {
            if (!mHandler.post(command)) {
                throw RejectedExecutionException("$mHandler is shutting down");
            }
        }
    }

    private val cameraManager: CameraManager by lazy {
        val context = applicationContext
        context.getSystemService(Context.CAMERA_SERVICE) as CameraManager
    }

    private val characteristics: CameraCharacteristics by lazy {
        cameraManager.getCameraCharacteristics(cameraId)
    }

    private val cameraId: String by lazy {
        val cameraIdList = cameraManager.cameraIdList
        cameraIdList[0]
    }

    private val cameraThread = HandlerThread("CameraThread").apply { start() }

    private val cameraHandler = Handler(cameraThread.looper)


    private lateinit var session: CameraCaptureSession

    private lateinit var camera: CameraDevice

    private val previewRequest: CaptureRequest? by lazy {
        session.device.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW).apply {
            addTarget(mSurfaceView.holder.surface)

            if (previewStabilization) {
                set(
                    CaptureRequest.CONTROL_VIDEO_STABILIZATION_MODE,
                    CaptureRequest.CONTROL_VIDEO_STABILIZATION_MODE_PREVIEW_STABILIZATION
                )
            }
        }.build()
    }

    private val recordRequest: CaptureRequest by lazy {
        session.device.createCaptureRequest(CameraDevice.TEMPLATE_RECORD).apply {
            addTarget(mSurfaceView.holder.surface)
            set(CaptureRequest.CONTROL_AE_TARGET_FPS_RANGE, Range(10, 60))

            if (previewStabilization) {
                set(
                    CaptureRequest.CONTROL_VIDEO_STABILIZATION_MODE,
                    CaptureRequest.CONTROL_VIDEO_STABILIZATION_MODE_PREVIEW_STABILIZATION
                )
            }
        }.build()
    }

    private val orientation: Int by lazy {
        characteristics.get(CameraCharacteristics.SENSOR_ORIENTATION)!!
    }

    private fun initializeCamera() = lifecycleScope.launch(Dispatchers.Main) {

        // Open the selected camera
        val cameraIdList = cameraManager.cameraIdList
        val cameraId = cameraIdList[0]
        Log.d("TAG","cameraId:$cameraId")

        camera = openCamera(cameraManager, cameraId, cameraHandler)

        // Creates list of Surfaces where the camera will output frames
        val previewTargets = listOf(mSurfaceView.holder.surface)

        // Start a capture session using our open camera and list of Surfaces where frames will go
        session = createCaptureSession(camera, previewTargets, cameraHandler)

        // Sends the capture request as frequently as possible until the session is torn down or
        //  session.stopRepeating() is called
        if (previewRequest == null) {
            session.setRepeatingRequest(recordRequest, null, cameraHandler)
        } else {
            session.setRepeatingRequest(previewRequest!!, null, cameraHandler)
        }
    }

    @SuppressLint("MissingPermission")
    private suspend fun openCamera(
        manager: CameraManager,
        cameraId: String,
        handler: Handler? = null
    ): CameraDevice = suspendCancellableCoroutine { cont ->
        manager.openCamera(cameraId, object : CameraDevice.StateCallback() {

            override fun onOpened(device: CameraDevice) = cont.resume(device)

            override fun onDisconnected(device: CameraDevice) {
                Log.w("TAG", "Camera $cameraId has been disconnected")
                finish()
            }

            override fun onError(device: CameraDevice, error: Int) {
                val msg = when (error) {
                    ERROR_CAMERA_DEVICE -> "Fatal (device)"
                    ERROR_CAMERA_DISABLED -> "Device policy"
                    ERROR_CAMERA_IN_USE -> "Camera in use"
                    ERROR_CAMERA_SERVICE -> "Fatal (service)"
                    ERROR_MAX_CAMERAS_IN_USE -> "Maximum cameras in use"
                    else -> "Unknown"
                }
                val exc = RuntimeException("Camera $cameraId error: ($error) $msg")
                Log.e("TAG", exc.message, exc)
                if (cont.isActive) cont.resumeWithException(exc)
            }
        }, handler)
    }

    private suspend fun createCaptureSession(
        device: CameraDevice,
        targets: List<Surface>,
        handler: Handler
    ): CameraCaptureSession = suspendCoroutine { cont ->
        val stateCallback = object : CameraCaptureSession.StateCallback() {
            override fun onConfigured(session: CameraCaptureSession) = cont.resume(session)

            override fun onConfigureFailed(session: CameraCaptureSession) {
                val exc = RuntimeException("Camera ${device.id} session configuration failed")
                Log.e("TAG", exc.message, exc)
                cont.resumeWithException(exc)
            }

            override fun onClosed(session: CameraCaptureSession) {

            }
        }
        setupSessionWithDynamicRangeProfile(device, targets, handler, stateCallback)
    }


    private fun setupSessionWithDynamicRangeProfile(
        device: CameraDevice,
        targets: List<Surface>,
        handler: Handler,
        stateCallback: CameraCaptureSession.StateCallback
    ): Boolean {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            val outputConfigs = mutableListOf<OutputConfiguration>()
            for (target in targets) {
                val outputConfig = OutputConfiguration(target)
                outputConfigs.add(outputConfig)
            }

            val sessionConfig = SessionConfiguration(SessionConfiguration.SESSION_REGULAR,
                outputConfigs, HandlerExecutor(handler), stateCallback)
            device.createCaptureSession(sessionConfig)
            return true
        } else {
            device.createCaptureSession(targets, stateCallback, handler)
            return false
        }
    }


    companion object {
        const val useHardware = false
        const val previewStabilization = true
    }


}