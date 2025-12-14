package interfaces;

public interface ConectionManager {
    void asignarId(int id);
    void empezarHistoria();
    void empezarJuego();
    void servidorDesconectado();
    void reiniciarNivel(); // ✅ NUEVO
}
