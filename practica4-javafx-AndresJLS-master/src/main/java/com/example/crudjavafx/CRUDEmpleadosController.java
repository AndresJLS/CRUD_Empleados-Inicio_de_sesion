package com.example.crudjavafx;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;

public class CRUDEmpleadosController {
    @FXML
    private TextField idText;
    @FXML
    private CheckBox csCheckbox;
    @FXML
    private TextField nombreText;
    @FXML
    private TextField puestoText;
    @FXML
    private TextField salarioText;

    @FXML
    private TableView<Empleado> tablaEmpleados;
    @FXML
    private TableColumn<Empleado, Integer> columnaId;
    @FXML
    private TableColumn<Empleado, String> columnaNombre;
    @FXML
    private TableColumn<Empleado, String> columnaPuesto;
    @FXML
    private TableColumn<Empleado, Double> columnaSalario;

    private ManejadorEmpleadoDB manejadorEmpleadosDB;
    private ObservableList<Empleado> listObservable;

    public void initData(String url, String user, String password) {
        manejadorEmpleadosDB = new ManejadorEmpleadoDB(url, user, password);
        //manejadorEmpleadosDB = new ManejadorEmpleadoDB("jdbc:mysql://localhost:3306/empresaDS3", "DS3", "ds3");
        listObservable = FXCollections.observableArrayList(manejadorEmpleadosDB.getEmpleadosPS());
        tablaEmpleados.setItems(listObservable);
        tablaEmpleados.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
            if (newSel != null) {
                cargarSeleccionado(newSel);
            }
        });

        limpiarForm();
    }

    @FXML
    public void initialize() {

        columnaId.setCellValueFactory(new PropertyValueFactory<>("id"));
        columnaNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        columnaPuesto.setText("Puesto"); //encabezado
        columnaPuesto.setCellValueFactory(new PropertyValueFactory<>("puesto")); //atributo al que mapea
        columnaSalario.setText("Salario");
        columnaSalario.setCellValueFactory(new PropertyValueFactory<>("salario"));
        tablaEmpleados.setItems(listObservable);

    }

    @FXML
    private void guardarEmpleado() {
        String nombre = nombreText.getText();
        String puesto = puestoText.getText();
        String salarioStr = salarioText.getText();

        if (nombre.isEmpty()) {
            mostrarAlerta("Debe ingresar un nombre", "Erro", Alert.AlertType.ERROR);
            nombreText.requestFocus();
            return;
        } else if (puesto.isEmpty()) {
            mostrarAlerta("Debe ingresar un puesto", "Erro", Alert.AlertType.ERROR);
            puestoText.requestFocus();
            return;
        }
        double salario;
        try {
            salario = Double.parseDouble(salarioStr);
        } catch (NumberFormatException e) {
            mostrarAlerta("El salario debe ser unn numero valido", "Erro", Alert.AlertType.ERROR);
            salarioText.requestFocus();
            return;
        }


        int idGuardar = Integer.parseInt(idText.getText());

        Empleado empleado = new Empleado(idGuardar, nombre, puesto, salario);

        int resultado;
        if (idGuardar == 0) {
            if (csCheckbox.isSelected()) {
                resultado = manejadorEmpleadosDB.insertarCS(empleado);
                System.out.println("Insertar Callable");


                // mandar llamar a CallableStatement
            } else {
                resultado = manejadorEmpleadosDB.insertarPS(empleado);
            }

            if (resultado > 0) { //El insert afecto a 1 0 mas renglones
                mostrarAlerta("Empleado agregado corectamente.", "Correcto", Alert.AlertType.INFORMATION);

            } else { //El insert no afecta ningun renglon
                mostrarAlerta("No se pudo agregar el empleado.", "Correcto", Alert.AlertType.ERROR);

            }
        } else { //significa que se esta actualizando un empleado existente
            if (csCheckbox.isSelected()) {
                resultado = manejadorEmpleadosDB.actualizarCS(empleado);
                // mandar llamar a CallableStatement
            } else {
                resultado = manejadorEmpleadosDB.actualizarPS(empleado);
            }
            if (resultado > 0) { //el update afrcto a 1 o mas regnloes
                mostrarAlerta("Empleado actualizado corectamente.", "Correcto", Alert.AlertType.INFORMATION);
            } else { //el update no afecta ningun renglon
                mostrarAlerta("No se pudo actualizar el empleado.", "Error", Alert.AlertType.ERROR);
            }
        }

        recargarDatos();
    }

    @FXML
    private void eliminarEmpleado() {
        int idEliminar = Integer.parseInt(idText.getText());
        int resultado;
        if (idEliminar != 0) {
            // TODO mandar a llamar a CallableStatement
            //int resultado;
            if (csCheckbox.isSelected()){
                resultado = manejadorEmpleadosDB.eliminarCS(idEliminar);
            } else {
                resultado = manejadorEmpleadosDB.eliminarPS(idEliminar);
            }

            if (resultado > 0) {
                mostrarAlerta("Empleado eliminado correctamente.", "Correcto", Alert.AlertType.INFORMATION);
            } else {
                mostrarAlerta("No se pudo eliminar el empleado.", "Error", Alert.AlertType.ERROR);
            }

            recargarDatos();

        } else {
            mostrarAlerta("Seleccione un empleado valido", "Aviso", Alert.AlertType.INFORMATION);
        }
    }


    @FXML
    private void filtrarEmpleados() {
        String nombreFiltro = nombreText.getText();
        String puestoFiltro = puestoText.getText();
        String salarioStr = salarioText.getText();

        if (nombreFiltro.isEmpty()) {
            nombreFiltro = null;
        }

        if (puestoFiltro.isEmpty()) {
            puestoFiltro = null;
        }

        double salarioFiltro = 0;
        if (!salarioStr.isEmpty()) {
            try {
                salarioFiltro = Double.parseDouble(salarioStr);
            } catch (NumberFormatException e) {
                mostrarAlerta("El salario ingresado no es un numero valido.", "Aviso de Filtro", Alert.AlertType.WARNING);
                salarioText.requestFocus();
            }
        }
        if (csCheckbox.isSelected()){
            listObservable.setAll(manejadorEmpleadosDB.getEmpleadosPorFiltroCS(nombreFiltro, puestoFiltro, salarioFiltro));

        }else {

            listObservable.setAll(manejadorEmpleadosDB.getEmpleadosPorFiltroPS(nombreFiltro, puestoFiltro, salarioFiltro));
        }
        if (listObservable.isEmpty()){
            mostrarAlerta("No se encontraron empleados con los filtros proporcionados", "Aviso", Alert.AlertType.INFORMATION);
            recargarDatos();
        }

    }

    private void cargarSeleccionado(Empleado empleado) {
        idText.setText(String.valueOf(empleado.getId()));
        nombreText.setText(empleado.getNombre());
        puestoText.setText(empleado.getPuesto());
        salarioText.setText(String.valueOf(empleado.getSalario()));

    }

    @FXML
    private void recargarDatos() {
        if (csCheckbox.isSelected()){
            listObservable.setAll(manejadorEmpleadosDB.getEmpleadosCS());

        }else {
            listObservable.setAll(manejadorEmpleadosDB.getEmpleadosPS());
        }
        limpiarForm();

    }


    private void limpiarForm() {
        idText.setText(String.valueOf(0));
        nombreText.clear();
        puestoText.clear();
        salarioText.clear();

        //Deseleccionar el renglon seleccionado en la tabla
        tablaEmpleados.getSelectionModel().clearSelection();

    }

    private void mostrarAlerta(String mensaje, String titulo, Alert.AlertType tipo) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);

        //obtener la ventana principal para centrar la alerta respecto a la vetana.
        Stage stage = (Stage) tablaEmpleados.getScene().getWindow();
        if (stage != null) {
            alerta.initOwner(stage);
        }

        //alerta.show();
        alerta.showAndWait();

    }
