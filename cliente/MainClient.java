import java.util.*;

public class MainClient {
  public static void main (String[] args){
    if (args.length < 3) {
      System.out.println("Usage: java MainClient <Port_number1> <Port_number2> <Port_number3>");
      System.out.println();
      System.exit(-1);
    }

    //Validacion numero de puertos.
    int portNumber1 = 0;
    int portNumber2 = 0;
    int portNumber3 = 0;
    try {
      portNumber1 = Integer.parseInt(args[0]);
      portNumber2 = Integer.parseInt(args[1]);
      portNumber3 = Integer.parseInt(args[2]);
    } catch (NumberFormatException e) {
      System.out.println("You must indicate a valid number for port numbers.");
      System.out.println();
      System.exit(-1);
    }

    //Validacion rango de puertos. Menores a 1024 estan reservados para el sistema.
    if (portNumber1 <= 1024 || portNumber1 > 65535 || 
      portNumber2 <= 1024 || portNumber2 > 65535 ||
      portNumber3 <= 1024 || portNumber3 > 65535) {
      System.out.println("Port numbers must be numbers between 1025 and 65535.");
      System.out.println();
      System.exit(-1);
    }

    ClientHandler c = new ClientHandler(portNumber1, portNumber2, portNumber3);
    c.start();
  }
}
