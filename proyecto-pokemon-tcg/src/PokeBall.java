public class PokeBall extends Entrenador {
    public PokeBall() {
        super("Poké Ball", false);
    }

    @Override
    public boolean activar(Jugador jugador, Jugador rival) {
        Pokemon basico = jugador.getMazo().buscarBasico();
        if (basico == null) {
            return false;
        }
        jugador.agregarAMano(basico);
        return true;
    }

    @Override 
    public void mostrarInformacion() {
    System.out.println("Nombre " + getNombre());
    System.out.println("Efecto: Busca un Pokémon Básico en tu mazo y añádelo a tu mano.");
    }
}