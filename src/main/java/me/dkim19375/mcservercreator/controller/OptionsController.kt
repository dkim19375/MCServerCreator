package me.dkim19375.mcservercreator.controller

import javafx.application.Platform
import javafx.collections.FXCollections
import javafx.fxml.FXML
import javafx.scene.Node
import javafx.scene.control.*
import javafx.scene.layout.VBox
import javafx.scene.paint.Color
import me.dkim19375.mcservercreator.MCServerCreator
import me.dkim19375.mcservercreator.util.*
import org.jetbrains.annotations.Contract
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.nio.file.Paths
import java.util.*
import java.util.function.Consumer
import java.util.stream.Collectors
import java.util.stream.IntStream

class OptionsController {
    private var shown = false
    private lateinit var properties: Properties

    @FXML
    private lateinit var background: VBox
    @FXML
    private lateinit var submitButton: Button
    @FXML
    private lateinit var scrollPane: ScrollPane
    @FXML
    private lateinit var vboxInScrollPane: VBox
    // GAMEPLAY
    @FXML
    private lateinit var gameplayLabel: Label
    @FXML
    private lateinit var spawnAnimals: CheckBox
    @FXML
    private lateinit var spawnMonsters: CheckBox
    @FXML
    private lateinit var spawnNPCs: CheckBox
    @FXML
    private lateinit var hardcoreMode: CheckBox
    @FXML
    private lateinit var netherWorld: CheckBox
    @FXML
    private lateinit var pvp: CheckBox
    @FXML
    private lateinit var flight: CheckBox
    @FXML
    private lateinit var forceGamemode: CheckBox
    @FXML
    private lateinit var difficulty: ComboBox<String>
    @FXML
    private lateinit var gamemode: ComboBox<String>
    @FXML
    private lateinit var viewDistance: ComboBox<Int>
    // APPEARANCE
    @FXML
    private lateinit var appearanceLabel: Label
    @FXML
    private lateinit var motdLabel: Label
    @FXML
    private lateinit var motd: TextField
    @FXML
    private lateinit var resourcePackLabel: Label
    @FXML
    private lateinit var resourcePack: TextField
    private lateinit var nodes: Set<Node>
    
    @FXML
    private fun initialize() {
        nodes = setOf(
            gameplayLabel,
            appearanceLabel,
            spawnAnimals,
            spawnMonsters,
            spawnNPCs,
            hardcoreMode,
            netherWorld,
            pvp,
            flight,
            forceGamemode,
            difficulty,
            gamemode,
            viewDistance,
            motdLabel,
            motd,
            resourcePackLabel,
            resourcePack
        )
        MCServerCreator.instance.optionsController = this
        background.background = ColorUtils.getBackground(20, 20, 20)
        submitButton.background = ColorUtils.getBackground(187, 134, 252)
        scrollPane.background = ColorUtils.getBackground(30, 30, 30)
        vboxInScrollPane.background = ColorUtils.getBackground(30, 30, 30)
        scrollPane.isFitToWidth = true
        difficulty.items = FXCollections.observableArrayList("peaceful", "easy", "medium", "hard")
        gamemode.items = FXCollections.observableArrayList("survival", "creative", "adventure", "spectator")
        viewDistance.items = FXCollections.observableArrayList(
            IntStream.range(3, 32).boxed().collect(Collectors.toList())
        )
        nodes.forEach(Consumer { node: Node ->
            if (node is Label) {
                node.textFill = Color.WHITE
            }
            node.style = "-fx-control-inner-background: #111111; " +
                    "-fx-text-background-color: #FFFFFF; " +
                    "-fx-text-fill: #FFFFFF; " +
                    "-fx-stroke-width: #FFFFFF; " +
                    "-fx-padding: 0;"
            if (node is ComboBox<*>) {
                node.stylesheets
                    .add(
                        Objects.requireNonNull(
                            javaClass.classLoader
                                .getResource("combo_box.css")
                        ).toExternalForm()
                    )
                node.background = ColorUtils.getBackground(0, 0, 0)
            }
            if (node is TextField) {
                node.background = ColorUtils.getBackground(0, 0, 0)
            }
        })
        ServerProperty.setupNodes(this)
    }

