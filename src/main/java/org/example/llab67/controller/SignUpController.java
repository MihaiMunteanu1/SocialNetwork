package org.example.llab67.controller;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import org.example.llab67.domain.Utilizator;
import org.example.llab67.exceptions.ServiceException;
import org.example.llab67.exceptions.ValidationException;
import org.example.llab67.service.Service;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.stage.Stage;


public class SignUpController {
    private Service service;

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


    Stage dialogStage;

    @FXML
    private void initialize() {
        textFieldEmail.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {

                if (isValidEmail(newValue)) {
                    if (service.checkIfEmailExists(newValue)) {
                        emailValidationLabel.setText("Email already exists");
                        emailValidationLabel.setStyle("-fx-text-fill: red;");
                    }
                    else{
                        emailValidationLabel.setText("Email valid");
                        emailValidationLabel.setStyle("-fx-text-fill: green;");
                    }
                } else {
                    emailValidationLabel.setText("Email invalid");
                    emailValidationLabel.setStyle("-fx-text-fill: red;");
                }

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

    private boolean isValidEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9._%+-]+@(yahoo\\.com|gmail\\.com)$";
        return email.matches(emailRegex);
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


    public void setService(Service service, Stage stage, Utilizator user) {
        this.service = service;
        this.dialogStage = stage;

    }

    @FXML
    public void handleSignUp(){
        String firstName = textFieldFirstName.getText();
        String lastName = textFieldLastName.getText();
        String email = textFieldEmail.getText();
        String password = textFieldPassword.getText();

        Utilizator user = new Utilizator(firstName, lastName, email, password);
        try {
            service.addUtilizator(user);
            dialogStage.close();
            MessageAlert.showMessage(null, Alert.AlertType.INFORMATION, "Sign up", "User added successfully!");
        } catch (ValidationException | ServiceException e) {
            MessageAlert.showErrorMessage(null, e.getMessage());
        }

    }



}
