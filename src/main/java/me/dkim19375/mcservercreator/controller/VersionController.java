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
import me.dkim19375.mcservercreator.util.ServerVersion;

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
    private boolean done = false;

    @FXML
    private void initialize() {
        MCServerCreator.getInstance().setVersionController(this);
        outerBackground.setBackground(ColorUtils.getBackground(20, 20, 20));
        background.setBackground(ColorUtils.getBackground(20, 20, 20));
        submitButton.setBackground(ColorUtils.getBackground(187, 134, 252));
        versionList.setBackground(ColorUtils.getBackground(40, 40, 40));
        backButton.setBackground(ColorUtils.getBackground(3, 218, 197));
        versionList.setScaleX(1.8);
        versionList.setScaleY(1.8);
        versionList.setScaleZ(1.8);
        mustSelectLabel.setVisible(false);
        backButton.setOnAction((event) -> {
            done = false;
            MCServerCreator.getInstance().getPrimaryStage().getScene()
                    .setRoot(MCServerCreator.getInstance().getChooseTypeRoot());
        });
    }

    public void onShow() {
        final ObservableList<String> versions = FXCollections.observableArrayList();
        switch (MCServerCreator.getInstance().getServerType()) {
            case PAPER:
            case SPIGOT:
                versions.add(ServerVersion.v1_8.getVersion());
                versions.add(ServerVersion.v1_9.getVersion());
                versions.add(ServerVersion.v1_10.getVersion());
                versions.add(ServerVersion.v1_11.getVersion());
                versions.add(ServerVersion.v1_12.getVersion());
                versions.add(ServerVersion.v1_13.getVersion());
                versions.add(ServerVersion.v1_14.getVersion());
            case TUINITY:
                versions.add(ServerVersion.v1_15.getVersion());
                versions.add(ServerVersion.v1_16.getVersion());
        }
        Collections.reverse(versions);
        versionList.setItems(versions);
        versionList.setBackground(ColorUtils.getBackground(40, 40, 40));
        submitButton.setOnAction((event) -> {
            if (done) {
                return;
            }
            done = true;
            if (versionList.getSelectionModel().getSelectedItems().size() < 1) {
                final ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
                mustSelectLabel.setVisible(true);
                service.schedule(() -> mustSelectLabel.setVisible(false), 3, TimeUnit.SECONDS);
                return;
            }
            MCServerCreator.getInstance().setVersion(ServerVersion.fromString(versionList.getSelectionModel().getSelectedItem()));
            MCServerCreator.getInstance().getPrimaryStage().getScene().setRoot(MCServerCreator.getInstance().getChooseDirectoryRoot());
        });
    }
}
