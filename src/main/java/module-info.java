module ba.unsa.etf.rpr.chatapp {
    requires javafx.controls;
    requires javafx.fxml;
    requires bcrypt;
    requires com.fasterxml.jackson.databind;


    opens ba.unsa.etf.rpr.chatapp to javafx.fxml;
    exports ba.unsa.etf.rpr.chatapp.domain;
    exports ba.unsa.etf.rpr.chatapp.business;
    exports ba.unsa.etf.rpr.chatapp;
}