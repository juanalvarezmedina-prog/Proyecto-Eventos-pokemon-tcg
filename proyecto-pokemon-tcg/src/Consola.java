import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

// se encarga de mostrar la informacion en la consola y de capturar la entrada del usuario
public class Consola {
    private static final int PUNTOS_PARA_GANAR = 3;

    private final Scanner scanner = new Scanner(System.in);

    // Mensajes de lo que paso en la(s) ultima(s) accion(es). Se muestran al redibujar y luego se borran
    private final List<String> registro = new ArrayList<>();

    public void mostrarMensaje(String mensaje) {
        System.out.println(mensaje);
    }

    // Guarda un mensaje para mostrarlo despues de limpiar la pantalla
    public void registrarAccion(String mensaje) {
        registro.add(mensaje);
    }

    
    public void limpiarPantalla() {
        System.out.print("\033[H\033[2J\033[3J");
        System.out.flush();
    }

    // Limpia la pantalla y muestra el registro de acciones 
    public void mostrarRegistro() {
        limpiarPantalla();
        if (registro.isEmpty()) {
            return;
        }
        System.out.println("Ultimas acciones:");
        for (String mensaje : registro) {
            System.out.println("  " + mensaje.replace("\n", "\n  "));
        }
        System.out.println();
    }

    
    public String capturarSalida(Runnable accion) {
        PrintStream original = System.out;
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        System.setOut(new PrintStream(buffer, true, StandardCharsets.UTF_8));
        try {
            accion.run();
        } finally {
            System.setOut(original);
        }
        return buffer.toString(StandardCharsets.UTF_8).trim();
    }

    public int leerOpcion(int min, int max) {
        while (true) {
            System.out.print("> ");
            String linea = scanner.nextLine().trim();
            try {
                int opcion = Integer.parseInt(linea);
                if (opcion >= min && opcion <= max) {
                    return opcion;
                }
            } catch (NumberFormatException e) {
                // se pide de nuevo
            }
            System.out.println("Opcion invalida. Escribe un numero entre " + min + " y " + max + ".");
        }
    }

    public boolean preguntarSiNo(String pregunta) {
        while (true) {
            System.out.print(pregunta + " (s/n): ");
            String r = scanner.nextLine().trim().toLowerCase();
            if (r.equals("s")) {
                return true;
            }
            if (r.equals("n")) {
                return false;
            }
            System.out.println("Responde s o n.");
        }
    }

    public void mostrarCampo(Jugador actual, Jugador rival, int turno) {
        mostrarRegistro();
        registro.clear();
        System.out.println("TURNO " + turno + " - " + actual.getNombre());
        System.out.println();

        System.out.println("RIVAL (" + rival.getNombre() + ")");
        System.out.println(descripcionCampo(rival.getCampo()));
        System.out.println("Puntos de premio rival: " + rival.getPuntosPremio() + "/" + PUNTOS_PARA_GANAR);
        System.out.println();

        System.out.println("TU CAMPO (" + actual.getNombre() + ")");
        System.out.println(descripcionCampo(actual.getCampo()));
        System.out.println("Puntos de premio propios: " + actual.getPuntosPremio() + "/" + PUNTOS_PARA_GANAR);
        System.out.println();

        TipoEnergia zona = actual.getEnergiaDisponible();
        if (zona != null) {
            System.out.println("Zona de energia: " + zona + " (disponible)");
        } else {
            System.out.println("Zona de energia: ya usada");
        }

        List<Carta> mano = actual.getMano();
        System.out.println("Tu mano (" + mano.size() + " cartas):");
        for (int i = 0; i < mano.size(); i++) {
            System.out.println((i + 1) + ". " + mano.get(i).getNombre());
        }
        System.out.println();
    }

    public void mostrarMenuAcciones() {
        System.out.println("Que deseas hacer?");
        System.out.println("1. Adjuntar energia de zona");
        System.out.println("2. Jugar carta de entrenador");
        System.out.println("3. Colocar Pokemon en banco");
        System.out.println("4. Retirar Pokemon activo");
        System.out.println("5. Atacar");
        System.out.println("6. Pasar turno");
    }


    public Pokemon elegirPokemonDeMano(Jugador jugador) {
        List<Pokemon> opciones = new ArrayList<>();
        for (Carta c : jugador.getMano()) {
            if (c instanceof Pokemon) {
                opciones.add((Pokemon) c);
            }
        }
        if (opciones.isEmpty()) {
            registrarAccion("No tienes Pokemon en la mano.");
            return null;
        }

        boolean puedeCancelar = jugador.getCampo().getActivo() != null;
        System.out.println(jugador.getNombre() + ", elige un Pokemon de tu mano:");
        for (int i = 0; i < opciones.size(); i++) {
            Pokemon p = opciones.get(i);
            System.out.println((i + 1) + ". " + p.getNombre() + " (" + p.getPsMaximos() + " PS)");
        }
        if (puedeCancelar) {
            System.out.println("0. Cancelar");
        }

        int opcion = leerOpcion(puedeCancelar ? 0 : 1, opciones.size());
        return opcion == 0 ? null : opciones.get(opcion - 1);
    }

