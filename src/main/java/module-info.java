module ba.unsa.etf.rpr.chatapp {
    requires javafx.controls;
    requires javafx.fxml;
    requires bcrypt;


    opens ba.unsa.etf.rpr.chatapp to javafx.fxml;
    exports ba.unsa.etf.rpr.chatapp;
}