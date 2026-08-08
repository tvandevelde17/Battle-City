package controlador;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import modelo.util.ModoJuego;

public class ControladorMenu {

    @FXML private AnchorPane VentanaMenu;
    @FXML private ImageView MenuSelector;
    @FXML private Label UnJugador;
    @FXML private Label DosJugadores;

    private int opcionSeleccionada = 0;
    private Label[] opciones;

    @FXML
    public void initialize() {
        opciones = new Label[]{UnJugador, DosJugadores};

        double[] posicionesY = {240.0, 340.0};

        Platform.runLater(() -> {
            MenuSelector.setLayoutX(180);

            MenuSelector.setLayoutY(posicionesY[opcionSeleccionada]);
            MenuSelector.setVisible(true);
            VentanaMenu.requestFocus();
        });

        VentanaMenu.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.UP) {
                opcionSeleccionada = (opcionSeleccionada - 1 + opciones.length) % opciones.length;
                MenuSelector.setLayoutY(posicionesY[opcionSeleccionada]);
            } else if (event.getCode() == KeyCode.DOWN) {
                opcionSeleccionada = (opcionSeleccionada + 1) % opciones.length;
                MenuSelector.setLayoutY(posicionesY[opcionSeleccionada]);
            } else if (event.getCode() == KeyCode.ENTER) {
                ejecutarOpcionSeleccionada(event);
            }
        });
        VentanaMenu.setFocusTraversable(true);
    }

    private void ejecutarOpcionSeleccionada(KeyEvent event) {
        try {
            ModoJuego modo = switch (opcionSeleccionada) {
                case 0 -> ModoJuego.UN_JUGADOR;
                case 1 -> ModoJuego.DOS_JUGADORES;
                default -> null;
            };

            if (modo != null) {
                Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                ControladorVentana controladorVentana = new ControladorVentana(currentStage);
                IniciarJuego iniciarJuego = new IniciarJuego(modo, controladorVentana);
                new Thread(iniciarJuego).start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
