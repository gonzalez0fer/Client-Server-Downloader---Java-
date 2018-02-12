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
      salida = new DataOutputStream(socket.getOutputStream());
      String mensaje = entrada.readLine();

      if (mensaje.equals("LISTA_LIBROS")) {

        String sDirectorio = "/home/fernando/Universidad/Redes/Cliente-Servidor/servidor/books";
        File f = new File(sDirectorio);
        File[] ficheros = f.listFiles();
        for (int x=0;x<ficheros.length;x++){
          salida.writeUTF(ficheros[x].getName());
        }

      }
    salida.writeUTF("recibido");
    }catch (Exception e){};

  }
}
