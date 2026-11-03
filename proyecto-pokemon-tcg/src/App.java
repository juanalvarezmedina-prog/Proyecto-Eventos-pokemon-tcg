public class App {
    public static void main(String[] args) {
        Consola consola = new Consola();
        System.out.println("Bienvenido a POKEMON TCG");
        Jugador player1 = crearJugador(consola, 1);
        Jugador player2 = crearJugador(consola, 2);
        new Partida(player1, player2, consola).iniciar();
    }

    private static Jugador crearJugador(Consola consola, int numero) {
        String nombre = consola.leerTexto("Jugador " + numero + ", ingresa tu nombre:");
        System.out.println("Elige tu mazo:");
        System.out.println("1. Fuego\n2. Agua\n3. Eléctrico");
        int opcion = consola.leerOpcion(1, 3);
        return switch (opcion) {
            case 1 -> new Jugador(nombre, CartaFactory.mazoFuego(), TipoEnergia.Fuego);
            case 2 -> new Jugador(nombre, CartaFactory.mazoAgua(), TipoEnergia.Agua);
            default -> new Jugador(nombre, CartaFactory.mazoElectrico(), TipoEnergia.Electrico);
        };
    }
}