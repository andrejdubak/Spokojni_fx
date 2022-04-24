package com.example.spokojni.frontend;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.text.Text;

public class MainController {
    @FXML
    private Text actiontarget;

    @FXML
    private DatePicker datepicker;

    @FXML
    protected void handleDatePickerAction(ActionEvent event)
    {
        actiontarget.setText(datepicker.getValue().toString());
    }
}
