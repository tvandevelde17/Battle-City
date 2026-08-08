package controlador;

import modelo.entidades.*;
import modelo.niveles.Nivel;
import modelo.mapa.Mapa;
import modelo.util.ModoJuego;
import modelo.util.TipoBloque;
import modelo.util.TipoEnemigo;

import org.w3c.dom.*;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class LevelLoader {
    private static final int TAMANO_CELDA = 20;

    public static Nivel cargarNivel(String xmlPath, ModoJuego modo) {
        try {
            InputStream is = LevelLoader.class.getResourceAsStream(xmlPath);
            if (is == null) {
                throw new RuntimeException("No se pudo encontrar el archivo: " + xmlPath);
            }

            Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(is);
            doc.getDocumentElement().normalize();

            Element levelElem = (Element) doc.getElementsByTagName("level").item(0);
            int cols = Integer.parseInt(levelElem.getAttribute("cols"));
            int rows = Integer.parseInt(levelElem.getAttribute("rows"));

            // ✅ CREAR UN NUEVO MAPA PARA CADA NIVEL
            Mapa mapa = new Mapa(cols, rows);

            Base base = null;
            boolean baseEncontrada = false;

            // ---- Cargar bloques estáticos ----
            NodeList staticObjects = levelElem.getElementsByTagName("staticObject");
            for (int i = 0; i < staticObjects.getLength(); i++) {
                Element s = (Element) staticObjects.item(i);
                String type = s.getAttribute("type").toLowerCase();
                int x = Integer.parseInt(s.getAttribute("x")) / TAMANO_CELDA;
                int y = Integer.parseInt(s.getAttribute("y")) / TAMANO_CELDA;

                switch (type) {
                    case "brickblock" -> mapa.agregarBloque(TipoBloque.LADRILLO, x, y);
                    case "steelblock" -> mapa.agregarBloque(TipoBloque.ACERO, x, y);
                    case "forestblock" -> mapa.agregarBloque(TipoBloque.BOSQUE, x, y);
                    case "waterblock" -> mapa.agregarBloque(TipoBloque.AGUA, x, y);
                    case "baseblock" -> {
                        base = new Base(x, y);
                        mapa.agregarEntidad(base);
                        baseEncontrada = true;
                    }
                }
            }

            if (!baseEncontrada) {
                throw new RuntimeException("No se encontró la base en el nivel: " + xmlPath);
            }

            // ---- Cargar jugadores ----
            NodeList players = levelElem.getElementsByTagName("player");
            int[][] posicionesSpawnJugadores = new int[players.getLength()][2];
            for (int i = 0; i < players.getLength(); i++) {
                Element p = (Element) players.item(i);
                int x = Integer.parseInt(p.getAttribute("x")) / TAMANO_CELDA;
                int y = Integer.parseInt(p.getAttribute("y")) / TAMANO_CELDA;
                posicionesSpawnJugadores[i][0] = x;
                posicionesSpawnJugadores[i][1] = y;
            }

            // ---- Cargar enemigos ----
            NodeList enemies = levelElem.getElementsByTagName("enemy");
            List<TipoEnemigo> tiposEnemigos = new ArrayList<>();
            for (int i = 0; i < enemies.getLength(); i++) {
                Element e = (Element) enemies.item(i);
                String type = e.getAttribute("type").toLowerCase();
                TipoEnemigo tipoEnemigo = switch (type) {
                    case "regularenemy" -> TipoEnemigo.BASICO;
                    case "fastenemy" -> TipoEnemigo.RAPIDO;
                    case "powerfulenemy" -> TipoEnemigo.POTENTE;
                    case "heavyenemy" -> TipoEnemigo.BLINDADO;
                    default -> throw new IllegalStateException("Tipo de enemigo desconocido: " + type);
                };
                tiposEnemigos.add(tipoEnemigo);
            }


            return new Nivel(
                    mapa,  // ← Este es un NUEVO mapa para este nivel
                    base,
                    posicionesSpawnJugadores,
                    tiposEnemigos,
                    10,
                    60,
                    enemies.getLength(),
                    modo
            );

        } catch (Exception ex) {
            ex.printStackTrace();
            throw new RuntimeException("Error cargando nivel desde XML: " + xmlPath + " - " + ex.getMessage());
        }
    }


}
