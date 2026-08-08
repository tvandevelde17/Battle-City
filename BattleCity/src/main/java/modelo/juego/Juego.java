package modelo.juego;

import controlador.LevelLoader;
import eventos.EventManager;
import eventos.JuegoTerminadoEvento;
import modelo.entidades.Jugador;
import modelo.niveles.Nivel;
import modelo.util.ModoJuego;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;


public class Juego {
    private final List<String> rutasNiveles;
    private final ModoJuego modo;
    private Nivel nivelActual;
    private int indiceNivelActual = 0;
    private boolean juegoTerminado = false;
    private final List<Consumer<JuegoTerminadoEvento>> listeners = new ArrayList<>();

    public Juego(List<String> rutasNiveles, ModoJuego modo) {
        this.rutasNiveles = rutasNiveles;
        this.modo = modo;
        cargarNivelActual(); // Cargar el primer nivel
    }

    private void cargarNivelActual() {
        // Limpiar completamente el nivel anterior si existe
        if (nivelActual != null) {
            nivelActual.destruir();
            nivelActual = null;
        }


        EventManager.getInstance().limpiarCompletamente();

        // Cargar nuevo nivel
        if (indiceNivelActual < rutasNiveles.size()) {
            String rutaNivel = rutasNiveles.get(indiceNivelActual);
            nivelActual = LevelLoader.cargarNivel(rutaNivel, modo);

        } else {
            // Juego completado
            juegoTerminado = true;
            notificarJuegoTerminado(new JuegoTerminadoEvento(
                    JuegoTerminadoEvento.TipoResultado.VICTORIA_JUEGO_COMPLETADO,
                    "¡Felicidades! Has completado todos los niveles"
            ));
        }
    }

    public void actualizarJuego() {
        if (isJuegoTerminado() || nivelActual == null) return;

        nivelActual.actualizar();

        if (nivelActual.nivelGanado()) {
            // Notificar victoria del nivel actual
            notificarJuegoTerminado(new JuegoTerminadoEvento(
                    JuegoTerminadoEvento.TipoResultado.VICTORIA,
                    "¡Nivel " + (indiceNivelActual + 1) + " completado!"
            ));
        } else if (nivelActual.nivelPerdido()) {
            juegoTerminado = true;
            notificarJuegoTerminado(new JuegoTerminadoEvento(
                    JuegoTerminadoEvento.TipoResultado.DERROTA,
                    generarMensajeDerrota(nivelActual)
            ));
        }
    }

    public void avanzarASiguienteNivel() {
        if (indiceNivelActual < rutasNiveles.size() - 1) {
            indiceNivelActual++;
            juegoTerminado = false;
            cargarNivelActual();
        } else {
            // Último nivel completado
            juegoTerminado = true;
            notificarJuegoTerminado(new JuegoTerminadoEvento(
                    JuegoTerminadoEvento.TipoResultado.VICTORIA_JUEGO_COMPLETADO,
                    "¡Felicidades! Has completado todos los niveles"
            ));
        }
    }

    private String generarMensajeDerrota(Nivel nivel) {
        if (!nivel.getBase().estaVivo()) {
            return "Tu base ha sido destruida";
        } else {
            for (Jugador jugador : nivel.getJugadores()) {
                if (jugador.getVidasJuego() > 0) {
                    return "Has perdido el nivel " + (indiceNivelActual + 1);
                }
            }
            return "Todos los tanques han sido destruidos";
        }
    }

    // Sistema de eventos
    public void agregarListener(Consumer<JuegoTerminadoEvento> listener) {
        listeners.add(listener);
    }

    private void notificarJuegoTerminado(JuegoTerminadoEvento evento) {
        for (Consumer<JuegoTerminadoEvento> listener : listeners) {
            listener.accept(evento);
        }
    }

    public Nivel getNivelActual() {
        return nivelActual;
    }



    public boolean isJuegoTerminado() {
        return juegoTerminado;
    }

    public ModoJuego getModoJuego() {
        return this.modo;
    }

}


