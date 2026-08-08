package controlador;

import javafx.application.Platform;
import modelo.juego.Juego;
import modelo.util.ModoJuego;
import java.util.Arrays;
import java.util.List;

public class IniciarJuego implements Runnable {
    private final ModoJuego modo;
    private final ControladorVentana controladorVentana;

    public IniciarJuego(ModoJuego modo, ControladorVentana controladorVentana) {
        this.modo = modo;
        this.controladorVentana = controladorVentana;
    }

    @Override
    public void run() {
        try {
            // --- Rutas de los niveles ---
            List<String> rutasNiveles = Arrays.asList(
                    "/YABC-Assets/levels/nivel1.xml",
                    "/YABC-Assets/levels/nivel2.xml",
                    "/YABC-Assets/levels/nivel3.xml"
            );

            // --- Crear juego con las rutas ---
            Juego juego = new Juego(rutasNiveles, modo);

            // --- Informar a la ventana principal ---
            Platform.runLater(() -> {
                controladorVentana.setJuego(juego);
                controladorVentana.mostrarJuego();
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}