package httpserver.Trie;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.Test;
import httpserver.Exceptions.InvalidSegmentException;
import httpserver.Http.HttpVerb;
import httpserver.Server.IHandler;
import httpserver.Server.Trie;

public class TrieTest {
  @Test
  public void addAndGetHandler() {
    Trie trie = new Trie();
    IHandler testHandler = (ctx -> {
    });
    assertDoesNotThrow(() -> trie.addHandler(HttpVerb.GET, "/v1/tasks", testHandler));
    Map<String, String> paramMap = new HashMap<>();
    IHandler foundHandler = trie.getHandler(HttpVerb.GET, "/v1/tasks", paramMap);
    assertEquals(testHandler, foundHandler);
    assertEquals(paramMap.size(), 0);
  }

  @Test
  public void addAndGetRootHandler() {
    Trie trie = new Trie();
    IHandler testHandler = (ctx -> {
    });
    assertDoesNotThrow(() -> trie.addHandler(HttpVerb.GET, "/", testHandler));
    Map<String, String> paramMap = new HashMap<>();
    IHandler foundHandler = trie.getHandler(HttpVerb.GET, "/", paramMap);
    assertEquals(testHandler, foundHandler);
    assertEquals(paramMap.size(), 0);
  }

  @Test
  public void addParamPathHandler() {
    Trie trie = new Trie();
    IHandler testHandler = (ctx -> {
    });
    assertDoesNotThrow(() -> trie.addHandler(HttpVerb.GET, "/v1/:id", testHandler));
    Map<String, String> paramMap = new HashMap<>();
    IHandler foundHandler = trie.getHandler(HttpVerb.GET, "/v1/5", paramMap);
    assertEquals(paramMap.size(), 1);
    assertTrue(paramMap.containsKey("id"));
    assertEquals(paramMap.get("id"), "5");
    assertEquals(testHandler, foundHandler);
  }

  @Test
  public void addParamPathHandlerMultParams() {
    Trie trie = new Trie();
    IHandler testHandler = (ctx -> {
    });
    assertDoesNotThrow(() -> trie.addHandler(HttpVerb.GET, "/v1/:id/hello/:status", testHandler));
    Map<String, String> paramMap = new HashMap<>();
    IHandler foundHandler = trie.getHandler(HttpVerb.GET, "/v1/5/hello/done", paramMap);
    assertEquals(paramMap.size(), 2);
    assertTrue(paramMap.containsKey("id"));
    assertEquals(paramMap.get("id"), "5");
    assertTrue(paramMap.containsKey("status"));
    assertEquals(paramMap.get("status"), "done");
    assertEquals(testHandler, foundHandler);
  }


  @Test
  public void addParamPathHandlerDupParam() {
    Trie trie = new Trie();
    IHandler testHandler = (ctx -> {
    });
    assertDoesNotThrow(() -> trie.addHandler(HttpVerb.GET, "/v1/:id", testHandler));
    assertThrows(InvalidSegmentException.class, () -> trie.addHandler(HttpVerb.GET, "/v1/:another", testHandler));
  }

  @Test
  public void addParamPathHandlerDupParamDiffVerbs() {
    Trie trie = new Trie();
    IHandler testHandler = (ctx -> {
    });
    assertDoesNotThrow(() -> trie.addHandler(HttpVerb.GET, "/v1/:id", testHandler));
    assertThrows(InvalidSegmentException.class, () -> trie.addHandler(HttpVerb.POST, "/v1/:another", testHandler));
  }

  @Test
  public void addParamPathHandlerDiffVerbs() {
    Trie trie = new Trie();
    IHandler testHandlerGet = (ctx -> {
    });
    IHandler testHandlerPost = (ctx -> {
    });
    assertDoesNotThrow(() -> trie.addHandler(HttpVerb.GET, "/v1/:id", testHandlerGet));
    assertDoesNotThrow(() -> trie.addHandler(HttpVerb.POST, "/v1/:id", testHandlerPost));
    Map<String, String> paramMapGet = new HashMap<>();
    IHandler foundHandlerGet = trie.getHandler(HttpVerb.GET, "/v1/5", paramMapGet);
    assertEquals(paramMapGet.size(), 1);
    assertTrue(paramMapGet.containsKey("id"));
    assertEquals(paramMapGet.get("id"), "5");
    assertEquals(testHandlerGet, foundHandlerGet);
    Map<String, String> paramMapPost = new HashMap<>();
    IHandler foundHandlerPost = trie.getHandler(HttpVerb.POST, "/v1/5", paramMapPost);
    assertEquals(paramMapPost.size(), 1);
    assertTrue(paramMapPost.containsKey("id"));
    assertEquals(paramMapPost.get("id"), "5");
    assertEquals(testHandlerPost, foundHandlerPost);
  }
}
