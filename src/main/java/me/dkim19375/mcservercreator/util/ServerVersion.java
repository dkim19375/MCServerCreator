package me.dkim19375.mcservercreator.util;

import java.util.Collection;

public enum ServerVersion {
    v1_8("1.8.8"),
    v1_9("1.9.4"),
    v1_10("1.10.2"),
    v1_11("1.11.2"),
    v1_12("1.12.2"),
    v1_13("1.13.2"),
    v1_14("1.14.4"),
    v1_15("1.15.2"),
    v1_16("1.16.5");

    private final String version;

    ServerVersion(String version) {
        this.version = version;
    }

    public static ServerVersion fromString(String version) {
        try {
            return ServerVersion.valueOf(version.toLowerCase());
        } catch (IllegalArgumentException ignored) {}
        for (ServerVersion ver : values()) {
            if (ver.version.equalsIgnoreCase(version)) {
                return ver;
            }
        }
        return null;
    }

    public String getVersion() {
        return version;
    }
}
