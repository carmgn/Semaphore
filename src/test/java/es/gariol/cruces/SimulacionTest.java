package es.gariol.cruces;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.Test;

class SimulacionTest {

    @Test
    void construyeUnaCuadriculaDeCrucesYCarreteras() {
        Simulacion simulacion = new Simulacion(2, 3);

        assertEquals(6, simulacion.cruces().size());
        assertEquals(5, simulacion.carreteras().size());
        simulacion.carreteras().forEach(c ->
                assertTrue(c.cruces().size() == 2 || c.cruces().size() == 3));
    }

    @Test
    void todosLosCochesTerminanSinInterbloqueoYConExclusionMutua() throws InterruptedException {
        Simulacion simulacion = new Simulacion(3, 4);
        AtomicInteger terminados = new AtomicInteger();
        Observador observador = (coche, evento, cruce) -> {
            if (evento == Observador.Evento.TERMINA) terminados.incrementAndGet();
        };

        boolean termino = simulacion.ejecutar(200, 3,
                Duration.ofMillis(1), Duration.ofMillis(1), Duration.ofSeconds(30), observador);

        assertTrue(termino, "la simulación no debe quedarse bloqueada");
        assertEquals(200, terminados.get());
        simulacion.cruces().forEach(c -> assertEquals(1, c.maxOcupantesObservado(), c.nombre()));
        int pasos = simulacion.cruces().stream().mapToInt(Cruce::crucesCompletados).sum();
        // Los coches se reparten en turno rotatorio entre las carreteras.
        int esperados = 0;
        for (int i = 0; i < 200; i++) {
            esperados += simulacion.carreteras().get(i % simulacion.carreteras().size()).cruces().size() * 3;
        }
        assertEquals(esperados, pasos);
    }
}
