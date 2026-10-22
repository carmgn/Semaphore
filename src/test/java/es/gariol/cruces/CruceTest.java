package es.gariol.cruces;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.Test;

class CruceTest {

    @Test
    void nuncaHayDosCochesDentroDelMismoCruce() throws InterruptedException {
        Cruce cruce = new Cruce("unico");
        Carretera carretera = new Carretera("R", List.of(cruce));

        Thread[] hilos = new Thread[50];
        for (int i = 0; i < hilos.length; i++) {
            hilos[i] = Thread.ofVirtual().start(new Coche("C" + i, carretera, 5,
                    Duration.ZERO, Duration.ofMillis(1), Observador.NINGUNO));
        }
        for (Thread hilo : hilos) hilo.join();

        assertEquals(1, cruce.maxOcupantesObservado());
        assertEquals(50 * 5, cruce.crucesCompletados());
    }

    @Test
    void unCocheEsperaSiElCruceEstaOcupadoYPasaAlLiberarse() throws InterruptedException {
        Cruce cruce = new Cruce("X");
        Carretera carretera = new Carretera("R", List.of(cruce));
        CountDownLatch entro = new CountDownLatch(1);
        Observador observador = (coche, evento, c) -> {
            if (evento == Observador.Evento.ENTRA) entro.countDown();
        };

        cruce.entrar(); // otro coche ocupa el cruce
        Thread coche = Thread.ofVirtual().start(
                new Coche("C", carretera, 1, Duration.ZERO, Duration.ZERO, observador));

        assertFalse(entro.await(200, TimeUnit.MILLISECONDS), "no debe entrar con el cruce ocupado");
        assertEquals(1, cruce.cochesEsperando());

        cruce.salir();
        assertTrue(entro.await(1, TimeUnit.SECONDS), "debe entrar en cuanto se libera");
        coche.join();
        assertFalse(cruce.estaOcupado());
    }

    @Test
    void elCruceSeLiberaAunqueElCocheSeaInterrumpidoDentro() throws InterruptedException {
        Cruce cruce = new Cruce("X");
        Carretera carretera = new Carretera("R", List.of(cruce));
        CountDownLatch entro = new CountDownLatch(1);
        Observador observador = (coche, evento, c) -> {
            if (evento == Observador.Evento.ENTRA) entro.countDown();
        };

        Thread coche = Thread.ofVirtual().start(
                new Coche("C", carretera, 1, Duration.ZERO, Duration.ofMinutes(1), observador));
        assertTrue(entro.await(1, TimeUnit.SECONDS));
        coche.interrupt();
        coche.join();

        assertFalse(cruce.estaOcupado());
    }
}
