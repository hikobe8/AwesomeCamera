package com.hikobe8.awesomecamera

import android.Manifest
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.tbruyelle.rxpermissions2.RxPermissions
import io.reactivex.disposables.Disposable

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

    }

    override fun onDestroy() {
        super.onDestroy()
        mPermissionSubscription?.dispose()
    }

}
