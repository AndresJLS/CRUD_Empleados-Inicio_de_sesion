package com.example.crudjavafx;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.IOException;

public class LoginController {

    @FXML
    private TextField usuarioText;

    @FXML
    private PasswordField passwordText;

    @FXML
    private TextField urlText;

    private Stage primaryStage;

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    @FXML
    protected void ingresar() {

        String usuario = usuarioText.getText();
        String password = passwordText.getText();
        String url = urlText.getText();

        // Si no se escribe usuario, muestra error
        if (usuario.isEmpty()) {
            mostrarAlerta(
                    "Ingrese un usuario por favor.",
                    "ERROR",
                    Alert.AlertType.WARNING
            );
            usuarioText.requestFocus();
            return;
        }

        // La contraseña puede estar vacía porque MariaDB no tiene contraseña
        // Por eso eliminamos la validación de password.isEmpty()

        // Si no se escribe URL, muestra error
        if (url.isEmpty()) {
            mostrarAlerta(
                    "Ingrese una URL correcta.",
                    "ERROR",
                    Alert.AlertType.WARNING
            );
            urlText.requestFocus();
            return;
        }

        try {

            // Intentar conexión con la base de datos
            ManejadorEmpleadoDB test =
                    new ManejadorEmpleadoDB(url, usuario, password);

            if (!test.probarConexion()) {

                mostrarAlerta(
                        "No se pudo conectar a la base de datos.\n" +
                        "Verifica la URL, usuario y datos de conexión.",
                        "ERROR de conexión",
                        Alert.AlertType.ERROR
                );

                return;
            }

            // Cargar ventana principal del CRUD
            FXMLLoader fxmlLoader =
                    new FXMLLoader(
                            LoginApplication.class.getResource(
                                    "CRUDEmpleados-view.fxml"
                            )
                    );

            Scene mainScene =
                    new Scene(fxmlLoader.load(), 600, 600);

            CRUDEmpleadosController mainController =
                    fxmlLoader.getController();

            mainController.initData(
                    url,
                    usuario,
                    password
            );

            // Crear la ventana principal del CRUD
            Stage mainStage = new Stage();

            mainStage.setTitle(
                    "Aplicacion CRUD Empleados JavaFX con JDBC"
            );

            mainStage.getIcons().add(
                    new Image(
                            getClass()
                                    .getResourceAsStream("icono.png")
                    )
            );

            mainStage.setScene(mainScene);

            // Cuando se cierre el CRUD, mostrar nuevamente el Login
            mainStage.setOnCloseRequest(
                    (WindowEvent event) -> {
                        primaryStage.show();
                    }
            );

            mainStage.setX(primaryStage.getX());
            mainStage.setY(primaryStage.getY());

            mainStage.show();

            // Ocultar Login
            primaryStage.hide();

        } catch (IOException e) {

            e.printStackTrace();

        }
    }

    private void mostrarAlerta(
            String mensaje,
            String titulo,
            Alert.AlertType tipo
    ) {

        Alert alerta = new Alert(tipo);

        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);

        // Obtener la ventana principal para centrar la alerta
        Stage stage = (Stage) primaryStage;

        if (stage != null) {
            alerta.initOwner(stage);
        }

        alerta.showAndWait();
    }
}