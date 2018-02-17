import java.net.*;
import java.io.*;
import java.util.*;

public class servidor {
  final int portNumber = 9000;

  public void iniciar() {
    boolean listening = true;
    int i = 1;
    try (ServerSocket server = new ServerSocket(portNumber)) {
      while (listening) {
        new LibraryHandler(server.accept(), i).start();
        System.out.println("Thread " + i + " open.");
        System.out.println("Cliente con la IP " + server.getInetAddress().getHostName() + " conectado.");
      }
    } catch (IOException e) {
      System.err.println("Can't listen on port number " + portNumber + ".");
      System.exit(-1);
    } catch (Exception e) {
      e.printStackTrace();
      System.exit(-1);
    }
  }
}
