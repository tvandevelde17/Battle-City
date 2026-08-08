package modelo.entidades;

import eventos.EventManager;
import modelo.util.TipoEnemigo;
import modelo.util.Direccion;
import java.util.Random;


public class Enemigo extends Tanque {
    private final TipoEnemigo tipo;
    private int resistencia; // Agrega este campo
    private long proxCambioDireccion;
    private long ultimoMovimiento = 0;
    private final Random random = new Random();

    // Para máquina de estados y detección de atasco
    private int ultimaX, ultimaY;
    private long tiempoEnPosicionActual;
    private static final long TIEMPO_MAX_ATASCADO = 2_000_000_000L; // 2 segundos
    private static final long INTERVALO_MOVIMIENTO_BASE = 1_000_000_000L; // 1 segundo

    public Enemigo(int x, int y, TipoEnemigo tipo) {
        super(x, y, tipo.getVidas(), Direccion.ABAJO, tipo.getCadenciaMs(), tipo.getVelocidad(), 1);
        this.tipo = tipo;
        this.resistencia = tipo.getVidas();

        long tiempoComportamiento = 1_000_000_000L + (random.nextInt(4000) * 1_000_000L);
        this.proxCambioDireccion = System.nanoTime() + tiempoComportamiento;

        this.ultimaX = x;
        this.ultimaY = y;
        this.tiempoEnPosicionActual = System.nanoTime();
    }

    @Override
    public void recibirImpacto(int danio) {
        if (!this.estaVivo()) {
            return;
        }
        resistencia -= danio;

        if (resistencia <= 0) {
            this.setVivo(false);

            try {
                EventManager.getInstance().notificarDestruccion(this);
            } catch (Exception e) {
                System.err.println(e.getMessage());
            }
        }
    }

    @Override
    public boolean esEnemigo() {
        return true;
    }

    public boolean actualizarIA() {
        if (!this.estaVivo()) return false;

        long ahoraNano = System.nanoTime();

        if (this.getX() == ultimaX && this.getY() == ultimaY) {
            if (ahoraNano - tiempoEnPosicionActual > TIEMPO_MAX_ATASCADO) {
                cambiarDireccionAleatoria();
                reiniciarTiempoComportamiento(ahoraNano);
                tiempoEnPosicionActual = ahoraNano;
                return true; // Cambió dirección, intentar mover
            }
        } else {
            ultimaX = this.getX();
            ultimaY = this.getY();
            tiempoEnPosicionActual = ahoraNano;
        }


        if (ahoraNano >= proxCambioDireccion) {
            cambiarDireccionAleatoria();
            reiniciarTiempoComportamiento(ahoraNano);
            return true; // Cambió dirección, intentar mover
        }

        int velocidad = Math.max(1, this.getVelocidad());
        long intervaloMovimiento = INTERVALO_MOVIMIENTO_BASE / velocidad;

        if (ahoraNano - ultimoMovimiento >= intervaloMovimiento) {
            ultimoMovimiento = ahoraNano;
            return true; // Es tiempo de moverse
        }

        return false; // No moverse en este frame
    }



    private void cambiarDireccionAleatoria() {
        Direccion[] direcciones = {Direccion.ARRIBA, Direccion.ABAJO, Direccion.IZQUIERDA, Direccion.DERECHA};
        Direccion nuevaDireccion;
        int intentos = 0;

        do {
            nuevaDireccion = direcciones[random.nextInt(direcciones.length)];
            intentos++;
        } while (nuevaDireccion == this.getDireccion() && intentos < 10);

        this.setDireccion(nuevaDireccion);
    }

    private void reiniciarTiempoComportamiento(long ahoraNano) {
        long tiempoComportamiento = 1_000_000_000L + (random.nextInt(4000) * 1_000_000L);
        this.proxCambioDireccion = ahoraNano + tiempoComportamiento;
    }


    public Bala intentarDisparo() {
        if (!this.estaVivo()) return null;

        long ahora = System.currentTimeMillis();
        if (ahora - this.ultimoDisparo < this.cadenciaMs) return null;

        if (random.nextDouble() < 0.3) {
            Bala bala = disparar();
            if (bala != null) {
                this.ultimoDisparo = ahora;
                return bala;
            }
        }
        return null;
    }
    public void forzarCambioDireccion() {
        cambiarDireccionAleatoria();
        reiniciarTiempoComportamiento(System.nanoTime());
    }
    public TipoEnemigo getTipo() {
        return tipo;
    }
}