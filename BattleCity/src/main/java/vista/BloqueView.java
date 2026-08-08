package vista;


public class BloqueView extends EntidadView {
    public BloqueView(int x, int y, String spritePath) {
        super(spritePath);
        actualizar(x, y, null, true);
    }

    @Override
    protected void actualizarVisual() {
        imageView.setLayoutX(x * TAMANO_CELDA);
        imageView.setLayoutY(y * TAMANO_CELDA);
        imageView.setVisible(vivo);
    }
}