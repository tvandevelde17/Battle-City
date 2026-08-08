package vista;

import modelo.util.Direccion;
import modelo.util.TipoPowerUp  ;

public class PowerUpView extends EntidadView {
    private final TipoPowerUp tipo;
    private static final int TAMANO_CELDA = 20;

    public PowerUpView(int x, int y, TipoPowerUp tipo) {
        super(obtenerSpritePath(tipo));
        this.x = x;
        this.y = y;
        this.tipo = tipo;
        this.vivo = true;
        actualizarVisual();
    }

    private static String obtenerSpritePath(TipoPowerUp tipo) {
        switch (tipo) {
            case GRANADA: return "/YABC-Assets/sprites/PowerUp-Grenade20x20.png";
            case CASCO: return "/YABC-Assets/sprites/PowerUp-Helmet20x20.png";
            case ESTRELLA: return "/YABC-Assets/sprites/PowerUp-Star20x20.png";
            default: return "/YABC-Assets/sprites/PowerUpGranada.png"; // default
        }
    }

    public void actualizar(int x, int y, boolean activo) {
        this.x = x;
        this.y = y;
        this.vivo = activo;
        actualizarVisual();
    }

    @Override
    protected void actualizarVisual() {
        if (vivo) {
            imageView.setLayoutX(x * TAMANO_CELDA);
            imageView.setLayoutY(y * TAMANO_CELDA);
            imageView.setVisible(true);
            imageView.setOpacity(opacidadNormal);
        } else {
            imageView.setVisible(false);
        }
    }
    @Override
    protected double calcularRotacion(Direccion dir) {
        return 0; // Los power-ups no rotan
    }
}