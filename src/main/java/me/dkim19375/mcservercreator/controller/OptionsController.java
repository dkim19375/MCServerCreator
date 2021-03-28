package me.dkim19375.mcservercreator.controller;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import me.dkim19375.mcservercreator.MCServerCreator;
import me.dkim19375.mcservercreator.util.ColorUtils;
import me.dkim19375.mcservercreator.util.ErrorUtils;
import me.dkim19375.mcservercreator.util.StringUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.Properties;
import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class OptionsController {
    private boolean shown = false;
    private Properties properties = null;

    @FXML
    private VBox background;
    @FXML
    private Button submitButton;
    @FXML
    private ScrollPane scrollPane;
    @FXML
    private VBox vboxInScrollPane;

    // GAMEPLAY
    @FXML
    private Label gameplayLabel;
    @FXML
    private CheckBox spawnAnimals;
    @FXML
    private CheckBox spawnMonsters;
    @FXML
    private CheckBox spawnNPCs;
    @FXML
    private CheckBox hardcoreMode;
    @FXML
    private CheckBox netherWorld;
    @FXML
    private CheckBox pvp;
    @FXML
    private CheckBox flight;
    @FXML
    private CheckBox forceGamemode;
    @FXML
    private ComboBox<String> difficulty;
    @FXML
    private ComboBox<String> gamemode;
    @FXML
    private ComboBox<Integer> viewDistance;

    // APPEARANCE
    @FXML
    private Label appearanceLabel;
    @FXML
    private Label motdLabel;
    @FXML
    private TextField motd;
    @FXML
    private Label resourcePackLabel;
    @FXML
    private TextField resourcePack;


    @SuppressWarnings("FieldCanBeLocal")
    private Set<Node> nodes = null;

    @FXML
    private void initialize() {
        nodes = Set.of(
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
                resourcePack);
        MCServerCreator.getInstance().setOptionsController(this);
        background.setBackground(ColorUtils.getBackground(20, 20, 20));
        submitButton.setBackground(ColorUtils.getBackground(187, 134, 252));
        scrollPane.setBackground(ColorUtils.getBackground(30, 30, 30));
        vboxInScrollPane.setBackground(ColorUtils.getBackground(30, 30, 30));
        scrollPane.setFitToWidth(true);
        difficulty.setItems(FXCollections.observableArrayList("peaceful", "easy", "medium", "hard"));
        gamemode.setItems(FXCollections.observableArrayList("survival", "creative", "adventure", "spectator"));
        viewDistance.setItems(FXCollections.observableArrayList(IntStream.range(3, 32).boxed().collect(Collectors.toList())));
        nodes.forEach(node -> {
            if (node instanceof Label) {
                ((Label) node).setTextFill(Color.WHITE);
            }
            node.setStyle("-fx-control-inner-background: #111111; " +
                    "-fx-text-background-color: #FFFFFF; " +
                    "-fx-text-fill: #FFFFFF; " +
                    "-fx-stroke-width: #FFFFFF; " +
                    "-fx-padding: 0;");
            if (node instanceof ComboBox<?>) {
                final ComboBox<?> box = (ComboBox<?>) node;
                box.getStylesheets()
                        .add(Objects.requireNonNull(getClass().getClassLoader()
                                .getResource("combo_box.css")).toExternalForm());
                box.setBackground(ColorUtils.getBackground(0, 0, 0));
            }
            if (node instanceof TextField) {
                ((TextField) node).setBackground(ColorUtils.getBackground(0, 0, 0));
            }
        });
        ServerProperty.setupNodes(this);
    }

    public void onShow() {
        if (!shown) {
            shown = true;
            try {
                Platform.runLater(this::onShow);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return;
        }
        properties = new Properties();
        final File propFile = Paths.get(MCServerCreator.getInstance().getDirectoryController()
                        .getDirectory().getAbsolutePath(),"server.properties").toFile();
        if (propFile.exists()) {
            try {
                properties.load(new FileInputStream(propFile));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        setDefaultValues();
    }

    private String getStringFromProperties(ServerProperty key) {
        return properties.getProperty(key.getKey()) == null
                ? getDefaultProperties().getProperty(key.getKey())
                : properties.getProperty(key.getKey());
    }

    @SuppressWarnings("SameParameterValue")
    private int getIntFromProperties(ServerProperty key) {
        if (!properties.containsKey(key.getKey())) {
            try {
                return Integer.parseInt(getDefaultProperties().getProperty(key.getKey()));
            } catch (Exception ignored) {
                return 0;
            }
        }
        try {
            return Integer.parseInt(properties.getProperty(key.getKey()));
        } catch (Exception ignored) {
            try {
                return Integer.parseInt(getDefaultProperties().getProperty(key.getKey()));
            } catch (Exception e) {
                return 0;
            }
        }
    }

    private boolean getBooleanFromProperties(ServerProperty key) {
        if (!properties.containsKey(key)) {
            try {
                return Boolean.getBoolean(getDefaultProperties().getProperty(key.getKey()));
            } catch (Exception ignored) {
                System.out.println("false 1: getDefaultProperties().getProperty(key.getKey()) "
                        + getDefaultProperties().getProperty(key.getKey()));
                return false;
            }
        }
        try {
            return Boolean.getBoolean(properties.getProperty(key.getKey()));
        } catch (Exception ignored) {
            try {
                return Boolean.getBoolean(getDefaultProperties().getProperty(key.getKey()));
            } catch (Exception e) {
                return false;
            }
        }
    }

    private void setInProperties(ServerProperty key, String value) {
        properties.setProperty(key.getKey(), value);
    }

    private void savePropertiesFile() {
        final File propFile = Paths.get(MCServerCreator.getInstance().getDirectoryController()
                .getDirectory().getAbsolutePath(),"server.properties").toFile();
        while (!propFile.getParentFile().mkdirs()) {
            if (ErrorUtils.prompt("Could not create folder")) {
                continue;
            }
            return;
        }
        while(true) {
            try {
                if (propFile.createNewFile()) {
                    break;
                }
                if (ErrorUtils.prompt("Could not create file")) {
                    continue;
                }
                return;
            } catch (IOException e) {
                e.printStackTrace();
                if (ErrorUtils.prompt(StringUtils.combineNewline("Could not create file", e.getLocalizedMessage()))) {
                    continue;
                }
                return;
            }
        }
        try {
            properties.store(new FileOutputStream(propFile), null);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setDefaultValues() {
        spawnAnimals.setSelected(getBooleanFromProperties(ServerProperty.SPAWN_ANIMALS));
        spawnMonsters.setSelected(getBooleanFromProperties(ServerProperty.SPAWN_MONSTERS));
        spawnNPCs.setSelected(getBooleanFromProperties(ServerProperty.SPAWN_NPCS));
        hardcoreMode.setSelected(getBooleanFromProperties(ServerProperty.HARDCORE));
        netherWorld.setSelected(getBooleanFromProperties(ServerProperty.NETHER));
        pvp.setSelected(getBooleanFromProperties(ServerProperty.PVP));
        flight.setSelected(getBooleanFromProperties(ServerProperty.FLIGHT));
        forceGamemode.setSelected(getBooleanFromProperties(ServerProperty.FORCE_GAMEMODE));
        difficulty.getSelectionModel().select(getStringFromProperties(ServerProperty.DIFFICULTY));
        gamemode.getSelectionModel().select(getStringFromProperties(ServerProperty.GAMEMODE));
        viewDistance.getSelectionModel().select(getIntFromProperties(ServerProperty.VIEW_DISTANCE));
        motd.setText(getStringFromProperties(ServerProperty.MOTD));
        resourcePack.setText(getStringFromProperties(ServerProperty.RESOURCE_PACK));
    }

    private Properties getDefaultProperties() {
        Properties prop = new Properties();
        for (ServerProperty property : ServerProperty.values()) {
            prop.put(property.getKey(), property.getDefaultValue());
        }
        return prop;
    }

    public enum ServerProperty {
        SPAWN_PROTECTION("spawn-protection", "16", ValueType.INTEGER),
        FORCE_GAMEMODE("force-gamemode", "false", ValueType.BOOLEAN),
        NETHER("allow-nether", "true", ValueType.BOOLEAN),
        GAMEMODE("gamemode", "survival", ValueType.STRING),
        DIFFICULTY("difficulty", "easy", ValueType.STRING),
        SPAWN_MONSTERS("spawn-monsters", "true", ValueType.BOOLEAN),
        PVP("pvp", "true", ValueType.BOOLEAN),
        SNOOPER("snooper-enabled", "true", ValueType.BOOLEAN),
        HARDCORE("hardcore", "false", ValueType.BOOLEAN),
        COMMAND_BLOCK("enable-command-block", "false", ValueType.BOOLEAN),
        MAX_PLAYERS("max-players", "20", ValueType.INTEGER),
        PORT("server-port", "25565", ValueType.INTEGER),
        SPAWN_NPCS("spawn-npcs", "true", ValueType.BOOLEAN),
        FLIGHT("allow-flight", "false", ValueType.BOOLEAN),
        LEVEL_NAME("level-name", "world", ValueType.STRING),
        VIEW_DISTANCE("view-distance", "10", ValueType.INTEGER),
        RESOURCE_PACK("resource-pack", "", ValueType.STRING),
        SPAWN_ANIMALS("spawn-animals", "true", ValueType.BOOLEAN),
        WHITELIST("white-list", "false", ValueType.BOOLEAN),
        GENERATE_STRUCTURES("generate-structures", "true", ValueType.BOOLEAN),
        MAX_BUILD_HEIGHT("max-build-height", "256", ValueType.INTEGER),
        ONLINE_MODE("online-mode", "true", ValueType.BOOLEAN),
        LEVEL_SEED("level-seed", "", ValueType.STRING),
        MOTD("motd", "A Minecraft Server", ValueType.STRING);

        public enum ValueType {
            STRING, BOOLEAN, INTEGER
        }

        private Node node = null;
        private final String key;
        private final String defaultValue;
        private final ValueType valueType;

        ServerProperty(String key, String defaultValue, ValueType valueType) {
            this.key = key;
            this.defaultValue = defaultValue;
            this.valueType = valueType;
        }

        public Node getNode() {
            return node;
        }

        public void setNode(Node node) {
            this.node = node;
        }

        public String getKey() {
            return key;
        }

        public String getDefaultValue() {
            return defaultValue;
        }

        public ValueType getValueType() {
            return valueType;
        }

        public static void setupNodes(OptionsController controller) {
            SPAWN_ANIMALS.setNode(controller.spawnAnimals);
            SPAWN_MONSTERS.setNode(controller.spawnMonsters);
            SPAWN_NPCS.setNode(controller.spawnNPCs);
            HARDCORE.setNode(controller.hardcoreMode);
            NETHER.setNode(controller.netherWorld);
            PVP.setNode(controller.pvp);
            FLIGHT.setNode(controller.flight);
            FORCE_GAMEMODE.setNode(controller.forceGamemode);
            DIFFICULTY.setNode(controller.difficulty);
            GAMEMODE.setNode(controller.gamemode);
            VIEW_DISTANCE.setNode(controller.viewDistance);
            MOTD.setNode(controller.motd);
            RESOURCE_PACK.setNode(controller.resourcePack);
        }

        @Override
        public String toString() {
            return "ServerProperty{" +
                    "node=" + node +
                    ", key='" + key + '\'' +
                    ", defaultValue='" + defaultValue + '\'' +
                    ", valueType=" + valueType +
                    '}';
        }
    }
}
