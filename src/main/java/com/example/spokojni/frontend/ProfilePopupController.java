package com.example.spokojni.frontend;

import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;

public class ProfilePopupController {
    ObservableList<String> languages = FXCollections.observableArrayList("Slovak","English");
    public ProfilePopupController() {

    }
    @FXML
    private ChoiceBox<String> language;

    @FXML
    private void initialize() {
        language.setValue("English");
        language.setItems(languages);
    }
}
