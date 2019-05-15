package com.hikobe8.awesomecamera.opengl

import android.graphics.SurfaceTexture
import android.opengl.GLSurfaceView
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class CameraRenderer : GLSurfaceView.Renderer {

    private var mSurfaceTexture: SurfaceTexture? = null

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {

    }

    override fun onDrawFrame(gl: GL10?) {
        mSurfaceTexture?.updateTexImage()
    }

}
