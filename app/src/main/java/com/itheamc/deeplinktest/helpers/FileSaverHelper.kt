package com.itheamc.deeplinktest.helpers

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.net.Uri
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat


class FileSaverHelper private constructor(
    private val activity: ComponentActivity,
    private val onPermissionGranted: (FileSaverHelper) -> Unit = {}
) {
    companion object {

        @SuppressLint("StaticFieldLeak")
        private var instance: FileSaverHelper? = null

        fun getInstance(
            activity: ComponentActivity,
            onPermissionGranted: (FileSaverHelper) -> Unit = {}
        ): FileSaverHelper {
            if (instance == null) {
                instance = FileSaverHelper(activity, onPermissionGranted)
            }
            return instance!!
        }
    }

    private val requestWritePermissionLauncher = activity.registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) onPermissionGranted.invoke(this)
    }

    /**
     * Method to save file using SAF Intent
     */
    fun save(
        uri: Uri,
        onSaved: (Uri) -> Unit
    ) {
        val contentResolver = activity.contentResolver
        val deeplinkUri = Uri.Builder()
            .scheme("https")
            .authority("nearbypasal.com")
            .path("/link-test")
            .appendPath("2024")
            .query("key1=value1&key2=value2")
            .build()

        val fileContent = """
                        <style>
                            * { font-family: system-ui; font-size:24px; }
                        </style>
                    
                        Test deeplink with https scheme
                    
                        <a href="$deeplinkUri">Open Click here</a>
                    """.trimIndent()

        val outputStream = contentResolver.openOutputStream(uri)

        if (outputStream == null) {
            Toast.makeText(activity, "Error saving file!", Toast.LENGTH_SHORT).show()
            return
        }

        try {
            outputStream.write(fileContent.toByteArray())
            Toast.makeText(activity, "File saved successfully!", Toast.LENGTH_SHORT).show()
            onSaved.invoke(uri)
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(activity, "Error saving file!", Toast.LENGTH_SHORT).show()
        } finally {
            outputStream.close()
        }
    }

    /**
     * Method to check storage permission
     */
    fun hasStoragePermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            activity.applicationContext,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED
    }

    /**
     * Method to request storage permission
     */
    fun requestStoragePermission() {
        requestWritePermissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    }

}