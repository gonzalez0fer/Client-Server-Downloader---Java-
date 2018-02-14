import java.net.*;
import java.io.*;
import java.util.*;

public class servidor {
  int portNumber = 9000;

  public void iniciar() {
    boolean listening = true;
    int i = 1;
    try (ServerSocket server = new ServerSocket(portNumber)) {
      while (listening) {
        new LibraryHandler(server.accept(), i).start();
        System.out.println("Thread " + i + " open");
        i++;
      }
    } catch (IOException e) {
      System.err.println("No se pudo escuchar en el puerto " + portNumber);
      System.exit(-1);
    } catch (Exception e) {
      System.out.println(e);
      System.exit(-1);
    }
  }
}