    fun onShow() {
        if (!shown) {
            shown = true
            try {
                Platform.runLater { onShow() }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return
        }
        properties = Properties()
        val propFile = Paths.get(
            MCServerCreator.instance.directoryController
                .getDirectory().absolutePath, "server.properties"
        ).toFile()
        if (propFile.exists()) {
            try {
                properties.load(FileInputStream(propFile))
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        setDefaultValues()
    }

    private fun getStringFromProperties(key: ServerProperty): String {
        return if (properties.getProperty(key.key) == null) defaultProperties.getProperty(key.key) else properties.getProperty(
            key.key
        )
    }

    @Contract("null -> null")
    private fun getIntFromString(str: String?): Int? {
        return if (str == null) {
            null
        } else try {
            str.toInt()
        } catch (ignored: Exception) {
            null
        }
    }

    @Contract(value = "null -> null", pure = true)
    private fun getBooleanFromString(str: String?): Boolean? {
        return if (str == null) {
            null
        } else try {
            java.lang.Boolean.parseBoolean(str)
        } catch (ignored: Exception) {
            null
        }
    }

    private fun getIntFromProperties(@Suppress("SameParameterValue") key: ServerProperty): Int {
        val property = getIntFromString(properties.getProperty(key.key))
        val defaultProp = getIntFromString(defaultProperties.getProperty(key.key))!!
        return property ?: defaultProp
    }

    private fun getBooleanFromProperties(key: ServerProperty): Boolean {
        val property = getBooleanFromString(properties.getProperty(key.key))
        val defaultProp = getBooleanFromString(defaultProperties.getProperty(key.key))!!
        return property ?: defaultProp
    }

    private fun setInProperties(key: ServerProperty, value: String) {
        properties.setProperty(key.key, value)
    }

    private fun savePropertiesFile() {
        val propFile = Paths.get(
            MCServerCreator.instance.directoryController
                .getDirectory().absolutePath, "server.properties"
        ).toFile()
        while (!propFile.parentFile.mkdirs()) {
            if ("Could not create folder".prompt()) {
                continue
            }
            return
        }
        while (true) {
            try {
                if (propFile.createNewFile()) {
                    break
                }
                if ("Could not create file".prompt()) {
                    continue
                }
                return
            } catch (e: IOException) {
                e.printStackTrace()
                if ("Could not create file".combineNewline(e.localizedMessage).prompt()) {
                    continue
                }
                return
            }
        }
        try {
            properties.store(FileOutputStream(propFile), null)
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun setDefaultValues() {
        spawnAnimals.isSelected = getBooleanFromProperties(ServerProperty.SPAWN_ANIMALS)
        spawnMonsters.isSelected = getBooleanFromProperties(ServerProperty.SPAWN_MONSTERS)
        spawnNPCs.isSelected = getBooleanFromProperties(ServerProperty.SPAWN_NPCS)
        hardcoreMode.isSelected = getBooleanFromProperties(ServerProperty.HARDCORE)
        netherWorld.isSelected = getBooleanFromProperties(ServerProperty.NETHER)
        pvp.isSelected = getBooleanFromProperties(ServerProperty.PVP)
        flight.isSelected = getBooleanFromProperties(ServerProperty.FLIGHT)
        forceGamemode.isSelected = getBooleanFromProperties(ServerProperty.FORCE_GAMEMODE)
        difficulty.selectionModel.select(getStringFromProperties(ServerProperty.DIFFICULTY))
        gamemode.selectionModel.select(getStringFromProperties(ServerProperty.GAMEMODE))
        select(viewDistance.selectionModel, (getIntFromProperties(ServerProperty.VIEW_DISTANCE) - 3))
        motd.text = getStringFromProperties(ServerProperty.MOTD)
        resourcePack.text = getStringFromProperties(ServerProperty.RESOURCE_PACK)
    }

    private fun <T> select(selectionModel: SingleSelectionModel<T>, value: Int) {
        selectionModel.select(value)
    }

    private val defaultProperties: Properties
        get() {
            val prop = Properties()
            for (property in ServerProperty.values()) {
                prop[property.key] = property.defaultValue
            }
            return prop
        }

    enum class ServerProperty(val key: String, val defaultValue: String, val valueType: ValueType) {
        SPAWN_PROTECTION("spawn-protection", "16", ValueType.INTEGER), FORCE_GAMEMODE(
            "force-gamemode",
            "false",
            ValueType.BOOLEAN
        ),
        NETHER("allow-nether", "true", ValueType.BOOLEAN), GAMEMODE(
            "gamemode",
            "survival",
            ValueType.STRING
        ),
        DIFFICULTY("difficulty", "easy", ValueType.STRING), SPAWN_MONSTERS(
            "spawn-monsters",
            "true",
            ValueType.BOOLEAN
        ),
        PVP("pvp", "true", ValueType.BOOLEAN), SNOOPER(
            "snooper-enabled",
            "true",
            ValueType.BOOLEAN
        ),
        HARDCORE("hardcore", "false", ValueType.BOOLEAN), COMMAND_BLOCK(
            "enable-command-block",
            "false",
            ValueType.BOOLEAN
        ),
        MAX_PLAYERS("max-players", "20", ValueType.INTEGER), PORT(
            "server-port",
            "25565",
            ValueType.INTEGER
        ),
        SPAWN_NPCS("spawn-npcs", "true", ValueType.BOOLEAN), FLIGHT(
            "allow-flight",
            "false",
            ValueType.BOOLEAN
        ),
        LEVEL_NAME("level-name", "world", ValueType.STRING), VIEW_DISTANCE(
            "view-distance",
            "10",
            ValueType.INTEGER
        ),
        RESOURCE_PACK("resource-pack", "", ValueType.STRING), SPAWN_ANIMALS(
            "spawn-animals",
            "true",
            ValueType.BOOLEAN
        ),
        WHITELIST("white-list", "false", ValueType.BOOLEAN), GENERATE_STRUCTURES(
            "generate-structures",
            "true",
            ValueType.BOOLEAN
        ),
        MAX_BUILD_HEIGHT("max-build-height", "256", ValueType.INTEGER), ONLINE_MODE(
            "online-mode",
            "true",
            ValueType.BOOLEAN
        ),
        LEVEL_SEED("level-seed", "", ValueType.STRING), MOTD("motd", "A Minecraft Server", ValueType.STRING);

        enum class ValueType {
            STRING, BOOLEAN, INTEGER
        }

        var node: Node? = null
        override fun toString(): String {
            return "ServerProperty{" +
                    "node=" + node +
                    ", key='" + key + '\'' +
                    ", defaultValue='" + defaultValue + '\'' +
                    ", valueType=" + valueType +
                    '}'
        }

        companion object {
            fun setupNodes(controller: OptionsController) {
                SPAWN_ANIMALS.node = controller.spawnAnimals
                SPAWN_MONSTERS.node = controller.spawnMonsters
                SPAWN_NPCS.node = controller.spawnNPCs
                HARDCORE.node = controller.hardcoreMode
                NETHER.node = controller.netherWorld
                PVP.node = controller.pvp
                FLIGHT.node = controller.flight
                FORCE_GAMEMODE.node = controller.forceGamemode
                DIFFICULTY.node = controller.difficulty
                GAMEMODE.node = controller.gamemode
                VIEW_DISTANCE.node = controller.viewDistance
                MOTD.node = controller.motd
                RESOURCE_PACK.node = controller.resourcePack
            }
        }
    }
}