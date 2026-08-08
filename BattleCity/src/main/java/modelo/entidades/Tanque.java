package modelo.entidades;

import modelo.util.Direccion;
import modelo.util.Movible;

public abstract class Tanque extends Entidad implements Movible {
    protected int cadenciaMs;
    protected int velocidad;
    protected Direccion direccion;
    protected int danioDisparo;
    protected long ultimoDisparo;

    public Tanque(int x, int y, int vida, Direccion direccion, int cadenciaMs, int velocidad, int danioBala) {
        super(x, y, false, true, vida, true, true);
        this.cadenciaMs = cadenciaMs;
        this.danioDisparo = danioBala;
        this.ultimoDisparo = 0;
        this.direccion = direccion;
        this.velocidad = velocidad;
    }

    //------------------------GETTERS Y SETTERS--------------------------------------------------
    @Override
    public Direccion getDireccion() { return this.direccion; }
    public int getDanioDisparo() { return this.danioDisparo; }

    public void setDireccion(Direccion direccion) { this.direccion = direccion; }

    //-----------------------------DISPAROS DE TANQUE---------------------------------------------------------
    public Bala disparar() {
        long ahora = System.currentTimeMillis();
        long tiempoDesdeUltimoDisparo = ahora - this.ultimoDisparo;

        if (tiempoDesdeUltimoDisparo < this.cadenciaMs) {
            return null;
        }

        // Calcular posición de disparo (una celda adelante)
        int balaX = this.x;
        int balaY = this.y;

        switch (this.direccion) {
            case ARRIBA -> balaY--;
            case ABAJO -> balaY++;
            case IZQUIERDA -> balaX--;
            case DERECHA -> balaX++;
        }

        this.ultimoDisparo = ahora;

        return new Bala(balaX, balaY, this.direccion, this.getDanioDisparo());
    }

    protected int getVelocidad() {
        return this.velocidad;
    }
}