/*
    @FXML
    private void abrirSubVentana() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(LoginApplication.class.getResource("login-view.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 320, 240);

            //crear la subventana
            Stage subStage = new Stage();
            subStage.setTitle("Subventana");
            subStage.setScene(scene);

            subStage.initOwner(tablaEmpleados.getScene().getWindow());
            subStage.initModality(Modality.WINDOW_MODAL); //bloquea la ventana principal
            subStage.show();

        } catch (IOException e) {
            e.printStackTrace();
            mostrarAlerta("No se pudo abriir la subventana", "Error", Alert.AlertType.ERROR);
        }
    }

 */

    @FXML
    private void probarConexion() {
        //-manejadorEmpleadosDB = new ManejadorEmpleadoDB();
        Connection conn = manejadorEmpleadosDB.abririConexion();

        if (conn != null) {
            mostrarAlerta("¡Conexion exitosa!", "Exito", Alert.AlertType.INFORMATION);
            manejadorEmpleadosDB.cerrarConexion(conn);
            mostrarAlerta("Se cerro la conexion", "Exito", Alert.AlertType.INFORMATION);
        } else {
            mostrarAlerta("No se pudo coectar a la base de datos", "Error", Alert.AlertType.ERROR);
        }
    }
    @FXML
    private void contarEmpleados(){
        String puesto = "Dev";
        int total = manejadorEmpleadosDB.contarEmpleadosPorPuestoCS(puesto);
        System.out.println("Total de empleados en el puesto "+puesto+": "+total);
    }

    @FXML
    private void aumentarPuesto(){
        //concatenar
        String puesto = "Dev";
        String nuevo = manejadorEmpleadosDB.aumentarPuestoCS(puesto, "Senior");
        System.out.println("Nuevo puesto: " + nuevo);
    }

    @FXML
    private void contarComboBox() {
        // Crear alerta para seleccionar el puesto
        Alert alerta = new Alert(Alert.AlertType.CONFIRMATION);
        alerta.setTitle("Contar empleados por puesto");
        alerta.setHeaderText("Selecciona el puesto que deseas contar:");

        // Crear ComboBox con los puestos disponibles desde la BD
        ComboBox<String> comboPuestos = new ComboBox<>();
        comboPuestos.getItems().addAll(manejadorEmpleadosDB.obtenerPuestos());
        comboPuestos.setPromptText("Selecciona un puesto");

        alerta.getDialogPane().setContent(comboPuestos);

        // Centrar la alerta respecto a la ventana principal
        Stage stage = (Stage) tablaEmpleados.getScene().getWindow();
        if (stage != null) {
            alerta.initOwner(stage);
        }

        // Mostrar alerta y esperar respuesta del usuario
        alerta.showAndWait().ifPresent(respuesta -> {
            if (respuesta == ButtonType.OK) {
                String puestoSeleccionado = comboPuestos.getValue();

                if (puestoSeleccionado == null || puestoSeleccionado.isEmpty()) {
                    mostrarAlerta("Debes seleccionar un puesto.", "Advertencia", Alert.AlertType.WARNING);
                    return;
                }

                // Usar el procedimiento almacenado con CallableStatement
                int total = manejadorEmpleadosDB.contarEmpleadosPorPuestoCS(puestoSeleccionado);

                mostrarAlerta("Hay " + total + " empleados con el puesto '" + puestoSeleccionado + "'.",
                        "Resultado del conteo", Alert.AlertType.INFORMATION);
            }
        });
    }
}