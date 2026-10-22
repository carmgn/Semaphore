# Semaphore · cruces con semáforos

[![CI](https://github.com/carmgn/Semaphore/actions/workflows/ci.yml/badge.svg)](https://github.com/carmgn/Semaphore/actions/workflows/ci.yml)

Simulación concurrente en **Java 21**: varias carreteras de sentido único se cruzan y cada coche es un hilo independiente. Cada cruce está protegido por un semáforo binario, así que nunca hay dos coches dentro a la vez.

Es la versión en Java de un ejercicio de programación concurrente que hice originalmente en C++. Hay una demo visual en mi web.

## Cómo funciona

| Concepto | Implementación |
| --- | --- |
| Cruce | `Cruce` con un `java.util.concurrent.Semaphore(1, true)` |
| Esperar a que el cruce quede libre (*wait*) | `semaforo.acquire()` en `Cruce.entrar()` |
| Liberar el cruce (*signal*) | `semaforo.release()` en `Cruce.salir()`, siempre dentro de un `finally` |
| Coche | `Coche implements Runnable`, uno por hilo virtual (`Executors.newVirtualThreadPerTaskExecutor()`) |
| Carretera | `record Carretera(String nombre, List<Cruce> cruces)` |

**Exclusión mutua:** el semáforo tiene un único permiso, de modo que un segundo coche se bloquea en `acquire()` hasta que el primero llama a `release()`.

**Sin interbloqueo:** un coche nunca retiene más de un semáforo a la vez, porque suelta el cruce antes de pedir el siguiente. Al romper la condición de *retener y esperar*, no puede formarse un ciclo de espera aunque las carreteras se crucen entre sí.

**Sin inanición:** el semáforo es *justo* (`fair = true`). Los coches entran en el orden en que llegaron.

## Ejecutar

Requiere JDK 21 y Maven.

```bash
mvn verify                                 # compila y pasa los tests
java -jar target/semaphore.jar 12 2        # 12 coches, 2 vueltas
```

Salida (resumida):

```
16:24:40.101  C3   ESPERA   V1 en H0-V1
16:24:40.126  C3   ENTRA    V1 en H0-V1
16:24:40.151  C3   SALE     V1 en H0-V1
...
H0-V1   10 pasos, máximo 1 coche(s) dentro a la vez
```

## Tests

- `CruceTest`
  - 50 hilos compiten por el mismo cruce y nunca hay más de un coche dentro.
  - Un coche se queda esperando mientras el cruce está ocupado y entra en cuanto se libera.
  - El cruce se libera aunque el hilo se interrumpa a mitad del cruce.
- `SimulacionTest`
  - 200 coches en una cuadrícula de 3×4 cruces terminan todos (sin interbloqueo).
  - Ningún cruce tiene nunca más de un ocupante.
  - El número total de pasos es exactamente el esperado.

## Autor

Carmelo García · [gariol.es](https://gariol.es) · [LinkedIn](https://www.linkedin.com/in/carmgn)
