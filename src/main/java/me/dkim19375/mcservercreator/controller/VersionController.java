package me.dkim19375.mcservercreator.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import me.dkim19375.mcservercreator.MCServerCreator;
import me.dkim19375.mcservercreator.util.ColorUtils;

import java.util.Collections;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class VersionController {
    @FXML
    private BorderPane outerBackground;
    @FXML
    private VBox background;
    @FXML
    private ListView<String> versionList;
    @FXML
    private Button submitButton;
    @FXML
    private Label mustSelectLabel;
    @FXML
    private Button backButton;

    @FXML
    private void initialize() {
        MCServerCreator.setVersionController(this);
        outerBackground.setBackground(ColorUtils.getBackground(20, 20, 20));
        background.setBackground(ColorUtils.getBackground(20, 20, 20));
        submitButton.setBackground(ColorUtils.getBackground(187, 134, 252));
        versionList.setBackground(ColorUtils.getBackground(40, 40, 40));
        backButton.setBackground(ColorUtils.getBackground(3, 218, 197));
        versionList.setScaleX(1.8);
        versionList.setScaleY(1.8);
        versionList.setScaleZ(1.8);
        mustSelectLabel.setVisible(false);
        backButton.setOnAction((event) -> MCServerCreator.getPrimaryStage().getScene().setRoot(MCServerCreator.getChooseTypeRoot()));
    }

    public void onShow() {
        final ObservableList<String> versions = FXCollections.observableArrayList();
        switch (MCServerCreator.getServerType()) {
            case PAPER:
            case SPIGOT:
                versions.add("1.8.8");
                versions.add("1.9.4");
                versions.add("1.10.2");
                versions.add("1.11.2");
                versions.add("1.12.2");
            case TUINITY:
                versions.add("1.13.2");
                versions.add("1.14.4");
                versions.add("1.15.2");
                versions.add("1.16.5");
        }
        Collections.reverse(versions);
        versionList.setItems(versions);
        versionList.setBackground(ColorUtils.getBackground(40, 40, 40));
        submitButton.setOnAction((event) -> {
            if (versionList.getSelectionModel().getSelectedItems().size() < 1) {
                final ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
                mustSelectLabel.setVisible(true);
                service.schedule(() -> mustSelectLabel.setVisible(false), 3, TimeUnit.SECONDS);
                return;
            }
            MCServerCreator.setVersion(versionList.getSelectionModel().getSelectedItem());
            MCServerCreator.getPrimaryStage().getScene().setRoot(MCServerCreator.getChooseDirectoryRoot());
        });
    }
}
