# Client-Server-Downloader-Java

Java project made to test the functionalities of sockets and how a client-server connection can be established using them.

In order to run it you need to compile them using CLASSPATH to preload the json library.

For the client you need to run in a terminal

Windows
javac -cp "../lib/*;." ClientThreadHandler.java ClientThread.java MainClient.java

Linux
javac -cp "../lib/*:." ClientThreadHandler.java ClientThread.java MainClient.java

To execute: java -cp "../lib/*:." MainClient

For the server you need to run in a terminal

Windows
javac -cp "../lib/*;." ServerThreadHandler.java ServerThread.java MainServer.java

Linux
javac -cp "../lib/*:." ServerThreadHandler.java ServerThread.java MainServer.java

To execute: java -cp "../lib/*:." MainServer <Port_Number>

It can handle multiple connections concurrently using threads and handle download interruptions and resume of downloads.
