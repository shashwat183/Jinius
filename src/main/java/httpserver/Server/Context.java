package httpserver.Server;

import java.util.Map;
import java.util.Optional;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import httpserver.Http.HttpRequest;
import httpserver.Http.HttpResponse;
import httpserver.Http.HttpStatus;

public class Context {
  public final HttpRequest request;
  private HttpResponse response;
  private static final ObjectMapper mapper = new ObjectMapper();
  private Map<String, String> params;

  public Context(HttpRequest request, Map<String, String> params) {
    this.request = request;
    this.params = params;
    this.response = new HttpResponse();
  }

  public void status(HttpStatus status) {
    response.setStatus(status);
  }

  public void statusWithMessage(HttpStatus status, String message) {
    response.setStatus(status);
    response.setBody(Optional.of(message));
  }

  public void header(String key, String value) {
    response.addHeader(key, value);
  }

  public void indentedJson(HttpStatus status, Object obj) {
    try {
      this.plainJson(status, mapper.writeValueAsString(obj));
    } catch (JsonProcessingException e) {
      status(HttpStatus.InternalServerError);
    }
  }

  public void plainJson(HttpStatus status, String jsonString) {
    try {
      status(status);
      JsonNode node = mapper.readTree(jsonString);
      node.asText();
      response.addHeader("Content-Type", "application/json");
      response.setBody(Optional.of(jsonString));
    } catch (JsonProcessingException e) {
      status(HttpStatus.InternalServerError);
    }
  }

  public <T> T bindJson(Class<T> classType) throws JsonMappingException, JsonProcessingException {
    System.out.println(request.json());
    return mapper.readValue(request.json(), classType);
  }

  public void body(HttpStatus status, String body) {
      status(status);
      response.setBody(Optional.of(body));
  }

  public HttpResponse getResponse() {
    return response;
  }

  public Map<String, String> getParams() {
    return params;
  }
}
