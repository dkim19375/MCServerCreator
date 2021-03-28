package me.dkim19375.mcservercreator.util

import org.jetbrains.annotations.Contract
import java.lang.IllegalArgumentException

@Suppress("EnumEntryName")
enum class ServerVersion(val version: String) {
    v1_8("1.8.8"), v1_9("1.9.4"), v1_10("1.10.2"), v1_11("1.11.2"), v1_12("1.12.2"), v1_13("1.13.2"), v1_14("1.14.4"), v1_15(
        "1.15.2"
    ),
    v1_16("1.16.5");

    companion object {
        @Contract("null -> null")
        fun fromString(version: String?): ServerVersion? {
            version?: return null
            try {
                return valueOf(version.toLowerCase())
            } catch (ignored: IllegalArgumentException) {
            }
            for (ver in values()) {
                if (ver.version.equals(version, ignoreCase = true)) {
                    return ver
                }
            }
            return null
        }
    }
}