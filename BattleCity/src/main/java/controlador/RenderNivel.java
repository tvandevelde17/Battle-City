package controlador;


import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import modelo.entidades.*;
import modelo.mapa.Mapa;
import modelo.niveles.Nivel;
import modelo.util.Direccion;
import modelo.util.TipoBloque;
import vista.*;
import static constantes.Constantes.*;
import java.util.*;

public class RenderNivel {

    // ==================== COMPONENTES DE VISUALIZACIÓN ====================
    private AnchorPane pane;
    private final Nivel nivel;
    private PowerUpView powerUpView;

    // ==================== VISTAS DE ELEMENTOS DEL JUEGO ====================
    private final Map<Bloque, BloqueView> vistasBloques = new HashMap<>();
    private BaseView baseView;
    private final List<JugadorView> vistasJugadores = new ArrayList<>();
    private final List<EnemigoView> vistasEnemigos = new ArrayList<>();
    private final Map<Bala, BalaView> mapaBalas = new HashMap<>();
    private final List<ImageView> anillosActivos = new ArrayList<>();

    // ==================== CONSTRUCTOR ====================

    public RenderNivel(Nivel nivel) {
        this.nivel = nivel;
        crearVistaCompletamenteNueva(nivel);
    }

    // ==================== INICIALIZACIÓN DE VISTAS ====================

    private void crearVistaCompletamenteNueva(Nivel nivel) {
        pane = new AnchorPane();
        Mapa mapa = nivel.getMapa();
        pane.setPrefSize(mapa.getAncho() * TAMANO_CELDA, mapa.getAlto() * TAMANO_CELDA);
        pane.setStyle("-fx-background-color: black;");

        vistasBloques.clear();
        vistasJugadores.clear();
        vistasEnemigos.clear();
        mapaBalas.clear();
        anillosActivos.clear();

        crearVistasBloques(mapa);
        crearVistasJugadores(nivel);
        crearVistasEnemigos(nivel);
        crearVistaBase(nivel);
    }

    private void crearVistasBloques(Mapa mapa) {
        agregarBloqueVista(mapa);
    }

    private void crearVistaBloque(Bloque bloque) {
        String sprite = obtenerSpriteParaBloque(bloque);
        BloqueView bv = new BloqueView(bloque.getX(), bloque.getY(), "/YABC-Assets/sprites/" + sprite);
        vistasBloques.put(bloque, bv);
        pane.getChildren().add(bv.getImageView());
    }

    private String obtenerSpriteParaBloque(Bloque bloque) {
        return switch (bloque.getTipo()) {
            case BOSQUE -> "ForestBlock20x20.png";
            case ACERO -> "SteelBlock20x20.png";
            case LADRILLO -> "BrickBlock20x20.png";
            case AGUA -> "WaterBlock20x20.png";
            case TANQUE_DESTRUIDO -> "TankDestroyed_20x20.png";
            default -> "BrickBlock20x20.png";
        };
    }

    private void crearVistasJugadores(Nivel nivel) {
        int id = 1;
        for (Jugador j : nivel.getJugadores()) {
            JugadorView jv = new JugadorView(j.getX(), j.getY(), j.getDireccion(), id);
            vistasJugadores.add(jv);
            pane.getChildren().add(jv.getImageView());

            ImageView anillo = jv.getAnilloImageView();
            if (anillo != null) {
                pane.getChildren().add(anillo);
                anillosActivos.add(anillo);
            }

            id++;
        }
    }

    private void crearVistasEnemigos(Nivel nivel) {
        for (Enemigo e : nivel.getEnemigos()) {
            EnemigoView ev = new EnemigoView(e.getX(), e.getY(), e.getDireccion(), e.getTipo());
            vistasEnemigos.add(ev);
            pane.getChildren().add(ev.getImageView());
        }
    }

    private void crearVistaBase(Nivel nivel) {
        Base base = nivel.getBase();
        if (base != null) {
            baseView = new BaseView(base.getX(), base.getY(), "/YABC-Assets/sprites/base20x20.png");
            pane.getChildren().add(baseView.getImageView());
        }
    }

    // ==================== ACTUALIZACIÓN PRINCIPAL ====================

    public void actualizar() {
        sincronizarConModelo();
        sincronizarVistasEnemigos();
        sincronizarVistasJugadores();
        actualizarJugadores();
        actualizarEnemigos();
        actualizarBloques();
        actualizarBase();
        actualizarBalas();
        actualizarCascosJugadores();
        actualizarPowerUp();
    }

    // ==================== SINCRONIZACIÓN CON MODELO ====================

    private void sincronizarConModelo() {
        sincronizarTanquesDestruidos();
        sincronizarBloques();
    }

