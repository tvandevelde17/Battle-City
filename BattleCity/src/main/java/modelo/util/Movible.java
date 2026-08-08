package modelo.util;
public interface Movible {
    Direccion getDireccion();
    int getX();
    int getY();

    default int[] calcularNuevaPosicion(int x, int y, Direccion direccion) {
        int nuevaX = x;
        int nuevaY = y;


        switch(direccion) {
            case ARRIBA -> nuevaY -= 1;
            case ABAJO -> nuevaY += 1;
            case IZQUIERDA -> nuevaX -= 1;
            case DERECHA -> nuevaX += 1;
        }

        return new int[]{nuevaX, nuevaY};
    }
}