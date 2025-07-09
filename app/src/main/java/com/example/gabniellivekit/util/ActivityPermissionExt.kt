package com.example.gabniellivekit.util

import android.Manifest
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat

fun ComponentActivity.requestNeededPermissions(onPermissionsGranted: (() -> Unit)? = null) {
    val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { grants ->
        //check if any permission waren't granted
        for (grant in grants.entries) {
            if (!grant.value) {
                Toast.makeText(this, "Missing permission : ${grant.key}", Toast.LENGTH_SHORT).show()
            }
        }

        // if all granted, notify id needed
        if (onPermissionsGranted != null && grants.all { it.value }) {
            onPermissionsGranted()
        }
    }

    val neededPermissions = listOf(Manifest.permission.RECORD_AUDIO, Manifest.permission.CAMERA)
        .filter { ContextCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_DENIED }
        .toTypedArray()

    if (neededPermissions.isNotEmpty()) {
        requestPermissionLauncher.launch(neededPermissions)
    } else {
        onPermissionsGranted?.invoke()
    }

}