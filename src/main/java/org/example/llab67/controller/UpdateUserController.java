package org.example.llab67.controller;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.paint.Color;
import org.example.llab67.domain.Utilizator;
import org.example.llab67.exceptions.ValidationException;
import org.example.llab67.service.Service;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.util.Objects;

public class UpdateUserController {
    @FXML
    private TextField textFieldFirstName;
    @FXML
    private TextField textFieldLastName;
    @FXML
    private TextField textFieldEmail;
    @FXML
    private TextField textFieldPassword;

    @FXML
    private ProgressBar progressBar;

    @FXML
    private Label emailValidationLabel;

    @FXML
    private Label passwordStrengthLabel;

    private Service service;
    Stage dialogStage;
    Utilizator utilizator;

    @FXML
    private void initialize() {
        textFieldEmail.textProperty().addListener((observable, oldValue, newValue) -> {
            if (isValidEmail(newValue)) {
                if (service.checkIfEmailExists(newValue) && !Objects.equals(utilizator.getEmail(), newValue)) {
                    emailValidationLabel.setText("Email already\nexists");
                    emailValidationLabel.setStyle("-fx-text-fill: red;");
                } else {
                    emailValidationLabel.setText("Email valid");
                    emailValidationLabel.setStyle("-fx-text-fill: green;");
                }
            } else {
                emailValidationLabel.setText("Email invalid");
                emailValidationLabel.setStyle("-fx-text-fill: red;");
            }
        });

        textFieldPassword.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                passwordStrengthLabel.setVisible(!newValue.isEmpty());
                updatePasswordStrengthBar(newValue);
            }
        });
    }
    private void updatePasswordStrengthBar(String password) {
        double progress = Math.min(password.length() / 5.0, 1.0);
        progressBar.setProgress(progress);

        if (password.length() >= 5) {
            progressBar.setStyle("-fx-accent: green;");
            passwordStrengthLabel.setText("Strong");
            passwordStrengthLabel.setTextFill(Color.GREEN);

        } else {
            progressBar.setStyle("-fx-accent: red;");
            passwordStrengthLabel.setText("Not strong");
            passwordStrengthLabel.setTextFill(Color.RED);
        }
    }

    public void setService(Service service,  Stage stage, Utilizator u) {
        this.service = service;
        this.dialogStage=stage;
        this.utilizator =u;
        setFields();
    }

    private void setFields(){
        textFieldFirstName.setText(utilizator.getFirstName());
        textFieldLastName.setText(utilizator.getLastName());
        textFieldEmail.setText(utilizator.getEmail());
        textFieldPassword.setText(utilizator.getPassword());
    }

    @FXML
    public void handleUpdate(){
        String firstNameText= textFieldFirstName.getText();
        String lastNameText= textFieldLastName.getText();
        String ema=textFieldEmail.getText();
        String pass=textFieldPassword.getText();

        Utilizator utilizator1=new Utilizator(firstNameText,lastNameText, ema, pass);

        if (null != this.utilizator) {
            utilizator1.setId(this.utilizator.getId());
        }

        try {
            service.updateUtilizator(utilizator1);
            MessageAlert.showMessage(null, Alert.AlertType.INFORMATION, "Salvare cu succes", "Utilizatorul a fost modificat cu succes!");
            dialogStage.close();
        } catch (ValidationException e) {
            MessageAlert.showErrorMessage(null, e.getMessage());
        }
    }


    private boolean isValidEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9._%+-]+@(yahoo\\.com|gmail\\.com)$";
        return email.matches(emailRegex);
    }
}


