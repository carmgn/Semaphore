package es.gariol.cruces;

import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Intersección compartida por varias carreteras. Un semáforo binario garantiza
 * que como mucho un coche la ocupa en cada instante.
 */
public final class Cruce {

    private final String nombre;
    // Justo (fair): los coches entran en el orden en que llegaron, sin inanición.
    private final Semaphore semaforo = new Semaphore(1, true);

    // Solo para observar el comportamiento; no participan en la sincronización.
    private final AtomicInteger ocupantes = new AtomicInteger();
    private final AtomicInteger maxOcupantes = new AtomicInteger();
    private final AtomicInteger cruces = new AtomicInteger();

    public Cruce(String nombre) {
        this.nombre = nombre;
    }

    /** wait(): bloquea al coche hasta que el cruce queda libre. */
    void entrar() throws InterruptedException {
        semaforo.acquire();
        int ahora = ocupantes.incrementAndGet();
        maxOcupantes.accumulateAndGet(ahora, Math::max);
    }

    /** signal(): libera el cruce para el siguiente coche en espera. */
    void salir() {
        ocupantes.decrementAndGet();
        cruces.incrementAndGet();
        semaforo.release();
    }

    public boolean estaOcupado() {
        return semaforo.availablePermits() == 0;
    }

    public int cochesEsperando() {
        return semaforo.getQueueLength();
    }

    /** Máximo de coches que llegaron a estar dentro a la vez. Debe ser 1. */
    public int maxOcupantesObservado() {
        return maxOcupantes.get();
    }

    public int crucesCompletados() {
        return cruces.get();
    }

    public String nombre() {
        return nombre;
    }

    @Override
    public String toString() {
        return nombre;
    }
}
