public class InvestigacionProfesor extends Entrenador {
    public InvestigacionProfesor() {
        super("Investigación del Profesor", true);
    }

    @Override
    public boolean activar(Jugador jugador, Jugador rival) {
        
        jugador.getMano().clear();
        jugador.robarCarta();
        jugador.robarCarta();
        return true;
    }

    @Override
    public void mostrarInformacion() {
        System.out.println("Nombre: " + getNombre());
        System.out.println("Efecto: Roba 2 cartas de tu mazo.");
    }
}
