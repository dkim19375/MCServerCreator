package me.dkim19375.mcservercreator;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import me.dkim19375.mcservercreator.controller.DirectoryController;
import me.dkim19375.mcservercreator.controller.InstallerController;
import me.dkim19375.mcservercreator.controller.ServerTypeController;
import me.dkim19375.mcservercreator.controller.VersionController;
import me.dkim19375.mcservercreator.util.ServerType;
import me.dkim19375.mcservercreator.util.ServerVersion;

import java.util.Objects;

public class MCServerCreator extends Application {
    private static MCServerCreator instance = null;
    private Stage primaryStage = null;
    private Parent chooseTypeRoot = null;
    private Parent chooseVersionRoot = null;
    private Parent chooseDirectoryRoot = null;
    private Parent installerRoot = null;
    private ServerType serverType = null;
    private ServerVersion version = null;
    private ServerTypeController serverTypeController = null;
    private VersionController versionController = null;
    private DirectoryController directoryController = null;
    private InstallerController installerController = null;

    protected static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        instance = this;
        this.primaryStage = primaryStage;
        primaryStage.getIcons().add(new Image(Objects.requireNonNull(getClass().getClassLoader().getResourceAsStream("icon.png"))));
        chooseTypeRoot = FXMLLoader.load(Objects.requireNonNull(getClass().getClassLoader()
                .getResource("1_chooseType.fxml")));
        chooseVersionRoot = FXMLLoader.load(Objects.requireNonNull(getClass().getClassLoader()
                .getResource("2_chooseVersion.fxml")));
        chooseDirectoryRoot = FXMLLoader.load(Objects.requireNonNull(getClass().getClassLoader()
                .getResource("3_chooseDirectory.fxml")));
        installerRoot = FXMLLoader.load(Objects.requireNonNull(getClass().getClassLoader()
                .getResource("4_installer.fxml")));
        primaryStage.setOnCloseRequest(event -> {
            if (event.getEventType() != WindowEvent.WINDOW_CLOSE_REQUEST) {
                return;
            }
            primaryStage.close();
            System.exit(0);
        });
        primaryStage.setTitle("MC Server Creator");
        primaryStage.setScene(new Scene(chooseTypeRoot, 1280, 720));
        primaryStage.setMaximized(true);
        primaryStage.show();
    }

    public Stage getPrimaryStage() {
        return primaryStage;
    }

    public Parent getChooseTypeRoot() {
        return chooseTypeRoot;
    }

    public Parent getChooseVersionRoot() {
        return chooseVersionRoot;
    }

    public ServerTypeController getServerTypeController() {
        return serverTypeController;
    }

    public void setServerTypeController(ServerTypeController controller) {
        this.serverTypeController = controller;
    }

    public VersionController getVersionController() {
        return versionController;
    }

    public void setVersionController(VersionController versionController) {
        this.versionController = versionController;
    }

    public ServerType getServerType() {
        return serverType;
    }

    public void setServerType(ServerType serverType) {
        this.serverType = serverType;
    }

    public ServerVersion getVersion() {
        return version;
    }

    public void setVersion(ServerVersion version) {
        this.version = version;
    }

    public Parent getChooseDirectoryRoot() {
        return chooseDirectoryRoot;
    }

    public DirectoryController getDirectoryController() {
        return directoryController;
    }

    public void setDirectoryController(DirectoryController directoryController) {
        this.directoryController = directoryController;
    }

    public InstallerController getInstallerController() {
        return installerController;
    }

    public void setInstallerController(InstallerController installerController) {
        this.installerController = installerController;
    }

    public Parent getInstallerRoot() {
        return installerRoot;
    }

    public static MCServerCreator getInstance() {
        return instance;
    }
}