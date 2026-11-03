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
                case 5 -> atacar(actual, rival);
                case 6 -> terminoTurno = true;
            }
        }

        if (ganador == null) {
            indiceJugadorActivo = 1 - indiceJugadorActivo;
            turno++;
        }
    }

    private void adjuntarEnergia(Jugador actual) {
        if (actual.getEnergiaDisponible() == null) {
            consola.mostrarMensaje("Ya usaste la energía de este turno.");
            return;
        }
        PokemonEnJuego destino = consola.elegirPokemonPropio(actual);
        if (destino != null) {
            actual.adjuntarEnergiaDeZona(destino);
        }
    }

    private void jugarEntrenador(Jugador actual, Jugador rival) {
        Entrenador e = consola.elegirEntrenadorDeMano(actual);
        if (e == null) {
            return;
        }
        if (!actual.jugarEntrenador(e, rival)) {
            consola.mostrarMensaje("No se pudo jugar " + e.getNombre() + ".");
        }
    }

    private void colocarEnBanco(Jugador actual) {
        Pokemon p = consola.elegirPokemonDeMano(actual);
        if (p == null) {
            return;
        }
        if (!actual.colocarEnBanco(p)) {
            consola.mostrarMensaje("El banco está lleno.");
        }
    }

    private void retirar(Jugador actual) {
        if (actual.getCampo().getBanco().isEmpty()) {
            consola.mostrarMensaje("No tienes Pokémon en el banco.");
            return;
        }
        int indice = consola.elegirDelBanco(actual, true);   // -1 si cancela
        if (indice == -1) {
            return;
        }
        if (!actual.retirarActivo(indice)) {
            consola.mostrarMensaje("No tienes energía suficiente para retirar.");
        }
    }

    private void atacar(Jugador actual, Jugador rival) {
        if (turno == 1) {
            consola.mostrarMensaje("El primer jugador no puede atacar en su primer turno.");
            return;
        }
        Ataque ataque = consola.elegirAtaque(actual.getCampo().getActivo());
        if (ataque == null) {
            return;
        }
        if (!actual.atacar(rival, ataque)) {
            consola.mostrarMensaje("No puedes usar ese ataque (energía insuficiente o ya atacaste).");
            return;
        }

        if (verificarVictoria()) {
            return;
        }
        while (rival.necesitaReemplazo()) {
            int indice = consola.elegirDelBanco(rival, false);
            rival.reemplazarActivo(indice);
        }
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
