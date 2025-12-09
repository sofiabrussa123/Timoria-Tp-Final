package personajes.accesorios;

public class MejoraTemporal {

    private static final int MAX_MEJORAS = 3;
    private static final float BONUS_POR_MEJORA = 2f;

    private int mejorasVida;
    private int mejorasVelocidad;
    private int mejorasSalto;

    public MejoraTemporal() {
        mejorasVida = 0;
        mejorasVelocidad = 0;
        mejorasSalto = 0;
    }

    public boolean mejorarVida() {
        if (mejorasVida < MAX_MEJORAS) {
            mejorasVida++;
            return true;
        }
        return false;
    }

    public boolean mejorarVelocidad() {
        if (mejorasVelocidad < MAX_MEJORAS) {
            mejorasVelocidad++;
            return true;
        }
        return false;
    }

    public boolean mejorarSalto() {
        if (mejorasSalto < MAX_MEJORAS) {
            mejorasSalto++;
            return true;
        }
        return false;
    }

    public float getBonusVida() {
        return mejorasVida * BONUS_POR_MEJORA;
    }

    public float getBonusVelocidad() {
        return mejorasVelocidad * BONUS_POR_MEJORA;
    }

    public float getBonusSalto() {
        return mejorasSalto * BONUS_POR_MEJORA;
    }

    // Para la UI
    public int getMejorasVida() { return mejorasVida; }
    public int getMejorasVelocidad() { return mejorasVelocidad; }
    public int getMejorasSalto() { return mejorasSalto; }

    public int getMaxMejoras() { return MAX_MEJORAS; }

    public void reset() {
        mejorasVida = 0;
        mejorasVelocidad = 0;
        mejorasSalto = 0;
    }
}
