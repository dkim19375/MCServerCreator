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
import java.util.concurrent.Executors;

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
    private Button nextButton;
    private File directoryFile = null;
    private boolean done = false;

    @FXML
    private void initialize() {
        MCServerCreator.getInstance().setDirectoryController(this);
        outerBackground.setBackground(ColorUtils.getBackground(20, 20, 20));
        background.setBackground(ColorUtils.getBackground(20, 20, 20));
        changeButton.setBackground(ColorUtils.getBackground(187, 134, 252));
        backButton.setBackground(ColorUtils.getBackground(3, 218, 197));
        nextButton.setBackground(ColorUtils.getBackground(187, 134, 252));
        changeButton.setOnAction((event) -> {
            final DirectoryChooser directoryChooser = new DirectoryChooser();
            directoryChooser.setTitle("Server Directory");
            directoryFile = directoryChooser.showDialog(MCServerCreator.getInstance().getPrimaryStage());
            if (directoryFile == null) {
                return;
            }
            directory.setText(directoryFile.getAbsolutePath());
        });
        backButton.setOnAction((event) -> {
            done = false;
            MCServerCreator.getInstance().getPrimaryStage().getScene()
                    .setRoot(MCServerCreator.getInstance().getChooseVersionRoot());
        });
        nextButton.setOnAction((event) -> {
            if (done) {
                return;
            }
            done = true;
            if (directoryFile != null) {
                MCServerCreator.getInstance().getPrimaryStage().getScene()
                        .setRoot(MCServerCreator.getInstance().getInstallerRoot());
                Executors.newSingleThreadExecutor().submit(() -> MCServerCreator.getInstance().getInstallerController().onShow());
            }
        });
    }

    public File getDirectory() {
        return directoryFile;
    }
}
