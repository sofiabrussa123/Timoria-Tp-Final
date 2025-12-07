package io.github.timoria;

import Red.ClientePrueba;
import Red.HiloServidor;

/* código original, reemplazar después
 * import com.badlogic.gdx.Game;

import interfaces.Menu;

public class Principal extends Game {
	
	public static final float PPM = 100;
    @Override
    public void create() {
    	
    	Menu menu = new Menu(this);
        setScreen(menu);
        }
       }
 */

public class Principal { 

 public static void main(String[] args) throws InterruptedException {
     
     System.out.println("INICIANDO HILO SERVIDOR");
     HiloServidor hiloServidor = new HiloServidor();
     
     //Arrancar hilo en paralelo
     hiloServidor.start(); 
     
     // Dar un pequeño tiempo al servidor para que el socket se inicialice
     Thread.sleep(500); 
     
     ClientePrueba cliente1 = new ClientePrueba();
     
     System.out.println("\nCONECTANDO CLIENTE 1");
     
     // Enviar solicitud de conexión
     cliente1.enviarMensaje("Conectar");
     
     // Esperar y procesar la respuesta
     String response1 = cliente1.recibirMensaje();
     cliente1.procesarRespuestaServidor(response1);

     if (cliente1.getConnected()) {
         
         // 3. Simular la Conexión del Cliente 2 (Debería iniciar la partida)
         System.out.println("\n--- 3. CONECTANDO CLIENTE 2 (Máximo alcanzado) ---");
         ClientePrueba cliente2 = new ClientePrueba();
         cliente2.enviarMensaje("Conectar");
         Thread.sleep(100); 
         
         
         // 4. CLIENTE 1 espera el mensaje 'Empezar'
         System.out.println("\n--- 4. CLIENTE 1 ESPERA 'Empezar' ---");
         String responseStart1 = cliente1.recibirMensaje();
         cliente1.procesarRespuestaServidor(responseStart1);
         
         // 5. CLIENTE 2 espera el mensaje 'Empezar'
         System.out.println("\n--- 5. CLIENTE 2 ESPERA 'Empezar' ---");
         String responseStart2 = cliente2.recibirMensaje();
         cliente2.procesarRespuestaServidor(responseStart2);

         // 6. Simular un movimiento del Cliente 1 si la partida inició
         if ("Empezar".equals(responseStart1)) {
             System.out.println("\n--- 6. SIMULANDO MOVIMIENTO DE CLIENTE 1 ---");
             cliente1.enviarMensaje("Mover:1");
             // El servidor procesará este mensaje e imprimirá: "Mensaje recibido Mover:1"
         }
         
         // 7. Limpiar clientes y servidor
         System.out.println("\n--- 7. TERMINANDO CLIENTES Y SERVIDOR ---");
         cliente1.close();
         cliente2.close();
     } else {
          System.out.println("\n--- La conexión inicial del Cliente 1 falló ---");
     }

     // Finalizar el hilo del servidor de forma limpia
     hiloServidor.terminar();
 }
}
