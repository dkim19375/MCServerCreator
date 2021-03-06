package me.dkim19375.mcservercreator.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import me.dkim19375.mcservercreator.MCServerCreator;
import me.dkim19375.mcservercreator.util.ColorUtils;
import me.dkim19375.mcservercreator.util.ServerType;

public class ServerTypeController {
    @FXML
    private VBox background;
    @FXML
    private Button tuinityButton;
    @FXML
    private Button paperButton;
    @FXML
    private Button spigotButton;

    @FXML
    private void initialize() {
        final MCServerCreator instance = MCServerCreator.getInstance();
        instance.setServerTypeController(this);
        tuinityButton.setBackground(ColorUtils.getBackground(187, 134, 252));
        paperButton.setBackground(ColorUtils.getBackground(187, 134, 252));
        spigotButton.setBackground(ColorUtils.getBackground(187, 134, 252));
        background.setBackground(ColorUtils.getBackground(20, 20, 20));
        tuinityButton.setOnAction((event) -> {
            instance.getPrimaryStage().getScene().setRoot(instance.getChooseVersionRoot());
            instance.setServerType(ServerType.TUINITY);
            instance.getVersionController().onShow();
        });
        paperButton.setOnAction((event) -> {
            instance.getPrimaryStage().getScene().setRoot(instance.getChooseVersionRoot());
            instance.setServerType(ServerType.PAPER);
            instance.getVersionController().onShow();
        });
        spigotButton.setOnAction((event) -> {
            instance.getPrimaryStage().getScene().setRoot(instance.getChooseVersionRoot());
            instance.setServerType(ServerType.SPIGOT);
            instance.getVersionController().onShow();
        });
    }
}
