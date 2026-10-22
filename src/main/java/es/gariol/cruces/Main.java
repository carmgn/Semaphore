package es.gariol.cruces;

import java.time.Duration;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * Ejecuta una simulación y muestra por consola cada coche que espera, entra o sale de un cruce.
 *
 * <p>Uso: {@code java -jar semaphore.jar [coches] [vueltas]}
 */
public final class Main {

    private static final DateTimeFormatter HORA = DateTimeFormatter.ofPattern("HH:mm:ss.SSS");

    public static void main(String[] args) throws InterruptedException {
        int coches = args.length > 0 ? Integer.parseInt(args[0]) : 12;
        int vueltas = args.length > 1 ? Integer.parseInt(args[1]) : 2;

        Simulacion simulacion = new Simulacion(2, 3);
        Observador traza = (coche, evento, cruce) -> {
            String donde = cruce == null ? "" : " en " + cruce;
            System.out.printf("%s  %-4s %-8s %s%s%n",
                    LocalTime.now().format(HORA), coche, evento, coche.carretera().nombre(), donde);
        };

        System.out.printf("%d coches, %d vueltas, %d cruces%n%n", coches, vueltas, simulacion.cruces().size());
        boolean termino = simulacion.ejecutar(coches, vueltas,
                Duration.ofMillis(40), Duration.ofMillis(25), Duration.ofMinutes(1), traza);

        System.out.println();
        for (Cruce cruce : simulacion.cruces()) {
            System.out.printf("%-6s %3d pasos, máximo %d coche(s) dentro a la vez%n",
                    cruce, cruce.crucesCompletados(), cruce.maxOcupantesObservado());
        }
        System.out.println(termino ? "\nTodos los coches han terminado." : "\nPlazo agotado.");
    }
}
