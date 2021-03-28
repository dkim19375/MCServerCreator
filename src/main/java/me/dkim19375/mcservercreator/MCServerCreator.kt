package me.dkim19375.mcservercreator

import javafx.application.Application
import javafx.fxml.FXMLLoader
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.scene.image.Image
import javafx.stage.Stage
import me.dkim19375.mcservercreator.controller.*
import me.dkim19375.mcservercreator.util.ServerType
import me.dkim19375.mcservercreator.util.ServerVersion
import java.util.*
import kotlin.system.exitProcess

fun main(args: Array<String>) {
    MCServerCreator.mainLaunch(args)
}

class MCServerCreator : Application() {
    lateinit var primaryStage: Stage
        private set
    lateinit var chooseTypeRoot: Parent
        private set
    lateinit var chooseVersionRoot: Parent
        private set
    lateinit var chooseDirectoryRoot: Parent
        private set
    lateinit var installerRoot: Parent
        private set
    lateinit var optionsRoot: Parent
        private set
    lateinit var serverType: ServerType
    lateinit var version: ServerVersion
    lateinit var serverTypeController: ServerTypeController
    lateinit var versionController: VersionController
    lateinit var directoryController: DirectoryController
    lateinit var installerController: InstallerController
    lateinit var optionsController: OptionsController

    override fun start(primaryStage: Stage) {
        instance = this
        this.primaryStage = primaryStage
        primaryStage.icons.add(Image(Objects.requireNonNull(javaClass.classLoader.getResourceAsStream("icon.png"))))
        chooseTypeRoot = FXMLLoader.load(
            Objects.requireNonNull(
                javaClass.classLoader
                    .getResource("1_chooseType.fxml")
            )
        )
        chooseVersionRoot = FXMLLoader.load(
            Objects.requireNonNull(
                javaClass.classLoader
                    .getResource("2_chooseVersion.fxml")
            )
        )
        chooseDirectoryRoot = FXMLLoader.load(
            Objects.requireNonNull(
                javaClass.classLoader
                    .getResource("3_chooseDirectory.fxml")
            )
        )
        installerRoot = FXMLLoader.load(
            Objects.requireNonNull(
                javaClass.classLoader
                    .getResource("4_installer.fxml")
            )
        )
        optionsRoot = FXMLLoader.load(
            Objects.requireNonNull(
                javaClass.classLoader
                    .getResource("5_options.fxml")
            )
        )
        primaryStage.title = "MC Server Creator"
        primaryStage.scene = Scene(chooseTypeRoot, 1280.0, 720.0)
        primaryStage.isMaximized = true
        primaryStage.show()
    }

    override fun stop() {
        exitProcess(0)
    }

    companion object {
        lateinit var instance: MCServerCreator
            private set

        fun mainLaunch(args: Array<String>) {
            launch(*args)
        }
    }
}