package httpserver.Http;

public enum HttpStatus {
  NotFound(404, "Not Found"),
  InternalServerError(500, "Internal Server Error"),
  BadRequest(400, "Bad Request"),
  StatusOK(200, "OK"),
  StatusCreated(201, "Created"),
  Unauthorized(401, "Unauthorized"),
  MethodNotAllowed(405, "Method Not Allowed");

  private final int code;
  private final String message;
  
  HttpStatus(int code, String message) {
    this.code = code;
    this.message = message;
  }

  public int getStatusCode() {
    return code;
  }

  public String getMessage() {
    return message;
  }
}
