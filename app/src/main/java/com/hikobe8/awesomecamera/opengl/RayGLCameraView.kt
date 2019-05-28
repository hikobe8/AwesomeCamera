package com.hikobe8.awesomecamera.opengl

import android.content.Context
import android.opengl.GLSurfaceView
import android.util.AttributeSet
import android.util.Log
import javax.microedition.khronos.egl.EGL10
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.egl.EGLContext
import javax.microedition.khronos.egl.EGLDisplay

class RayGLCameraView(context: Context?, attrs: AttributeSet?) : GLSurfaceView(context, attrs) {

    private var mEGLContext: EGLContext? = null
    private var mTextureId = -1
    private var mImageWidth: Int = 0
    private var mImageHeight: Int = 0

    constructor(context: Context?) : this(context = context, attrs = null)

    constructor(context: Context?, eGLContext: EGLContext? = null, textureId: Int, imageWidth: Int, imageHeight: Int) : this(context) {
        mEGLContext = eGLContext
        mTextureId = textureId
        mImageWidth = imageWidth
        mImageHeight = imageHeight
    }

    var mOnGLContextAndTextureAvailableListener: OnGLContextAndTextureAvailableListener? = null

    init {
        setEGLContextClientVersion(2)

        val rayEGLFactory = RayEGLFactory(mEGLContext)
        setEGLContextFactory(rayEGLFactory)
        setRenderer(BasicTextureRenderer(context!!, object : BasicTextureRenderer.OnTextureAvailableListener {
            override fun onTextureAvailable(textureId: Int, imageWidth: Int, imageHeight: Int) {
                mOnGLContextAndTextureAvailableListener?.onEGLContextAndTextureAvailable(
                    rayEGLFactory.getEGLContext(),
                    textureId,
                    imageWidth,
                    imageHeight
                    )
            }
        }, mTextureId, mImageWidth, mImageHeight))
        renderMode = RENDERMODE_WHEN_DIRTY

    }

    interface OnGLContextAndTextureAvailableListener {
        fun onEGLContextAndTextureAvailable(
            eGLContext: EGLContext?,
            textureId: Int,
            imageWidth: Int,
            imageHeight: Int
        )
    }

    class RayEGLFactory(private var mEGLContext: EGLContext? = null) : EGLContextFactory {

        companion object {
            private const val EGL_CONTEXT_CLIENT_VERSION = 0x3098
        }

        override fun createContext(egl: EGL10, display: EGLDisplay, config: EGLConfig): EGLContext {
            return if (mEGLContext != null) {
                mEGLContext!!
            } else {
                val attribList = intArrayOf(EGL_CONTEXT_CLIENT_VERSION, 2, EGL10.EGL_NONE)
                mEGLContext = egl.eglCreateContext(
                    display, config, EGL10.EGL_NO_CONTEXT,
                    attribList
                )
                return mEGLContext!!
            }

        }

        override fun destroyContext(
            egl: EGL10, display: EGLDisplay,
            context: EGLContext
        ) {
            if (!egl.eglDestroyContext(display, context)) {
                Log.e("DefaultContextFactory", "display:$display context: $context")
            }
        }

        fun getEGLContext(): EGLContext? = mEGLContext

    }
}