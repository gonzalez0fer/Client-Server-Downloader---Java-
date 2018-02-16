import java.net.*;
import java.io.*;
import java.util.*;

public class cliente {
  final int portNumber = 9000;
  final String ip = "127.0.0.1";
  final String ROOT_DIRECTORY = System.getProperty("user.dir");

  public void iniciar() {
    System.out.println("Welcome user, would you like to connect to the server? Y/N");
    Scanner scan = new Scanner(System.in);
    String init = scan.next();
    System.out.println();

    if (!init.trim().toLowerCase().equals("y")) {
      System.out.println("See you later.");
      System.exit(1);
    }

    try (
      Socket socket = new Socket(ip, portNumber);
      InputStream is = socket.getInputStream();
      PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
      BufferedReader in = new BufferedReader(
        new InputStreamReader(is));
    ) {
      BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
      String fromServer, fromUser;

      while ((fromServer = in.readLine()) != null) {
        System.out.println(fromServer);
        if (fromServer.equals("Bye")) {
          break;
        }

        System.out.println();
        System.out.println("Choose an option from the menu:");
        System.out.println("    1. ESTADO_DESCARGAS");
        System.out.println("    2. LISTA_LIBROS");
        System.out.println("    3. SOLICITUD_LIBRO");
        System.out.println("    4. LIBROS_DESCARGADOSXSERVIDOR");
        System.out.println("    5. Quit");

        fromUser = stdIn.readLine();
        System.out.println();
        if (fromUser != null) {
          if (fromUser.equals("3")) {
            System.out.print("Indicate file name: ");
            String sFile = stdIn.readLine();
            fromUser += ";" + sFile;

            out.println(fromUser);

            DataInputStream dis = new DataInputStream(is);
            String fileName = dis.readUTF();
            long fileSize = dis.readLong();
            OutputStream fileWriter = new FileOutputStream(ROOT_DIRECTORY + File.separator + fileName);

            System.out.print("File transfer progress bar: ");
            int bytesRead;
            byte[] buffer = new byte[8192]; // or 4096, or more
            while (fileSize > 0 && (bytesRead = is.read(buffer, 0, (int)Math.min(buffer.length, fileSize))) > 0)
            {
              fileWriter.write(buffer, 0, bytesRead);
              fileSize -= bytesRead;
              System.out.print("|"); //Writing progress indicator
            }
            fileWriter.close();
            System.out.println();
          } else {
            out.println(fromUser);
          }
        }
      }
    } catch (UnknownHostException e) {
      System.err.println("Host with ip number " + ip + " is unknown.");
      System.exit(1);
    } catch (IOException e) {
      System.err.println("Couldn't stablish I/O with host with ip number " + ip + ".");
      System.exit(1);
    } catch (Exception e) {
      System.out.println(e);
    }
  }
}
