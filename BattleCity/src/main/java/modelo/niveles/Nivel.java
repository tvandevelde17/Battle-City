package modelo.niveles;

import eventos.*;
import modelo.entidades.*;
import modelo.mapa.Mapa;
import modelo.mapa.Spawner;
import modelo.util.*;
import java.util.*;

    public class Nivel implements DestruccionListener {
        // ==================== CONFIGURACIÓN DEL NIVEL ====================
        private final Mapa mapa;
        private final Base baseJugador;
        private final int[][] posicionesSpawnJugadores;
        private final List<TipoEnemigo> tiposEnemigos;
        private final ModoJuego modoJuego;


        // ==================== ENTIDADES DEL JUEGO ====================
        private final List<Jugador> jugadores = new ArrayList<>();
        private final List<Enemigo> enemigos = new ArrayList<>();
        private final List<Bala> balas = new ArrayList<>();
        private final List<Bala> balasParaAgregar = new ArrayList<>();
        private final List<Bala> balasParaEliminar = new ArrayList<>();
        private final Map<Jugador, Bala> balasActivasJugadores = new HashMap<>();
        private final List<Enemigo> enemigosParaEliminar = new ArrayList<>();

        // ==================== SISTEMAS DEL JUEGO ====================
        private final Spawner spawner;
        private  final PowerUpManager powerUpManager;
        private final RespawnManager respawnManager;
        private final TanqueDestruidoManager tanqueDestruidoManager;

        // ==================== CONTROL DE TIEMPO ====================
        private long ultimoIntentoSpawn = 0;
        private static final long INTERVALO_SPAWN = 3000;

        // ==================== CONSTRUCTOR ====================

        public Nivel(Mapa mapa, Base baseJugador, int[][] posicionesSpawnJugadores,
                      List<TipoEnemigo> tiposEnemigos, int maxEnemigosSimultaneos,
                     int tiempoSpawnSegundos, int enemigosPorSpawnear, ModoJuego modoJuego) {

            this.mapa = mapa;
            this.baseJugador = baseJugador;
            this.posicionesSpawnJugadores = posicionesSpawnJugadores;
            this.tiposEnemigos = tiposEnemigos;
            this.modoJuego = modoJuego;
            this.spawner = new Spawner(mapa, maxEnemigosSimultaneos, tiempoSpawnSegundos * 1000, enemigosPorSpawnear);
            this.tanqueDestruidoManager = new TanqueDestruidoManager(mapa);
            this.respawnManager = new RespawnManager(mapa, tanqueDestruidoManager, posicionesSpawnJugadores);
            this.powerUpManager = new PowerUpManager(mapa);

            EventManager.getInstance().addListener(this);

            inicializarJugadores();
            inicializarEnemigos();
        }

        // ==================== INICIALIZACIÓN ====================
        private void inicializarJugadores() {
            int jugadoresACrear = (modoJuego == ModoJuego.UN_JUGADOR) ? 1 : Math.min(2, posicionesSpawnJugadores.length);

            for (int i = 0; i < jugadoresACrear; i++) {
                int x = posicionesSpawnJugadores[i][0];
                int y = posicionesSpawnJugadores[i][1];

                if (mapa.estaLibre(x, y)) {
                    Jugador jugador = new Jugador(x, y);
                    jugadores.add(jugador);
                    mapa.agregarEntidad(jugador);
                }
            }
        }

        private void inicializarEnemigos() {
            List<Enemigo> enemigosIniciales = spawner.spawnEnemigosIniciales(tiposEnemigos);
            enemigos.addAll(enemigosIniciales);
            mapa.agregarEntidad(baseJugador);
        }
        // ==================== BUCLE PRINCIPAL ====================
        public void actualizar() {
            actualizarRespawnJugadores();
            actualizarBalas();
            sincronizarBalas();
            verificarColisionesBalas();
            actualizarEnemigos();
            actualizarSpawningContinua();
            actualizarPowerUps();
            actualizarEfectosJugadores();
        }

        // ==================== ACTUALIZACIÓN DE EFECTOS TEMPORALES ====================
        private void actualizarEfectosJugadores() {
            for (Jugador jugador : jugadores) {
                if (jugador.estaVivo()) {
                    jugador.actualizarEfectosTemporales();
                }
            }
        }

        // ==================== ACTUALIZACIÓN DE JUGADORES ====================
        private void actualizarRespawnJugadores() {
            for (Jugador jugador : jugadores) {
                if (!jugador.estaVivo() && jugador.getVidasJuego() > 0) {
                    respawnManager.respawnearJugador(jugador);
                }
            }
        }

        // ==================== ACTUALIZACIÓN DE ENEMIGOS ====================
        private void actualizarSpawningContinua() {
            long ahora = System.currentTimeMillis();
            if (ahora - ultimoIntentoSpawn >= INTERVALO_SPAWN) {
                if (spawner.isSpawningActivo()) {
                    Enemigo nuevoEnemigo = spawner.spawnEnemigo(tiposEnemigos);
                    if (nuevoEnemigo != null) {
                        enemigos.add(nuevoEnemigo);
                    }
                }
                ultimoIntentoSpawn = ahora;
            }
        }

        private void actualizarEnemigos() {
            // Primero procesamos todos los enemigos
            Iterator<Enemigo> it = enemigos.iterator();
            while (it.hasNext()) {
                Enemigo e = it.next();

                if (!e.estaVivo()) {
                    enemigosParaEliminar.add(e);
                    continue;
                }

                // Solo procesar enemigos vivos
                boolean debeMoverse = e.actualizarIA();
                if (debeMoverse) {
                    moverTanque(e);
                }

                Bala balaEnemiga = e.intentarDisparo();
                if (balaEnemiga != null) {
                    if (hayColisionInmediata(balaEnemiga, e)) {
                        aplicarDanioColisionInmediata(balaEnemiga, e);
                    } else {
                        balas.add(balaEnemiga);
                        mapa.agregarEntidad(balaEnemiga);
                    }
                }
            }

            // Ahora eliminamos los enemigos muertos después de terminar la iteración
            if (!enemigosParaEliminar.isEmpty()) {
                for (Enemigo enemigoMuerto : enemigosParaEliminar) {
                    eliminarEnemigoCompletamente(enemigoMuerto);
                }
                enemigosParaEliminar.clear();
            }
        }

        private void eliminarEnemigoCompletamente(Enemigo enemigo) {
            // Limpieza completa del enemigo
            mapa.eliminarEntidad(enemigo.getX(), enemigo.getY());
            powerUpManager.intentarGenerarPowerUp();
            spawner.enemigoDestruido();
            enemigos.remove(enemigo);
        }

        // ==================== ACTUALIZACIÓN DE BALAS ====================
        private void actualizarBalas() {
            Iterator<Bala> it = balas.iterator();
            while (it.hasNext()) {
                Bala bala = it.next();
                if (!bala.estaVivo()) {
                    eliminarBala(bala);
                    it.remove();
                    continue;
                }
                moverBala(bala);
            }
        }

        private void sincronizarBalas() {
            balas.addAll(balasParaAgregar);
            balasParaAgregar.clear();
            balas.removeAll(balasParaEliminar);
            balasParaEliminar.clear();
        }

        private void verificarColisionesBalas() {
            List<Bala> balasParaEliminarLocal = new ArrayList<>();

            for (int i = 0; i < balas.size(); i++) {
                for (int j = i + 1; j < balas.size(); j++) {
                    Bala bala1 = balas.get(i);
                    Bala bala2 = balas.get(j);

                    if (bala1.estaVivo() && bala2.estaVivo() &&
                            bala1.getX() == bala2.getX() && bala1.getY() == bala2.getY()) {
                        balasParaEliminarLocal.add(bala1);
                        balasParaEliminarLocal.add(bala2);
                    }
                }
            }

            for (Bala bala : balasParaEliminarLocal) {
                eliminarBala(bala);
            }
        }

        private void eliminarBala(Bala b) {
            if (b == null) return;
            b.setVivo(false);
            mapa.eliminarEntidad(b.getX(), b.getY());
            balasParaEliminar.add(b);
            notificarBalaDestruida(b);
        }

        private void notificarBalaDestruida(Bala balaDestruida) {
            Iterator<Map.Entry<Jugador, Bala>> it = balasActivasJugadores.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry<Jugador, Bala> entry = it.next();
                if (entry.getValue() == balaDestruida) {
                    entry.getKey().balaDestruida();
                    it.remove();
                    break;
                }
            }
        }

        // ==================== MOVIMIENTO ====================
        public boolean moverJugador(Jugador j, Direccion dir) {
            if (!j.estaVivo()) return false;
            j.setDireccion(dir);
            return moverTanque(j);
        }

        public boolean moverTanque(Tanque t) {
            if (!t.estaVivo()) return false;

            int[] nuevaPos = t.calcularNuevaPosicion(t.getX(), t.getY(), t.getDireccion());
            int nuevaX = nuevaPos[0];
            int nuevaY = nuevaPos[1];

            if (!mapa.estaDentroDelMapa(nuevaX, nuevaY) || !mapa.estaLibre(nuevaX, nuevaY)) {
                // Si es un enemigo, forzar cambio de dirección
                if (t instanceof Enemigo) {
                    ((Enemigo) t).forzarCambioDireccion();
                }
                return false;
            }

            // Movimiento exitoso
            mapa.eliminarEntidad(t.getX(), t.getY());
            t.actualizarPosicion(nuevaX, nuevaY);
            mapa.agregarEntidad(t);

            // Verificar power-up si es jugador
            if (t.esJugador()) {
                verificarPowerUpEnPosicion((Jugador) t, nuevaX, nuevaY);
            }

            return true;
        }

        private void verificarPowerUpEnPosicion(Jugador jugador, int x, int y) {
            Entidad entidad = mapa.obtenerEntidad(x, y);
            if (entidad != null && entidad.esPowerUp() && entidad.estaVivo()) {
                powerUpManager.verificarConsumo(jugador, this);
            }
        }

        // ==================== DISPAROS ====================
        public void jugadorDispara(int indiceJugador) {
            if (indiceJugador < 0 || indiceJugador >= jugadores.size()) return;

            Jugador jugador = jugadores.get(indiceJugador);
            if (!jugador.estaVivo() || jugador.tieneBalaActiva()) return;

            Bala b = jugador.disparar();
            if (b != null) {
                if (hayColisionInmediata(b, jugador)) {
                    aplicarDanioColisionInmediata(b, jugador);
                    jugador.balaDestruida();
                } else {
                    balasParaAgregar.add(b);
                    mapa.agregarEntidad(b);
                    balasActivasJugadores.put(jugador, b);
                }
            }
        }

        // ==================== COLISIONES ====================
        private boolean hayColisionInmediata(Bala bala, Tanque disparador) {
            int x = bala.getX();
            int y = bala.getY();

            if (!mapa.estaDentroDelMapa(x, y)) return true;

            Bloque bloque = mapa.obtenerBloque(x, y);
            if (bloque != null && bloque.getTipo().recibeBalas() && bloque.getTipo() != TipoBloque.BOSQUE) {
                return true;
            }

            Entidad entidad = mapa.obtenerEntidad(x, y);
            return entidad != null && entidad.recibeDisparo() && entidad != disparador;
        }

        private void aplicarDanioColisionInmediata(Bala bala, Tanque disparador) {
            int x = bala.getX();
            int y = bala.getY();

            Bloque bloque = mapa.obtenerBloque(x, y);
            if (bloque != null && bloque.getTipo().recibeBalas()) {
                bloque.recibirImpacto(bala.getDanio());
                if (!bloque.estaVivo()) {
                    mapa.eliminarBloque(x, y);
                }
                return;
            }

            Entidad entidad = mapa.obtenerEntidad(x, y);
            if (entidad != null && entidad.recibeDisparo() && entidad != disparador) {
                entidad.recibirImpacto(bala.getDanio());
                if (!entidad.estaVivo()) {
                    eliminarEntidad(entidad);
                }
            }
        }

        private void moverBala(Bala b) {
            if (b == null || !b.estaVivo()) {
                eliminarBala(b);
                return;
            }

            int[] nuevaPos = b.calcularNuevaPosicion(b.getX(), b.getY(), b.getDireccion());
            int nuevaX = nuevaPos[0];
            int nuevaY = nuevaPos[1];

            if (!mapa.estaDentroDelMapa(nuevaX, nuevaY)) {
                eliminarBala(b);
                return;
            }

            Bloque bloque = mapa.obtenerBloque(nuevaX, nuevaY);
            if (bloque != null && bloque.getTipo().recibeBalas() && bloque.getTipo() != TipoBloque.BOSQUE) {
                bloque.recibirImpacto(b.getDanio());
                if (!bloque.estaVivo()) {
                    mapa.eliminarBloque(nuevaX, nuevaY);
                }
                eliminarBala(b);
                return;
            }

            Entidad entidad = mapa.obtenerEntidad(nuevaX, nuevaY);
            if (entidad != null && entidad.recibeDisparo() && entidad != b) {
                entidad.recibirImpacto(b.getDanio());
                if (!entidad.estaVivo()) {
                    eliminarEntidad(entidad);
                }
                eliminarBala(b);
                return;
            }

            mapa.eliminarEntidad(b.getX(), b.getY());
            b.actualizarPosicion(nuevaX, nuevaY);
            mapa.agregarEntidad(b);
        }

        private void eliminarEntidad(Entidad e) {
            if (e == null) return;
            mapa.eliminarEntidad(e.getX(), e.getY());

            if (e.esEnemigo()) {
                spawner.enemigoDestruido();
            }
        }

        // ==================== POWER-UPS ====================
        private void actualizarPowerUps() {
            for (Jugador jugador : jugadores) {
                if (jugador.estaVivo()) {
                    powerUpManager.verificarConsumo(jugador, this);
                }
            }
        }

        public void destruirTodosLosEnemigos() {
            for (Enemigo enemigo : new ArrayList<>(enemigos)) {
                enemigo.recibirImpacto(Integer.MAX_VALUE);
            }
        }

        // ==================== EVENTOS ====================
        @Override
        public void onEntidadDestruida(EventoDestruccion evento) {
            Entidad entidad = evento.getEntidadDestruida();
            int x = evento.getX();
            int y = evento.getY();

            if (entidad.esEnemigo() || entidad.esJugador()) {
                // Crear bloque de tanque destruido INMEDIATAMENTE
                tanqueDestruidoManager.crearTanqueDestruido(x, y);
            }
        }

        // ==================== ESTADO DEL NIVEL ====================
        public boolean nivelGanado() {
            return enemigos.isEmpty() && spawner.getTotalEnemigosSpawned() >= 10 && !spawner.isSpawningActivo();
        }

        public boolean nivelPerdido() {
            boolean todosJugadoresMuertos = true;
            for (Jugador jugador : jugadores) {
                if (jugador.estaVivo()) {
                    todosJugadoresMuertos = false;
                    break;
                }
            }
            return todosJugadoresMuertos || !baseJugador.estaVivo();
        }
        public void destruir() {
            EventManager.getInstance().removeListener(this);
            limpiarMapaCompletamente();
        }


        private void limpiarMapaCompletamente() {
            mapa.limpiarEntidadesMoviles();
            mapa.limpiarCompletamente();
            enemigos.clear();
            balas.clear();
            balasParaAgregar.clear();
            balasParaEliminar.clear();
            balasActivasJugadores.clear();
            enemigosParaEliminar.clear();
            powerUpManager.limpiarPowerUp();
            spawner.reiniciar();
            mapa.agregarEntidad(baseJugador);
            baseJugador.setVivo(true);
        }



        // ==================== GETTERS ====================
            public Mapa getMapa () {
                return mapa;
            }
            public List<Jugador> getJugadores () {
                return jugadores;
            }
            public List<Enemigo> getEnemigos () {
                return enemigos;
            }
            public List<Bala> getBalas () {
                return balas;
            }
            public Base getBase () {
                return baseJugador;
            }
            public PowerUp getPowerUpActual () {
                return powerUpManager.getPowerUpActual();
            }
        }





