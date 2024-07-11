package com.itheamc.deeplinktest.helpers

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.widget.Toast
import androidx.core.content.ContextCompat

class FileSaverHelper {

    companion object {

        /**
         * Storage permission code
         */
        const val STORAGE_PERMISSION_CODE = 100

        /**
         * Method to check storage permission
         */
        fun hasStoragePermission(context: Context): Boolean {
            return ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
        }


        /**
         * Method to save file using SAF Intent
         */
        fun save(
            uri: Uri,
            context: Context,
            onSuccess: () -> Unit
        ) {
            val contentResolver = context.contentResolver

            val deeplinkUri = Uri.Builder()
                .scheme("naxa")
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
                Toast.makeText(context, "Error saving file!", Toast.LENGTH_SHORT).show()
                return
            }

            try {
                outputStream.write(fileContent.toByteArray())
                Toast.makeText(context, "File saved successfully!", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(context, "Error saving file!", Toast.LENGTH_SHORT).show()
            } finally {
                outputStream.close()
                onSuccess.invoke()
            }
        }
    }

}