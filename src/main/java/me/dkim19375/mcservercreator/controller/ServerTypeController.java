package me.dkim19375.mcservercreator.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
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
        MCServerCreator.setServerTypeController(this);
        tuinityButton.setBackground(ColorUtils.getBackground(187, 134, 252));
        paperButton.setBackground(ColorUtils.getBackground(187, 134, 252));
        spigotButton.setBackground(ColorUtils.getBackground(187, 134, 252));
        background.setBackground(ColorUtils.getBackground(20, 20, 20));
        tuinityButton.setOnAction((event) -> {
            MCServerCreator.getPrimaryStage().getScene().setRoot(MCServerCreator.getChooseVersionRoot());
            MCServerCreator.setServerType(ServerType.TUINITY);
            MCServerCreator.getVersionController().onShow();
        });
        paperButton.setOnAction((event) -> {
            MCServerCreator.getPrimaryStage().getScene().setRoot(MCServerCreator.getChooseVersionRoot());
            MCServerCreator.setServerType(ServerType.PAPER);
            MCServerCreator.getVersionController().onShow();
        });
        spigotButton.setOnAction((event) -> {
            MCServerCreator.getPrimaryStage().getScene().setRoot(MCServerCreator.getChooseVersionRoot());
            MCServerCreator.setServerType(ServerType.SPIGOT);
            MCServerCreator.getVersionController().onShow();
        });
    }
}
