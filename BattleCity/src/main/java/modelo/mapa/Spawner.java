package modelo.mapa;

import modelo.entidades.Enemigo;
import modelo.util.TipoEnemigo;

import java.util.*;

public class Spawner {
    private final SpawnTarget target;
    private final int maxEnemigosActivos;
    private final long ventanaSpawnMs;
    private final int maxTotalEnemigos;
    private final Random rand;

    private int enemigosActivos = 0;
    private int totalEnemigosSpawned = 0;
    private boolean spawningCompletado = false;
    private long inicioVentana;

    private int enemigosIniciales;
    private int enemigosRestantes;
    private boolean enemigosInicialesSpawned = false;

    public Spawner(SpawnTarget target, int maxEnemigosActivos, long ventanaSpawnMs, int maxTotalEnemigos) {
        this.target = target;
        this.maxEnemigosActivos = maxEnemigosActivos;
        this.ventanaSpawnMs = ventanaSpawnMs;
        this.maxTotalEnemigos = maxTotalEnemigos;
        this.rand = new Random();
        this.inicioVentana = System.currentTimeMillis();
        this.enemigosRestantes = maxTotalEnemigos;

        // Calcular enemigos iniciales (mínimo entre 2 y el total disponible)
        this.enemigosIniciales = Math.min(2, maxTotalEnemigos);
    }

    public void reiniciar() {
        enemigosActivos = 0;
        totalEnemigosSpawned = 0;
        spawningCompletado = false;
        enemigosInicialesSpawned = false;
        inicioVentana = System.currentTimeMillis();
        enemigosRestantes = maxTotalEnemigos;

        // Recalcular enemigos iniciales
        this.enemigosIniciales = Math.min(2, maxTotalEnemigos);
    }

    public Enemigo spawnEnemigo(List<TipoEnemigo> tipos) {
        long ahora = System.currentTimeMillis();

        // Verificar límites
        if (spawningCompletado) {
            return null;
        }
        if (tipos == null || tipos.isEmpty()) {
            return null;
        }

        if (ahora - inicioVentana > ventanaSpawnMs && enemigosRestantes <= 0) {
            spawningCompletado = true;
            return null;
        }

        // Verificar límite total de enemigos
        if (totalEnemigosSpawned >= maxTotalEnemigos) {
            spawningCompletado = true;
            return null;
        }

        // Verificar si todavía hay enemigos por spawnear
        if (enemigosRestantes <= 0) {
            spawningCompletado = true;
            return null;
        }

        // Verificar límite de enemigos simultáneos - PERMITIR SPAWN SI HAY MENOS DEL MÁXIMO
        if (enemigosActivos >= maxEnemigosActivos) {
            // No spawnear ahora, pero no marcar como completado
            return null;
        }


        TipoEnemigo tipo = tipos.get(rand.nextInt(tipos.size()));


        List<int[]> posicionesPosibles = new ArrayList<>();


        for (int y = 0; y < 3; y++) {
            for (int x = 0; x < target.getAncho(); x++) {
                if (target.estaLibre(x, y)) {
                    posicionesPosibles.add(new int[]{x, y});
                }
            }
        }
        if (!posicionesPosibles.isEmpty()) {
            int[] posicion = posicionesPosibles.get(rand.nextInt(posicionesPosibles.size()));
            int x = posicion[0];
            int y = posicion[1];

            Enemigo enemigo = new Enemigo(x, y, tipo);
            target.agregarEntidad(enemigo);
            enemigosActivos++;
            totalEnemigosSpawned++;
            enemigosRestantes--;
            return enemigo;
        } else {
            return null;
        }
    }
    public List<Enemigo> spawnEnemigosIniciales(List<TipoEnemigo> tipos) {
        if (enemigosInicialesSpawned || tipos == null || tipos.isEmpty()) {
            return new ArrayList<>();
        }

        List<Enemigo> enemigosInicialesList = new ArrayList<>();
        int enemigosASpawnear = Math.min(enemigosIniciales, enemigosRestantes);

        for (int i = 0; i < enemigosASpawnear; i++) {
            Enemigo enemigo = spawnEnemigoInicial(tipos);
            if (enemigo != null) {
                enemigosInicialesList.add(enemigo);
            } else {
                // Si no se pudo spawnear, romper el bucle
                break;
            }
        }
        return enemigosInicialesList;
    }

    private Enemigo spawnEnemigoInicial(List<TipoEnemigo> tipos) {
        if (enemigosRestantes <= 0) {
            return null;
        }

        TipoEnemigo tipo = tipos.get(rand.nextInt(tipos.size()));

        // Posiciones preferenciales para spawn inicial
        int[][] posicionesPreferenciales = {
                {6, 0}, {5, 0}, {7, 0},  // Centro superior
                {4, 0}, {8, 0},           // Laterales superiores
                {6, 1}, {5, 1}, {7, 1},   // Segunda fila
                {4, 1}, {8, 1},
                {6, 2}, {5, 2}, {7, 2},   // Tercera fila
                {4, 2}, {8, 2}
        };

        // Primero intentar posiciones preferenciales en orden aleatorio
        List<int[]> posicionesMezcladas = new ArrayList<>(Arrays.asList(posicionesPreferenciales));
        Collections.shuffle(posicionesMezcladas, rand);

        for (int[] pos : posicionesMezcladas) {
            int x = pos[0];
            int y = pos[1];

            if (x < target.getAncho() && y < target.getAlto() && target.estaLibre(x, y)) {
                return crearEnemigo(tipo, x, y);
            }
        }

        return spawnEnemigo(tipos);
    }

    private Enemigo crearEnemigo(TipoEnemigo tipo, int x, int y) {
        Enemigo enemigo = new Enemigo(x, y, tipo);
        target.agregarEntidad(enemigo);
        enemigosActivos++;
        totalEnemigosSpawned++;
        enemigosRestantes--;
        return enemigo;
    }

    public void enemigoDestruido() {
        enemigosActivos--;
        if (enemigosActivos < 0) enemigosActivos = 0;
    }

    public boolean isSpawningActivo() {
        // El spawning está activo si no está completado Y hay enemigos restantes O tiempo restante
        long ahora = System.currentTimeMillis();
        boolean tiempoRestante = (ahora - inicioVentana) <= ventanaSpawnMs;
        boolean enemigosRestantes = this.enemigosRestantes > 0;

        boolean activo = !spawningCompletado && (enemigosRestantes || tiempoRestante);

        if (!activo && !spawningCompletado) {
            spawningCompletado = true;
        }

        return activo;
    }

    public int getTotalEnemigosSpawned() {
        return totalEnemigosSpawned;
    }


}
