package com.hikobe8.awesomecamera

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.os.Looper
import android.util.AttributeSet
import android.util.Log
import android.util.TypedValue
import android.view.View

/***
 *  Author : ryu18356@gmail.com
 *  Create at 2019-02-01 16:44
 *  description : 人脸特征点，线控件
 */
class FacePointLineView(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) :
    View(context, attrs, defStyleAttr) {

    constructor(context: Context?) : this(context, null, 0)

    constructor(context: Context?, attrs: AttributeSet?) : this(context, attrs, 0)

    private val pointRadius = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 3f, context!!.resources.displayMetrics)
    private val lineWidth = pointRadius/2f

    private val mPath = android.graphics.Path()
    private val mPaint = Paint().apply {
        isAntiAlias = true
        style = Paint.Style.STROKE
        strokeWidth = lineWidth
        color = Color.parseColor("#80FEFEFE")
    }

    private val mPointPaint = Paint().apply {
        isAntiAlias = true
        color = Color.WHITE
    }

    private val mTextPaint = Paint().apply {
        isAntiAlias = true
        color = Color.RED
        textSize = 24f
    }

    private lateinit var mPathMeasure: PathMeasure
    private var mPathLength = 0f
    private var mAnim: ObjectAnimator? = null
    private var mPoints: Array<PointF>? = null

    fun setPointsAndPath(points: Array<PointF>, path: Path, completeRunnable: Runnable) {
        mPoints = points
        mPath.reset()
        mPath.set(path)
        mPathMeasure = PathMeasure(mPath, false)
        mPathLength = mPathMeasure.length
        mAnim = ObjectAnimator.ofFloat(this, "phase", 0f, 1f).apply {
            duration = 3000L
            addListener(object :AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator?) {
                    super.onAnimationEnd(animation)
                    completeRunnable.run()
                }
            })
        }
        mAnim?.start()
    }

    fun clear(){
        mPoints = null
        mPath.reset()
        invalidate()
    }

    @SuppressLint("AnimatorKeep")
    fun setPhase(phase: Float) {
        Log.d("PathView", "setPhase called with:" + phase.toString())
        mPaint.pathEffect = createPathEffect(mPathLength, phase, 0.0f)
        if (Looper.myLooper() == Looper.getMainLooper()) {
            invalidate()//will calll onDraw
        } else {
            postInvalidate()
        }
    }

    private fun createPathEffect(pathLength: Float, phase: Float, offset: Float): PathEffect {
        return DashPathEffect(
            floatArrayOf(pathLength, pathLength),
            pathLength - phase * pathLength
        )
    }

    override fun dispatchDraw(canvas: Canvas?) {
        super.dispatchDraw(canvas)
        mPoints?.let {
//            val eyeHeight = mPoints!![73].y - mPoints!![72].y
//            canvas?.drawCircle(mPoints!![43].x, mPoints!![41].y - (mPoints!![73].y - mPoints!![72].y), pointRadius, mPointPaint)
//            canvas?.drawCircle(mPoints!![43].x, (mPoints!![43].y + mPoints!![41].y - eyeHeight) * .5f, pointRadius, mPointPaint)
//            canvas?.drawCircle(mPoints!![93].x, (mPoints!![46].y + mPoints!![93].y) * 0.5f, pointRadius, mPointPaint)
//            val slice = mPoints!!.slice(
//                listOf(
//                    0, 3, 8, 12, 16, 20, 24, 29, 32, 34,
//                    41, 46, 49, 52, 55, 58, 61, 72, 73,
//                    75, 76, 82, 83, 84, 90, 93
//                )
//            )
            for (p in it) {
                canvas?.drawCircle(p.x, p.y, pointRadius, mPointPaint)
                canvas?.drawText(it.indexOf(p).toString(),  p.x, p.y, mTextPaint)
            }
        }
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas?.drawPath(mPath, mPaint)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        if (mAnim?.isRunning == true) {
            mAnim?.cancel()
        }
    }

}