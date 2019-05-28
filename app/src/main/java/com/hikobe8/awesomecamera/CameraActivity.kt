package com.hikobe8.awesomecamera

import android.Manifest
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.LinearLayout
import com.hikobe8.awesomecamera.opengl.RayGLCameraView
import com.tbruyelle.rxpermissions2.RxPermissions
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.activity_camera.*
import javax.microedition.khronos.egl.EGLContext

class CameraActivity : AppCompatActivity() {

    private var mPermissionSubscription: Disposable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera)
        mPermissionSubscription = RxPermissions(this)
            .request(Manifest.permission.CAMERA)
            .subscribe { granted ->
                if (granted) {
                    initViews()
                } else {
                    // Oups permission denied
                    finish()
                }
            }
    }

    private fun initViews() {
        gl_view.mOnGLContextAndTextureAvailableListener =
            object : RayGLCameraView.OnGLContextAndTextureAvailableListener {
                override fun onEGLContextAndTextureAvailable(
                    eGLContext: EGLContext?,
                    textureId: Int,
                    imageWidth: Int,
                    imageHeight: Int
                ) {
                    runOnUiThread {
                        for (i in 0..4) {
                            ll_tmp.addView(RayGLCameraView(this@CameraActivity,
                                eGLContext,
                                textureId,
                                imageWidth,
                                imageHeight),
                                LinearLayout.LayoutParams(0, -1).apply {
                                weight = 1f
                            })
                        }
                    }
                }

            }
    }

    override fun onDestroy() {
        super.onDestroy()
        mPermissionSubscription?.dispose()
    }

}
