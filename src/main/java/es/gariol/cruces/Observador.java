package es.gariol.cruces;

/** Recibe los eventos de la simulación (para trazas o tests). Debe ser seguro entre hilos. */
public interface Observador {

    enum Evento { ESPERA, ENTRA, SALE, TERMINA }

    void notificar(Coche coche, Evento evento, Cruce cruce);

    Observador NINGUNO = (coche, evento, cruce) -> { };
}
