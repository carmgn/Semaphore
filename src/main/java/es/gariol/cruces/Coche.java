package es.gariol.cruces;

import java.time.Duration;

/**
 * Un coche es un proceso independiente que recorre su carretera un número de vueltas.
 *
 * <p>Nunca retiene más de un semáforo a la vez: suelta el cruce antes de pedir el
 * siguiente. Al romper la condición de "retener y esperar" no puede haber interbloqueo,
 * aunque las carreteras se crucen en ciclo.
 */
public final class Coche implements Runnable {

    private final String nombre;
    private final Carretera carretera;
    private final int vueltas;
    private final Duration tramo;
    private final Duration cruzar;
    private final Observador observador;

    public Coche(String nombre, Carretera carretera, int vueltas,
                 Duration tramo, Duration cruzar, Observador observador) {
        this.nombre = nombre;
        this.carretera = carretera;
        this.vueltas = vueltas;
        this.tramo = tramo;
        this.cruzar = cruzar;
        this.observador = observador;
    }

    @Override
    public void run() {
        try {
            for (int v = 0; v < vueltas; v++) {
                for (Cruce cruce : carretera.cruces()) {
                    Thread.sleep(tramo);
                    if (cruce.estaOcupado()) {
                        observador.notificar(this, Observador.Evento.ESPERA, cruce);
                    }
                    cruce.entrar();
                    try {
                        observador.notificar(this, Observador.Evento.ENTRA, cruce);
                        Thread.sleep(cruzar);
                    } finally {
                        // Se libera siempre, incluso si el hilo se interrumpe dentro del cruce.
                        cruce.salir();
                        observador.notificar(this, Observador.Evento.SALE, cruce);
                    }
                }
            }
            observador.notificar(this, Observador.Evento.TERMINA, null);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public String nombre() {
        return nombre;
    }

    public Carretera carretera() {
        return carretera;
    }

    @Override
    public String toString() {
        return nombre;
    }
}
