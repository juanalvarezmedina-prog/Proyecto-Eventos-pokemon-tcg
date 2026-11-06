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

   
    public Pokemon buscarBasico() {
        for (int i = 0; i < mazo.size(); i++) {
            if (mazo.get(i) instanceof Pokemon) {
                return (Pokemon) mazo.remove(i);
            }
        }
        return null;
    }

   
    public void devolverCartas(List<Carta> cartas) {
        mazo.addAll(cartas);
    }

    
    public boolean esValido() {
        if (mazo.size() != 20) {
            return false;
        }

        Map<String, Integer> copias = new HashMap<>();
        int cantidadPokemons = 0;

        for (Carta carta : mazo) {
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