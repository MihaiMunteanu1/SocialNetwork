package org.example.llab67.controller;
import javafx.animation.KeyFrame;
import javafx.animation.PauseTransition;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.scene.control.*;
import javafx.util.Duration;
import org.example.llab67.domain.Prietenie;
import org.example.llab67.domain.Utilizator;
import org.example.llab67.service.Service;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.List;

public class LoginController {

    private Service service;

    @FXML
    private ToggleButton showPasswordButton;

    @FXML
    private TextField textFieldEmail;

    @FXML
    private PasswordField textFieldPassword;

    @FXML
    private ProgressBar progressBar;

    @FXML
    private TextField textFieldPasswordVisible;

    Stage dialogStage;


    @FXML
    private void initialize() {
        textFieldPasswordVisible.setManaged(false);
        textFieldPasswordVisible.setVisible(false);
        textFieldPasswordVisible.textProperty().bindBidirectional(textFieldPassword.textProperty());

    }
    public void setService(Service service) {
        this.service = service;
    }

    @FXML
    public void handleLogin() {
        String email = textFieldEmail.getText();
        String password = textFieldPassword.getText();

        progressBar.setVisible(true);

        Timeline timeline = new Timeline(
                new KeyFrame(Duration.ZERO, e -> progressBar.setProgress(0)),
                new KeyFrame(Duration.seconds(0.2), e -> progressBar.setProgress(0.2)),
                new KeyFrame(Duration.seconds(0.4), e -> progressBar.setProgress(0.4)),
                new KeyFrame(Duration.seconds(0.8), e -> progressBar.setProgress(0.8)),
                new KeyFrame(Duration.seconds(1.2), e -> progressBar.setProgress(1.2)),
                new KeyFrame(Duration.seconds(1.4), e -> progressBar.setProgress(1.4)),
                new KeyFrame(Duration.seconds(1.8), e -> progressBar.setProgress(1.8)),
                new KeyFrame(Duration.seconds(2), e -> progressBar.setProgress(2))
        );
        timeline.setCycleCount(1);

        PauseTransition pause = new PauseTransition(Duration.seconds(2));
        pause.setOnFinished(event -> {
            Utilizator utilizator = service.findOneByMailAndPassword(email, password);
            if (utilizator != null) {
                Stage dialogStage = (Stage) textFieldEmail.getScene().getWindow();
                dialogStage.close();
                try {
                    FXMLLoader loader = new FXMLLoader();
                    loader.setLocation(getClass().getResource("/org/example/llab67/views/user-window.fxml"));
                    AnchorPane root = loader.load();
                    Stage stage = new Stage();
                    stage.initModality(Modality.WINDOW_MODAL);
                    Scene scene = new Scene(root);
                    stage.setScene(scene);
                    stage.show();
                    List<Prietenie> pendingRequests = service.getPendingFriendRequests(utilizator.getId());
                    if (!pendingRequests.isEmpty()) {
                        Platform.runLater(() -> MessageAlert.showMessage(null, Alert.AlertType.INFORMATION, "Friend requests", "You have pending friend requests!"));
                    }
                    UserController userController = loader.getController();
                    userController.setUtilizatorService(service, utilizator, stage);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                Platform.runLater(() -> MessageAlert.showErrorMessage(null, "Utilizatorul nu exista!"));
                progressBar.setProgress(0);
            }
        });

        timeline.play();
        pause.play();
    }

    @FXML
    public void handleShowPasswordButton(){
        if (showPasswordButton.isSelected()) {
            textFieldPasswordVisible.setManaged(true);
            textFieldPasswordVisible.setVisible(true);
            textFieldPassword.setManaged(false);
            textFieldPassword.setVisible(false);
            textFieldPasswordVisible.setText(textFieldPassword.getText());
        } else {
            textFieldPasswordVisible.setManaged(false);
            textFieldPasswordVisible.setVisible(false);
            textFieldPassword.setManaged(true);
            textFieldPassword.setVisible(true);
            textFieldPassword.setText(textFieldPasswordVisible.getText());
        }
    }

    @FXML
    public void handleSignup(){
        showSignUpDialog(null);
    }

    private void showSignUpDialog(Utilizator user) {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("../views/sign-up-window.fxml"));

            AnchorPane root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Utilizatori");
            stage.initModality(Modality.WINDOW_MODAL);
            Scene scene = new Scene(root);
            stage.setScene(scene);
            SignUpController signUpController = loader.getController();
            signUpController.setService(service, stage, user);
            stage.show();


        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    @FXML
    public void handleCancel(){
        if (dialogStage != null) {
            dialogStage.close();
        }
    }
}
