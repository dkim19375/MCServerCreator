package me.dkim19375.mcservercreator.util;

public enum ServerType {
    SPIGOT("spigot.jar"),
    PAPER("paper.jar"),
    TUINITY("tuinity.jar");

    private final String jarFile;

    ServerType(String jarFile) {
        this.jarFile = jarFile;
    }

    public String getJarFile() {
        return jarFile;
    }
}
