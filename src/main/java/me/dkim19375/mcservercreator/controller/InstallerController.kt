package me.dkim19375.mcservercreator.controller

import javafx.application.Platform
import javafx.beans.value.ObservableValue
import javafx.event.EventHandler
import javafx.fxml.FXML
import javafx.scene.control.Button
import javafx.scene.control.TextArea
import javafx.scene.layout.VBox
import me.dkim19375.mcservercreator.MCServerCreator
import me.dkim19375.mcservercreator.util.*
import org.apache.commons.io.FileUtils
import java.io.BufferedReader
import java.io.File
import java.io.IOException
import java.io.InputStreamReader
import java.net.MalformedURLException
import java.net.URL
import java.nio.file.Paths
import java.util.*
import java.util.concurrent.Executors

class InstallerController {
    private val mainThread = Thread.currentThread()
    @FXML
    lateinit var submitButton: Button
    @FXML
    lateinit var closeButton: Button
    @FXML
    private lateinit var background: VBox
    @FXML
    private lateinit var consoleText: TextArea
    private var shown = false

    @FXML
    private fun initialize() {
        MCServerCreator.instance.installerController = this
        submitButton.isVisible = false
        closeButton.isVisible = false
        background.background = ColorUtils.getBackground(20, 20, 20)
        submitButton.background = ColorUtils.getBackground(187, 134, 252)
        closeButton.background = ColorUtils.getBackground(187, 134, 252)
        consoleText.background = ColorUtils.getBackground(40, 40, 40)
        consoleText.scaleX = 1.8
        consoleText.scaleY = 1.8
        consoleText.scaleZ = 1.8
        consoleText.isCache = false
        for (node in consoleText.childrenUnmodifiable) {
            node.isCache = false
        }
        consoleText.textProperty()
            .addListener { _: ObservableValue<out String>, _: String, newValue: String ->
                consoleText.scrollTop = Double.MAX_VALUE
                if (!newValue.endsWith("\n")) {
                    consoleText.text = "$newValue\n"
                    for (node in consoleText.childrenUnmodifiable) {
                        node.isCache = false
                    }
                }
            }
        submitButton.onAction = EventHandler {
            submitButton.isVisible = false
            MCServerCreator.instance.primaryStage.scene.root = MCServerCreator.instance.optionsRoot
            Executors.newSingleThreadExecutor()
                .submit { MCServerCreator.instance.optionsController.onShow() }
        }
    }

