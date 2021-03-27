package me.dkim19375.mcservercreator.util;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class StringUtils {
    @NotNull
    public static String combineNewline(@Nullable String first, @Nullable String second) {
        if (first == null && second == null) {
            return "";
        }
        if (first == null) {
            return second;
        }
        if (second == null) {
            return first;
        }
        return first + "\n" + second;
    }
}
