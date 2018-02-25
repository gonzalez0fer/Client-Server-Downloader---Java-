import java.net.*;
import java.nio.file.Files;
import java.nio.file.Paths;
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
  private long currentBytesRead = 0;
  private final String ROOT_DIRECTORY = System.getProperty("user.dir");
  private final String DOWNLOAD_DIRECTORY = ROOT_DIRECTORY + File.separator + "downloads";
  private final String TEMP_DIRECTORY = ROOT_DIRECTORY + File.separator + "temp";

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

    //Get file size from the server, whichever server finds it first.
    if (!getFileSize(portNumber1) && !getFileSize(portNumber2) && !getFileSize(portNumber3)) {
      System.out.println("Couldn't find book " + bookName + 
        " or couldn't stablish connection to the servers.");
      return;
    }
    if (fileSize == 0) return;
    currentBytesRead = fileSize;

    // Download file concurrently
    while (currentBytesRead > 0) {
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
        sleep(6000 / tries); //Trying to connect in 6 secs divided by the number of tries left
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
      tries--;
    }
  }

//#region Get file size method
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
//#endregion

//#region Download file method
  private boolean downloadFile(int portNumber) {
    try (
      Socket socket = new Socket(ip, portNumber);
      InputStream is = socket.getInputStream();
      OutputStream os = socket.getOutputStream();
      DataOutputStream dos = new DataOutputStream(os);
      PrintWriter pw = new PrintWriter(os, true);
    ) {
      String path = DOWNLOAD_DIRECTORY + File.separator + bookName;
      FileOutputStream fileWriter;
      pw.println("getFile"); //Send option to the server
      pw.println(bookName); //Send file name to the server
      pw.println(fileSize); //Send file size to the server

      int bytesRead;
      byte[] buffer = new byte[8192];
      long half = fileSize / 2;

      //Send data to resume if connection was lost
      String status = readDownloadStatusFile();
      if (!status.equals("") && !status.equals("0")) {
        try {
          pw.println(Long.parseLong(status));
          currentBytesRead = Long.parseLong(status);
        } catch (NumberFormatException e) { pw.println(0); }
        fileWriter = new FileOutputStream(path, true);
      } else {
        fileWriter = new FileOutputStream(path, false);
        pw.println(0);
      }

      while ( (bytesRead = is.read(buffer)) != -1 )
      {
        fileWriter.write(buffer, 0, bytesRead);
        currentBytesRead -= bytesRead;
        if (currentBytesRead <= half) {
          writeDownloadStatusFile(currentBytesRead + "");
        }
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
//#endregion

//#region Write how much left to download from the file
  private void writeDownloadStatusFile(String text) {
    File statusFile = new File(TEMP_DIRECTORY + File.separator + bookName + ".txt");
    try (
      BufferedWriter writer = new BufferedWriter(new FileWriter(statusFile));
    ) {
      writer.write(text);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
//#endregion

//#region Read how much left to download from the file
  private String readDownloadStatusFile() {
    String path = TEMP_DIRECTORY + File.separator + bookName + ".txt";
    String status = "";

    if (!Files.exists(Paths.get(path)) || Files.isDirectory(Paths.get(path)))
      return status;

    try (BufferedReader br = new BufferedReader(new FileReader(path))) {
      status = br.readLine();
    } catch (IOException e) { }

    return status;
  }
//#endregion
}