    fun onShow() {
        if (!shown) {
            shown = true
            try {
                onShow()
            } catch (e: Exception) {
                e.printStackTrace()
                val message = e.localizedMessage
                if (("Unknown error while installing"
                            + if (message == null) "" else ":\n$message").prompt()
                ) {
                    consoleText.appendText("Retrying")
                    onShow()
                    return
                }
                consoleText.appendText("Cancelled")
                showCloseButton()
            }
            return
        }
        sendMessage("Starting installation")
        val version: ServerVersion? = MCServerCreator.instance.version
        if (version == null) {
            consoleText.appendText("ERROR: Server version is not set!")
            showCloseButton()
            return
        }
        val type: ServerType? = MCServerCreator.instance.serverType
        if (type == null) {
            consoleText.appendText("ERROR: Server type is not set!")
            showCloseButton()
            return
        }
        val installDir: File = MCServerCreator.instance.directoryController.getDirectory()
        val serverJar: File?
        when (type) {
            ServerType.SPIGOT -> {
                sendMessage("Looking to see if BuildTools was already ran for this version")
                val mavenJar = Paths.get(
                    System.getProperty("user.home"),
                    ".m2", "repository", "org", "spigotmc", "spigot",
                    version.version + "-R0.1-SNAPSHOT",
                    "spigot-" + version.version + "-R0.1-SNAPSHOT.jar"
                ).toFile()
                val file = Paths.get("data", "spigot", "buildtools", "BuildTools.jar").toFile()
                if (!mavenJar.exists()) {
                    sendMessage("Buildtools has not been ran before, downloading BuildTools.jar")
                    val before = System.currentTimeMillis()
                    downloadFile(
                        "https://hub.spigotmc.org/jenkins/job/BuildTools/lastSuccessfulBuild/artifact/target/BuildTools.jar",
                        file,
                        "BuildTools.jar"
                    )
                    val current = System.currentTimeMillis()
                    sendMessage(
                        "Successfully downloaded in "
                                + (current - before).toDouble() / 1000.0 + " seconds (" + (current - before) + "ms)"
                    )
                    sendMessage("Running BuildTools, this will take 10+ minutes.")
                    try {
                        Thread.sleep(1000)
                    } catch (e: InterruptedException) {
                        e.printStackTrace()
                    }
                    sendMessage("NOTE: It might seem like nothing is happening... it might take up to a minute for anything to output.")
                    sendMessage("If nothing outputs within 2-5 minutes, you should restart.")
                    val builder = ProcessBuilder(
                        "cmd.exe", "/c", "java -jar " + file.absolutePath
                                + " --rev " + version.version
                    )
                    builder.redirectErrorStream(true)
                    val process: Process
                    while (true) {
                        process = try {
                            builder.start()
                        } catch (e: IOException) {
                            e.printStackTrace()
                            if ("Could not start process:".combineNewline(
                                    e.localizedMessage
                                ).prompt()
                            ) {
                                consoleText.appendText("Retrying to start BuildTools")
                                continue
                            }
                            showCloseButton()
                            return
                        }
                        break
                    }
                    val reader = BufferedReader(InputStreamReader(Objects.requireNonNull(process).inputStream))
                    reader.lines().forEach { message: String -> sendMessage(message) }
                    var status: Int
                    while (true) {
                        status = try {
                            process.waitFor()
                        } catch (e: InterruptedException) {
                            e.printStackTrace()
                            if ("Could not block thread to wait:".combineNewline(e.localizedMessage).prompt()
                            ) {
                                consoleText.appendText("Retrying to start BuildTools")
                                continue
                            }
                            showCloseButton()
                            return
                        }
                        if (status != 0) {
                            if ("Status while running BuildTools is $status".prompt()) {
                                consoleText.appendText("Retrying to start BuildTools")
                                continue
                            }
                            showCloseButton()
                            return
                        }
                        if (!mavenJar.exists()) {
                            if (("Could not find server jar! (" + mavenJar.absolutePath + ")").prompt()) {
                                consoleText.appendText("Retrying to start BuildTools")
                                continue
                            }
                            showCloseButton()
                            return
                        }
                        break
                    }
                    serverJar = mavenJar
                    sendMessage("Finished running BuildTools")
                } else {
                    sendMessage("BuildTools was already ran, skipping the BuildTools installation")
                    serverJar = file
                }
            }
            ServerType.PAPER -> {
                val file = Paths.get("data", "paper", type.jarFile).toFile()
                sendMessage("Downloading " + type.jarFile)
                val before = System.currentTimeMillis()
                downloadFile(
                    "https://papermc.io/api/v1/paper/" + version.version + "/latest/download",
                    file,
                    type.jarFile
                )
                val current = System.currentTimeMillis()
                sendMessage(
                    "Successfully downloaded in "
                            + (current - before).toDouble() / 1000.0 + " seconds (" + (current - before) + "ms)"
                )
                serverJar = file
            }
            ServerType.TUINITY -> {
                val file = Paths.get("data", "tuinity", type.jarFile).toFile()
                when (version) {
                    ServerVersion.v1_15 -> {
                        sendMessage("Downloading " + type.jarFile)
                        val before = System.currentTimeMillis()
                        downloadFile(
                            "https://ci.codemc.io/job/Spottedleaf/job/Tuinity/139/artifact/tuinity-paperclip.jar",
                            file,
                            type.jarFile
                        )
                        val current = System.currentTimeMillis()
                        sendMessage(
                            "Successfully downloaded in "
                                    + (current - before).toDouble() / 1000.0 + " seconds (" + (current - before) + "ms)"
                        )
                    }
                    ServerVersion.v1_16 -> {
                        sendMessage(
                            "WARNING: Downloading the latest Tuinity build. " +
                                    "If it is not 1.16, then you will have to manually download the server jar"
                        )
                        sendMessage("Downloading " + type.jarFile)
                        val before = System.currentTimeMillis()
                        downloadFile(
                            "https://ci.codemc.io/job/Spottedleaf/job/Tuinity/lastSuccessfulBuild/artifact/tuinity-paperclip.jar",
                            file,
                            type.jarFile
                        )
                        val current = System.currentTimeMillis()
                        sendMessage(
                            "Successfully downloaded in "
                                    + (current - before).toDouble() / 1000.0 + " seconds (" + (current - before) + "ms)"
                        )
                    }
                    else -> {
                        sendMessage("Downloading " + type.jarFile)
                        consoleText.appendText("Tuinity software MUST be 1.15 - 1.16, instead it was " + version.version)
                        showCloseButton()
                        return
                    }
                }
                serverJar = file
            }
        }
        sendMessage("Copying the jar to " + installDir.absolutePath + "...")
        if (!installDir.exists()) {
            sendMessage("Directory does not exist, creating one")
            while (true) {
                if (!installDir.mkdirs()) {
                    if (("Could not create directory " + installDir.absolutePath).prompt()) {
                        consoleText.appendText("Retrying to create directory")
                        continue
                    }
                    showCloseButton()
                    return
                }
                break
            }
            sendMessage("Successfully created directory")
        }
        val installFile = Paths.get(installDir.absolutePath, type.jarFile).toFile()
        if (installFile.exists()) {
            while (true) {
                if (!installFile.delete()) {
                    if (("Could not delete server jar - " + installFile.absolutePath).prompt()) {
                        consoleText.appendText("Retrying to delete server jar")
                        continue
                    }
                    showCloseButton()
                    return
                }
                break
            }
        }
        while (true) {
            try {
                FileUtils.copyFile(serverJar, installFile)
            } catch (e: IOException) {
                e.printStackTrace()
                if ("Could not copy jar".combineNewline(e.localizedMessage).prompt()) {
                    consoleText.appendText("Retrying to copy jar")
                    continue
                }
                showCloseButton()
                return
            }
            break
        }
        sendMessage("Done! Server jar is in " + installDir.absolutePath)
        showSubmitButton()
    }

