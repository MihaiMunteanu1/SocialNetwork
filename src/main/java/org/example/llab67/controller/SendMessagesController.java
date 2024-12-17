package org.example.llab67.controller;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import org.example.llab67.domain.Message;
import org.example.llab67.domain.Utilizator;
import org.example.llab67.service.Service;

import java.time.format.DateTimeFormatter;


public class SendMessagesController {

    ObservableList<Message> messagesModel = FXCollections.observableArrayList();

    @FXML
    private ListView<Message> listMessages;

    @FXML
    private ListView<Utilizator> listFriends;

    @FXML
    private TextArea message;

    private Service service;
    private Utilizator user;

    public void setService(Service service, Utilizator user) {
        this.service = service;
        this.user = user;
        loadFriends();
    }

    @FXML
    public void initialize() {
        listFriends.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                loadListMessages(user.getId(), newValue.getId());
            }
        });
   }

    @FXML
    public void handleSendMessage() {
        Utilizator selectedFriend = listFriends.getSelectionModel().getSelectedItem();
        if (selectedFriend != null) {
            Long idFrom = user.getId();
            Long idTo = selectedFriend.getId();

            String msg = message.getText();

            service.addMessage(idFrom, idTo, msg);
            loadListMessages(idFrom, idTo);

            message.clear();
            listFriends.getSelectionModel().select(selectedFriend);
        } else {
            MessageAlert.showErrorMessage(null, "You must select a friend to send a message!");
        }
    }

    public void loadListMessages(Long id1, Long id2) {
        listMessages.getItems().clear();
        messagesModel.clear();
        messagesModel.addAll(service.getMessages(id1, id2));

        listMessages.setItems(messagesModel);
        listMessages.setCellFactory(param -> new ListCell<Message>() {
            @Override
            protected void updateItem(Message msg, boolean empty) {
                super.updateItem(msg, empty);
                if (empty || msg == null) {
                    setText(null);
                } else {
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
                    String formattedDate = msg.getData().format(formatter);
                    if (msg.getFrom().getId().equals(user.getId())) {
                        setText("You:  " + msg.getMessage() + "   at " + formattedDate);
                    } else {
                        setText(msg.getFrom().getFirstName() + ":  " + msg.getMessage() + "   at " + formattedDate);
                    }
                }
            }
        });
    }

    private void loadFriends() {
        ObservableList<Utilizator> friends = FXCollections.observableArrayList(service.getPrieteniiUnuiUtilizator(user.getId()));
        listFriends.setItems(friends);
        listFriends.setCellFactory(param -> new ListCell<Utilizator>() {
            @Override
            protected void updateItem(Utilizator friend, boolean empty) {
                super.updateItem(friend, empty);
                if (empty || friend == null) {
                    setText(null);
                } else {
                    setText(friend.getFirstName() + " " + friend.getLastName() + " " + friend.getEmail());
                }
            }
        });
    }


}