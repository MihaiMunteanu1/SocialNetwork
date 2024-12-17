package org.example.llab67.controller;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.example.llab67.domain.Prietenie;
import org.example.llab67.domain.Utilizator;
import org.example.llab67.exceptions.ServiceException;
import org.example.llab67.service.Service;
import org.example.llab67.utils.events.UtilizatorEntityChangeEvent;
import org.example.llab67.utils.observer.Observer;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.example.llab67.domain.FriendshipDTO;
import org.example.llab67.utils.paging.Pageable;


public class UserController implements Observer<UtilizatorEntityChangeEvent> {
    Service service;

    ObservableList<FriendshipDTO> modelFriends = FXCollections.observableArrayList();
    ObservableList<FriendshipDTO> modelFriendRequests = FXCollections.observableArrayList();

    Utilizator user;
    Stage dialogStage;

    private int currentPage=0;
    private static final int PAGE_SIZE = 5;



    @FXML
    Label labelCurrentPage;

    @FXML
    Button buttonNextPage;

    @FXML
    Button buttonPreviousPage;

    @FXML
    TableView<FriendshipDTO> tableViewFriendsRequests;

    @FXML
    TableView<FriendshipDTO> tableViewFriends;

    @FXML
    TableColumn<FriendshipDTO, String> tableColumnFirstName;
    @FXML
    TableColumn<FriendshipDTO, String> tableColumnLastName;
    @FXML
    TableColumn<FriendshipDTO, String> tableColumnEmail;
    @FXML
    TableColumn<FriendshipDTO, LocalDateTime> tableColumnSendDate;

    @FXML
    TableColumn<FriendshipDTO, String> tableColumnFirstNameFriend;
    @FXML
    TableColumn<FriendshipDTO, String> tableColumnLastNameFriend;
    @FXML
    TableColumn<FriendshipDTO, String> tableColumnEmailFriend;
    @FXML
    TableColumn<FriendshipDTO, LocalDateTime> tableColumnFriendsFrom;


    public void setUtilizatorService(Service service, Utilizator utilizator, Stage dialogStage) {
        this.service = service;
        this.user=utilizator;
        this.dialogStage=dialogStage;
        service.addObserver(this);
        initModelFriendRequests();
        initModelFriends();
        initializeTableViews();
    }



    private void initModelFriendRequests() {
        List<FriendshipDTO> friendRequests = service.getFriendRequests(user.getId());
        modelFriendRequests.setAll(friendRequests);
    }

//    private void initModelFriends() {
//        List<FriendshipDTO> friends = service.getPrieteniiUnuiUtilizatorDTO(user.getId());
//        modelFriends.setAll(friends);
//    }

    private void initModelFriends() {
        Pageable pageable = new Pageable(currentPage, PAGE_SIZE);
        List<FriendshipDTO> friends = (List<FriendshipDTO>) service.findAllUserFriends(pageable,user).getElementsOnPage();
        modelFriends.setAll(friends);
        updatePageLabel();
    }

    private void updatePageLabel() {
        labelCurrentPage.setText("Page: " + (currentPage + 1));
    }

    @FXML
    public void handleNextPage(ActionEvent actionEvent) {
        Pageable pageable = new Pageable(currentPage, PAGE_SIZE);
        int totalFriends = service.findAllUserFriends(pageable, user).getTotalElementCount();
        int maxFriendsOnCurrentPage = (currentPage + 1) * PAGE_SIZE;

        if (totalFriends > maxFriendsOnCurrentPage) {
            currentPage++;
            initModelFriends();
        } else {
            MessageAlert.showErrorMessage(null, "You are on the last page.");
        }
    }

    @FXML
    public void handlePreviousPage(ActionEvent actionEvent) {
        if (currentPage > 0) {
            currentPage--;
        }
        initModelFriends();
    }

    @Override
    public void update(UtilizatorEntityChangeEvent utilizatorEntityChangeEvent) {
        initModelFriends();
        initModelFriendRequests();
    }

    @FXML
    public void initialize() {

    }

