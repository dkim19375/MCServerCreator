package me.dkim19375.mcservercreator.controller

import javafx.collections.FXCollections
import javafx.event.EventHandler
import javafx.fxml.FXML
import javafx.scene.control.*
import javafx.scene.layout.BorderPane
import javafx.scene.layout.VBox
import me.dkim19375.mcservercreator.MCServerCreator
import me.dkim19375.mcservercreator.util.ColorUtils
import me.dkim19375.mcservercreator.util.ServerType
import me.dkim19375.mcservercreator.util.ServerVersion
import java.util.*
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

class VersionController {
    @FXML
    private lateinit var outerBackground: BorderPane
    @FXML
    private lateinit var background: VBox
    @FXML
    private lateinit var versionList: ListView<String>
    @FXML
    private lateinit var submitButton: Button
    @FXML
    private lateinit var mustSelectLabel: Label
    @FXML
    private lateinit var backButton: Button
    private var done = false

    @FXML
    private fun initialize() {
        MCServerCreator.instance.versionController = this
        outerBackground.background = ColorUtils.getBackground(20, 20, 20)
        background.background = ColorUtils.getBackground(20, 20, 20)
        submitButton.background = ColorUtils.getBackground(187, 134, 252)
        versionList.background = ColorUtils.getBackground(40, 40, 40)
        backButton.background = ColorUtils.getBackground(3, 218, 197)
        versionList.scaleX = 1.8
        versionList.scaleY = 1.8
        versionList.scaleZ = 1.8
        mustSelectLabel.isVisible = false
        backButton.onAction = EventHandler {
            done = false
            MCServerCreator.instance.primaryStage.scene.root = MCServerCreator.instance.chooseTypeRoot
        }
    }

    fun onShow() {
        val versions = FXCollections.observableArrayList<String?>()
        when (MCServerCreator.instance.serverType) {
            ServerType.PAPER, ServerType.SPIGOT -> {
                versions.add(ServerVersion.v1_8.version)
                versions.add(ServerVersion.v1_9.version)
                versions.add(ServerVersion.v1_10.version)
                versions.add(ServerVersion.v1_11.version)
                versions.add(ServerVersion.v1_12.version)
                versions.add(ServerVersion.v1_13.version)
                versions.add(ServerVersion.v1_14.version)
                versions.add(ServerVersion.v1_15.version)
                versions.add(ServerVersion.v1_16.version)
            }
            ServerType.TUINITY -> {
                versions.add(ServerVersion.v1_15.version)
                versions.add(ServerVersion.v1_16.version)
            }
        }
        versions.reverse()
        versionList.items = versions
        versionList.background = ColorUtils.getBackground(40, 40, 40)
        submitButton.onAction = EventHandler setOnAction@{
            if (done) {
                return@setOnAction
            }
            done = true
            if (versionList.selectionModel.selectedItems.size < 1) {
                val service = Executors.newSingleThreadScheduledExecutor()
                mustSelectLabel.isVisible = true
                service.schedule({ mustSelectLabel.isVisible = false }, 3, TimeUnit.SECONDS)
                return@setOnAction
            }
            MCServerCreator.instance
                .version = ServerVersion.fromString(versionList.selectionModel.selectedItem)!!
            MCServerCreator.instance.primaryStage.scene.root = MCServerCreator.instance.chooseDirectoryRoot
        }
    }
}