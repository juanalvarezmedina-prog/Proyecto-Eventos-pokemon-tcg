public class TurboVelocidad extends Entrenador {
    public TurboVelocidad() {
        super("Turbo Velocidad", false);
    }

    @Override
    public boolean activar(Jugador jugador, Jugador rival) {
        PokemonEnJuego activo = jugador.getCampo().getActivo();
        if (activo == null) {
            return false;
        }
        activo.aplicarTurbo();
        return true;
    }

    @Override 
    public void mostrarInformacion() {
        System.out.println("Nombre: " + getNombre());
        System.out.println("Efecto: reduce en 1 el costo de retirada del Pokémon Activo este turno.");
    }
}
