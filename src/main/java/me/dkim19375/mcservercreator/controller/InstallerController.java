package me.dkim19375.mcservercreator.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import me.dkim19375.mcservercreator.MCServerCreator;
import me.dkim19375.mcservercreator.util.ColorUtils;
import me.dkim19375.mcservercreator.util.ServerType;
import me.dkim19375.mcservercreator.util.ServerVersion;
import org.apache.commons.io.FileUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.Arrays;

public class InstallerController {
    @FXML
    Button submitButton;
    @FXML
    private VBox background;
    @FXML
    private TextArea consoleText;
    private boolean shown = false;
    private final Thread mainThread = Thread.currentThread();

    @FXML
    private void initialize() {
        MCServerCreator.getInstance().setInstallerController(this);
        submitButton.setVisible(false);
        background.setBackground(ColorUtils.getBackground(20, 20, 20));
        submitButton.setBackground(ColorUtils.getBackground(187, 134, 252));
        consoleText.setBackground(ColorUtils.getBackground(40, 40, 40));
        consoleText.setScaleX(1.8);
        consoleText.setScaleY(1.8);
        consoleText.setScaleZ(1.8);
        consoleText.setCache(false);
        for (final Node node : consoleText.getChildrenUnmodifiable()) {
            node.setCache(false);
        }
        consoleText.textProperty().addListener((observable, oldValue, newValue) -> {
            consoleText.setScrollTop(Double.MAX_VALUE);
            if (!newValue.endsWith("\n")) {
                consoleText.setText(newValue + "\n");
                for (final Node node : consoleText.getChildrenUnmodifiable()) {
                    node.setCache(false);
                }
            }
        });
    }

