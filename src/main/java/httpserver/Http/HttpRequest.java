package httpserver.Http;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public record HttpRequest(String method, String resource, String version,
    Map<String, String> headers) {

  public static HttpRequest fromRequestLines(ArrayList<String> requestLines) {
    String reqLine = requestLines.get(0);
    String method = reqLine.split(" ")[0];
    String resource = reqLine.split(" ")[1];
    String version = reqLine.split(" ")[2];
    Map<String, String> headers = new HashMap<>();
    for (String headerLine : requestLines.subList(2, requestLines.size())) {
      String[] split = headerLine.split(":", 2);
      headers.put(split[0].trim(), split[1].trim());
    }

    return new HttpRequest(method, resource, version, headers);
  }
}
