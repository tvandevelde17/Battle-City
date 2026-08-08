package app;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

public class MainApp extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/menu.fxml"));
            Parent root = loader.load();

            //Establecer el stage
            Scene scene = new Scene(root);
            primaryStage.setScene(scene);
            primaryStage.setTitle("YABC - Yet Another Battle City");
            primaryStage.show();

        } catch (Exception e) {
            e.printStackTrace();

            // Mostrar diálogo de error
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error de Inicio");
            alert.setHeaderText("No se pudo cargar la aplicación");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
        primaryStage.setOnCloseRequest(e -> {
            System.out.println("Cerrando aplicación desde ventana principal...");
            Platform.exit();
            System.exit(0);
        });

    }

}