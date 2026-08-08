package vista;

public class BalaView extends EntidadView {
    public BalaView(String spritePath) {
        super(spritePath);
    }

    @Override
    protected void actualizarVisual() {
        // Las balas no necesitan animación compleja
        imageView.setLayoutX(x * TAMANO_CELDA);
        imageView.setLayoutY(y * TAMANO_CELDA);
        imageView.setRotate(calcularRotacion(direccion));
        imageView.setVisible(vivo);
    }
}