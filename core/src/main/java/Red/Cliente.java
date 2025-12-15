package Red;

import java.net.InetAddress;

public class Cliente {

    private String id;
    private int num;
    private InetAddress ip;
    private int puerto;

    public Cliente(int num, InetAddress ip, int puerto) {
        this.num = num;
        this.id = ip.toString() + ":" + puerto;
        this.ip = ip;
        this.puerto = puerto;
    }

    public String getId() {
        return id;
    }

    public InetAddress getIp() {
        return ip;
    }

    public int getPuerto() {
        return puerto;
    }

    public int getNum() {
        return num;
    }
}
