package me.dkim19375.mcservercreator.util

import javax.swing.JFrame
import javax.swing.JOptionPane

fun String.prompt(): Boolean {
    val window = JFrame("MCServerCreator ERROR")
    // create ui
    val result = JOptionPane.showConfirmDialog(
        window, """
     An error has occurred: 
     $this.
     Do you want to retry?
     """.trimIndent(), "MCServerCreator ERROR",
        JOptionPane.YES_NO_OPTION
    )
    return result == JOptionPane.YES_OPTION
}