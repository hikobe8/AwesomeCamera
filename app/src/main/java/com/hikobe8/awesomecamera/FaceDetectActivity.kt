package com.hikobe8.awesomecamera

import android.Manifest
import android.graphics.*
import android.os.Bundle
import android.support.annotation.NonNull
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.google.android.gms.tasks.OnFailureListener
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.google.firebase.ml.vision.face.FirebaseVisionFace
import com.google.firebase.ml.vision.face.FirebaseVisionFaceContour
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetectorOptions
import com.google.firebase.ml.vision.face.FirebaseVisionFaceLandmark
import com.tbruyelle.rxpermissions2.RxPermissions
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.activity_main.*


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
            inSampleSize = 2
        }
        val processBitmap = BitmapFactory.decodeResource(resources, R.drawable.man, bitmapOptions)
        // High-accuracy landmark detection and face classification
        val options = FirebaseVisionFaceDetectorOptions.Builder()
            .setPerformanceMode(FirebaseVisionFaceDetectorOptions.ACCURATE)
            .setLandmarkMode(FirebaseVisionFaceDetectorOptions.ALL_LANDMARKS)
            .setContourMode(FirebaseVisionFaceDetectorOptions.ALL_CONTOURS)
            .setClassificationMode(FirebaseVisionFaceDetectorOptions.ALL_CLASSIFICATIONS)
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
                    c.drawRect(bounds, Paint().apply {
                        color = Color.RED
                        style = Paint.Style.STROKE
                        strokeWidth = 4f
                    })
                    iv.setImageBitmap(copy)
                    val rotY = face.getHeadEulerAngleY()  // Head is rotated to the right rotY degrees
                    val rotZ = face.getHeadEulerAngleZ()  // Head is tilted sideways rotZ degrees

                    // If landmark detection was enabled (mouth, ears, eyes, cheeks, and
                    // nose available):
                    val leftEar = face.getLandmark(FirebaseVisionFaceLandmark.LEFT_EAR)
                    if (leftEar != null) {
                        val leftEarPos = leftEar!!.getPosition()
                    }

                    // If contour detection was enabled:
                    val leftEyeContour = face.getContour(FirebaseVisionFaceContour.LEFT_EYE).getPoints()
                    val upperLipBottomContour = face.getContour(FirebaseVisionFaceContour.UPPER_LIP_BOTTOM).getPoints()

                    // If classification was enabled:
                    if (face.getSmilingProbability() != FirebaseVisionFace.UNCOMPUTED_PROBABILITY) {
                        val smileProb = face.getSmilingProbability()
                    }
                    if (face.getRightEyeOpenProbability() != FirebaseVisionFace.UNCOMPUTED_PROBABILITY) {
                        val rightEyeOpenProb = face.getRightEyeOpenProbability()
                    }

                    // If face tracking was enabled:
                    if (face.getTrackingId() != FirebaseVisionFace.INVALID_ID) {
                        val id = face.getTrackingId()
                    }
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

}
