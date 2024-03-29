package httpserver.Http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.json.JsonMapper;

public record HttpRequest(HttpVerb method, String path, String version,
    Optional<Map<String, String>> query, Map<String, String> headers, Optional<String> body) {
  private static final Logger logger = LoggerFactory.getLogger(HttpRequest.class);

  public static HttpRequest fromClient(Socket client) throws IOException {
    BufferedReader bufferedReader =
        new BufferedReader(new InputStreamReader(client.getInputStream()));

    // read request line
    String reqLine = bufferedReader.readLine();
    if (reqLine == null) {
      return null;
    }
    String methodStr = reqLine.split(" ")[0];
    HttpVerb method;
    try {
      method = HttpVerb.valueOf(methodStr);
    } catch (IllegalArgumentException e) {
      logger.error("Http verb in request is malformed", e);
      return null;
    }
    String target = reqLine.split(" ")[1];
    String version = reqLine.split(" ")[2];

    // parse query params if any
    Optional<Map<String, String>> query;
    String path;
    if ((target.split("\\?").length) > 1) {
      Map<String, String> queryMap = new HashMap<>();
      String[] targetSplit = target.split("\\?");
      path = targetSplit[0];
      String queryLine = targetSplit[1];
      for (String queryString : queryLine.split("&")) {
        String[] quertStringSplit = queryString.split("=");
        queryMap.put(quertStringSplit[0], quertStringSplit[1]);
      }
      query = Optional.of(queryMap);
    } else {
      query = Optional.empty();
      path = StringUtils.stripEnd(target, "/");
    }

    // read headers, according to the HTTP/1.1 RFC 7230, Section 3.1.1,
    // an HTTP request message must contain at least one header field
    bufferedReader.readLine();
    Map<String, String> headers = new HashMap<>();
    String headerLine;
    while (!(headerLine = bufferedReader.readLine()).isBlank()) {
      String[] split = headerLine.split(":", 2);
      headers.put(split[0].trim(), split[1].trim());
    }

    // read body content stored as string if present
    if (bufferedReader.ready()) {
      StringBuilder stringBuilder = new StringBuilder();
      while (bufferedReader.ready()) {
        stringBuilder.append((char) bufferedReader.read());
      }
      return new HttpRequest(method, path, version, query, headers,
          Optional.of(stringBuilder.toString()));
    } else {
      return new HttpRequest(method, path, version, query, headers, Optional.empty());
    }

  }

  public String json() throws JsonMappingException, JsonProcessingException {
    if (body.isEmpty()) {
      return null;
    }
    // verify body content is in json format
    JsonMapper mapper = new JsonMapper();
    String content = body.get();
    mapper.readTree(content);
    return content;
  }
}