    // Activo y banco propios. Devuelve null si cancela.
    public PokemonEnJuego elegirPokemonPropio(Jugador jugador) {
        List<PokemonEnJuego> opciones = new ArrayList<>();
        Campo campo = jugador.getCampo();
        if (campo.getActivo() != null) {
            opciones.add(campo.getActivo());
        }
        opciones.addAll(campo.getBanco());

        if (opciones.isEmpty()) {
            registrarAccion("No tienes Pokemon en juego.");
            return null;
        }

        System.out.println("Elige un Pokemon:");
        for (int i = 0; i < opciones.size(); i++) {
            PokemonEnJuego p = opciones.get(i);
            String lugar = (p == campo.getActivo()) ? "Activo" : "Banco";
            System.out.println((i + 1) + ". [" + lugar + "] " + descripcionPokemon(p));
        }
        System.out.println("0. Cancelar");

        int opcion = leerOpcion(0, opciones.size());
        return opcion == 0 ? null : opciones.get(opcion - 1);
    }

    
    public PokemonEnJuego elegirObjetivo(Jugador jugador, List<PokemonEnJuego> opciones) {
        Campo campo = jugador.getCampo();
        System.out.println("Elige un Pokemon:");
        for (int i = 0; i < opciones.size(); i++) {
            PokemonEnJuego p = opciones.get(i);
            String lugar = (p == campo.getActivo()) ? "Activo" : "Banco";
            System.out.println((i + 1) + ". [" + lugar + "] " + descripcionPokemon(p));
        }
        System.out.println("0. Cancelar");

        int opcion = leerOpcion(0, opciones.size());
        return opcion == 0 ? null : opciones.get(opcion - 1);
    }

    // Devuelve null si cancela o no hay entrenadores en la mano.
    public Entrenador elegirEntrenadorDeMano(Jugador jugador) {
        List<Entrenador> opciones = new ArrayList<>();
        for (Carta c : jugador.getMano()) {
            if (c instanceof Entrenador) {
                opciones.add((Entrenador) c);
            }
        }
        if (opciones.isEmpty()) {
            registrarAccion("No tienes cartas de entrenador en la mano.");
            return null;
        }

        System.out.println("Elige una carta de entrenador:");
        for (int i = 0; i < opciones.size(); i++) {
            System.out.println((i + 1) + ". " + opciones.get(i).getNombre());
        }
        System.out.println("0. Cancelar");

        int opcion = leerOpcion(0, opciones.size());
        return opcion == 0 ? null : opciones.get(opcion - 1);
    }


    public int elegirDelBanco(Jugador jugador, boolean puedeCancelar) {
        List<PokemonEnJuego> banco = jugador.getCampo().getBanco();
        if (banco.isEmpty()) {
            return -1;
        }

        System.out.println(jugador.getNombre() + ", elige un Pokemon del banco:");
        for (int i = 0; i < banco.size(); i++) {
            System.out.println((i + 1) + ". " + descripcionPokemon(banco.get(i)));
        }
        if (puedeCancelar) {
            System.out.println("0. Cancelar");
        }

        int opcion = leerOpcion(puedeCancelar ? 0 : 1, banco.size());
        return opcion == 0 ? -1 : opcion - 1;
    }

  
    public Ataque elegirAtaque(PokemonEnJuego activo) {
        if (activo == null) {
            registrarAccion("No tienes Pokemon activo.");
            return null;
        }
        List<Ataque> ataques = activo.getCarta().getAtaques();
        if (ataques.isEmpty()) {
            registrarAccion("Este Pokemon no tiene ataques.");
            return null;
        }

        System.out.println("Ataques de " + activo.getCarta().getNombre() + ":");
        for (int i = 0; i < ataques.size(); i++) {
            Ataque a = ataques.get(i);
            System.out.println((i + 1) + ". " + a.getNombre() + " (costo: " + a.getCosto() + " "
                    + a.getTipoEnergia() + ", dano: " + a.getDano() + ")");
        }
        System.out.println("0. Cancelar");

        int opcion = leerOpcion(0, ataques.size());
        return opcion == 0 ? null : ataques.get(opcion - 1);
    }

    public void anunciarGanador(Jugador ganador) {
        mostrarRegistro();
        registro.clear();
        System.out.println("   ¡EL DUELO HA TERMINADO!");
        System.out.println();
        System.out.println("   ¡" + ganador.getNombre() + " se corona como nuevo Maestro Pokémon!");
        System.out.println("   Puntos de premio: " + ganador.getPuntosPremio() + "/" + PUNTOS_PARA_GANAR);
    }


    private String descripcionPokemon(PokemonEnJuego p) {
        return p.getCarta().getNombre() + " - PS: " + p.getPsActuales() + "/" + p.getCarta().getPsMaximos()
                + " - Energia: " + energias(p);
    }

    private String energias(PokemonEnJuego p) {
        if (p.getEnergias().isEmpty()) {
            return "ninguna";
        }
        StringBuilder sb = new StringBuilder();
        for (TipoEnergia e : p.getEnergias()) {
            sb.append(e).append(" ");
        }
        return sb.toString().trim();
    }

    private String descripcionCampo(Campo campo) {
        StringBuilder sb = new StringBuilder();
        if (campo.getActivo() == null) {
            sb.append("[Activo] ninguno");
        } else {
            sb.append("[Activo] ").append(descripcionPokemon(campo.getActivo()));
        }
        sb.append("\n[Banco]");
        if (campo.getBanco().isEmpty()) {
            sb.append(" vacio");
        } else {
            for (PokemonEnJuego p : campo.getBanco()) {
                sb.append("\n  ").append(descripcionPokemon(p));
            }
        }
        return sb.toString();
    }
    public String leerTexto(String pregunta) {
        System.out.print(pregunta + " ");
        return scanner.nextLine().trim();
    }
}
//no me gusta java