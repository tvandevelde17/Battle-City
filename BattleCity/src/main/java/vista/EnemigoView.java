package vista;

import javafx.scene.image.Image;
import modelo.util.Direccion;
import modelo.util.TipoEnemigo;

import java.io.InputStream;

public class EnemigoView extends EntidadView {
    private final TipoEnemigo tipo;
    private boolean animacionFrame = false;
    private long ultimoCambioAnimacion = 0;
    private static final long INTERVALO_ANIMACION = 200;
    private boolean mostrandoDestruccion = false;
    private long tiempoDestruccion = 0;

    public EnemigoView(int x, int y, Direccion direccion, TipoEnemigo tipo) {
        super(getSpritePath(tipo, false));
        this.tipo = tipo;
        actualizar(x, y, direccion, true);
    }
    public TipoEnemigo getTipo() {
        return tipo;
    }

    @Override
    public void actualizar(int x, int y, Direccion direccion, boolean vivo) {
        if (!vivo && !mostrandoDestruccion) {
            mostrarDestruccion();
            return;
        }

        if (vivo) {
            mostrandoDestruccion = false;
            super.actualizar(x, y, direccion, true);
            actualizarAnimacion();
        }
    }

    private void actualizarAnimacion() {
        long ahora = System.currentTimeMillis();
        if (ahora - ultimoCambioAnimacion >= INTERVALO_ANIMACION) {
            animacionFrame = !animacionFrame;
            String spritePath = getSpritePath(tipo, animacionFrame);
            actualizarSprite(spritePath);
            ultimoCambioAnimacion = ahora;
        }
    }

    public void mostrarDestruccion() {
        if (!mostrandoDestruccion) {
            actualizarSprite("/YABC-Assets/sprites/TankDestroyed_20x20.png");
            imageView.setVisible(true);
            mostrandoDestruccion = true;
            tiempoDestruccion = System.currentTimeMillis();
        }
    }

    public boolean debeEliminarse() {
        return mostrandoDestruccion && (System.currentTimeMillis() - tiempoDestruccion) > 1000;
    }

    private void actualizarSprite(String spritePath) {
        try {
            InputStream resourceStream = getClass().getResourceAsStream(spritePath);
            if (resourceStream != null) {
                Image nuevaImagen = new Image(resourceStream);
                imageView.setImage(nuevaImagen);
            } else {
                // Sprite de emergencia
                InputStream emergencyStream = getClass().getResourceAsStream("/YABC-Assets/sprites/EnemyTankRegular0_20x20.png");
                if (emergencyStream != null) {
                    imageView.setImage(new Image(emergencyStream));
                }
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

    private static String getSpritePath(TipoEnemigo tipo, boolean frameAlternativo) {
        String tipoStr = "";
        switch (tipo) {
            case RAPIDO:
                tipoStr = "Fast";
                break;
            case BLINDADO:
                tipoStr = "Heavy";
                break;
            case POTENTE:
                tipoStr = "Powerful";
                break;
            case BASICO:
            default:
                tipoStr = "Regular";
                break;
        }
        int frame = frameAlternativo ? 1 : 0;
        String path = "/YABC-Assets/sprites/EnemyTank" + tipoStr + frame + "_20x20.png";
        return path;
    }
}