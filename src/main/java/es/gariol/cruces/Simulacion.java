package es.gariol.cruces;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Cuadrícula de carreteras horizontales y verticales. Cada intersección es un {@link Cruce}
 * y cada coche corre en su propio hilo virtual.
 */
public final class Simulacion {

    private final List<Cruce> cruces = new ArrayList<>();
    private final List<Carretera> carreteras = new ArrayList<>();

    public Simulacion(int horizontales, int verticales) {
        Cruce[][] rejilla = new Cruce[horizontales][verticales];
        for (int h = 0; h < horizontales; h++) {
            for (int v = 0; v < verticales; v++) {
                rejilla[h][v] = new Cruce("H" + h + "-V" + v);
                cruces.add(rejilla[h][v]);
            }
        }
        // Sentidos alternos: unas carreteras van "hacia delante" y otras "hacia atrás".
        for (int h = 0; h < horizontales; h++) {
            List<Cruce> tramo = new ArrayList<>(List.of(rejilla[h]));
            if (h % 2 == 1) tramo = tramo.reversed();
            carreteras.add(new Carretera("H" + h, tramo));
        }
        for (int v = 0; v < verticales; v++) {
            List<Cruce> tramo = new ArrayList<>();
            for (int h = 0; h < horizontales; h++) tramo.add(rejilla[h][v]);
            if (v % 2 == 1) tramo = tramo.reversed();
            carreteras.add(new Carretera("V" + v, tramo));
        }
    }

    /**
     * Lanza los coches repartidos entre las carreteras y espera a que terminen.
     *
     * @return {@code true} si todos terminaron dentro del plazo
     */
    public boolean ejecutar(int coches, int vueltas, Duration tramo, Duration cruzar,
                            Duration plazo, Observador observador) throws InterruptedException {
        try (ExecutorService hilos = Executors.newVirtualThreadPerTaskExecutor()) {
            for (int i = 0; i < coches; i++) {
                Carretera carretera = carreteras.get(i % carreteras.size());
                hilos.submit(new Coche("C" + i, carretera, vueltas, tramo, cruzar, observador));
            }
            hilos.shutdown();
            return hilos.awaitTermination(plazo.toMillis(), TimeUnit.MILLISECONDS);
        }
    }

    public List<Cruce> cruces() {
        return List.copyOf(cruces);
    }

    public List<Carretera> carreteras() {
        return List.copyOf(carreteras);
    }
}
