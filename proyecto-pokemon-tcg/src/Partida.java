import java.util.List;

public class Partida {
    private static final int PUNTOS_PARA_GANAR = 3;

    private final Jugador[] jugadores;
    private final Consola consola;
    private int turno;
    private int indiceJugadorActivo;
    private Jugador ganador;

    public Partida(Jugador j1, Jugador j2, Consola consola) {
        this.jugadores = new Jugador[] { j1, j2 };
        this.consola = consola;
        this.turno = 1;
        this.indiceJugadorActivo = 0;
        this.ganador = null;
    }

    public Jugador getGanador() {
        return ganador;
    }

    public void iniciar() {
        prepararJugador(jugadores[0]);
        prepararJugador(jugadores[1]);

        while (ganador == null) {
            jugarTurno();
        }
        consola.anunciarGanador(ganador);
    }

    // Reparto de 5 cartas, mulligan, activo y banco inicial
    private void prepararJugador(Jugador j) {
        j.getMazo().barajar();
        j.hacerMulligan();
        while (!j.tieneBasicoEnMano()) {
            consola.mostrarMensaje(j.getNombre() + " no tiene Pokémon básico: mulligan.");
            j.hacerMulligan();
        }

        Pokemon activo = consola.elegirPokemonDeMano(j);
        j.colocarActivoDesdeMano(activo);

        while (j.getCampo().hayEspacioEnBanco() && j.tieneBasicoEnMano()
                && consola.preguntarSiNo("¿Colocar un Pokémon en el banco?")) {
            Pokemon p = consola.elegirPokemonDeMano(j);
            if (p != null) {
                j.colocarEnBanco(p);
            }
        }
    }

    public void jugarTurno() {
        Jugador actual = jugadores[indiceJugadorActivo];
        Jugador rival = jugadores[1 - indiceJugadorActivo];

        actual.iniciarTurno();
        if (!actual.robarCarta()) {          // mazo vacío: pierde
            consola.registrarAccion(actual.getNombre() + " no tiene cartas para robar.");
            ganador = rival;
            return;
        }

        boolean terminoTurno = false;
        while (!terminoTurno && ganador == null) {
            consola.mostrarCampo(actual, rival, turno);
            consola.mostrarMenuAcciones();
            int opcion = consola.leerOpcion(1, 6);

            switch (opcion) {
                case 1 -> adjuntarEnergia(actual);
                case 2 -> jugarEntrenador(actual, rival);
                case 3 -> colocarEnBanco(actual);
                case 4 -> retirar(actual);
                case 5 -> {
                    if (atacar(actual, rival)) {
                        terminoTurno = true;
                    }
                }
                case 6 -> {
                    consola.registrarAccion(actual.getNombre() + " pasó el turno.");
                    terminoTurno = true;
                }
            }
        }

        if (ganador == null) {
            indiceJugadorActivo = 1 - indiceJugadorActivo;
            turno++;
        }
    }

    private void adjuntarEnergia(Jugador actual) {
        TipoEnergia energia = actual.getEnergiaDisponible();
        if (energia == null) {
            consola.registrarAccion("Ya usaste la energía de este turno.");
            return;
        }
        PokemonEnJuego destino = consola.elegirPokemonPropio(actual);
        if (destino != null && actual.adjuntarEnergiaDeZona(destino)) {
            consola.registrarAccion(actual.getNombre() + " adjuntó energía " + energia + " a "
                    + destino.getCarta().getNombre() + ".");
        }
    }

    private void jugarEntrenador(Jugador actual, Jugador rival) {
        Entrenador e = consola.elegirEntrenadorDeMano(actual);
        if (e == null) {
            return;
        }
        if (!actual.puedeJugarEntrenador(e)) {
            consola.registrarAccion("Ya jugaste una carta de Soporte este turno.");
            return;
        }

        // Si la carta necesita que el jugador elija un Pokémon (ej. Misty), se le pregunta
        PokemonEnJuego objetivo = null;
        List<PokemonEnJuego> objetivos = e.objetivosPosibles(actual);
        if (!objetivos.isEmpty()) {
            objetivo = consola.elegirObjetivo(actual, objetivos);
            if (objetivo == null) {
                return;   // canceló
            }
        }

        if (actual.jugarEntrenador(e, rival, objetivo)) {
            String info = consola.capturarSalida(e::mostrarInformacion);
            consola.registrarAccion(actual.getNombre() + " jugó una carta de entrenador:\n" + info);
            if (objetivo != null) {
                consola.registrarAccion("Objetivo: " + objetivo.getCarta().getNombre());
            }
        } else {
            consola.registrarAccion("No se pudo jugar " + e.getNombre() + ".");
        }
    }

