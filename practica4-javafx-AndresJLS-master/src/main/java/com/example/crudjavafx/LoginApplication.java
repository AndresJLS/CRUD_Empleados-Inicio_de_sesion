package com.example.crudjavafx;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;

public class LoginApplication extends Application {
    @Override
    public void start(Stage primaryStage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(LoginApplication.class.getResource("login-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 320, 240);
        primaryStage.setTitle("Login");
        primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("icono.png")));
        primaryStage.setScene(scene);
        primaryStage.show();

        // Controlador del login 1
        LoginController controller = fxmlLoader.getController();
        controller.setPrimaryStage(primaryStage);
    }

    public static void main(String[] args) {launch();

    }
}