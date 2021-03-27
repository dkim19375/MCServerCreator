package me.dkim19375.mcservercreator.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import me.dkim19375.mcservercreator.MCServerCreator;
import me.dkim19375.mcservercreator.util.*;
import org.apache.commons.io.FileUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Objects;

public class InstallerController {
    private final Thread mainThread = Thread.currentThread();
    @FXML
    Button submitButton;
    @FXML
    Button closeButton;
    @FXML
    private VBox background;
    @FXML
    private TextArea consoleText;
    private boolean shown = false;

    @FXML
    private void initialize() {
        MCServerCreator.getInstance().setInstallerController(this);
        submitButton.setVisible(false);
        closeButton.setVisible(false);
        background.setBackground(ColorUtils.getBackground(20, 20, 20));
        submitButton.setBackground(ColorUtils.getBackground(187, 134, 252));
        closeButton.setBackground(ColorUtils.getBackground(187, 134, 252));
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
                if (ErrorUtils.prompt("Unknown error while installing"
                        + (message == null ? "" : (":\n" + message)))) {
                    consoleText.appendText("Retrying");
                    onShow();
                    return;
                }
                consoleText.appendText("Cancelled");
                showCloseButton();
            }
            return;
        }
        sendMessage("Starting installation");
        final ServerVersion version = MCServerCreator.getInstance().getVersion();
        if (version == null) {
            consoleText.appendText("ERROR: Server version is not set!");
            showCloseButton();
            return;
        }
        final ServerType type = MCServerCreator.getInstance().getServerType();
        if (type == null) {
            consoleText.appendText("ERROR: Server type is not set!");
            showCloseButton();
            return;
        }
        final File installDir = MCServerCreator.getInstance().getDirectoryController().getDirectory();
        if (installDir == null) {
            consoleText.appendText("ERROR: Install directory is not set!");
            showCloseButton();
            return;
        }
        File serverJar;
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
                }
                sendMessage("NOTE: It might seem like nothing is happening... it might take up to a minute for anything to output.");
                sendMessage("If nothing outputs within 2-5 minutes, you should restart.");
                final ProcessBuilder builder = new ProcessBuilder("cmd.exe", "/c", "java -jar " + file.getAbsolutePath()
                        + " --rev " + version.getVersion());
                builder.redirectErrorStream(true);
                Process process;
                while (true) {
                    try {
                        process = builder.start();
                    } catch (IOException e) {
                        e.printStackTrace();
                        if (ErrorUtils.prompt(StringUtils.combineNewline("Could not start process:", e.getLocalizedMessage()))) {
                            consoleText.appendText("Retrying to start BuildTools");
                            continue;
                        }
                        showCloseButton();
                        return;
                    }
                    break;
                }
                final BufferedReader reader = new BufferedReader(new InputStreamReader(Objects.requireNonNull(process).getInputStream()));
                reader.lines().forEach(this::sendMessage);
                int status;
                while (true) {
                    try {
                        status = process.waitFor();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        if (ErrorUtils.prompt(StringUtils.combineNewline("Could not block thread to wait:", e.getLocalizedMessage()))) {
                            consoleText.appendText("Retrying to start BuildTools");
                            continue;
                        }
                        showCloseButton();
                        return;
                    }
                    if (status != 0) {
                        if (ErrorUtils.prompt("Status while running BuildTools is " + status)) {
                            consoleText.appendText("Retrying to start BuildTools");
                            continue;
                        }
                        showCloseButton();
                        return;
                    }
                    if (!mavenJar.exists()) {
                        if (ErrorUtils.prompt("Could not find server jar! (" + mavenJar.getAbsolutePath() + ")")) {
                            consoleText.appendText("Retrying to start BuildTools");
                            continue;
                        }
                        showCloseButton();
                        return;
                    }
                    break;
                }
                serverJar = mavenJar;
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
                        consoleText.appendText("Tuinity software MUST be 1.15 - 1.16, instead it was " + version.getVersion());
                        showCloseButton();
                        return;
                    }
                }
                serverJar = file;
                break;
            }
            default: {
                consoleText.appendText("Server software must be either: " + Arrays.toString(ServerType.values()) + ", instead it was " + type.name());
                showCloseButton();
                return;
            }
        }
        sendMessage("Copying the jar to " + installDir.getAbsolutePath() + "...");
        if (!installDir.exists()) {
            sendMessage("Directory does not exist, creating one");
            while (true) {
                if (!installDir.mkdirs()) {
                    if (ErrorUtils.prompt(("Could not create directory " + installDir.getAbsolutePath()))) {
                        consoleText.appendText("Retrying to create directory");
                        continue;
                    }
                    showCloseButton();
                    return;
                }
                break;
            }
            sendMessage("Successfully created directory");
        }
        final File installFile = Paths.get(installDir.getAbsolutePath(), type.getJarFile()).toFile();
        if (installFile.exists()) {
            while (true) {
                if (!installFile.delete()) {
                    if (ErrorUtils.prompt("Could not delete server jar - " + installFile.getAbsolutePath())) {
                        consoleText.appendText("Retrying to delete server jar");
                        continue;
                    }
                    showCloseButton();
                    return;
                }
                break;
            }
        }
        while (true) {
            try {
                FileUtils.copyFile(serverJar, installFile);
            } catch (IOException e) {
                e.printStackTrace();
                if (ErrorUtils.prompt(StringUtils.combineNewline("Could not copy jar", e.getLocalizedMessage()))) {
                    consoleText.appendText("Retrying to copy jar");
                    continue;
                }
                showCloseButton();
                return;
            }
            break;
        }
        sendMessage("Done! Server jar is in " + installDir.getAbsolutePath());
        showSubmitButton();
    }

    private void showCloseButton() {
        if (submitButton.isVisible()) {
            return;
        }
        consoleText.appendText("Canceled");
        closeButton.setVisible(true);
    }

    private void showSubmitButton() {
        closeButton.setVisible(false);
        submitButton.setVisible(true);
    }

    private void downloadFile(String url, File file, String fileName) throws RuntimeException {
        sendMessage("Making directory: " + file.getParentFile().getAbsolutePath());
        if (!file.getParentFile().exists()) {
            while (true) {
                if (!file.getParentFile().mkdirs()) {
                    if (ErrorUtils.prompt("Could not create directory: " + file.getParentFile().getAbsolutePath())) {
                        consoleText.appendText("Retrying to create directory");
                        continue;
                    }
                    showCloseButton();
                    return;
                }
                break;
            }
        }
        sendMessage("Successfully created directory: " + file.getParentFile().getAbsolutePath());
        URL link;
        while (true) {
            try {
                link = new URL(url);
            } catch (MalformedURLException e) {
                e.printStackTrace();
                if (ErrorUtils.prompt(StringUtils.combineNewline("Could not form URL: " + url, e.getLocalizedMessage()))) {
                    consoleText.appendText("Retrying to form URL");
                    continue;
                }
                showCloseButton();
                return;
            }
            break;
        }
        sendMessage("Starting download of " + fileName);
        while (true) {
            try {
                FileUtils.copyURLToFile(link, file);
            } catch (IOException e) {
                e.printStackTrace();
                if (ErrorUtils.prompt(StringUtils.combineNewline("Could not download " + fileName, e.getLocalizedMessage()))) {
                    consoleText.appendText("Retrying download");
                    continue;
                }
                showCloseButton();
                return;
            }
            break;
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
}
