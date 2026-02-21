package frb.axeron.manager.ui.util

import android.os.Build

object SystemUtils {
    
    fun getSystemVersion(): String {
        return "${Build.VERSION.RELEASE} ${if (Build.VERSION.PREVIEW_SDK_INT != 0) "Preview" else ""} (API ${Build.VERSION.SDK_INT})"
    }

    fun getDeviceInfo(): String {
        var manufacturer = Build.MANUFACTURER[0].uppercaseChar().toString() + Build.MANUFACTURER.substring(1)
        if (!Build.BRAND.equals(Build.MANUFACTURER, ignoreCase = true)) {
            manufacturer += " " + Build.BRAND[0].uppercaseChar() + Build.BRAND.substring(1)
        }
        manufacturer += " " + Build.MODEL
        return manufacturer
    }

    fun getKernelVersion(): String {
        return System.getProperty("os.version") ?: "Unknown"
    }

    fun getSELinuxStatus(): String {
        return try {
            android.system.Os.getenv("ENFORCE")?.let { enforce ->
                when (enforce) {
                    "0" -> "Permissive"
                    "1" -> "Enforcing"
                    else -> "Unknown"
                }
            } ?: try {
                val file = java.io.File("/sys/fs/selinux/enforce")
                if (file.exists()) {
                    val enforce = file.readText().trim()
                    when (enforce) {
                        "0" -> "Permissive"
                        "1" -> "Enforcing"
                        else -> "Unknown"
                    }
                } else {
                    "Disabled"
                }
            } catch (e: Exception) {
                "Unknown"
            }
        } catch (e: Exception) {
            "Unknown"
        }
    }

    fun getSupportedABIs(): String {
        return Build.SUPPORTED_ABIS.joinToString(", ")
    }
}