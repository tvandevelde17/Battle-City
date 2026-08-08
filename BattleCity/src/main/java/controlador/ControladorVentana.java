package controlador;


import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import modelo.juego.Juego;
import modelo.util.ModoJuego;

import java.io.IOException;

public class ControladorVentana {
    private Juego juego;
    private ModoJuego modoJuego;
    private final Stage stage;


    public ControladorVentana(Stage stage) {
        this.stage = stage;
    }


    public void setJuego(Juego juego) {
        this.juego = juego;
        if (juego != null) {
            this.modoJuego = juego.getModoJuego();
        }
    }

    public void mostrarJuego() {
        try {
            if (juego == null) {
                throw new IllegalStateException("Juego no ha sido inicializado");
            }

            // Cargar la escena de juego COMPLETAMENTE NUEVA
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/juego.fxml"));
            Parent root = loader.load();

            ControladorJuego controladorJuego = loader.getController();
            controladorJuego.setJuego(juego);
            controladorJuego.setModoJuego(modoJuego);
            controladorJuego.setControladorVentana(this);

            // Crear NUEVA escena
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();

            // Iniciar el juego en la NUEVA escena
            controladorJuego.iniciarJuego();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void cerrarAplicacion() {
        Platform.exit();
        System.exit(0);
    }
}