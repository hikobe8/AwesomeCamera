package com.hikobe8.awesomecamera.camera

import android.content.Context
import android.hardware.Camera
import android.util.AttributeSet
import android.view.SurfaceHolder
import android.view.SurfaceView

/***
 *  Author : ryu18356@gmail.com
 *  Create at 2019-04-23 15:55
 *  description : SurfaceView with Camera
 */
class RayCameraView(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) :
    SurfaceView(context, attrs, defStyleAttr), SurfaceHolder.Callback {

    constructor(context: Context?):this(context, null, 0)

    constructor(context: Context?, attrs: AttributeSet?):this(context, attrs, 0)

    init {
        holder.addCallback(this)
    }

    override fun surfaceChanged(holder: SurfaceHolder?, format: Int, width: Int, height: Int) {

    }

    override fun surfaceDestroyed(holder: SurfaceHolder?) {

    }

    override fun surfaceCreated(holder: SurfaceHolder?) {
        val camera = Camera.open()
        camera.setPreviewDisplay(holder)
        camera.setDisplayOrientation(90)
        camera.parameters = camera.parameters.apply {
            setPreviewSize(supportedPreviewSizes[6].width, supportedPreviewSizes[6].height)
        }
        camera.startPreview()
    }


}