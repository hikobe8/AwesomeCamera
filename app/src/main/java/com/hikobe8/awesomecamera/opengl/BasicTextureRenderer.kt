package com.hikobe8.awesomecamera.opengl

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.opengl.GLUtils
import android.opengl.Matrix
import android.util.Log
import com.hikobe8.awesomecamera.R
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class BasicTextureRenderer(
    context: Context,
    private val onTextureAvailableListener: OnTextureAvailableListener? = null,
    private var mTextureId: Int = -1,
    private var mImageWidth: Int = 0,
    private var mImageHeight: Int = 0
) : GLSurfaceView.Renderer {

    companion object {
        val COORDS = floatArrayOf(
            -1f, 1f, // left top
            -1f, -1f, // left bottom
            1f, 1f, // right top
            1f, -1f // right bottom
        )
        val TEXTURE_COORDS = floatArrayOf(
            0f, 0f, // left top
            0f, 1f, // left bottom
            1f, 0f, // right top
            1f, 1f // right bottom
        )
        const val COUNT_PER_COORD = 2
    }

    private var mContext = context.applicationContext
    private var mPositionHandle = -1
    private var mTextureCoordinateHandle = -1
    private var mMatrixHandle = -1
    private var mProgram = -1
    private lateinit var mVertextBuffer: FloatBuffer
    private lateinit var mTextureVertexBuffer: FloatBuffer
    private val mMatrix = FloatArray(16)
    private lateinit var mBitmap: Bitmap

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        val vertexShader = RayGLUtil.loadShader(mContext, "t_vertex.glsl", GLES20.GL_VERTEX_SHADER)
        val fragmentShader = RayGLUtil.loadShader(mContext, "t_fragment.glsl", GLES20.GL_FRAGMENT_SHADER)
        mProgram = RayGLUtil.createGLProgram(vertexShader, fragmentShader)
        mPositionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition")
        mTextureCoordinateHandle = GLES20.glGetAttribLocation(mProgram, "vCoordinate")
        mMatrixHandle = GLES20.glGetUniformLocation(mProgram, "vMatrix")

        mVertextBuffer = ByteBuffer
            .allocateDirect(COORDS.size * COUNT_PER_COORD * 4)
            .order(ByteOrder.nativeOrder())
            .asFloatBuffer()
            .put(COORDS).apply {
                position(0)
            }
        mTextureVertexBuffer = ByteBuffer
            .allocateDirect(TEXTURE_COORDS.size * COUNT_PER_COORD * 4)
            .order(ByteOrder.nativeOrder())
            .asFloatBuffer()
            .put(TEXTURE_COORDS).apply {
                position(0)
            }
        if (mTextureId < 0) {
            mBitmap = BitmapFactory.decodeResource(mContext.resources, R.drawable.man)
            mImageWidth = mBitmap.width
            mImageHeight = mBitmap.height
            mTextureId = createTexture()
            onTextureAvailableListener?.onTextureAvailable(mTextureId, mImageWidth, mImageHeight)
        }
    }

    interface OnTextureAvailableListener {
        fun onTextureAvailable(textureId: Int, imageWidth: Int, imageHeight: Int)
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        GLES20.glViewport(0, 0, width, height)
        if (width > height) {
            Matrix.orthoM(mMatrix, 0, -width.toFloat() / height, width.toFloat() / height, -1f, 1f, 0f, 1f)
            //横屏
        } else {
            //竖屏
            if (mImageWidth > mImageHeight) {
                //横图
                Matrix.orthoM(mMatrix, 0, -1f, 1f, -height.toFloat() / width, height.toFloat() / width, 0f, 1f)
            } else {
                //竖图, 图片高度充满，宽度缩放
                Matrix.orthoM(
                    mMatrix,
                    0,
//                    -width/(height.toFloat()/mImageHeight * mImageWidth),
                    height.toFloat()/(-mImageHeight.toFloat()/mImageWidth),
                    mImageHeight.toFloat()/mImageWidth,
//                    width/(height.toFloat()/mImageHeight * mImageWidth),
                    -1f,
                    1f,
                    0f,
                    1f
                )
            }
        }
    }

    override fun onDrawFrame(gl: GL10?) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT or GLES20.GL_DEPTH_BUFFER_BIT)
//        GLES20.glClearColor(1f, 1f, 1f, 1f)
        GLES20.glUseProgram(mProgram)
        GLES20.glUniformMatrix4fv(mMatrixHandle, 1, false, mMatrix, 0)
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextureId)
        GLES20.glEnableVertexAttribArray(mPositionHandle)
        GLES20.glEnableVertexAttribArray(mTextureCoordinateHandle)
        GLES20.glVertexAttribPointer(
            mPositionHandle,
            COUNT_PER_COORD,
            GLES20.GL_FLOAT,
            false,
            COUNT_PER_COORD * 4,
            mVertextBuffer
        )
        GLES20.glVertexAttribPointer(
            mTextureCoordinateHandle,
            COUNT_PER_COORD,
            GLES20.GL_FLOAT,
            false,
            COUNT_PER_COORD * 4,
            mTextureVertexBuffer
        )
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4)
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0)
        GLES20.glDisableVertexAttribArray(mPositionHandle)
        GLES20.glDisableVertexAttribArray(mTextureCoordinateHandle)
    }

    private fun createTexture(): Int {
        val texture = IntArray(1)
        if (!mBitmap.isRecycled) {
            //生成纹理
            GLES20.glGenTextures(1, texture, 0)
            //绑定纹理
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texture[0])
            //设置缩小过滤为使用纹理中坐标最接近的一个像素的颜色作为需要绘制的像素颜色
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST)
            //设置放大过滤为使用纹理中坐标最接近的若干个颜色，通过加权平均算法得到需要绘制的像素颜色
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR)
            //设置环绕方向S，截取纹理坐标到[1/2n,1-1/2n]。将导致永远不会与border融合
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE)
            //设置环绕方向T，截取纹理坐标到[1/2n,1-1/2n]。将导致永远不会与border融合
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE)
            //根据以上指定的参数，生成一个2D纹理
            GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, mBitmap, 0)
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0)
            return texture[0]
        }
        return 0
    }


}