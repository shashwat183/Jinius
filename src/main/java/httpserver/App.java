package httpserver;

import java.io.IOException;

import httpserver.Server.Server;

public class App {
  public static void main(String[] args) throws IOException {
    Server server = new Server(8080, "./assets/");
    server.run();
  }
}
