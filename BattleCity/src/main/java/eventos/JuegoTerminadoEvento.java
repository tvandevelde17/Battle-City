package eventos;


public class JuegoTerminadoEvento {
    public enum TipoResultado {
        VICTORIA,
        DERROTA,
        VICTORIA_JUEGO_COMPLETADO
    }

    private final TipoResultado tipo;
    private final String mensaje;


    public JuegoTerminadoEvento(TipoResultado tipo, String mensaje) {
        this.tipo = tipo;
        this.mensaje = mensaje;
    }

    public TipoResultado getTipo() {
        return tipo;
    }

    public String getMensaje() {
        return mensaje;
    }

}