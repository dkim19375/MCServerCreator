module MCServerCreator.main {
    requires javafx.base;
    requires javafx.controls;
    requires javafx.graphics;
    requires javafx.fxml;
    requires org.apache.commons.io;

    opens me.dkim19375.mcservercreator to javafx.fxml;
    opens me.dkim19375.mcservercreator.util to javafx.fxml;
    opens me.dkim19375.mcservercreator.controller to javafx.fxml;

    exports me.dkim19375.mcservercreator;
    exports me.dkim19375.mcservercreator.util;
    exports me.dkim19375.mcservercreator.controller;
}