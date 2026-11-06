import java.util.List;

public abstract class Entrenador extends Carta implements Activable {
    private boolean soporte;

    public Entrenador(String nombre, boolean soporte) {
        super(nombre);
        this.soporte = soporte;
    }

    public boolean esSoporte() {
        return soporte;
    }

    // activar(jugador, rival) lo heredan de Activable y cada carta lo implementa

    // esto es para que mysty elija que pokeom darle la energia
    public List<PokemonEnJuego> objetivosPosibles(Jugador jugador) {
        return List.of();
    }

    // y este es para cuando no se elije a quiern 
    public boolean activar(Jugador jugador, Jugador rival, PokemonEnJuego objetivo) {
        return activar(jugador, rival);
    }
}