package httpserver.Server;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import httpserver.Exceptions.InvalidSegmentException;
import httpserver.Http.HttpStatus;
import httpserver.Http.HttpVerb;

public class Trie {
  private static final Logger logger = LoggerFactory.getLogger(Trie.class);
  private Node root;
  private String staticPrefix;
  private String staticDirectory;

  public Trie() {
    root = new Node();
  }

  public IHandler getHandler(HttpVerb method, String path, Map<String, String> params) {
    if ((staticPrefix != null) && (HttpVerb.GET.equals(method)) && (path.startsWith(staticPrefix))) {
      return getStaticHandler(path);
    }
    String[] segments = path.split("/");
    if (segments.length == 0) {
      return root.getHandler(method);
    }

    var curr = root;
    for (String segment : Arrays.asList(segments).subList(1, segments.length)) {
      var next = curr.getChild(segment);
      if (next == null) {
        if (curr.getParamKey() != null) {
          next = curr.getParamChild();
          params.put(curr.getParamKey(), segment);
        } else {
          return null;
        }
      }
      curr = next;
    }
    return curr.getHandler(method);
  }

  public void addHandler(HttpVerb method, String path, IHandler handler)
      throws InvalidSegmentException {
    String[] segments = path.split("/");
    if (segments.length == 0) {
      root.addHandler(handler, method);
      return;
    }

    var curr = root;
    for (String segment : Arrays.asList(segments).subList(1, segments.length)) {
      if (segment.contains(":")) {
        curr.addParamChild(StringUtils.stripStart(segment, ":"));
        curr = curr.getParamChild();
      } else {
        curr.addChild(segment);
        curr = curr.getChild(segment);
      }
    }
    curr.addHandler(handler, method);
  }

  public void addStaticHandler(String prefix, String directory) {
    staticPrefix = prefix;
    staticDirectory = directory;
  }

  public IHandler getStaticHandler(String path) {
    try {
      String leftover = path.replaceFirst(staticPrefix, "");
      Path filePath = Paths.get(staticDirectory + leftover);
      if (!filePath.toFile().exists()) {
        return (ctx) -> {
          ctx.status(HttpStatus.NotFound);
        };
      }
      byte[] fileBytes = Files.readAllBytes(filePath);
      String contents = new String(fileBytes);
      return (ctx) -> {
        ctx.body(HttpStatus.StatusOK, contents);
      };
    } catch (IOException e) {
      logger.error("Unexpected IOException occured while processing request", e);
      return (ctx) -> {
        ctx.status(HttpStatus.InternalServerError);
      };
    }
  }
}


class Node {
  private Map<HttpVerb, IHandler> handlers;
  private Map<String, Node> next;
  private String paramKey;
  private Node paramNode;

  public Node() {
    this.handlers = new HashMap<>();
    this.next = new HashMap<>();
  }

  public void addChild(String segment) {
    if (!next.containsKey(segment)) {
      next.put(segment, new Node());
    }
  }

  public void addParamChild(String key) throws InvalidSegmentException {
    if (key.equals(paramKey)) {
      return;
    }
    if (paramKey != null) {
      throw new InvalidSegmentException(String.format(
          "parameterized segment: want to register key %s but %s is already registered", key,
          paramKey));
    }
    paramKey = key;
    paramNode = new Node();
  }

  public Node getChild(String segment) {
    return next.get(segment);
  }

  public Node getParamChild() {
    return paramNode;
  }

  public void addHandler(IHandler handler, HttpVerb method) {
    this.handlers.put(method, handler);
  }

  public IHandler getHandler(HttpVerb method) {
    return handlers.get(method);
  }

  public void setParamKey(String paramKey) {
    this.paramKey = paramKey;
  }

  public String getParamKey() {
    return paramKey;
  }
}
