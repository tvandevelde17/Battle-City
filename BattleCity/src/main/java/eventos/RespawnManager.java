package eventos;

import modelo.entidades.Bloque;
import modelo.entidades.Entidad;
import modelo.entidades.Jugador;
import modelo.mapa.Mapa;
import modelo.util.Direccion;
import modelo.util.TipoBloque;

public class RespawnManager {
    private final Mapa mapa;
    private final TanqueDestruidoManager tanqueDestruidoManager;
    private final int[][] posicionesSpawn;

    public RespawnManager(Mapa mapa, TanqueDestruidoManager tanqueDestruidoManager, int[][] posicionesSpawn) {
        this.mapa = mapa;
        this.tanqueDestruidoManager = tanqueDestruidoManager;
        this.posicionesSpawn = posicionesSpawn;
    }

    public boolean respawnearJugador(Jugador jugador) {
        if (!jugador.estaVivo() && jugador.getVidasJuego() > 0) {

            for (int i = 0; i < posicionesSpawn.length; i++) {
                int x = posicionesSpawn[i][0];
                int y = posicionesSpawn[i][1];

                if (esPosicionValidaParaRespawn(x, y)) {
                    prepararPosicionRespawn(x, y);
                    posicionarJugador(jugador, x, y);
                    return true;
                }
            }

            return false;
        }

        return false;
    }

    private boolean esPosicionValidaParaRespawn(int x, int y) {
        if (!mapa.estaDentroDelMapa(x, y)) {
            return false;
        }


        Entidad entidad = mapa.obtenerEntidad(x, y);
        if (entidad != null && entidad.estaVivo() && !entidad.esJugador()) {
            return false;
        }


        Bloque bloque = mapa.obtenerBloque(x, y);
        if (bloque != null) {
            return   bloque.getTipo() == TipoBloque.TANQUE_DESTRUIDO ||
                    bloque.getTipo().esPasablePorTanque();
        }

        return true;
    }

    private void prepararPosicionRespawn(int x, int y) {
        // También limpiar cualquier entidad muerta en esa posición
        Entidad entidad = mapa.obtenerEntidad(x, y);
        if (entidad != null && !entidad.estaVivo()) {
            mapa.eliminarEntidad(x, y);
        }
    }

    private void posicionarJugador(Jugador jugador, int x, int y) {
        // Primero eliminar al jugador de su posición anterior (si existe)
        mapa.eliminarEntidad(jugador.getX(), jugador.getY());

        // Configurar nueva posición
        jugador.setVivo(true);
        jugador.setVida(1); // O la vida inicial del jugador
        jugador.setX(x);
        jugador.setY(y);
        jugador.setDireccion(Direccion.ARRIBA);

        // Agregar al mapa en nueva posición
        mapa.agregarEntidad(jugador);
    }
}