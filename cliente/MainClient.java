import java.util.*;

public class MainClient {
  public static void main (String[] args){
    //Ports for connection
    int portNumber1 = 9000;
    int portNumber2 = 9001;
    int portNumber3 = 9002;

    ClientHandler client = new ClientHandler(portNumber1, portNumber2, portNumber3);
    client.start();
  }
}
