package httpserver.Server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import httpserver.Exceptions.InvalidSegmentException;
import httpserver.Http.HttpVerb;

public class Server {
  private static final Logger logger = LoggerFactory.getLogger(Server.class);
  private Trie handlers;

  public Server() {
    this.handlers = new Trie();
  }

  public void registerHandler(HttpVerb method, String path, IHandler handler) throws InvalidSegmentException {
    handlers.addHandler(method, path, handler);
  }

  public void registerStaticHanlder(String prefix, String directory) {
    handlers.addStaticHandler(prefix, directory);
  }

  public void listenAndServe(Integer port) throws IOException {
    logger.info("Listening on Port {}", port);
    try (ServerSocket serverSocket = new ServerSocket(port)) {
      while (true) {
        Socket client = serverSocket.accept();
        ServerThread thread = new ServerThread(client, handlers);
        thread.start();
      }
    }
  }
}
