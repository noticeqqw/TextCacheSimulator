module nstu.rgz {
    requires transitive javafx.controls;
    requires transitive javafx.graphics;
    requires javafx.fxml;
    
    opens nstu.rgz to javafx.fxml;
    exports nstu.rgz;
    exports nstu.rgz.cache;
    exports nstu.rgz.util;
}
