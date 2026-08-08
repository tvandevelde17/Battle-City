package eventos;

import modelo.entidades.Jugador;
import modelo.entidades.PowerUp;
import modelo.mapa.Mapa;
import modelo.niveles.Nivel;
import modelo.util.TipoPowerUp;
import java.util.Random;

public class PowerUpManager {
    private final Random random = new Random();
    private final Mapa mapa;
    private PowerUp powerUpActual;
    private static final double PROBABILIDAD_BASE = 0.20;
    private static final double INCREMENTO_ACUMULATIVO = 0.05;
    private static final double PROBABILIDAD_MAXIMA = 0.80;
    private static final int MAX_ENEMIGOS_SIN_POWERUP = 8;

    private int enemigosDestruidosSinPowerUp = 0;

    public PowerUpManager(Mapa mapa) {
        this.mapa = mapa;
    }

    public void intentarGenerarPowerUp() {

        if (powerUpActual != null) {
            return;
        }

        // Incrementar contador y calcular probabilidad acumulativa
        enemigosDestruidosSinPowerUp++;

        double probabilidadAcumulativa = PROBABILIDAD_BASE +
                (enemigosDestruidosSinPowerUp * INCREMENTO_ACUMULATIVO);

        boolean forzarGeneracion = enemigosDestruidosSinPowerUp >= MAX_ENEMIGOS_SIN_POWERUP;
        double probabilidadFinal = forzarGeneracion ? 1.0 : Math.min(probabilidadAcumulativa, PROBABILIDAD_MAXIMA);

        if (random.nextDouble() <= probabilidadFinal) {
            generarPowerUp();
        }
    }

    private void generarPowerUp() {
        enemigosDestruidosSinPowerUp = 0;

        // Seleccionar tipo aleatorio
        TipoPowerUp tipo = TipoPowerUp.values()[random.nextInt(TipoPowerUp.values().length)];

        // Buscar posición libre
        for (int intentos = 0; intentos < 30; intentos++) {
            int x = random.nextInt(mapa.getAncho());
            int y = random.nextInt(mapa.getAlto());

            if (mapa.estaLibre(x, y) && esPosicionValida(x, y)) {
                powerUpActual = new PowerUp(x, y, tipo);
                mapa.agregarEntidad(powerUpActual);
                return;
            }
        }
    }

    private boolean esPosicionValida(int x, int y) {
        return  x > 1 && x < mapa.getAncho() - 2 &&
                y > 1 && y < mapa.getAlto() - 2;
    }

    public void verificarConsumo(Jugador jugador, Nivel nivel) {
        if (powerUpActual != null && powerUpActual.estaActivo()) {

            if (jugador.getX() == powerUpActual.getX() && jugador.getY() == powerUpActual.getY()) {
                aplicarEfectoPowerUp(jugador, nivel);
                mapa.eliminarEntidad(powerUpActual.getX(), powerUpActual.getY());
                powerUpActual = null;
            }
        }
    }

    private void aplicarEfectoPowerUp(Jugador jugador, Nivel nivel) {
        TipoPowerUp tipo = powerUpActual.getTipo();
        switch (tipo) {
            case GRANADA:
                nivel.destruirTodosLosEnemigos();
                break;
            case CASCO:
                jugador.activarCasco(10000); // 10 segundos
                break;
            case ESTRELLA:
                jugador.mejorarDisparo();
                break;
        }
    }

    public PowerUp getPowerUpActual() {
        return powerUpActual;
    }


    public void limpiarPowerUp() {
        if (powerUpActual != null && powerUpActual.estaVivo()) {
            mapa.eliminarEntidad(powerUpActual.getX(), powerUpActual.getY());
        }
        powerUpActual = null;
    }
}