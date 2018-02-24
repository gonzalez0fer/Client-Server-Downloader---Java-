import java.net.*;
import java.io.*;
import java.util.*;

class ClientThreadHandler extends Thread {
  private String ip;
  private int portNumber1;
  private int portNumber2;
  private int portNumber3;
  private String bookName;
  private String clientName;
  private long fileSize = 0;
  private int currentBytesRead = 0;
  private final String ROOT_DIRECTORY = System.getProperty("user.dir");
  private final String DOWNLOAD_DIRECTORY = System.getProperty("user.dir") + 
    File.separator + "downloads";

  public ClientThreadHandler(
    String ip, 
    int portNumber1, 
    int portNumber2, 
    int portNumber3, 
    String bookName,
    String clientName
  ) {
    super("ClientThreadHandler");
    this.ip = ip;
    this.portNumber1 = portNumber1;
    this.portNumber2 = portNumber2;
    this.portNumber3 = portNumber3;
    this.bookName = bookName.trim();
    this.clientName = clientName.trim();
  }

  @Override
  public void run() {
    int tries = 3;

    //Get file size from the server for whichever server finds it first.
    if (!getFileSize(portNumber1) && !getFileSize(portNumber2) && !getFileSize(portNumber3)) {
      System.out.println("Couldn't find book " + bookName + 
        " or couldn't stablish connection to the servers.");
      return;
    }
    if (fileSize == 0) return;

    // Download file concurrently
    while (this.fileSize > 0) {
      if (downloadFile(portNumber1)) {
        continue;
      }
      if (downloadFile(portNumber2)) {
        continue;
      }
      if (downloadFile(portNumber3)) {
        continue;
      }
      if (tries < 1) {
        System.out.println("Couldn't download book " + bookName + ".");
        return;
      }
      try {
        sleep(6000 / tries);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
      tries--;
    }
  }

  private boolean getFileSize(int portNumber) {
    try (
      Socket socket = new Socket(ip, portNumber);
      InputStream is = socket.getInputStream();
      OutputStream os = socket.getOutputStream();
      DataInputStream dis = new DataInputStream(is);
      PrintWriter pw = new PrintWriter(os, true);
    ) {
      pw.println("getFileSize");
      pw.println(bookName);

      this.fileSize = dis.readLong(); //Get file size from the server
    } catch (UnknownHostException e) {
      return false;
    } catch (IOException e) {
      return false;
    } catch (Exception e) {
      System.err.println(e.getMessage());
      return false;
    }
    return true;
  }

  private boolean downloadFile(int portNumber) {
    try (
      Socket socket = new Socket(ip, portNumber);
      InputStream is = socket.getInputStream();
      OutputStream os = socket.getOutputStream();
      DataOutputStream dos = new DataOutputStream(os);
      PrintWriter pw = new PrintWriter(os, true);
    ) {
      pw.println("getFile"); //Send option to the server
      pw.println(bookName); //Send file name to the server
      pw.println(fileSize); //Send file size to the server

      OutputStream fileWriter = 
        new FileOutputStream(DOWNLOAD_DIRECTORY + File.separator + bookName);

      int bytesRead;
      byte[] buffer = new byte[8192];
      while (
        fileSize > 0 && 
        (bytesRead = is.read(buffer, 0, (int)Math.min(buffer.length, this.fileSize))) > 0
      )
      {
        fileWriter.write(buffer, 0, bytesRead);
        fileSize -= bytesRead;
      }
      fileWriter.close();
    } catch (UnknownHostException e) {
      System.err.println("Host with ip number " + ip + " and port " + portNumber + 
        " is unknown.");
      return false;
    } catch (IOException e) {
      System.err.println("Couldn't stablish I/O with host with ip number " + 
        ip + " and port " + portNumber + ".");
      return false;
    } catch (Exception e) {
      System.err.println(e.getMessage());
      return false;
    }
    return true;
  }
}
