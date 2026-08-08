package controlador;

import eventos.JuegoTerminadoEvento;
import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import modelo.entidades.Jugador;
import modelo.juego.Juego;
import modelo.niveles.Nivel;
import modelo.util.Direccion;
import modelo.util.ModoJuego;


import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ControladorJuego {
    // ==================== CONTENEDORES FXML ====================
    @FXML private AnchorPane NivelContainer;
    @FXML private AnchorPane JuegoTerminadoContainer;

    // ==================== COMPONENTES DEL JUEGO ====================
    private Juego juego;
    private AnimationTimer timer;
    private RenderNivel renderNivel;
    private ModoJuego modoJuego;
    private ControladorVentana controladorVentana;

    // ==================== CONTROL DE TECLADO ====================
    private final Map<KeyCode, Boolean> teclasPresionadas = new HashMap<>();
    private long ultimoMovimientoJugador1 = 0;
    private long ultimoMovimientoJugador2 = 0;
    private static final long INTERVALO_MOVIMIENTO = 100_000_000;

    // ==================== CONTROLADORES ====================
    private JuegoTerminadoController juegoTerminadoController;

    // ==================== CONFIGURACIÓN INICIAL ====================

    public void setJuego(Juego juego) {
        this.juego = juego;
    }

    public void setModoJuego(ModoJuego modoJuego) {
        this.modoJuego = modoJuego;
    }

    public void setControladorVentana(ControladorVentana controladorVentana) {
        this.controladorVentana = controladorVentana;
    }

    public void iniciarJuego() {
        if (modoJuego == null) {
            throw new IllegalStateException("ModoJuego debe establecerse antes de iniciar el juego");
        }

        // VERIFICAR que los contenedores están cargados
        if (NivelContainer == null) {
            return;
        }

        cargarPantallasAdicionales();
        ocultarTodasLasPantallas();
        mostrarPantallaJuego();
        inicializarNivel();
        configurarControles();
        iniciarTimers();
        juego.agregarListener(this::manejarJuegoTerminado);
    }

    private void inicializarNivel() {
        Nivel nivel = juego.getNivelActual();
        renderNivel = new RenderNivel(nivel);
        NivelContainer.getChildren().setAll(renderNivel.getPane());
    }
    // ==================== MANEJO DE PANTALLAS ====================

    private void cargarPantallasAdicionales() {
        cargarPantallaJuegoTerminado();
    }

    private void cargarPantallaJuegoTerminado() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/juego_terminado.fxml"));
            AnchorPane juegoTerminadoPane = loader.load();

            juegoTerminadoController =loader.getController();
            juegoTerminadoController.setOnNextLevelAction(this::siguienteNivel);
            juegoTerminadoController.setOnSalirAction(this::salirDelJuego);

            JuegoTerminadoContainer.getChildren().setAll(juegoTerminadoPane);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void manejarJuegoTerminado(JuegoTerminadoEvento evento) {
        Platform.runLater(() -> {
            detenerJuego();

            if (juegoTerminadoController != null) {
                juegoTerminadoController.configurarPantalla(evento);
                mostrarPantallaJuegoTerminado();
            }
        });
    }

    private void salirDelJuego() {
        detenerJuego();

        if (controladorVentana != null) {
            controladorVentana.cerrarAplicacion();
        } else {
            Platform.exit();
            System.exit(0);
        }
    }

    // ==================== RECREAR VISTA COMPLETA ====================

    private void recrearVistaCompleta() {
        // 1. DETENER Y LIMPIAR COMPLETAMENTE LO ANTERIOR
        detenerYLimpiarCompletamente();

        ocultarTodasLasPantallas();
        mostrarPantallaJuego();

        // 2. CREAR NUEVA VISTA desde cero
        Nivel nivelActual = juego.getNivelActual();
        renderNivel = new RenderNivel(nivelActual);
        NivelContainer.getChildren().setAll(renderNivel.getPane());

        // 3. Reconfigurar controles
        configurarControles();

        // 4. Reiniciar timers
        iniciarTimers();

    }

    private void detenerYLimpiarCompletamente() {
        if (timer != null) {
            timer.stop();
            timer = null;
        }

        if (renderNivel != null) {
            renderNivel.limpiarCompletamente();
            renderNivel = null;
        }

        NivelContainer.getChildren().clear();

        teclasPresionadas.clear();

        System.gc();
    }

    private void ocultarTodasLasPantallas() {
        JuegoTerminadoContainer.setVisible(false);
        NivelContainer.setVisible(true);
    }

    private void mostrarPantallaJuego() {
        NivelContainer.setVisible(true);
        NivelContainer.toFront();
    }

    private void mostrarPantallaJuegoTerminado() {
        JuegoTerminadoContainer.setVisible(true);
        JuegoTerminadoContainer.toFront();
        NivelContainer.setVisible(false);
    }

    // ==================== CONTROL DE ENTRADA ====================

    private void configurarControles() {
        NivelContainer.setFocusTraversable(true);
        NivelContainer.setOnMouseClicked(event -> NivelContainer.requestFocus());
        NivelContainer.setOnKeyPressed(this::manejarTeclaPresionada);
        NivelContainer.setOnKeyReleased(event -> teclasPresionadas.put(event.getCode(), false));
    }

    private void manejarTeclaPresionada(KeyEvent event) {
        teclasPresionadas.put(event.getCode(), true);

        if (event.getCode() == KeyCode.SPACE) {
            juego.getNivelActual().jugadorDispara(0);
        }

        if (modoJuego == ModoJuego.DOS_JUGADORES && event.getCode() == KeyCode.ENTER) {
            juego.getNivelActual().jugadorDispara(1);
        }
    }

    // ==================== MOVIMIENTO DE JUGADORES ====================

    private void manejarMovimientoJugadores(Nivel nivel, long now) {
        List<Jugador> jugadores = nivel.getJugadores();

        if (!jugadores.isEmpty()) {
            manejarMovimientoJugador(jugadores.getFirst(), now,
                    KeyCode.W, KeyCode.S, KeyCode.A, KeyCode.D,
                    ultimoMovimientoJugador1,
                    nuevoTiempo -> ultimoMovimientoJugador1 = nuevoTiempo);
        }

        if (modoJuego == ModoJuego.DOS_JUGADORES && jugadores.size() > 1) {
            manejarMovimientoJugador(jugadores.get(1), now,
                    KeyCode.UP, KeyCode.DOWN, KeyCode.LEFT, KeyCode.RIGHT,
                    ultimoMovimientoJugador2,
                    nuevoTiempo -> ultimoMovimientoJugador2 = nuevoTiempo);
        }
    }

    private void manejarMovimientoJugador(Jugador jugador, long now,
                                          KeyCode arriba, KeyCode abajo,
                                          KeyCode izquierda, KeyCode derecha,
                                          long ultimoMovimiento,
                                          java.util.function.Consumer<Long> actualizarTiempo) {
        if (jugador.estaVivo()) {
            Direccion dir = obtenerDireccionDesdeTeclas(arriba, abajo, izquierda, derecha);
            if (dir != null && now - ultimoMovimiento >= INTERVALO_MOVIMIENTO) {
                if (juego.getNivelActual().moverJugador(jugador, dir)) {
                    actualizarTiempo.accept(now);
                }
            }
        }
    }

    private Direccion obtenerDireccionDesdeTeclas(KeyCode arriba, KeyCode abajo, KeyCode izquierda, KeyCode derecha) {
        boolean presionadoArriba = estaPresionada(arriba);
        boolean presionadoAbajo = estaPresionada(abajo);
        boolean presionadoIzquierda = estaPresionada(izquierda);
        boolean presionadoDerecha = estaPresionada(derecha);

        if (presionadoArriba && !presionadoAbajo) return Direccion.ARRIBA;
        if (presionadoAbajo && !presionadoArriba) return Direccion.ABAJO;
        if (presionadoIzquierda && !presionadoDerecha) return Direccion.IZQUIERDA;
        if (presionadoDerecha && !presionadoIzquierda) return Direccion.DERECHA;

        return null;
    }

    private boolean estaPresionada(KeyCode key) {
        return teclasPresionadas.getOrDefault(key, false);
    }

    // ==================== BUCLE PRINCIPAL ====================

    private void iniciarTimers() {
        // Timer para mantener el foco
        AnimationTimer focusTimer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (!NivelContainer.isFocused()) {
                    NivelContainer.requestFocus();
                }
            }
        };
        focusTimer.start();

        // Timer principal del juego
        if (timer != null) {
            timer.stop();
        }

        timer = new AnimationTimer() {
            private long lastUpdate = 0;

            @Override
            public void handle(long now) {
                if (lastUpdate == 0) {
                    lastUpdate = now;
                    return;
                }
                actualizar(now);
                lastUpdate = now;
            }
        };
        timer.start();
    }

    private void actualizar(long now) {
        Nivel nivel = juego.getNivelActual();
        manejarMovimientoJugadores(nivel, now);
        juego.actualizarJuego();

        if (renderNivel != null) {
            renderNivel.actualizar();
        }
    }

    // ==================== SIGUIENTE NIVEL ====================

    private void siguienteNivel() {
        juego.avanzarASiguienteNivel();

        if (!juego.isJuegoTerminado()) {

            // Recrear la vista para el nuevo nivel
            recrearVistaCompleta();
        } else {
            if (juegoTerminadoController != null) {
                JuegoTerminadoEvento evento = new JuegoTerminadoEvento(
                        JuegoTerminadoEvento.TipoResultado.VICTORIA_JUEGO_COMPLETADO,
                        "¡Felicidades! Has completado todos los niveles."
                );
                juegoTerminadoController.configurarPantalla(evento);
                mostrarPantallaJuegoTerminado();
            }
        }
    }

    // ==================== CONTROL DE ESTADO ====================

    public void detenerJuego() {
        if (timer != null) {
            timer.stop();
            timer = null;
        }
    }
}