    private fun showCloseButton() {
        if (submitButton.isVisible) {
            return
        }
        consoleText.appendText("Canceled")
        closeButton.isVisible = true
    }

    private fun showSubmitButton() {
        closeButton.isVisible = false
        submitButton.isVisible = true
    }

    @Throws(RuntimeException::class)
    private fun downloadFile(url: String, file: File, fileName: String?) {
        sendMessage("Making directory: " + file.parentFile.absolutePath)
        if (!file.parentFile.exists()) {
            while (true) {
                if (!file.parentFile.mkdirs()) {
                    if (("Could not create directory: " + file.parentFile.absolutePath).prompt()) {
                        consoleText.appendText("Retrying to create directory")
                        continue
                    }
                    showCloseButton()
                    return
                }
                break
            }
        }
        sendMessage("Successfully created directory: " + file.parentFile.absolutePath)
        val link: URL
        while (true) {
            link = try {
                URL(url)
            } catch (e: MalformedURLException) {
                e.printStackTrace()
                if ("Could not form URL: $url".combineNewline(e.localizedMessage).prompt()
                ) {
                    consoleText.appendText("Retrying to form URL")
                    continue
                }
                showCloseButton()
                return
            }
            break
        }
        sendMessage("Starting download of $fileName")
        while (true) {
            try {
                FileUtils.copyURLToFile(link, file)
            } catch (e: IOException) {
                e.printStackTrace()
                if ("Could not download $fileName".combineNewline(e.localizedMessage).prompt()
                ) {
                    consoleText.appendText("Retrying download")
                    continue
                }
                showCloseButton()
                return
            }
            break
        }
        sendMessage("Successfully downloaded $fileName")
    }

    private fun sendMessage(message: String) {
        if (Thread.currentThread() !== mainThread) {
            Platform.runLater { consoleText.appendText(message) }
            return
        }
        consoleText.appendText(message)
    }
}