package com.hikobe8.awesomecamera

import android.Manifest
import android.graphics.*
import android.os.Bundle
import android.support.annotation.NonNull
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.vision.face.Landmark
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.google.firebase.ml.vision.face.FirebaseVisionFace
import com.google.firebase.ml.vision.face.FirebaseVisionFaceContour
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetectorOptions
import com.google.firebase.ml.vision.face.FirebaseVisionFaceLandmark
import com.tbruyelle.rxpermissions2.RxPermissions
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*
import kotlin.collections.ArrayList


class FaceDetectActivity : AppCompatActivity() {

    private var mPermissionSubscription: Disposable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
        setContentView(R.layout.activity_main)
        val bitmapOptions = BitmapFactory.Options().apply {
            //            inSampleSize = 2
        }
        val processBitmap = BitmapFactory.decodeResource(resources, R.drawable.man, bitmapOptions)
        // High-accuracy landmark detection and face classification
        val options = FirebaseVisionFaceDetectorOptions.Builder()
            .setPerformanceMode(FirebaseVisionFaceDetectorOptions.FAST)
            .setLandmarkMode(FirebaseVisionFaceDetectorOptions.ALL_LANDMARKS)
            .setContourMode(FirebaseVisionFaceDetectorOptions.ALL_CONTOURS)
            .setClassificationMode(FirebaseVisionFaceDetectorOptions.NO_CLASSIFICATIONS)
            .build()

