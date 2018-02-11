import java.net.*;
import java.io.*;
import java.util.*;


public class servidor{
  ServerSocket server;
  Socket socket;
  int puerto = 9000;
  DataOutputStream salida;
  BufferedReader entrada;

  public void iniciar(){
    try {
      server = new ServerSocket(puerto);
      socket = new Socket();
      socket = server.accept();

      entrada = new BufferedReader(new InputStreamReader(socket.getInputStream()));
      String mensaje = entrada.readLine();
      System.out.println(mensaje);
      salida = new DataOutputStream(socket.getOutputStream());
      salida.writeUTF("recibido");
    }catch (Exception e){};

  }
}
