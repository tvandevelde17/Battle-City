package eventos;

import modelo.entidades.Entidad;

import java.util.ArrayList;
import java.util.List;

public class EventManager {
    private static EventManager instance;
    private final List<DestruccionListener> listeners = new ArrayList<>();

    private EventManager() {}

    public static EventManager getInstance() {
        if (instance == null) {
            instance = new EventManager();
        }
        return instance;
    }

    public void addListener(DestruccionListener listener) {
        if (!listeners.contains(listener)) {
            listeners.add(listener);
        }
    }

    public void removeListener(DestruccionListener listener) {
        listeners.remove(listener);
    }

    public void notificarDestruccion(Entidad entidad) {

        for (DestruccionListener listener : listeners) {
            listener.onEntidadDestruida(new EventoDestruccion(entidad, entidad.getX(), entidad.getY()));
        }
    }
    public void limpiarCompletamente() {
        listeners.clear();
    }
}
