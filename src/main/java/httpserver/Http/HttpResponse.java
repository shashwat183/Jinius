package httpserver.Http;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class HttpResponse {
  private String version;
  private HttpStatus status;
  private Map<String, String> headers;
  private Optional<String> body;

  public HttpResponse() {
    this.version = "HTTP/1.1";
    this.status = HttpStatus.InternalServerError;
    this.headers = new HashMap<>();
    this.body = Optional.empty();
  }

  public String toHttpMessage() {
    String message = "";
    message += version + " " + status.getStatusCode() + " " + status.getMessage() + " \r\n";
    for (Map.Entry<String, String> entry : headers.entrySet()) {
      message += entry.getKey() + ": " + entry.getValue() + "\r\n";
    }
    message += "\r\n";
    if (body.isPresent()) {
      String bodyStr = body.get();
      message += bodyStr + "\r\n";
      message += "\r\n";
    }
    return message;
  }

  public void setVersion(String version) {
    this.version = version;
  }

  public void setStatus(HttpStatus status) {
    this.status = status;
  }

  public void addHeader(String key, String value) {
    this.headers.put(key, value);
  }

  public void setBody(Optional<String> body) {
    this.body = body;
  }

  public HttpStatus getStatus() {
    return status;
  }
}
