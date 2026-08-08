package modelo.util;

public enum TipoEnemigo{
    BASICO(1,1,1000),
    RAPIDO(7,1,1000),
    POTENTE(1,1,500),
    BLINDADO(1,3,1000);
    private final int velocidad;
    private final int vidas;
    private final int cadenciaMs;

    TipoEnemigo(int velocidad, int vidas, int cadenciaMs){
        this.velocidad = velocidad;
        this.vidas = vidas;
        this.cadenciaMs = cadenciaMs;
    }
    public int getVelocidad() { return velocidad; }
    public int getVidas() { return vidas; }
    public int getCadenciaMs() { return cadenciaMs; }
}
