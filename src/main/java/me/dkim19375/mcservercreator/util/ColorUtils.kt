package me.dkim19375.mcservercreator.util

import javafx.geometry.Insets
import javafx.scene.layout.Background
import javafx.scene.layout.BackgroundFill
import javafx.scene.layout.CornerRadii
import javafx.scene.paint.Color

object ColorUtils {
    fun getBackground(red: Int, green: Int, blue: Int): Background {
        return Background(
            BackgroundFill(
                Color.rgb(red, green, blue), CornerRadii(1.0),
                Insets(0.0, 0.0, 0.0, 0.0)
            )
        )
    }
}