    @FXML
    public void handleChat() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/org/example/llab67/views/send-messages-window.fxml"));
            SplitPane root = loader.load();
            Stage stage = new Stage();
            stage.initModality(Modality.WINDOW_MODAL);
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
            SendMessagesController sendMessagesController = loader.getController();
            sendMessagesController.setService(service, user);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void initializeTableViews() {
        tableColumnFirstName.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        tableColumnLastName.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        tableColumnEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        tableColumnSendDate.setCellValueFactory(new PropertyValueFactory<>("friendsFrom"));
        tableViewFriendsRequests.setItems(modelFriendRequests);

        tableColumnFirstNameFriend.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        tableColumnLastNameFriend.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        tableColumnEmailFriend.setCellValueFactory(new PropertyValueFactory<>("email"));
        tableColumnFriendsFrom.setCellValueFactory(new PropertyValueFactory<>("friendsFrom"));
        tableViewFriends.setItems(modelFriends);
    }
    @FXML
    public void handleAcceptFriendRequest() {
        FriendshipDTO friend = tableViewFriendsRequests.getSelectionModel().getSelectedItem();
        if (friend != null) {
            try {
                service.acceptFriendRequest(user.getId(), service.findOneUserByNameAndEmail(friend.getFirstName(), friend.getLastName(), friend.getEmail()).getId());
                update(null);
            } catch (ServiceException e) {
                MessageAlert.showErrorMessage(null, e.getMessage());
            }
        } else {
            MessageAlert.showErrorMessage(null, "You must select a friend request!");
        }
    }

    @FXML
    public void handleDeclineFriendRequest() {
        FriendshipDTO friend = tableViewFriendsRequests.getSelectionModel().getSelectedItem();
        if (friend != null) {
            try{
                service.declineFriendRequest(user.getId(), service.findOneUserByNameAndEmail(friend.getFirstName(), friend.getLastName(), friend.getEmail()).getId());
                update(null);
            } catch (ServiceException e) {
                MessageAlert.showErrorMessage(null, e.getMessage());
            }
        } else {
            MessageAlert.showErrorMessage(null, "You must select a friend request!");
        }
    }

    /// Cu TableView Prietenii
    @FXML
    public void handleDeleteFriend() {
        FriendshipDTO friend = tableViewFriends.getSelectionModel().getSelectedItem();
        System.out.print(friend+" ");
        if (friend != null) {
            Long idFriend = service.findOneUserByNameAndEmail(friend.getFirstName(), friend.getLastName(), friend.getEmail()).getId();
            System.out.println(idFriend);
            service.removePrietenie(user.getId(), idFriend);
            update(null);

        } else {
            MessageAlert.showErrorMessage(null, "You must select a friend!");
        }
    }

    @FXML
    public void handleAddFriend() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/org/example/llab67/views/send-friend-request-window.fxml"));
            AnchorPane root = loader.load();
            Stage stage = new Stage();
            stage.initModality(Modality.WINDOW_MODAL);
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
            SendFriendRequestController sendFriendRequestController = loader.getController();
            sendFriendRequestController.setService(service, stage, user);
       } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void handleDeleteUser(){
        service.removeUtilizator(user.getId());
        MessageAlert.showMessage(null, Alert.AlertType.CONFIRMATION, "Delete user", "Userul a fost sters");
        dialogStage.close();
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/org/example/llab67/views/login-window.fxml"));
            AnchorPane root = loader.load();
            Stage stage = new Stage();
            stage.initModality(Modality.WINDOW_MODAL);
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
            LoginController loginController = loader.getController();
            loginController.setService(service);
        } catch (IOException e) {
            e.printStackTrace();
        }
        update(null);
    }

    @FXML
    public void handleUpdateUser() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/org/example/llab67/views/user-update-window.fxml"));
            AnchorPane root = loader.load();
            Stage stage = new Stage();
            stage.initModality(Modality.WINDOW_MODAL);
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
            UpdateUserController updateUserController = loader.getController();
            updateUserController.setService(service, stage, user);
        } catch (IOException e) {
            e.printStackTrace();
        }
        update(null);
    }

    @FXML
    public void handleBackToLogin() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/org/example/llab67/views/login-window.fxml"));
            AnchorPane root = loader.load();
            Stage stage = new Stage();
            stage.initModality(Modality.WINDOW_MODAL);
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
            LoginController loginController = loader.getController();
            loginController.setService(service);
            if (dialogStage != null) {
                dialogStage.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
