import java.util.*;

public class MainServer {
  public static void main (String[] args){
    if (args.length < 1) {
      System.out.println("Usage: java MainServer <Port_number>");
      System.out.println();
      System.exit(-1);
    }

    //Validacion numero de puerto.
    int portNumber = 0;
    try {
      portNumber = Integer.parseInt(args[0]);
    } catch (NumberFormatException e) {
      System.out.println("You must indicate a valid number for port number.");
      System.out.println();
      System.exit(-1);
    }

    //Validacion rango de puerto. Menores a 1024 estan reservados para el sistema.
    if (portNumber <= 1024 || portNumber > 65535) {
      System.out.println("Port number must be a number between 1025 and 65535.");
      System.out.println();
      System.exit(-1);
    }

    servidor server = new servidor(portNumber);
    server.iniciar();
  }
}
