# YABC: YET ANOTHER BATTLE CITY

## DESCRIPCIÓN
YABC (Yet Another Battle City) es nuestra reversión del clásico Battle City: misma esencia arcade, código moderno. Lo hicimos en Java (JDK 24) con JavaFX y Maven, y lo armamos con separación Modelo–Vista–Controlador (MVC) para que la lógica del juego no dependa de la parte gráfica.El Modelo piensa, la Vista dibuja, y el Controlador escucha el teclado y coordina todo.

La pantalla de juego es fija: 800×600 px (no se redimensiona) y encima corre una grilla de 40×30 celdas (cada celda de 20×20 px). Las colisiones se calculan a nivel píxel para que el movimiento se sienta preciso. El objetivo es simple y clásico: defender el águila (la base) y destrozar a todos los tanques enemigos. Hay 3 niveles y, si te gusta el modo cooperativo, podés jugar solo o de a dos al mismo tiempo.


### Modos de juego:

1 Jugador

2 Jugadores (coop simultáneo)

Controles por defecto: J1: WASD + Espacio (disparo) · J2: Flechas + Enter (disparo).

Enemigos con IA simple (máquina de estados): avanzan, disparan cuando pueden y cada tanto cambian de dirección y conducta en tiempos aleatorios. Si se quedan “pegados” más de un rato, se reubican solos.

Power-ups que aparecen al destruir enemigos (con porbabilidades):
Granada (limpia la pantalla), Casco (invulnerabilidad temporal), Estrella (tu disparo pasa a ser one-shot).

Sprites 20×20 hechos a mano y sonidos listos para usar. Disparos de 6×6 px. Animaciones básicas de orugas alternando frames para dar sensación de movimiento.

Niveles en XML, validados con XSD. Podés editar posiciones de jugadores, base y bloques (ladrillo, acero, agua, bosque), y el set de enemigos por nivel. Cargar un nivel no “ensucia” la clase que lo representa (el parser va aparte).

Flujo de app bien clásico: pantalla de inicio con 1 Jugador, 2 Jugadores y Salir → jugás → victoria/derrota → volvés al inicio.

Stack y arquitectura (sin vueltas)

Java (JDK 24) + JavaFX + Maven

MVC con separación estricta (el Modelo no importa clases de JavaFX ni de la Vista).

Repositorio Git preparado para compilar y correr fácil.


### INSTRUCCIONES PARA EJECUCION
Si estas en Intellij Idea y lo queres ejecutar por terminal,debes de ejecutar el siguiente comando:
```bash
:~$ mvn clean javafx:run
```
Ahora si lo queremos correr desde el proyecto,debemos desde el archivo MainApp.java: 
- Utilizar Mayus + f10.
- Apretar el boton triangular verde que tiene el siguiente estilo:
![Ejemplo de boton de ejecucion](/recursos_readme./recursos_readme/Ejecución_Triangulo_Verde.png)

Si queremos ejecutar con maven debemos seguir los siguientes pasos:
- En la barra de herramientas clickear el logo de Maven:
![Ejemplo Logo Maven](/recursos_readme./recursos_readme/Logo_Maven.png)
- Desplegamos carpetas en el siguiente orden:
  - Tp1
    - Plugins
      - javafx

Damos click dos veces en javafx:run
![Ejemplo ejecución con maven](/recursos_readme./recursos_readme/Ejecución_con_Maven.png)
### INSTRUCCIONES DE JUEGO (COMANDOS)
- En el menu nos movemos con las flechas y seleccionamos la opcion con enter

Para el Jugador 1 los controles son los siguientes:
- W (Adelante)
- S (Atras)
- A (Izquierda)
- D (Derecha)
- Barra Espaciadora (Disparo)

Para el Jugador 2 los controles son los siguientes:
- ↑ (Adelante)
- ↓ (Atras)
- ← (Izquierda)
- → (Derecha)
- Enter (Disparo)
