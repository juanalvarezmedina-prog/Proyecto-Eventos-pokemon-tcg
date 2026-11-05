import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Mazo {
    private List<Carta> mazo;

    public Mazo() {
        this.mazo = new ArrayList<>();
    }

    public void agregar(Carta carta) {
        mazo.add(carta);
    }

    public void barajar() {
        Collections.shuffle(mazo);
    }

    // Roba la carta de arriba. Devuelve null si el mazo está vacío.
    public Carta robar() {
        if (mazo.isEmpty()) {
            return null;
        }
        return mazo.remove(0);
    }

    // Poké Ball: saca del mazo un Pokémon básico y lo devuelve.
    public Pokemon buscarBasico() {
        for (int i = 0; i < mazo.size(); i++) {
            if (mazo.get(i) instanceof Pokemon) {
                return (Pokemon) mazo.remove(i);
            }
        }
        return null;
    }

    // Mulligan: la mano vuelve al mazo (después se baraja)
    public void devolverCartas(List<Carta> cartas) {
        mazo.addAll(cartas);
    }

    // RF4: exactamente 20 cartas, máximo 2 copias de cada una, mínimo 6 Pokémon
    public boolean esValido() {
        if (mazo.size() != 20) {
            return false;
        }

        Map<String, Integer> copias = new HashMap<>();
        int cantidadPokemons = 0;

        for (Carta carta : mazo) {
            if (carta == null) {
                return false;
            }
            int veces = copias.getOrDefault(carta.getNombre(), 0) + 1;
            if (veces > 2) {
                return false;
            }
            copias.put(carta.getNombre(), veces);

            if (carta instanceof Pokemon) {
                cantidadPokemons++;
            }
        }
        return cantidadPokemons >= 6;
    }

    public boolean estaVacio() {
        return mazo.isEmpty();
    }

    public int cantidadCartas() {
        return mazo.size();
    }
}