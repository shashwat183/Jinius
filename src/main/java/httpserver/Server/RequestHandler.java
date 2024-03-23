package httpserver.Server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import httpserver.Http.HttpRequest;
import httpserver.Http.HttpResponse;

public class RequestHandler implements Runnable {
  Socket client;
  String assetsDir;

  public RequestHandler(Socket client, String assetsDir) {
    this.assetsDir = assetsDir;
    this.client = client;
  }

  @Override
  public void run() {
    try {
      HttpRequest request = parseRequest();
      HttpResponse response = handleRequest(request);
      sendResponse(client, response);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private HttpRequest parseRequest() throws IOException {
    BufferedReader bufferedReader =
        new BufferedReader(new InputStreamReader(client.getInputStream()));
    ArrayList<String> requestLines = new ArrayList<>();
    String line;
    while (!(line = bufferedReader.readLine()).isBlank()) {
      requestLines.add(line);
    }
    return HttpRequest.fromRequestLines(requestLines);
  }

  private HttpResponse handleRequest(HttpRequest request) throws IOException {
    if ("GET".equals(request.method())) { // for now, we only support GET requests
      return handleGet(request);
    }
    // if ("POST".equals(request.method())) {
    // return handlePost(request);
    // }
    return HttpResponse.methodNotFound(List.of("GET"));
  }

  private HttpResponse handleGet(HttpRequest request) throws IOException {
    String resource;
    if ("/".equals(request.resource())) {
      resource = "/index.html";
    } else {
      resource = request.resource();
    }
    Path path = Paths.get(assetsDir, resource);
    if (Files.exists(path)) {
      Map<String, String> headers = new HashMap<>();
      headers.put("Content-Type", Files.probeContentType(path));
      return HttpResponse.ok(Optional.of(headers), Optional.of(Files.readString(path)));
    } else {
      return HttpResponse.notFound(Optional.empty());
    }
  }

  // private HttpResponse handlePost(HttpRequest request) {
  // }

  private void sendResponse(Socket client, HttpResponse response) throws IOException {
    OutputStream output = client.getOutputStream();
    output.write(response.toHttpMessage().getBytes());
    output.flush();
    output.close();
  }
}