        val image = FirebaseVisionImage.fromBitmap(processBitmap)
        val detector = FirebaseVision.getInstance()
            .getVisionFaceDetector(options)
        val result = detector.detectInImage(image)
            .addOnSuccessListener {
                Log.e("test", it.toString())
                for (face in it) {
                    val bounds = face.getBoundingBox()
                    val copy = processBitmap.copy(Bitmap.Config.ARGB_8888, true)
                    val c = Canvas(copy)
                    val paint = Paint().apply {
                        color = Color.RED
                        style = Paint.Style.STROKE
                        strokeWidth = 1f
                    }
                    c.drawRect(bounds, paint)
//                    for (f in face.getContour(FirebaseVisionFaceContour.FACE).points) {
//                        c.drawCircle(f.x, f.y, 1f, paint)
//                    }
//                    for (f in face.getContour(FirebaseVisionFaceContour.LEFT_EYE).points) {
//                        c.drawCircle(f.x, f.y, 1f, paint.apply {
//                            color = Color.parseColor("#123456")
//                        })
//                    }
//                    for (f in face.getContour(FirebaseVisionFaceContour.RIGHT_EYE).points) {
//                        c.drawCircle(f.x, f.y, 1f, paint.apply {
//                            color = Color.BLUE
//                        })
//                    }
//                    for (f in face.getContour(FirebaseVisionFaceContour.UPPER_LIP_TOP).points) {
//                        c.drawCircle(f.x, f.y, 1f, paint.apply {
//                            color = Color.parseColor("#75ee12")
//                        })
//                    }
//                    for (f in face.getContour(FirebaseVisionFaceContour.LOWER_LIP_BOTTOM).points) {
//                        c.drawCircle(f.x, f.y, 1f, paint.apply {
//                            color = Color.parseColor("#75eef0")
//                        })
//                    }
//                    for (f in face.getContour(FirebaseVisionFaceContour.NOSE_BOTTOM).points) {
//                        c.drawCircle(f.x, f.y, 1f, paint.apply {
//                            color = Color.parseColor("#00ee12")
//                        })
//                    }
//
//                    for (f in face.getContour(FirebaseVisionFaceContour.NOSE_BRIDGE).points) {
//                        c.drawCircle(f.x, f.y, 1f, paint.apply {
//                            color = Color.parseColor("#ffff00")
//                        })
//                    }

                    val pointList = ArrayList<PointF>()
                    val path: Path = face.getContour(FirebaseVisionFaceContour.FACE).points.let {
                        Path().apply {
                            moveTo(it[0].x, it[0].y)
                            pointList.add(PointF(it[0].x, it[0].y))
                            for (i in 2 until it.size step 2) {
                                lineTo(it[i].x, it[i].y)
                                pointList.add(PointF(it[i].x, it[i].y))
                            }
                            lineTo(it[0].x, it[0].y)
                        }
                    }
                    path.addPath(face.getContour(FirebaseVisionFaceContour.LEFT_EYE).points.let {
                        Path().apply {
                            moveTo(it[0].x, it[0].y)
                            pointList.add(PointF(it[0].x, it[0].y))
                            lineTo(it[4].x, it[4].y)
                            pointList.add(PointF(it[4].x, it[4].y))
                            lineTo(it[8].x, it[8].y)
                            pointList.add(PointF(it[8].x, it[8].y))
                            lineTo(it[12].x, it[12].y)
                            pointList.add(PointF(it[12].x, it[12].y))
                            lineTo(it[0].x, it[0].y)
                        }
                    })
                    path.addPath(face.getContour(FirebaseVisionFaceContour.RIGHT_EYE).points.let {
                        Path().apply {
                            moveTo(it[0].x, it[0].y)
                            pointList.add(PointF(it[0].x, it[0].y))
                            lineTo(it[4].x, it[4].y)
                            pointList.add(PointF(it[4].x, it[4].y))
                            lineTo(it[8].x, it[8].y)
                            pointList.add(PointF(it[8].x, it[8].y))
                            lineTo(it[12].x, it[12].y)
                            pointList.add(PointF(it[12].x, it[12].y))
                            lineTo(it[0].x, it[0].y)
                        }
                    })
                    path.addPath(face.getContour(FirebaseVisionFaceContour.UPPER_LIP_TOP).points.let {
                        Path().apply {
                            moveTo(it[0].x, it[0].y)
                            pointList.add(PointF(it[0].x, it[0].y))
                            lineTo(it[it.size / 2].x, it[it.size / 2].y)
                            pointList.add(PointF(it[it.size / 2].x, it[it.size / 2].y))
                            lineTo(it[it.size - 1].x, it[it.size - 1].y)
                            pointList.add(PointF(it[it.size - 1].x, it[it.size - 1].y))
                        }
                    })
                    face.getContour(FirebaseVisionFaceContour.LOWER_LIP_BOTTOM).points.let { list ->
                        path.lineTo(list[list.size / 2].x, list[list.size / 2].y)
                        pointList.add(PointF(list[list.size / 2].x, list[list.size / 2].y))
                        path.lineTo(pointList[26].x, pointList[26].y)
                    }
                    path.addPath(face.getContour(FirebaseVisionFaceContour.NOSE_BRIDGE).points.let {
                        Path().apply {
                            moveTo(it[0].x, it[0].y)
                            pointList.add(PointF(it[0].x, it[0].y))
                            for (i in 1 until it.size) {
                                lineTo(it[i].x, it[i].y)
                                pointList.add(PointF(it[i].x, it[i].y))
                            }
                        }
                    })
                    path.addPath(face.getContour(FirebaseVisionFaceContour.NOSE_BOTTOM).points.let {
                        Path().apply {
                            moveTo(it[0].x, it[0].y)
                            pointList.add(PointF(it[0].x, it[0].y))
                            for (i in 1 until it.size) {
                                lineTo(it[i].x, it[i].y)
                                pointList.add(PointF(it[i].x, it[i].y))
                            }
                        }
                    })
                    //头顶中间点
                    val headCenter = PointF().apply {
                        x = pointList[0].x + (pointList[30].x - pointList[0].x) / 2f
                        y = pointList[0].y + (pointList[30].y - pointList[0].y) / 2f
                    }
                    pointList.add(headCenter)

                    //添加点与点之间的path

                    //头顶的点辐射出去
                    path.moveto(pointList[35])
                    path.lineTo(pointList[17])
                    path.moveto(pointList[35])
                    path.lineTo(pointList[0])
                    path.moveto(pointList[35])
                    path.lineTo(pointList[1])
                    path.moveto(pointList[35])
                    path.lineTo(pointList[19])
                    path.moveto(pointList[35])
                    path.lineTo(pointList[20])
                    path.moveto(pointList[35])
                    path.lineTo(pointList[30])
                    path.moveto(pointList[35])
                    path.lineTo(pointList[22])
                    path.moveto(pointList[35])
                    path.lineTo(pointList[23])
                    path.moveto(pointList[35])
                    path.lineTo(pointList[1])

                    //左眼上部折线path
                    path.moveto(pointList[17])
                    path.lineTo(pointList[19])
                    path.lineTo(pointList[16])
                    path.lineTo(pointList[18])
                    path.lineTo(pointList[15])

                    //右眼上部折线path
                    path.moveto(pointList[1])
                    path.lineTo(pointList[23])
                    path.lineTo(pointList[2])
                    path.lineTo(pointList[24])
                    path.lineTo(pointList[3])

                    //下巴折线path
                    path.moveto(pointList[12])
                    path.lineTo(pointList[26])
                    path.lineTo(pointList[11])
                    path.lineTo(pointList[29])
                    path.lineTo(pointList[10])
                    path.moveto(pointList[29])
                    path.lineTo(pointList[9])
                    path.moveto(pointList[6])
                    path.lineTo(pointList[28])
                    path.lineTo(pointList[7])
                    path.lineTo(pointList[29])
                    path.lineTo(pointList[8])

                    //左脸折线
                    path.moveto(pointList[14])
                    path.lineTo(pointList[18])
                    path.lineTo(pointList[13])
                    path.lineTo(pointList[21])
                    path.lineTo(pointList[32])
                    path.lineTo(pointList[13])
                    path.moveto(pointList[20])
                    path.lineTo(pointList[30])
                    path.lineTo(pointList[31])
                    path.lineTo(pointList[32])
                    path.moveto(pointList[12])
                    path.lineTo(pointList[32])
                    path.lineTo(pointList[26])
                    path.lineTo(pointList[33])
                    path.lineTo(pointList[27])

                    //右脸折线
                    path.moveto(pointList[4])
                    path.lineTo(pointList[24])
                    path.lineTo(pointList[5])
                    path.lineTo(pointList[25])
                    path.lineTo(pointList[34])
                    path.lineTo(pointList[5])
                    path.moveto(pointList[22])
                    path.lineTo(pointList[30])
                    path.lineTo(pointList[31])
                    path.lineTo(pointList[34])
                    path.moveto(pointList[6])
                    path.lineTo(pointList[34])
                    path.lineTo(pointList[28])
                    path.lineTo(pointList[33])

                    //鼻尖连线
                    path.moveto(pointList[31])
                    path.lineTo(pointList[33])


                    fpl_view.setPointsAndPath(pointList.toArray(Array(0) { PointF() }), path, Runnable { })
                    iv.setImageBitmap(copy)
                }
            }
            .addOnFailureListener { e ->
                // Task failed with an exception
                // ...
                Log.e("test", e.toString())
            }
    }

    override fun onDestroy() {
        super.onDestroy()
        mPermissionSubscription?.dispose()
    }

    fun Path.moveto(pointF: PointF){
        moveTo(pointF.x, pointF.y)
    }

    fun Path.lineTo(pointF: PointF){
        lineTo(pointF.x, pointF.y)
    }

}
