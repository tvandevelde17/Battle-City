package modelo.entidades;

import modelo.util.TipoPowerUp;

public class PowerUp extends Entidad {
    private final TipoPowerUp tipo;
    private final boolean activo = true;

    public PowerUp(int x, int y, TipoPowerUp tipo) {
        super(x, y, true, false, 0, false, true);
        this.tipo = tipo;
    }

    public boolean estaActivo() {
        return activo && estaVivo();
    }

    public TipoPowerUp getTipo() {
        return tipo;
    }

    @Override
    public void recibirImpacto(int danio) {
        // Los power-ups no reciben daño
    }

    @Override
    public boolean esPowerUp() {
        return true;
    }

}