    public void onShow() {
        if (!shown) {
            shown = true;
            try {
                onShow();
            } catch (Exception e) {
                e.printStackTrace();
                final String message = e.getLocalizedMessage();
                throwError(message == null ? "Unknown error while installing" : message);
            }
            return;
        }
        sendMessage("Starting installation");
        final ServerVersion version = MCServerCreator.getInstance().getVersion();
        if (version == null) {
            throwError("Server version is not set!");
            return;
        }
        final ServerType type = MCServerCreator.getInstance().getServerType();
        if (type == null) {
            throwError("Server type is not set!");
            return;
        }
        final File installDir = MCServerCreator.getInstance().getDirectoryController().getDirectory();
        if (installDir == null) {
            throwError("Install directory is not set!");
            return;
        }
        File serverJar = null;
        switch (type) {
            case SPIGOT: {
                sendMessage("Looking to see if BuildTools was already ran for this version");
                final File mavenJar = Paths.get(
                        System.getProperty("user.home"),
                        ".m2", "repository", "org", "spigotmc", "spigot",
                        version.getVersion() + "-R0.1-SNAPSHOT",
                        "spigot-" + version.getVersion() + "-R0.1-SNAPSHOT.jar").toFile();
                final File file = Paths.get("data", "spigot", "buildtools", "BuildTools.jar").toFile();
                if (mavenJar.exists()) {
                    sendMessage("BuildTools was already ran, skipping the BuildTools installation");
                    serverJar = file;
                    break;
                }
                sendMessage("Buildtools has not been ran before, downloading BuildTools.jar");
                final long before = System.currentTimeMillis();
                downloadFile("https://hub.spigotmc.org/jenkins/job/BuildTools/lastSuccessfulBuild/artifact/target/BuildTools.jar",
                        file,
                        "BuildTools.jar");
                final long current = System.currentTimeMillis();
                sendMessage("Successfully downloaded in "
                        + (((double) (current - before)) / 1000.0) + " seconds (" + (current - before) + "ms)");
                sendMessage("Running BuildTools, this will take 10+ minutes.");
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    throwError("Could not sleep thread");
                }
                final ProcessBuilder builder = new ProcessBuilder("java -jar " + file.getAbsolutePath()
                       + " --rev " + version.getVersion());
                builder.redirectErrorStream(true);
                final Process process;
                try {
                    process = builder.start();
                } catch (IOException e) {
                    e.printStackTrace();
                    throwError("Could not start process!");
                    return;
                }
                final BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                reader.lines().forEach(consoleText::appendText);
                final int status;
                try {
                    status = process.waitFor();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    throwError("Could not block thread to wait");
                    return;
                }
                if (status != 0) {
                    throwError("Status while running BuildTools is " + status);
                    return;
                }
                final File serverFile = Paths.get("data", "spigot", "buildtools", "spigot-" + version.getVersion() + ".jar").toFile();
                if (!serverFile.exists()) {
                    throwError("Could not find server jar! (" + serverFile.getAbsolutePath() + ")");
                    return;
                }
                serverJar = serverFile;
                sendMessage("Finished running BuildTools");
                break;
            }
            case PAPER: {
                final File file = Paths.get("data", "paper", type.getJarFile()).toFile();
                sendMessage("Downloading " + type.getJarFile());
                final long before = System.currentTimeMillis();
                downloadFile("https://papermc.io/api/v1/paper/" + version.getVersion() + "/latest/download",
                        file,
                        type.getJarFile());
                final long current = System.currentTimeMillis();
                sendMessage("Successfully downloaded in "
                        + (((double) (current - before)) / 1000.0) + " seconds (" + (current - before) + "ms)");
                serverJar = file;
                break;
            }
            case TUINITY: {
                final File file = Paths.get("data", "tuinity", type.getJarFile()).toFile();
                switch (version) {
                    case v1_15: {
                        sendMessage("Downloading " + type.getJarFile());
                        final long before = System.currentTimeMillis();
                        downloadFile("https://ci.codemc.io/job/Spottedleaf/job/Tuinity/139/artifact/tuinity-paperclip.jar",
                                file,
                                type.getJarFile());
                        final long current = System.currentTimeMillis();
                        sendMessage("Successfully downloaded in "
                                + (((double) (current - before)) / 1000.0) + " seconds (" + (current - before) + "ms)");
                        break;
                    }
                    case v1_16: {
                        sendMessage("WARNING: Downloading the latest Tuinity build. " +
                                "If it is not 1.16, then you will have to manually download the server jar");
                        sendMessage("Downloading " + type.getJarFile());
                        final long before = System.currentTimeMillis();
                        downloadFile("https://ci.codemc.io/job/Spottedleaf/job/Tuinity/lastSuccessfulBuild/artifact/tuinity-paperclip.jar",
                                file,
                                type.getJarFile());
                        final long current = System.currentTimeMillis();
                        sendMessage("Successfully downloaded in "
                                + (((double) (current - before)) / 1000.0) + " seconds (" + (current - before) + "ms)");
                        break;
                    }
                    default: {
                        sendMessage("Downloading " + type.getJarFile());
                        throwError("Tuinity software MUST be 1.15 - 1.16, instead it was " + version.getVersion());
                        return;
                    }
                }
                serverJar = file;
                break;
            }
            default: {
                throwError("Server software must be either: " + Arrays.toString(ServerType.values()) + ", instead it was " + type.name());
                return;
            }
        }
        sendMessage("Copying the jar to " + installDir.getAbsolutePath() + "...");
        if (!installDir.exists()) {
            sendMessage("Directory does not exist, creating one");
            if (!installDir.mkdirs()) {
                throwError("Could not create directory " + installDir.getAbsolutePath());
                return;
            }
            sendMessage("Successfully created directory");
        }
        final File installFile = Paths.get(installDir.getAbsolutePath(), type.getJarFile()).toFile();
        if (installFile.exists()) {
            if (!installFile.delete()) {
                throwError("Could not delete server jar - " + installFile.getAbsolutePath());
                return;
            }
        }
        try {
            FileUtils.copyFile(serverJar, installFile);
        } catch (IOException e) {
            e.printStackTrace();
            throwError("Could not copy jar");
            return;
        }
        sendMessage("Done! Server jar is in " + installDir.getAbsolutePath());
    }

    private void downloadFile(String url, File file, String fileName) throws RuntimeException {
        sendMessage("Making directory: " + file.getParentFile().getAbsolutePath());
        if (!file.getParentFile().exists()) {
            if (!file.getParentFile().mkdirs()) {
                throwError("Could not create directory: " + file.getParentFile().getAbsolutePath());
                return;
            }
        }
        sendMessage("Successfully created directory: " + file.getParentFile().getAbsolutePath());
        final URL link;
        try {
            link = new URL(url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
            throwError("Could not form URL: " + url);
            return;
        }
        sendMessage("Starting download of " + fileName);
        try {
            FileUtils.copyURLToFile(link, file);
        } catch (IOException e) {
            e.printStackTrace();
            throwError("Could not download " + fileName);
            return;
        }
        sendMessage("Successfully downloaded " + fileName);
    }
    
    private void sendMessage(String message) {
        if (Thread.currentThread() != mainThread) {
            Platform.runLater(() -> consoleText.appendText(message));
            return;
        }
        consoleText.appendText(message);
    }

    private void throwError(String error) {
        sendMessage("FATAL ERROR: " + error);
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            throw new RuntimeException(error);
        }));
        System.exit(1); // I might remove this lol
    }
}
