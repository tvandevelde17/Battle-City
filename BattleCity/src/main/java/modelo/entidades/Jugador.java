package modelo.entidades;


import modelo.util.Direccion;

public class Jugador extends Tanque {
    private int vidasJuego;
    private boolean disparoMejorado;
    private long tiempoCascoFin;
    private boolean tieneBalaActiva;

    public Jugador(int x, int y) {
        super(x, y, 1, Direccion.ARRIBA, 500, 1, 1);
        this.vidasJuego = 3;
        this.disparoMejorado = false;
        this.tiempoCascoFin = 0;
        this.tieneBalaActiva = false;
    }

    // ---------------- GETTERS Y SETTERS ----------------
    public int getVidasJuego() { return this.vidasJuego; }
    @Override
    public boolean esJugador() { return true; }

    @Override
    public int getDanioDisparo() {
        return disparoMejorado ? Integer.MAX_VALUE : 1;
    }

    public void balaDestruida() {
        this.tieneBalaActiva = false;
    }

    public boolean tieneBalaActiva() {
        return tieneBalaActiva;
    }

    // ---------------- POWER-UPS ----------------
    public void activarCasco(long duracionMillis) {
        this.setDestructible(false);
        this.setVida(Integer.MAX_VALUE);
        this.tiempoCascoFin = System.currentTimeMillis() + duracionMillis;
    }

    public void actualizarCasco() {
        if (!this.destructible && System.currentTimeMillis() >= this.tiempoCascoFin) {
            this.setDestructible(true);
            this.setVida(1);
        }
    }
    public void actualizarEfectosTemporales() {
        // Actualizar casco
        if (!this.destructible && System.currentTimeMillis() >= this.tiempoCascoFin) {
            this.setDestructible(true);
            this.setVida(1);
        }

        // El disparo mejorado es permanente hasta que el jugador muere
    }
    @Override
    public Bala disparar() {
        if (tieneBalaActiva) {
            return null;
        }

        Bala bala = super.disparar();
        if (bala != null) {
            tieneBalaActiva = true;
        }
        return bala;
    }

    // ----------------- MÉTODOS DE VIDA -----------------
    public void perderVida() {
        if (this.vidasJuego <= 0) return;

        this.vidasJuego--;

        if (this.vidasJuego > 0) {
            this.vida = 1;
            this.vivo = true;
            this.disparoMejorado = false;
            this.tieneBalaActiva = false;
        } else {
            this.vivo = false;
        }
    }

    @Override
    public void recibirImpacto(int danio) {
        if (!this.destructible) return;

        this.vida -= danio;
        if (this.vida <= 0) {
            this.vida = 0;
            this.vivo = false;
            perderVida();
        }
    }

    public void mejorarDisparo() {
        this.disparoMejorado = true;
    }
    public boolean isCascoActivo() {
        return !this.destructible;
    }
}