    private void sincronizarTanquesDestruidos() {
        Set<String> posicionesTanquesDestruidosModelo = new HashSet<>();

        for (int y = 0; y < nivel.getMapa().getAlto(); y++) {
            for (int x = 0; x < nivel.getMapa().getAncho(); x++) {
                Bloque bloque = nivel.getMapa().obtenerBloque(x, y);
                if (bloque != null && bloque.getTipo() == TipoBloque.TANQUE_DESTRUIDO) {
                    posicionesTanquesDestruidosModelo.add(x + "," + y);
                }
            }
        }

        Iterator<Map.Entry<Bloque, BloqueView>> it = vistasBloques.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<Bloque, BloqueView> entry = it.next();
            Bloque bloque = entry.getKey();

            if (bloque.getTipo() == TipoBloque.TANQUE_DESTRUIDO) {
                String pos = bloque.getX() + "," + bloque.getY();
                if (!posicionesTanquesDestruidosModelo.contains(pos)) {
                    pane.getChildren().remove(entry.getValue().getImageView());
                    it.remove();
                }
            }
        }
    }

    private void sincronizarBloques() {
        Iterator<Map.Entry<Bloque, BloqueView>> it = vistasBloques.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<Bloque, BloqueView> entry = it.next();
            Bloque bloqueVista = entry.getKey();
            Bloque bloqueModelo = nivel.getMapa().obtenerBloque(bloqueVista.getX(), bloqueVista.getY());

            if (bloqueModelo == null || bloqueModelo != bloqueVista) {
                pane.getChildren().remove(entry.getValue().getImageView());
                it.remove();
            }
        }
    }

    private void sincronizarVistasJugadores() {
        List<Jugador> jugadoresModelo = nivel.getJugadores();

        while (vistasJugadores.size() < jugadoresModelo.size()) {
            int id = vistasJugadores.size() + 1;
            JugadorView nuevaVista = new JugadorView(0, 0, Direccion.ARRIBA, id);
            vistasJugadores.add(nuevaVista);
            pane.getChildren().add(nuevaVista.getImageView());
        }

        for (int i = 0; i < jugadoresModelo.size(); i++) {
            Jugador jugador = jugadoresModelo.get(i);
            if (i < vistasJugadores.size()) {
                JugadorView jv = vistasJugadores.get(i);
                if (jugador.estaVivo()) {
                    jv.resetear();
                }
            }
        }
    }

    private void sincronizarVistasEnemigos() {
        List<Enemigo> enemigosModelo = nivel.getEnemigos();

        for (Enemigo enemigo : enemigosModelo) {
            if (!enemigo.estaVivo()) continue;

            boolean existeVista = vistasEnemigos.stream()
                    .anyMatch(v -> v.getX() == enemigo.getX() && v.getY() == enemigo.getY());

            if (!existeVista) {
                EnemigoView nuevaVista = new EnemigoView(
                        enemigo.getX(), enemigo.getY(),
                        enemigo.getDireccion(), enemigo.getTipo()
                );
                vistasEnemigos.add(nuevaVista);
                pane.getChildren().add(nuevaVista.getImageView());
            }
        }

        List<EnemigoView> vistasParaEliminar = new ArrayList<>();
        for (EnemigoView vista : vistasEnemigos) {
            boolean enemigoExiste = enemigosModelo.stream().anyMatch(e ->
                    e.estaVivo() && e.getX() == vista.getX() && e.getY() == vista.getY());
            if (!enemigoExiste && vista.debeEliminarse()) {
                vistasParaEliminar.add(vista);
            }
        }

        vistasParaEliminar.forEach(v -> {
            pane.getChildren().remove(v.getImageView());
            vistasEnemigos.remove(v);
        });
    }

    // ==================== ACTUALIZACIÓN DE ELEMENTOS ====================

    private void actualizarJugadores() {
        List<Jugador> jugadoresModelo = nivel.getJugadores();

        for (int i = 0; i < jugadoresModelo.size(); i++) {
            Jugador jugador = jugadoresModelo.get(i);

            if (i < vistasJugadores.size()) {
                JugadorView jv = vistasJugadores.get(i);

                if (jugador.estaVivo()) {
                    jv.actualizar(jugador.getX(), jugador.getY(), jugador.getDireccion(), true);
                    jv.mostrarAnilloInvulnerabilidad(jugador.isCascoActivo());

                    if (jugador.isCascoActivo()) {
                        ImageView anillo = jv.getAnilloImageView();
                        if (anillo != null) {
                            anillo.setLayoutX(jugador.getX() * TAMANO_CELDA);
                            anillo.setLayoutY(jugador.getY() * TAMANO_CELDA);
                            anillo.toFront();
                        }
                    }

                    aplicarEfectoBosque(jugador, jv);
                } else {
                    jv.mostrarDestruccion();
                    jv.mostrarAnilloInvulnerabilidad(false);
                }
            }
        }
    }

    private void actualizarEnemigos() {
        List<Enemigo> enemigosModelo = nivel.getEnemigos();
        List<EnemigoView> vistasParaEliminar = new ArrayList<>();

        for (EnemigoView vista : vistasEnemigos) {
            boolean enemigoExiste = enemigosModelo.stream().anyMatch(e ->
                    e.estaVivo() && e.getX() == vista.getX() && e.getY() == vista.getY());
            if (!enemigoExiste) {
                vistasParaEliminar.add(vista);
            }
        }

        vistasParaEliminar.forEach(v -> {
            pane.getChildren().remove(v.getImageView());
            vistasEnemigos.remove(v);
        });

        for (Enemigo enemigo : enemigosModelo) {
            if (!enemigo.estaVivo()) continue;

            EnemigoView vistaExistente = vistasEnemigos.stream()
                    .filter(v -> v.getX() == enemigo.getX() && v.getY() == enemigo.getY())
                    .findFirst()
                    .orElseGet(() -> {
                        EnemigoView nuevaVista = new EnemigoView(
                                enemigo.getX(), enemigo.getY(),
                                enemigo.getDireccion(), enemigo.getTipo()
                        );
                        vistasEnemigos.add(nuevaVista);
                        pane.getChildren().add(nuevaVista.getImageView());
                        return nuevaVista;
                    });

            vistaExistente.actualizar(enemigo.getX(), enemigo.getY(), enemigo.getDireccion(), true);
            aplicarEfectoBosque(enemigo, vistaExistente);
        }
    }

    private void actualizarBalas() {
        List<Bala> balasModelo = nivel.getBalas();
        List<Bala> balasParaEliminar = new ArrayList<>();

        for (Bala bala : mapaBalas.keySet()) {
            if (!balasModelo.contains(bala) || !bala.estaVivo()) {
                balasParaEliminar.add(bala);
            }
        }

        for (Bala bala : balasParaEliminar) {
            BalaView vista = mapaBalas.remove(bala);
            if (vista != null) pane.getChildren().remove(vista.getImageView());
        }

        for (Bala bala : balasModelo) {
            mapaBalas.computeIfAbsent(bala, b -> {
                BalaView vista = new BalaView("/YABC-Assets/sprites/Shot.png");
                pane.getChildren().add(vista.getImageView());
                return vista;
            }).actualizar(bala.getX(), bala.getY(), bala.getDireccion(), bala.estaVivo());
        }
    }

    private void actualizarBloques() {
        Mapa mapa = nivel.getMapa();

        Iterator<Map.Entry<Bloque, BloqueView>> it = vistasBloques.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<Bloque, BloqueView> entry = it.next();
            Bloque bloque = entry.getKey();

            Bloque bloqueActual = mapa.obtenerBloque(bloque.getX(), bloque.getY());
            boolean bloqueYaNoExiste = bloqueActual != bloque;
            boolean esTanqueDestruido = bloque.getTipo() == TipoBloque.TANQUE_DESTRUIDO;

            if (bloqueYaNoExiste || esTanqueDestruido) {
                pane.getChildren().remove(entry.getValue().getImageView());
                it.remove();
            }
        }

        agregarBloqueVista(mapa);
    }

    private void actualizarBase() {
        if (baseView != null && nivel.getBase() != null) {
            Base base = nivel.getBase();
            baseView.actualizar(base.getX(), base.getY(), base.estaVivo());
        }
    }

    private void actualizarPowerUp() {
        PowerUp powerUp = nivel.getPowerUpActual();

        if (powerUp != null && powerUp.estaActivo()) {
            if (powerUpView == null) {
                powerUpView = new PowerUpView(powerUp.getX(), powerUp.getY(), powerUp.getTipo());
                pane.getChildren().add(powerUpView.getImageView());
            } else {
                powerUpView.actualizar(powerUp.getX(), powerUp.getY(), powerUp.estaActivo());
            }
        } else if (powerUpView != null) {
            pane.getChildren().remove(powerUpView.getImageView());
            powerUpView = null;
        }
    }

    private void actualizarCascosJugadores() {
        for (Jugador jugador : nivel.getJugadores()) {
            jugador.actualizarCasco();
        }
    }

    // ==================== AUXILIARES ====================

    private void aplicarEfectoBosque(Entidad entidad, EntidadView vista) {
        Bloque bloque = nivel.getMapa().obtenerBloque(entidad.getX(), entidad.getY());
        vista.setOpacidad((bloque != null && bloque.getTipo() == TipoBloque.BOSQUE) ? 0.4 : 1.0);
    }

    private void agregarBloqueVista(Mapa mapa) {
        for (int y = 0; y < mapa.getAlto(); y++) {
            for (int x = 0; x < mapa.getAncho(); x++) {
                Bloque b = mapa.obtenerBloque(x, y);
                if (b != null && !vistasBloques.containsKey(b)) {
                    crearVistaBloque(b);
                }
            }
        }
    }
    public void limpiarCompletamente() {
        pane.getChildren().clear();
        vistasBloques.clear();
        vistasJugadores.clear();
        vistasEnemigos.clear();
        mapaBalas.clear();
        anillosActivos.clear();
    }

    // ==================== GETTERS ====================

    public AnchorPane getPane() {
        return pane;
    }
}
