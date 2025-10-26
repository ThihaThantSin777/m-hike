package com.mhike.app.util

import android.Manifest
import android.os.Build

object Permissions {
    val CAMERA = Manifest.permission.CAMERA

    fun readImages(): String =
        if (Build.VERSION.SDK_INT >= 33)
            Manifest.permission.READ_MEDIA_IMAGES
        else
            Manifest.permission.READ_EXTERNAL_STORAGE
}
