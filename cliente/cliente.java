import java.net.*;
import java.io.*;
import java.util.*;

public class cliente {
  int portNumber = 9000;
  String ip = "127.0.0.1";

  public void iniciar() {
    System.out.println("Bienvenido usuario, desea conectarse? S/N");
    Scanner scan = new Scanner(System.in);
    String init = scan.next();

    if (!init.trim().toLowerCase().equals("s")) {
      System.out.println("Hasta luego");
      System.exit(1);
    }

    try (
      Socket client = new Socket(ip, portNumber);
      PrintStream out = new PrintStream(client.getOutputStream());
      BufferedReader in = new BufferedReader(
        new InputStreamReader(client.getInputStream()));
    ) {
      BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
      String fromServer, fromUser;

      while ((fromServer = in.readLine()) != null) {
        if (fromServer.equals("Bye")) {
          break;
        }
        //System.out.println("Server: " + fromServer);
        System.out.println(fromServer);

        System.out.println();
        System.out.println("Indique un numero del menu de opciones:");
        System.out.println("1. ESTADO_DESCARGAS");
        System.out.println("2. LISTA_LIBROS");
        System.out.println("3. SOLICITUD_LIBRO");
        System.out.println("4. LIBROS_DESCARGADOSXSERVIDOR");
        System.out.println("5. SALIR");

        fromUser = stdIn.readLine();
        if (fromUser != null) {
          if (fromUser.equals("3")) {
            System.out.println("Indique el libro que desea descargar:");
            String book = scan.next();
          }

          //System.out.println("Client: " + fromUser);
          out.println(fromUser);
        }
      }
    } catch (UnknownHostException e) {
      System.err.println("No conoce el host con ip " + ip);
      System.exit(1);
    } catch (IOException e) {
      System.err.println("No pudo obtener I/O para la conexión con " + ip);
      System.exit(1);
    } catch (Exception e) {
      System.out.println(e);
    }
  }
}
