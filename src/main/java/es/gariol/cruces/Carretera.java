package es.gariol.cruces;

import java.util.List;

/** Carretera de sentido único: la secuencia de cruces que atraviesa, en orden. */
public record Carretera(String nombre, List<Cruce> cruces) {

    public Carretera {
        cruces = List.copyOf(cruces);
    }
}
