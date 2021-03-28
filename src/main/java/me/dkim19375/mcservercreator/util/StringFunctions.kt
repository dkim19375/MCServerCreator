package me.dkim19375.mcservercreator.util

fun String.combineNewline(second: String?): String {
    return if (second == null) {
        this
    } else """
     $this
     $second
     """
}