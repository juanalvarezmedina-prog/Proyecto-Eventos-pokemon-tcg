import java.util.List;

public class Ataque {
    private final String nombre;
    private final TipoEnergia tipoEnergia;
    private final byte costo;
    private final short dano;
// FINAL ES PARA QUE EN NINGUN LADO EL ATRIBUTO PUEDA SER MODIFICADO, SOLO SE PUEDE MODIFICAR EN EL CONSTRUCTOR

    public Ataque(String nombre, TipoEnergia tipoEnergia, byte costo, short dano) {
        this.nombre = nombre;
        this.tipoEnergia = tipoEnergia;
        this.costo = costo;
        this.dano = dano;
    }
    public String getNombre() {
        return nombre;
    }
    public byte getCosto() {
        return costo;
    }
    public TipoEnergia getTipoEnergia() {
        return tipoEnergia;
    }
    public short getDano() {
        return dano;
    }

    public boolean puedeUsarse(List<TipoEnergia> energiasAgregadas) {
        if (tipoEnergia == TipoEnergia.Incoloro) {
            return energiasAgregadas.size() >= costo;
        }
        int Acorde = 0;
        for (TipoEnergia energia : energiasAgregadas) {
            if (energia == tipoEnergia) {
                Acorde++;
            }
        }
        return Acorde >= costo;
    }
}

