package controlador;

import eventos.JuegoTerminadoEvento;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;

public class JuegoTerminadoController {

    @FXML private Label tituloLabel;
    @FXML private Label mensajeLabel;
    @FXML private Button siguienteNivelButton;
    @FXML private Button salirButton;

    private Runnable onNextLevelAction;
    private Runnable onSalirAction;

    @FXML
    public void initialize() {
        if (siguienteNivelButton != null) {
            siguienteNivelButton.setOnAction(e -> {
                if (onNextLevelAction != null) onNextLevelAction.run();
            });
        }

        if (salirButton != null) {
            salirButton.setOnAction(e -> {
                if (onSalirAction != null) {
                    onSalirAction.run();
                }
            });
        }
    }

    public void configurarPantalla(JuegoTerminadoEvento evento) {
        switch (evento.getTipo()) {
            case VICTORIA:
                configurarVictoriaNivel(evento);
                break;
            case VICTORIA_JUEGO_COMPLETADO:
                configurarVictoriaJuego(evento);
                break;
            case DERROTA:
                configurarDerrota(evento);
                break;
        }
    }

    private void configurarVictoriaNivel(JuegoTerminadoEvento evento) {
        tituloLabel.setText("¡VICTORIA!");
        tituloLabel.setTextFill(Color.GREEN);
        mensajeLabel.setText(evento.getMensaje());
        siguienteNivelButton.setVisible(true);
        salirButton.setVisible(false);
    }

    private void configurarVictoriaJuego(JuegoTerminadoEvento evento) {
        tituloLabel.setText("¡JUEGO COMPLETADO!");
        tituloLabel.setTextFill(Color.GOLD);
        mensajeLabel.setText(evento.getMensaje());
        siguienteNivelButton.setVisible(false);
        salirButton.setVisible(true);
    }

    private void configurarDerrota(JuegoTerminadoEvento evento) {
        tituloLabel.setText("GAME OVER");
        tituloLabel.setTextFill(Color.RED);
        mensajeLabel.setText(evento.getMensaje());
        siguienteNivelButton.setVisible(false);
        salirButton.setVisible(true);
    }

    public void setOnNextLevelAction(Runnable onNextLevelAction) {
        this.onNextLevelAction = onNextLevelAction;
    }

    public void setOnSalirAction(Runnable onSalirAction) {
        this.onSalirAction = onSalirAction;
    }
}

