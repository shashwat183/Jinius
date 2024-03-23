package httpserver.Server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server implements Runnable {
  Integer port;
  String assetsDir;

  public Server(Integer port, String assetsDir) {
    this.port = port;
    this.assetsDir = assetsDir;
  }

  @Override
  public void run() {
    try {
      this.startServer();
    } catch (IOException e) {
      System.out.println("Failed starting server on port: " + port + e.toString());
    }
  }

  private void startServer() throws IOException {
    try (ServerSocket serverSocket = new ServerSocket(port)) {
      while (true) {
        Socket client = serverSocket.accept();
        Thread thread = new Thread(new RequestHandler(client, assetsDir));
        thread.start();
      }
    }
  }

}
