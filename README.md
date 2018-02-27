# Client-Server-Downloader-Java

Java project made to test the functionalities of sockets and how a client-server connection can be established using them. It can handle multiple connections concurrently using threads and handle download interruptions and resume of downloads.

## Installing

In order to compile it and run it you need to compile them using CLASSPATH to preload the json library.

### Client

Windows
```
javac -cp "../lib/*;." ClientThreadHandler.java ClientHandler.java MainClient.java
```

Linux
```
javac -cp "../lib/*:." ClientThreadHandler.java ClientHandler.java MainClient.java
```

### Server

Windows
```
javac -cp "../lib/*;." ActionsThreadHandler.java ServerThreadHandler.java ServerHandler.java MainServer.java
```

Linux
```
javac -cp "../lib/*:." ActionsThreadHandler.java ServerThreadHandler.java ServerHandler.java MainServer.java
```

## Executing

Windows
```
java -cp "../lib/*;." MainClient
```

```
java -cp "../lib/*;." MainServer <Port_Number>
```

Linux
```
java -cp "../lib/*:." MainClient
```

```
java -cp "../lib/*:." MainServer <Port_Number>
```
