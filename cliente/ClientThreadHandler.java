import java.net.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.io.*;
import java.util.*;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

class ClientThreadHandler extends Thread {
  private String ip;
  private String ip2;
  private String ip3;
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
    String ip2, 
    String ip3, 
    int portNumber1, 
    int portNumber2, 
    int portNumber3, 
    String bookName,
    String clientName
  ) {
    super("ClientThreadHandler");
    this.ip = ip;
    this.ip2 = ip2;
    this.ip3 = ip3;
    this.portNumber1 = portNumber1;
    this.portNumber2 = portNumber2;
    this.portNumber3 = portNumber3;
    this.bookName = bookName.trim();
    this.clientName = clientName.trim();
  }

  @Override
  public void run() {
    int tries = 3;    

    // Download file concurrently
    do {
      if (getFileSize(ip, portNumber1) && downloadFile(ip, portNumber1, 1)) {
        continue;
      }
      if (getFileSize(ip2, portNumber2) && downloadFile(ip2, portNumber2, 2)) 
      {
        continue;
      }
      if (getFileSize(ip3, portNumber3) && downloadFile(ip3, portNumber3, 3)) {
        continue;
      }
      System.out.println();
      if (tries < 1) {
        System.out.println("Couldn't download book " + bookName + ".");
        return;
      }
      if (currentBytesRead <= 0) {
      	System.out.println("Book " + bookName + " couldn't be found.");
      } else {
      	System.out.println("Connection failed, will retry in " + (6000/(tries*1000)) + " seconds.");
      }

      try {
        sleep(6000 / tries); //Trying to connect in 6 secs divided by the number of tries left
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
      tries--;
    } while (currentBytesRead > 0);
  }

//#region Get file size method
  private boolean getFileSize(String ip, int portNumber) {
    try (
      Socket socket = new Socket(ip, portNumber);
      InputStream is = socket.getInputStream();
      OutputStream os = socket.getOutputStream();
      DataInputStream dis = new DataInputStream(is);
      BufferedReader br = new BufferedReader(new InputStreamReader(is));
      PrintWriter pw = new PrintWriter(os, true);
    ) {
      pw.println("getFileSize");
      pw.println(bookName);

      // this.fileSize = dis.readLong(); //Get file size from the server
      this.fileSize = Long.parseLong(br.readLine()); //Get file size from the server
      if (this.fileSize <= 0) {
        return false;      	
      }
      currentBytesRead = fileSize;
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
  private boolean downloadFile(String ip, int portNumber, int serverNumber) {
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
      pw.println(clientName); //Send name of the client doing the request to the server

      //Download file from the server
      while ( (bytesRead = is.read(buffer)) != -1 )
      {
        fileWriter.write(buffer, 0, bytesRead);
        currentBytesRead -= bytesRead;
        if (currentBytesRead <= half) { //If at least half the file has been downloaded it saves progress
          writeDownloadStatusFile(currentBytesRead + "");
        }
      }
      fileWriter.close();

      //Write JSON file with name of file downloaded from server
      writeJson(serverNumber);
    } catch (UnknownHostException e) {
      // System.err.println("Host with ip number " + ip + " and port " + portNumber + " is unknown.");
      return false;
    } catch (IOException e) {
      // System.err.println("Couldn't stablish I/O with host with ip number " + ip + " and port " + portNumber + ".");
      return false;
    } catch (Exception e) {
      System.err.println(e.getMessage());
      return false;
    }
    return true;
  }
//#endregion

//#region Write JSON file with downloads
  private void writeJson(int serverNumber) {
    String path = TEMP_DIRECTORY + File.separator + "downloads.json";
    JSONParser parser = new JSONParser();
    JSONObject jsonObj = new JSONObject();
    JSONArray files = new JSONArray();

    //Verify if the server is already added to add the book to the list and if
    //it's not then creates a new node for it
    try (FileReader reader = new FileReader(path)) {
      jsonObj = (JSONObject)parser.parse(reader);
      files = (JSONArray)jsonObj.get("Server" + serverNumber);

      if (files == null) { files = new JSONArray(); }
    } catch (FileNotFoundException e) {
      //
    } catch (Exception e) {
      e.printStackTrace();
    }

    Object[] array = files.toArray();
    if (!Arrays.asList(array).contains(bookName)) {
      files.add(bookName);
    }
    jsonObj.put("Server" + serverNumber, files);

    //Writes the JSON object to the downloads.json file
    try (FileWriter writer = new FileWriter(path)) {
      writer.write(jsonObj.toJSONString());
    } catch (Exception e) {
      e.printStackTrace();
    }
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
