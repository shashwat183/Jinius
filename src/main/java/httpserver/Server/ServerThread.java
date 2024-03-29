package httpserver.Server;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import httpserver.Http.HttpRequest;
import httpserver.Http.HttpStatus;

public class ServerThread extends Thread {
  private static final Logger logger = LoggerFactory.getLogger(ServerThread.class);
  private Socket client;
  private Trie handlers;

  public ServerThread(Socket client, Trie handlers) {
    this.client = client;
    this.handlers = handlers;
  }

  @Override
  public void run() {
    try {
      HttpRequest request = HttpRequest.fromClient(client);
      if (request == null) {
        logger.error("Malformed Http request recieved");
        client.close();
        return;
      }
      Map<String, String> params = new HashMap<>();
      IHandler handler = handlers.getHandler(request.method(), request.path(), params);
      Context ctx = new Context(request, params);
      if (handler == null) {
        logger.error("no handler found for requested resource");
        ctx.status(HttpStatus.NotFound);
        sendResponse(client, ctx);
        return;
      }
      handler.handle(ctx);
      sendResponse(client, ctx);
    } catch (IOException e) {
      logger.error("Unexpected IOException occured while processing request", e);
    }
  }

  private void sendResponse(Socket client, Context ctx) throws IOException {
    OutputStream output = client.getOutputStream();
    logger.info("{} {} {}", ctx.request.method(), ctx.request.path(),
        ctx.getResponse().getStatus().getStatusCode());
    output.write(ctx.getResponse().toHttpMessage().getBytes());
    output.flush();
    output.close();
  }
}