    private void colocarEnBanco(Jugador actual) {
        Pokemon p = consola.elegirPokemonDeMano(actual);
        if (p == null) {
            return;
        }
        if (actual.colocarEnBanco(p)) {
            consola.registrarAccion(actual.getNombre() + " colocó a " + p.getNombre() + " en el banco.");
        } else {
            consola.registrarAccion("El banco está lleno.");
        }
    }

    private void retirar(Jugador actual) {
        if (actual.getCampo().getBanco().isEmpty()) {
            consola.registrarAccion("No tienes Pokémon en el banco.");
            return;
        }
        int indice = consola.elegirDelBanco(actual, true);   // -1 si cancela
        if (indice == -1) {
            return;
        }
        String retirado = actual.getCampo().getActivo().getCarta().getNombre();
        if (actual.retirarActivo(indice)) {
            consola.registrarAccion(actual.getNombre() + " retiró a " + retirado + ". Ahora "
                    + actual.getCampo().getActivo().getCarta().getNombre() + " es el Pokémon activo.");
        } else {
            consola.registrarAccion("No tienes energía suficiente para retirar.");
        }
    }

    private boolean atacar(Jugador actual, Jugador rival) {
        if (turno == 1) {
            consola.registrarAccion("El primer jugador no puede atacar en su primer turno.");
            return false;
        }
        Ataque ataque = consola.elegirAtaque(actual.getCampo().getActivo());
        if (ataque == null) {
            return false;
        }

        PokemonEnJuego atacante = actual.getCampo().getActivo();
        PokemonEnJuego defensor = rival.getCampo().getActivo();
        int psAntes = defensor.getPsActuales();

        if (!actual.atacar(rival, ataque)) {
            consola.registrarAccion("No puedes usar ese ataque (energía insuficiente o ya atacaste).");
            return false;
        }

        int danoHecho = psAntes - defensor.getPsActuales();
        consola.registrarAccion(actual.getNombre() + " atacó: " + atacante.getCarta().getNombre() + " usó "
                + ataque.getNombre() + " y " + defensor.getCarta().getNombre() + " pierde " + danoHecho + " PS.");
        if (defensor.getCarta().getTipo().esDebilA(atacante.getCarta().getTipo())) {
            consola.registrarAccion("¡Es débil a ese tipo! (+20 de daño)");
        }
        if (defensor.estaDerrotado()) {
            consola.registrarAccion("¡" + defensor.getCarta().getNombre() + " fue derrotado! "
                    + actual.getNombre() + " gana " + defensor.getCarta().getPuntosPremio()
                    + " punto(s) de premio.");
        }

        if (verificarVictoria()) {
            return true;
        }
        while (rival.necesitaReemplazo()) {
            consola.mostrarRegistro();   // que el rival vea lo que pasó antes de elegir el reemplazo
            int indice = consola.elegirDelBanco(rival, false);
            rival.reemplazarActivo(indice);
            consola.registrarAccion(rival.getNombre() + " puso a "
                    + rival.getCampo().getActivo().getCarta().getNombre() + " como Pokémon activo.");
        }
        return true;
    }

    // Revisa puntos de premio y Pokémon en juego. Si alguien ganó, lo guarda en 'ganador'
    public boolean verificarVictoria() {
        for (int i = 0; i < 2; i++) {
            Jugador j = jugadores[i];
            Jugador otro = jugadores[1 - i];
            if (j.getPuntosPremio() >= PUNTOS_PARA_GANAR || !otro.getCampo().tienePokemonEnJuego()) {
                ganador = j;
                return true;
            }
        }
        return false;
    }
}