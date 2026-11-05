import java.util.ArrayList;
import java.util.List;

public class Misty extends Entrenador {
    public Misty() {
        super("Misty", true);
    }

    // Pokemon de Agua propios (activo y banco) a los que se pueden adjuntar las energias
    @Override
    public List<PokemonEnJuego> objetivosPosibles(Jugador jugador) {
        List<PokemonEnJuego> objetivos = new ArrayList<>();
        Campo campo = jugador.getCampo();
        PokemonEnJuego activo = campo.getActivo();
        if (activo != null && activo.getCarta().getTipo() == TipoEnergia.Agua) {
            objetivos.add(activo);
        }
        for (PokemonEnJuego p : campo.getBanco()) {
            if (p.getCarta().getTipo() == TipoEnergia.Agua) {
                objetivos.add(p);
            }
        }
        return objetivos;
    }

    //si no se elile un pokemon, se la da de una al activo si es de agua
    @Override
    public boolean activar(Jugador jugador, Jugador rival) {
        List<PokemonEnJuego> objetivos = objetivosPosibles(jugador);
        if (objetivos.isEmpty()) {
            return false;
        }
        return activar(jugador, rival, objetivos.get(0));
    }

    @Override
    public boolean activar(Jugador jugador, Jugador rival, PokemonEnJuego objetivo) {
        if (objetivo == null || !objetivosPosibles(jugador).contains(objetivo)) {
            return false;
        }
        for (int i = 0; i < 3; i++) {
            objetivo.adjuntarEnergia(TipoEnergia.Agua);
        }
        return true;
    }

    @Override
    public void mostrarInformacion() {
        System.out.println("Nombre: " + getNombre());
        System.out.println("Efecto: Adjunta 3 Energías de Agua a un Pokémon de Agua en tu campo.");
    }
}