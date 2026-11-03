import java.util.ArrayList;
import java.util.List;
import java.util.Collections;

public class Jugador {
    private static final int ManoInicial = 5;

    private String nombre;
    private Mazo mazo;
    private List<Carta> mano;
    private Campo campo;
    private int puntosPremio;
    private TipoEnergia tipoZona;          
    private TipoEnergia energiaDisponible; 
    private boolean yaUsoSoporte;
    private boolean yaAtaco;

    public Jugador(String nombre, Mazo mazo, TipoEnergia tipoZona) {
        this.nombre = nombre;
        this.mazo = mazo;
        this.tipoZona = tipoZona;
        this.mano = new ArrayList<>();
        this.campo = new Campo();
        this.puntosPremio = 0;
        this.energiaDisponible = null;
        this.yaUsoSoporte = false;
        this.yaAtaco = false;
    }

    public String getNombre() {
        return nombre;
    }

    public List<Carta> getMano() {
    return Collections.unmodifiableList(mano);
}

public void agregarAMano(Carta carta) {
    mano.add(carta);
}

public void descartarMano() {
    mano.clear();
}

    public Campo getCampo() {
        return campo;
    }

    public Mazo getMazo() {
        return mazo;
    }

    public int getPuntosPremio() {
        return puntosPremio;
    }

    public TipoEnergia getEnergiaDisponible() {
        return energiaDisponible;
    }

    // Reinicia lo que cambia cada turno y genera la energía de la zona
    public void iniciarTurno() {
        yaUsoSoporte = false;
        yaAtaco = false;
        energiaDisponible = tipoZona;

        if (campo.getActivo() != null) {
            campo.getActivo().reiniciarEfectosTurno();
        }
        for (PokemonEnJuego p : campo.getBanco()) {
            p.reiniciarEfectosTurno();
        }
    }

    // false si el mazo está vacío (Partida decide que pierde)
    public boolean robarCarta() {
        if (mazo.estaVacio()) {
            return false;
        }
        mano.add(mazo.robar());
        return true;
    }

    public boolean adjuntarEnergiaDeZona(PokemonEnJuego destino) {
        if (energiaDisponible == null || destino == null) {
            return false;
        }
        boolean esPropio = destino == campo.getActivo() || campo.getBanco().contains(destino);
        if (!esPropio) {
            return false;
        }
        destino.adjuntarEnergia(energiaDisponible);
        energiaDisponible = null;
        return true;
    }

    public boolean jugarEntrenador(Entrenador c, Jugador rival) {
        if (!mano.contains(c)) {
            return false;
        }
        if (c.esSoporte() && yaUsoSoporte) {
            return false;
        }
        // Polimorfismo porque no sabemos qué carta es, cada una ejecuta su propio efecto
        boolean seJugo = c.activar(this, rival);
        if (seJugo) {
            if (c.esSoporte()) {
                yaUsoSoporte = true;
            }
            mano.remove(c);
        }
        return seJugo;
    }

    public boolean colocarEnBanco(Pokemon c) {
        if (!mano.contains(c) || !campo.hayEspacioEnBanco()) {
            return false;
        }
        campo.colocarEnBanco(new PokemonEnJuego(c));
        mano.remove(c);
        return true;
    }

    public boolean retirarActivo(int indiceBanco) {
        PokemonEnJuego activo = campo.getActivo();
        if (activo == null || indiceBanco < 0 || indiceBanco >= campo.getBanco().size()) {
            return false;
        }
        int costo = activo.getCostoRetiradaActual();
        if (activo.getEnergias().size() < costo) {
            return false;
        }
        activo.descartarEnergias(costo);
        campo.intercambiarActivo(indiceBanco);
        return true;
    }

    public boolean atacar(Jugador rival, Ataque ataque) {
        PokemonEnJuego atacante = campo.getActivo();
        PokemonEnJuego defensor = rival.getCampo().getActivo();

        if (yaAtaco || atacante == null || defensor == null) {
            return false;
        }
        if (!atacante.getCarta().getAtaques().contains(ataque)) {
            return false;
        }
        if (!ataque.puedeUsarse(atacante.getEnergias())) {
            return false;
        }

        int dano = ataque.getDano();
        if (defensor.getCarta().getTipo().esDebilA(atacante.getCarta().getTipo())) {
            dano += 20;
        }
        defensor.recibirDano(dano);
        yaAtaco = true;

        if (defensor.estaDerrotado()) {
            sumarPuntos(defensor.getCarta().getPuntosPremio());
            rival.getCampo().eliminarActivo();
        }
        return true;
    }

    public void sumarPuntos(int n) {
        puntosPremio += n;
    }

    public boolean tieneBasicoEnMano() {
        for (Carta c : mano) {
            if (c instanceof Pokemon) {
                return true;
            }
        }
        return false;
    }

    // Devuelve la mano al mazo, baraja y reparte 5. Sirve para el reparto inicial y para el mulligan
    public void hacerMulligan() {
        mazo.devolverCartas(mano);
        mano.clear();
        mazo.barajar();
        for (int i = 0; i < ManoInicial; i++) {
            robarCarta();
        }
    }
    public boolean colocarActivoDesdeMano(Pokemon c) {
    if (!mano.contains(c) || campo.getActivo() != null) {
        return false;
    }
    campo.setActivo(new PokemonEnJuego(c));
    mano.remove(c);
    return true;
    }
    public boolean necesitaReemplazo() {
    return campo.getActivo() == null && campo.tienePokemonEnJuego();
}

public boolean reemplazarActivo(int indiceBanco) {
    if (campo.getActivo() != null) {
        return false;
    }
    if (indiceBanco < 0 || indiceBanco >= campo.getBanco().size()) {
        return false;
    }
    campo.promoverDelBanco(indiceBanco);
    return true;
}
}
