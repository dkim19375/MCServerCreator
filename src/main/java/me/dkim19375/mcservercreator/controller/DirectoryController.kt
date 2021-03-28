package me.dkim19375.mcservercreator.controller

import javafx.event.EventHandler
import javafx.fxml.FXML
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.layout.BorderPane
import javafx.scene.layout.VBox
import javafx.stage.DirectoryChooser
import me.dkim19375.mcservercreator.MCServerCreator
import me.dkim19375.mcservercreator.util.ColorUtils
import java.io.*
import java.util.concurrent.Executors

class DirectoryController {
    @FXML
    private lateinit var outerBackground: BorderPane
    @FXML
    private lateinit var background: VBox
    @FXML
    private lateinit var changeButton: Button
    @FXML
    private lateinit var directory: Label
    @FXML
    private lateinit var backButton: Button
    @FXML
    private lateinit var nextButton: Button
    private lateinit var directoryFile: File
    private var done = false
    @FXML
    private fun initialize() {
        MCServerCreator.instance.directoryController = this
        outerBackground.background = ColorUtils.getBackground(20, 20, 20)
        background.background = ColorUtils.getBackground(20, 20, 20)
        changeButton.background = ColorUtils.getBackground(187, 134, 252)
        backButton.background = ColorUtils.getBackground(3, 218, 197)
        nextButton.background = ColorUtils.getBackground(187, 134, 252)
        changeButton.onAction = EventHandler setOnAction@{
            val directoryChooser = DirectoryChooser()
            directoryChooser.title = "Server Directory"
            directoryFile = directoryChooser.showDialog(MCServerCreator.instance.primaryStage)
            directory.text = directoryFile.absolutePath
        }
        backButton.onAction = EventHandler {
            done = false
            MCServerCreator.instance.primaryStage.scene.root = MCServerCreator.instance.chooseVersionRoot
        }
        nextButton.onAction = EventHandler setOnAction@{
            if (done) {
                return@setOnAction
            }
            done = true
            MCServerCreator.instance.primaryStage.scene.root = MCServerCreator.instance.installerRoot
            Executors.newSingleThreadExecutor()
                .submit { MCServerCreator.instance.installerController.onShow() }
        }
    }

    fun getDirectory(): File {
        return directoryFile
    }
}