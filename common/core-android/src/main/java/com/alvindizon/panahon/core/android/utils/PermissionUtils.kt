package com.alvindizon.panahon.core.android.utils

import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat

object PermissionUtils {

    fun hasSelfPermissions(context: Context, vararg permissions: String): Boolean {
        var hasPermission = true
        permissions.forEach { permission ->
            if (ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                hasPermission = false
                return@forEach
            }
        }
        return hasPermission
    }

}
