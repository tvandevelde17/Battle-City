package modelo.mapa;

import modelo.entidades.Bloque;
import modelo.entidades.Entidad;
import modelo.util.TipoBloque;

public class Mapa implements SpawnTarget {
    private final int ancho;
    private final int alto;
    private final Entidad[][] grilla;
    private final Bloque[][] bloques;

    public Mapa(int ancho, int alto) {
        this.ancho = ancho;
        this.alto = alto;
        this.grilla = new Entidad[alto][ancho];
        this.bloques = new Bloque[alto][ancho];
    }

    public int getAncho() { return ancho; }
    public int getAlto() { return alto; }

    public Entidad obtenerEntidad(int x, int y) {
        if (estaDentroDelMapa(x, y)) return grilla[y][x];
        return null;
    }

    public Bloque obtenerBloque(int x, int y) {
        if (estaDentroDelMapa(x, y)) return bloques[y][x];
        return null;
    }

    public boolean estaDentroDelMapa(int x, int y) {
        return x >= 0 && y >= 0 && x < ancho && y < alto;
    }

    public boolean estaLibre(int x, int y) {
        if (!estaDentroDelMapa(x, y)) return false;

        Entidad entidad = grilla[y][x];
        boolean entidadLibre = entidad == null || entidad.esPowerUp();
        Bloque bloque = bloques[y][x];
        boolean bloqueLibre = bloque == null || bloque.getTipo().esPasablePorTanque();

        return entidadLibre && bloqueLibre;
    }

    public void agregarEntidad(Entidad e) {
        if (estaDentroDelMapa(e.getX(), e.getY())) grilla[e.getY()][e.getX()] = e;
    }

    public void eliminarEntidad(int x, int y) {
        if (estaDentroDelMapa(x, y)) grilla[y][x] = null;
    }

    public void agregarBloque(TipoBloque tipo, int x, int y) {
        if (estaDentroDelMapa(x, y)) bloques[y][x] = new Bloque(tipo, x, y);
    }

    public void eliminarBloque(int x, int y) {
        if (estaDentroDelMapa(x, y)) bloques[y][x] = null;
    }

    public void limpiarEntidadesMoviles() {
        for (int y = 0; y < alto; y++) {
            for (int x = 0; x < ancho; x++) {
                grilla[y][x] = null;
                Bloque bloque = bloques[y][x];
                if (bloque != null && bloque.getTipo() == TipoBloque.TANQUE_DESTRUIDO) {
                    bloques[y][x] = null;
                }
            }
        }
    }

    public void limpiarCompletamente() {
        for (int y = 0; y < alto; y++) {
            for (int x = 0; x < ancho; x++) {
                grilla[y][x] = null;
                bloques[y][x] = null;
            }
        }
    }
}