package httpserver.Http;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public record HttpResponse(String version, Integer statusCode, String statusText,
    Optional<Map<String, String>> headers, Optional<String> body) {

  private static final String httpVersion = "HTTP/1.1";

  public static HttpResponse methodNotFound(List<String> allowedMethods) {
    Map<String, String> headers = new HashMap<>();
    headers.put("Allow", String.join(",", allowedMethods));
    return new HttpResponse(httpVersion, 405, "Method Not Allowed", Optional.of(headers), Optional.empty());
  }

  public static HttpResponse notFound(Optional<Map<String, String>> headers) {
    return new HttpResponse(httpVersion, 404, "Not Found", headers, Optional.empty());
  }

  public static HttpResponse ok(Optional<Map<String, String>> headers, Optional<String> body) {
    return new HttpResponse(httpVersion, 200, "OK", headers, body);
  }

  public String toHttpMessage() {
    String message = "";
    message += version + " " + statusCode + " " + statusText + " \r\n";
    if (headers.isPresent()) {
      Map<String, String> headersMap = headers.get();
      for (Map.Entry<String, String> entry: headersMap.entrySet()) {
        message += entry.getKey() + ": " + entry.getValue() + "\r\n";
      }
    }
    message += "\r\n";
    if (body.isPresent()) {
      String bodyStr = body.get();
      message += bodyStr + "\r\n";
      message += "\r\n";
    }
    return message;
  }
}
