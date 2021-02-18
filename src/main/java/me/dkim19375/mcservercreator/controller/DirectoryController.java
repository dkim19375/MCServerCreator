package me.dkim19375.mcservercreator.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import me.dkim19375.mcservercreator.MCServerCreator;
import me.dkim19375.mcservercreator.util.ColorUtils;

import java.io.File;

public class DirectoryController {
    @FXML
    private BorderPane outerBackground;
    @FXML
    private VBox background;
    @FXML
    private Button changeButton;
    @FXML
    private Label directory;
    @FXML
    private Button backButton;

    @FXML
    private void initialize() {
        outerBackground.setBackground(ColorUtils.getBackground(20, 20, 20));
        background.setBackground(ColorUtils.getBackground(20, 20, 20));
        changeButton.setBackground(ColorUtils.getBackground(187, 134, 252));
        backButton.setBackground(ColorUtils.getBackground(3, 218, 197));
        changeButton.setOnAction((event) -> {
            final DirectoryChooser directoryChooser = new DirectoryChooser();
            directoryChooser.setTitle("Server Directory");
            final File selectedDirectory = directoryChooser.showDialog(MCServerCreator.getPrimaryStage());
            if (selectedDirectory == null) {
                return;
            }
            directory.setText(selectedDirectory.getAbsolutePath());
        });
        backButton.setOnAction((event) -> MCServerCreator.getPrimaryStage().getScene().setRoot(MCServerCreator.getChooseVersionRoot()));
    }
}