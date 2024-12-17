package org.example.llab67;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.example.llab67.repository.paging.FriendRequestPagingRepository;
import org.example.llab67.repository.paging.PagingRepository;
import org.example.llab67.service.config.Config;
import org.example.llab67.controller.LoginController;
import org.example.llab67.domain.Prietenie;
import org.example.llab67.domain.Utilizator;
import org.example.llab67.domain.validators.PrietenieValidator;
import org.example.llab67.domain.validators.UtilizatorValidator;
import org.example.llab67.repository.Repository;
import org.example.llab67.repository.database.MessagesDbRepository;
import org.example.llab67.repository.database.PrietenieDbRepository;
import org.example.llab67.repository.database.UtilizatorDbRepository;
import org.example.llab67.service.Service;
import org.example.llab67.service.SocialCommunities;

import java.io.IOException;

public class HelloApplication extends Application {
    Repository<Long, Utilizator> userRepository;
    Service socialNetwork;
    SocialCommunities socialCommunities;

    @Override
    public void start(Stage primaryStage) throws IOException {
        String url = Config.getProperties().getProperty("db.url");
        String username = Config.getProperties().getProperty("db.username");
        String password = Config.getProperties().getProperty("db.password");

        UtilizatorDbRepository repoUser =
                new UtilizatorDbRepository(url, username, password, new UtilizatorValidator());

        FriendRequestPagingRepository<Long, Prietenie> repoFriendship =
                new PrietenieDbRepository(url, username, password, new PrietenieValidator());

        MessagesDbRepository messagesRepository = new MessagesDbRepository(url, username, password, repoUser);

        socialNetwork = new Service(repoUser, repoFriendship, messagesRepository);
        socialCommunities = new SocialCommunities(socialNetwork);

        initView(primaryStage);
        primaryStage.setWidth(800);
        primaryStage.show();

        Iterable<Utilizator> users = socialNetwork.getUtilizatori();
        users.forEach(System.out::println);
        Iterable<Prietenie> friendships = socialNetwork.getPrietenii();
        friendships.forEach(System.out::println);
    }
    private void initView(Stage primaryStage) throws IOException {

        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("views/login-window.fxml"));

        AnchorPane userLayout = fxmlLoader.load();
        primaryStage.setScene(new Scene(userLayout));

        LoginController loginController = fxmlLoader.getController();
        loginController.setService(socialNetwork);

    }

    public static void main(String[] args) {
        launch();
    }
}