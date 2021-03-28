package me.dkim19375.mcservercreator.controller

import javafx.event.EventHandler
import javafx.fxml.FXML
import javafx.scene.control.Button
import javafx.scene.layout.VBox
import me.dkim19375.mcservercreator.MCServerCreator
import me.dkim19375.mcservercreator.util.ColorUtils
import me.dkim19375.mcservercreator.util.ServerType

class ServerTypeController {
    @FXML
    private lateinit var background: VBox
    @FXML
    private lateinit var tuinityButton: Button
    @FXML
    private lateinit var paperButton: Button
    @FXML
    private lateinit var spigotButton: Button

    @FXML
    private fun initialize() {
        val instance: MCServerCreator = MCServerCreator.instance
        instance.serverTypeController = this
        tuinityButton.background = ColorUtils.getBackground(187, 134, 252)
        paperButton.background = ColorUtils.getBackground(187, 134, 252)
        spigotButton.background = ColorUtils.getBackground(187, 134, 252)
        background.background = ColorUtils.getBackground(20, 20, 20)
        tuinityButton.onAction = EventHandler {
            instance.primaryStage.scene.root = instance.chooseVersionRoot
            instance.serverType = ServerType.TUINITY
            instance.versionController.onShow()
        }
        paperButton.onAction = EventHandler {
            instance.primaryStage.scene.root = instance.chooseVersionRoot
            instance.serverType = ServerType.PAPER
            instance.versionController.onShow()
        }
        spigotButton.onAction = EventHandler {
            instance.primaryStage.scene.root = instance.chooseVersionRoot
            instance.serverType = ServerType.SPIGOT
            instance.versionController.onShow()
        }
    }
}