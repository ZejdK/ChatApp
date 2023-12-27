module ba.unsa.etf.rpr.chatapp {
    requires javafx.controls;
    requires javafx.fxml;
    requires bcrypt;
    requires com.fasterxml.jackson.databind;
    requires java.sql;


    opens ba.unsa.etf.rpr.chatapp to javafx.fxml;
    exports ba.unsa.etf.rpr.chatapp.business;
    exports ba.unsa.etf.rpr.chatapp;
    exports ba.unsa.etf.rpr;
    opens ba.unsa.etf.rpr to javafx.fxml;
}