package modelo.entidades;


import modelo.util.Direccion;
import modelo.util.Movible;

public class Bala extends Entidad implements Movible {
    private final Direccion direccion;
    private final int danio;

    public Bala(int x, int y, Direccion direccion, int danio) {
        super(x, y, false, true, 1, true, true);
        this.direccion = direccion != null ? direccion : Direccion.ABAJO;
        this.danio = danio;
        this.setVivo(true);
    }

    // Implementación de Movible
    @Override
    public Direccion getDireccion() { return direccion; }

    public int getDanio() {
        return danio;
    }
}