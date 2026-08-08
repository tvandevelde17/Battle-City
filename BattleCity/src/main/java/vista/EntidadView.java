package vista;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import modelo.util.Direccion;

import java.util.Objects;

public abstract class EntidadView {
    protected static final int TAMANO_CELDA = 20;
    protected ImageView imageView;
    protected int x;
    protected int y;
    protected Direccion direccion;
    protected boolean vivo;
    protected double opacidadNormal;

    public EntidadView(String spritePath) {
        cargarSprite(spritePath);
        this.vivo = true;
        this.opacidadNormal = 1.0;
    }

    protected void cargarSprite(String spritePath) {
        try {
            Image image = new Image(Objects.requireNonNull(getClass().getResourceAsStream(spritePath)));
            this.imageView = new ImageView(image);
            this.imageView.setFitWidth(TAMANO_CELDA);
            this.imageView.setFitHeight(TAMANO_CELDA);
        } catch (Exception e) {
            this.imageView = new ImageView();
            this.imageView.setStyle("-fx-background-color: red; -fx-border-color: black;");
            this.imageView.setFitWidth(TAMANO_CELDA);
            this.imageView.setFitHeight(TAMANO_CELDA);
        }
    }

    public void actualizar(int x, int y, Direccion direccion, boolean vivo) {
        this.x = x;
        this.y = y;
        this.direccion = direccion;
        this.vivo = vivo;
        actualizarVisual();
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    protected void actualizarVisual() {
        if (vivo) {
            imageView.setLayoutX(x * TAMANO_CELDA);
            imageView.setLayoutY(y * TAMANO_CELDA);
            imageView.setRotate(calcularRotacion(direccion));
            imageView.setVisible(true);
            imageView.setOpacity(opacidadNormal);
        } else {
            imageView.setVisible(false);
        }
    }

    protected double calcularRotacion(Direccion dir) {
        if (dir == null) return 0;
        return switch (dir) {
            case ARRIBA -> 0;
            case DERECHA -> 90;
            case ABAJO -> 180;
            case IZQUIERDA -> 270;
        };
    }

    public void setOpacidad(double opacidad) {
        this.opacidadNormal = opacidad;
        imageView.setOpacity(opacidad);
    }

    public ImageView getImageView() {
        return imageView;
    }


    public void mostrarDestruccion() {
        this.vivo = false;
        actualizarVisual();
    }

    public boolean debeEliminarse() {
        return !vivo;
    }
}
