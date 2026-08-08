package modelo.mapa;

import modelo.entidades.Entidad;

public interface SpawnTarget {
    boolean estaLibre(int x, int y);
    int getAncho();
    int getAlto();
    void agregarEntidad(Entidad entidad);
}