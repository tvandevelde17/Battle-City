package eventos;

import modelo.entidades.Bloque;
import modelo.mapa.Mapa;
import modelo.util.TipoBloque;


public class TanqueDestruidoManager {
    private final Mapa mapa;


    public TanqueDestruidoManager(Mapa mapa) {
        this.mapa = mapa;
    }

    public void crearTanqueDestruido(int x, int y) {
        if (!mapa.estaDentroDelMapa(x, y)) {
            return;
        }

        // VERIFICAR que no haya ya un tanque destruido aquí
        Bloque bloqueExistente = mapa.obtenerBloque(x, y);
        if (bloqueExistente != null && bloqueExistente.getTipo() == TipoBloque.TANQUE_DESTRUIDO) {
            return;
        }

        // Limpiar posición anterior de entidades
        mapa.eliminarEntidad(x, y);

        // Crear bloque de tanque destruido
        mapa.agregarBloque(TipoBloque.TANQUE_DESTRUIDO, x, y);


    }
}


