package me.dkim19375.mcservercreator.util;

import javax.swing.*;

public class ErrorUtils {
    public static boolean prompt(final String error) {
        final JFrame window = new JFrame("MCServerCreator ERROR");
        // create ui
        final int result = JOptionPane.showConfirmDialog(window, "An error has occurred: \n" +
                        error + ".\n" +
                        "Do you want to retry?", "MCServerCreator ERROR",
                JOptionPane.YES_NO_OPTION);
        return result == JOptionPane.YES_OPTION;
    }
}
