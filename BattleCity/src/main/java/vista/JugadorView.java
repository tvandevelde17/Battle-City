package vista;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import modelo.util.Direccion;

import java.io.InputStream;
import java.util.Objects;

public class JugadorView extends EntidadView {
    private final int idJugador;
    private boolean animacionFrame = false;
    private long ultimoCambioAnimacion = 0;
    private static final long INTERVALO_ANIMACION = 200;
    private boolean mostrandoDestruccion = false;
    private long tiempoDestruccion = 0;

    private ImageView anilloInvulnerabilidad;
    private boolean mostrandoAnillo = false;

    public JugadorView(int x, int y, Direccion direccion, int idJugador) {
        super("/YABC-Assets/sprites/Player" + idJugador + "Tank0_20x20.png");
        this.idJugador = idJugador;
        inicializarAnilloInvulnerabilidad();
        actualizar(x, y, direccion, true);
    }
    private void inicializarAnilloInvulnerabilidad() {
        try {
            InputStream stream = getClass().getResourceAsStream("/YABC-Assets/sprites/InvulnerableRing20x20.png");
            if (stream == null) {
                crearAnilloFallback();
                return;
            }

            Image imagenAnillo = new Image(stream);
            anilloInvulnerabilidad = new ImageView(imagenAnillo);
            anilloInvulnerabilidad.setFitWidth(TAMANO_CELDA);
            anilloInvulnerabilidad.setFitHeight(TAMANO_CELDA);
            anilloInvulnerabilidad.setVisible(false);
            anilloInvulnerabilidad.setOpacity(0.7);
            anilloInvulnerabilidad.setMouseTransparent(true);

        } catch (Exception e) {
            crearAnilloFallback();
        }
    }
    private void crearAnilloFallback() {
        anilloInvulnerabilidad = new ImageView();
        anilloInvulnerabilidad.setFitWidth(TAMANO_CELDA);
        anilloInvulnerabilidad.setFitHeight(TAMANO_CELDA);
        anilloInvulnerabilidad.setVisible(false);
        anilloInvulnerabilidad.setMouseTransparent(true);
    }

    @Override
    public void actualizar(int x, int y, Direccion direccion, boolean vivo) {
        this.x = x;
        this.y = y;
        this.direccion = direccion;

        if (!vivo && !mostrandoDestruccion) {
            mostrarDestruccion();
            return;
        }

        if (vivo) {
            mostrandoDestruccion = false;
            actualizarVisual();
            actualizarAnimacion();
            actualizarPosicionAnillo();
        }
    }

    private void actualizarAnimacion() {
        long ahora = System.currentTimeMillis();
        if (ahora - ultimoCambioAnimacion >= INTERVALO_ANIMACION) {
            animacionFrame = !animacionFrame;
            String spritePath = "/YABC-Assets/sprites/Player" + idJugador +
                    "Tank" + (animacionFrame ? "1" : "0") + "_20x20.png";
            actualizarSprite(spritePath);
            ultimoCambioAnimacion = ahora;
        }
    }

    public void mostrarAnilloInvulnerabilidad(boolean mostrar) {
        if (anilloInvulnerabilidad == null) {
            inicializarAnilloInvulnerabilidad();
        }

        if (anilloInvulnerabilidad != null) {
            boolean cambio = (mostrar != mostrandoAnillo);
            mostrandoAnillo = mostrar;
            anilloInvulnerabilidad.setVisible(mostrar);

            if (cambio) {
                if (mostrar) {
                    actualizarPosicionAnillo();
                    anilloInvulnerabilidad.toFront();
                }
            }
        }
    }

    private void actualizarPosicionAnillo() {
        if (anilloInvulnerabilidad != null && mostrandoAnillo) {
            anilloInvulnerabilidad.setLayoutX(x * TAMANO_CELDA);
            anilloInvulnerabilidad.setLayoutY(y * TAMANO_CELDA);
            anilloInvulnerabilidad.toFront(); // ✅ SIEMPRE AL FRENTE
        }
    }

    public ImageView getAnilloImageView() {
        if (anilloInvulnerabilidad == null) {
            inicializarAnilloInvulnerabilidad();
        }
        return anilloInvulnerabilidad;
    }

    @Override
    protected void actualizarVisual() {
        if (vivo) {
            // Actualizar tanque
            imageView.setLayoutX(x * TAMANO_CELDA);
            imageView.setLayoutY(y * TAMANO_CELDA);
            imageView.setRotate(calcularRotacion(direccion));
            imageView.setVisible(true);
            imageView.setOpacity(opacidadNormal);

            if (mostrandoAnillo && anilloInvulnerabilidad != null) {
                anilloInvulnerabilidad.setLayoutX(x * TAMANO_CELDA);
                anilloInvulnerabilidad.setLayoutY(y * TAMANO_CELDA);
                anilloInvulnerabilidad.setVisible(true);
                anilloInvulnerabilidad.toFront();
            }
        } else {
            imageView.setVisible(false);
            if (anilloInvulnerabilidad != null) {
                anilloInvulnerabilidad.setVisible(false);
            }
        }
    }

    public void mostrarDestruccion() {
        if (!mostrandoDestruccion) {
            actualizarSprite("/YABC-Assets/sprites/TankDestroyed_20x20.png");
            imageView.setVisible(true);
            if (anilloInvulnerabilidad != null) {
                anilloInvulnerabilidad.setVisible(false);
                mostrandoAnillo = false;
            }
            mostrandoDestruccion = true;
            tiempoDestruccion = System.currentTimeMillis();
        }
    }

    private void actualizarSprite(String spritePath) {
        try {
            Image nuevaImagen = new Image(Objects.requireNonNull(getClass().getResourceAsStream(spritePath)));
            imageView.setImage(nuevaImagen);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public boolean debeEliminarse() {
        return mostrandoDestruccion && (System.currentTimeMillis() - tiempoDestruccion) > 1000;
    }

    public void resetear() {
        mostrandoDestruccion = false;
        imageView.setVisible(true);
        if (anilloInvulnerabilidad != null) {
            anilloInvulnerabilidad.setVisible(false);
            mostrandoAnillo = false;
        }
    }
}