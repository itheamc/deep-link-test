package com.itheamc.deeplinktest.helpers

import android.content.Context
import android.content.pm.PackageInfo


object VersionInfoHelper {
    private var packageInfo: PackageInfo? = null

    fun getVersionName(context: Context): String {
        if (packageInfo == null) {
            try {
                packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
            } catch (e: Exception) {
                // Handle exception
            }
        }
        return packageInfo?.versionName ?: "Unknown"
    }
}