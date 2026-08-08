package modelo.entidades;

import eventos.EventManager;
import eventos.EventoDestruccion;

public abstract class Entidad {
    protected int x;
    protected int y;
    protected boolean permitePaso;
    protected boolean recibeDisparo;
    protected boolean destructible;
    protected int vida;
    protected boolean vivo;

    public Entidad(int x, int y, boolean permitePaso, boolean recibeDisparo, int vida, boolean destructible, boolean vivo) {
        this.x = x;
        this.y = y;
        this.recibeDisparo = recibeDisparo;
        this.permitePaso = permitePaso;
        this.vida = vida;
        this.destructible = destructible;
        this.vivo = vivo;
    }

    //-------------------------GETTERS Y SETTERS-----------------------------
    public boolean esJugador() { return false; }
    public boolean esEnemigo() { return false; }
    public boolean recibeDisparo() { return recibeDisparo; }

    public int getX() { return x; }
    public int getY() { return y; }
    public void setX(int x) { this.x = x; }
    public void setY(int y) { this.y = y; }
    public void setVida(int vida) { this.vida = vida; }
    public void setVivo(boolean vivo) { this.vivo = vivo; }

    public boolean estaVivo() { return this.vivo; }
    public void setDestructible(boolean destructible) { this.destructible = destructible; }

    //-------------------------MOVIMIENTO Y VIDA---------------------------------------------
    public void actualizarPosicion(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void recibirImpacto(int danio) {
        if (this.destructible && this.vida > 0) {
            this.setVida(this.vida - danio);
            if (this.vida <= 0) {
                this.setVivo(false);
                notificarDestruccion();
            }
        }
    }
    protected void notificarDestruccion() {
        EventManager.getInstance().notificarDestruccion(
                new EventoDestruccion(this, getX(), getY()).getEntidadDestruida()
        );
    }

    public boolean esPowerUp() {
        return false;
    }
}