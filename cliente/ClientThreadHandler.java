import java.net.*;
import java.io.*;
import java.util.*;

class ClientThreadHandler extends Thread {
  private String ip;
  private int threadNumber;
  private int portNumber1;
  private String bookName;
  private final String ROOT_DIRECTORY = System.getProperty("user.dir");

  public ClientThreadHandler(String ip, int portNumber1, String bookName) {
    super("ClientThreadHandler");
    this.ip = ip;
    this.portNumber1 = portNumber1;
    this.bookName = bookName.trim();
  }

  @Override
  public void run() {
    try (
      Socket socket = new Socket(ip, portNumber1);
      InputStream is = socket.getInputStream();
      PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
      BufferedReader in = new BufferedReader(new InputStreamReader(is));
      DataInputStream dis = new DataInputStream(is);
    ) {
      out.println("3;" + bookName); //Send option to the server along with file name

      String fileName = dis.readUTF(); //Get file name from the server
      long fileSize = dis.readLong(); //Get file size from the server
      OutputStream fileWriter = new FileOutputStream(ROOT_DIRECTORY + File.separator + fileName);

      int bytesRead;
      byte[] buffer = new byte[8192];
      while (fileSize > 0 && (bytesRead = is.read(buffer, 0, (int)Math.min(buffer.length, fileSize))) > 0)
      {
        fileWriter.write(buffer, 0, bytesRead);
        fileSize -= bytesRead;
      }
      fileWriter.close();
      System.out.println();
    } catch (UnknownHostException e) {
      System.err.println("Host with ip number " + ip + " is unknown.");
      System.exit(1);
    } catch (IOException e) {
      System.err.println("Couldn't stablish I/O with host with ip number " + ip + ".");
      System.exit(1);
    } catch (Exception e) {
      System.err.println(e.getMessage());
      System.exit(1);
    }
  }
}
