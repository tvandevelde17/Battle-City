package modelo.entidades;

import modelo.util.TipoBloque;

public class Bloque extends Entidad {
    private final TipoBloque tipo;
    public Bloque(TipoBloque tipo, int x, int y) {
        super(x,y,tipo.esPasablePorTanque(),tipo.recibeBalas(),tipo.getResistencia(), tipo.esDestructible(),  true);
        this.tipo = tipo;
    }
    public TipoBloque getTipo() {
        return tipo;
    }

}