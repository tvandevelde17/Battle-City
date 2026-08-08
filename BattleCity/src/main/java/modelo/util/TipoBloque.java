package modelo.util;

public enum TipoBloque {
    AGUA(false, false, false, 0),
    ACERO(false, true, false, Integer.MAX_VALUE),
    LADRILLO(false, true, true, 3),
    BOSQUE(true, true, false, 0),
    TANQUE_DESTRUIDO(false,true,false,Integer.MAX_VALUE);
    private final boolean permitePaso;
    private final boolean recibeDisparo;
    private final boolean destructible;
    private final int resistencia;

    TipoBloque(boolean pasablePorTanque, boolean recibeDisparo, boolean destructible, int resistencia){
        this.permitePaso = pasablePorTanque;
        this.recibeDisparo = recibeDisparo;
        this.destructible = destructible;
        this.resistencia = resistencia;
    }

    public boolean esPasablePorTanque() { return this.permitePaso; }
    public boolean recibeBalas() { return this.recibeDisparo; }
    public boolean esDestructible() { return this.destructible; }
    public int getResistencia() { return this.resistencia; }
}