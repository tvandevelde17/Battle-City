package vista;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.util.Objects;

import static constantes.Constantes.*;


public class BaseView {
    private final ImageView imageView;
    private int x;
    private int y;
    private boolean visible;

    public BaseView(int x, int y, String spritePath) {
        this.x = x;
        this.y = y;
        this.imageView = new ImageView(new Image(Objects.requireNonNull(getClass().getResourceAsStream(spritePath))));
        imageView.setFitWidth(TAMANO_CELDA);
        imageView.setFitHeight(TAMANO_CELDA);
        this.visible = true;
        actualizarVisual();
    }

    public void actualizar(int x, int y, boolean visible) {
        this.x = x;
        this.y = y;
        this.visible = visible;
        actualizarVisual();
    }

    private void actualizarVisual() {
        if (visible) {
            imageView.setLayoutX(x * TAMANO_CELDA);
            imageView.setLayoutY(y * TAMANO_CELDA);
            imageView.setVisible(true);
        } else {
            imageView.setVisible(false);
        }
    }

    public ImageView getImageView() {
        return imageView;
    }
}

