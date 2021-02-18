package me.dkim19375.mcservercreator;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import me.dkim19375.mcservercreator.controller.ServerTypeController;
import me.dkim19375.mcservercreator.controller.VersionController;
import me.dkim19375.mcservercreator.util.ServerType;

import java.util.Objects;

public class MCServerCreator extends Application {
    private static Stage primaryStage = null;
    private static Parent chooseTypeRoot = null;
    private static Parent chooseVersionRoot = null;
    private static Parent chooseDirectoryRoot = null;
    private static ServerType serverType = null;
    private static String version = null;
    private static ServerTypeController serverTypeController = null;
    private static VersionController versionController = null;

    protected static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        MCServerCreator.primaryStage = primaryStage;
        chooseTypeRoot = FXMLLoader.load(Objects.requireNonNull(getClass().getClassLoader()
                .getResource("1_chooseType.fxml")));
        chooseVersionRoot = FXMLLoader.load(Objects.requireNonNull(getClass().getClassLoader()
                .getResource("2_chooseVersion.fxml")));
        chooseDirectoryRoot = FXMLLoader.load(Objects.requireNonNull(getClass().getClassLoader()
                .getResource("3_chooseDirectory.fxml")));
        primaryStage.setTitle("MC Server Creator");
        primaryStage.setScene(new Scene(chooseTypeRoot, 1280, 720));
        primaryStage.setMaximized(true);
        primaryStage.show();
    }

    public static Stage getPrimaryStage() {
        return primaryStage;
    }

    public static Parent getChooseTypeRoot() {
        return chooseTypeRoot;
    }

    public static Parent getChooseVersionRoot() {
        return chooseVersionRoot;
    }

    public static ServerTypeController getServerTypeController() {
        return serverTypeController;
    }

    public static void setServerTypeController(ServerTypeController controller) {
        MCServerCreator.serverTypeController = controller;
    }

    public static VersionController getVersionController() {
        return versionController;
    }

    public static void setVersionController(VersionController versionController) {
        MCServerCreator.versionController = versionController;
    }

    public static ServerType getServerType() {
        return serverType;
    }

    public static void setServerType(ServerType serverType) {
        MCServerCreator.serverType = serverType;
    }

    public static String getVersion() {
        return version;
    }

    public static void setVersion(String version) {
        MCServerCreator.version = version;
    }

    public static Parent getChooseDirectoryRoot() {
        return chooseDirectoryRoot;
    }
}