package eventos;

import modelo.entidades.Entidad;

public class EventoDestruccion {
    private final Entidad entidadDestruida;
    private final int x;
    private final int y;

    public EventoDestruccion(Entidad entidad, int x, int y) {
        this.entidadDestruida = entidad;
        this.x = x;
        this.y = y;
    }

    public Entidad getEntidadDestruida() {
        return entidadDestruida;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
}

