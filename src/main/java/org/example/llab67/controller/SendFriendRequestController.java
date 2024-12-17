package org.example.llab67.controller;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import org.example.llab67.domain.Prietenie;
import org.example.llab67.domain.Utilizator;
import org.example.llab67.service.Service;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;


public class SendFriendRequestController {
    Service service;
    Utilizator user;

    ObservableList<Utilizator> model = FXCollections.observableArrayList();

    @FXML
    TableView<Utilizator> tableView;
    @FXML
    private TextField textFieldFirstName;
    @FXML
    private TextField textFieldLastName;
    @FXML
    TableColumn<Utilizator,String> tableColumnFirstName;
    @FXML
    TableColumn<Utilizator,String> tableColumnLastName;
    @FXML
    TableColumn<Utilizator,String> tableColumnEmail;

    Stage dialogStage;

    public void setService(Service service,Stage stage , Utilizator utilizator) {
        this.service = service;
        this.user=utilizator;
        this.dialogStage=stage;
        //initModel();
    }

    @FXML
    public void initialize(){
        tableColumnFirstName.setCellValueFactory(new PropertyValueFactory<Utilizator, String>("firstName"));
        tableColumnLastName.setCellValueFactory(new PropertyValueFactory<Utilizator, String>("lastName"));
        tableColumnEmail.setCellValueFactory(new PropertyValueFactory<Utilizator, String>("email"));
        tableView.setItems(model);
    }

    private void initModel(String firstName, String lastName) {
        Iterable<Utilizator> messages = service.findOneByName(firstName, lastName);
        List<Utilizator> users = StreamSupport.stream(messages.spliterator(), false)
                .collect(Collectors.toList());
        model.setAll(users);
    }

    private void initModelOnlyWithFirstName(String firstName) {
        Iterable<Utilizator> messages = service.findByFirstName(firstName);
        List<Utilizator> users = StreamSupport.stream(messages.spliterator(), false)
                .collect(Collectors.toList());
        model.setAll(users);
    }

    private void initModelOnlyWithLastName(String lastName) {
        Iterable<Utilizator> messages = service.findByLastName(lastName);
        List<Utilizator> users = StreamSupport.stream(messages.spliterator(), false)
                .collect(Collectors.toList());
        model.setAll(users);
    }

    @FXML
    public void handleCauta(){
        if(textFieldFirstName.getText().isEmpty() && textFieldLastName.getText().isEmpty()){
            MessageAlert.showErrorMessage(null, "Nu ati introdus numele si prenumele");
        }
        if(!textFieldFirstName.getText().isEmpty() && textFieldLastName.getText().isEmpty()){
            initModelOnlyWithFirstName(textFieldFirstName.getText());
        }
        else if(textFieldFirstName.getText().isEmpty() && !textFieldLastName.getText().isEmpty()){
            initModelOnlyWithLastName(textFieldLastName.getText());
        }
        else {
            initModel(textFieldFirstName.getText(), textFieldLastName.getText());
        }
    }

    @FXML
    public void handleSendFriendRequest(){
        Utilizator utilizator = tableView.getSelectionModel().getSelectedItem();
        if(utilizator == null){
            MessageAlert.showErrorMessage(null, "Nu ati selectat niciun utilizator");
        }
        else{
            Prietenie p = new Prietenie(user.getId(), utilizator.getId(), LocalDateTime.now());
            //service.sendFriendRequest(user.getId(), utilizator.getId());
            try {
                service.addPrietenie(p);
                dialogStage.close();
                MessageAlert.showMessage(null, Alert.AlertType.INFORMATION,"Info", "Cerere trimisa cu succes!");
            } catch (Exception e) {
                MessageAlert.showErrorMessage(null, e.getMessage());
            }
        }
    }
}
