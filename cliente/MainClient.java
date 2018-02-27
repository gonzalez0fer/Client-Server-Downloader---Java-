import java.util.*;

public class MainClient {
  public static void main (String[] args){
    //Ports for connection
    int portNumber1 = 9000;
    int portNumber2 = 9001;
    int portNumber3 = 9002;

    File tempDir = new File(System.getProperty("user.dir") + File.separator + "temp");
    File downloadsDir = new File(System.getProperty("user.dir") + File.separator + "downloads");
    if (!tempDir.exists()){
      tempDir.mkdir();
    }
    if (!downloadsDir.exists()){
      downloadsDir.mkdir();
    }

    ClientHandler client = new ClientHandler(portNumber1, portNumber2, portNumber3);
    client.start();
  }
}
