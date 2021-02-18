package me.dkim19375.mcservercreator.util;

import javafx.geometry.Insets;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;

public class ColorUtils {
    private ColorUtils() {}

    public static Background getBackground(int red, int green, int blue) {
        return new Background(new BackgroundFill(Color.rgb(red, green, blue), new CornerRadii(1),
                new Insets(0.0, 0.0, 0.0, 0.0)));
    }
}
