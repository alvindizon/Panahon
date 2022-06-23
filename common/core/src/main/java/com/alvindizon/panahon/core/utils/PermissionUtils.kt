package com.alvindizon.panahon.core.utils

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

object PermissionUtils {

    /**
     * Returns true if the Activity or Fragment has access to all given permissions.
     *
     * @param context     context
     * @param permissions permission list
     * @return returns true if the Activity or Fragment has access to all given permissions.
     */
    fun hasSelfPermissions(context: Context, vararg permissions: String): Boolean {
        for (permission in permissions) {
            if (!hasSelfPermission(context, permission)) {
                return false
            }
        }
        return true
    }

    /**
     * Determine context has access to the given permission.
     *
     *
     * This is a workaround for RuntimeException of Parcel#readException.
     * For more detail, check this issue https://github.com/hotchemi/PermissionsDispatcher/issues/107
     *
     * @param context    context
     * @param permission permission
     * @return true if context has access to the given permission, false otherwise.
     * @see .hasSelfPermissions
     */
    private fun hasSelfPermission(context: Context, permission: String): Boolean =
        try {
            ContextCompat.checkSelfPermission(
                context,
                permission
            ) == PackageManager.PERMISSION_GRANTED
        } catch (t: RuntimeException) {
            false
        }

    /**
     * Checks given permissions are needed to show rationale.
     *
     * @param activity    activity
     * @param permissions permission list
     * @return returns true if one of the permission is needed to show rationale.
     */
    fun shouldShowRequestPermissionRationale(
        activity: Activity,
        vararg permissions: String
    ): Boolean {
        for (permission in permissions) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)) {
                return true
            }
        }
        return false
    }

    /**
     * Determine context has been denied access to the given permission.
     * @param context    context
     * @param permission permission
     * @return true if context has been denied access to the given permission, false otherwise.
     */
    fun hasDeniedPermissions(context: Context, permission: String): Boolean =
        try {
            ContextCompat.checkSelfPermission(
                context,
                permission
            ) == PackageManager.PERMISSION_DENIED
        } catch (t: RuntimeException) {
            false
        }
}
