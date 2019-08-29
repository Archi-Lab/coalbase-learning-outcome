package io.archilab.coalbase.learningoutcomeservice.core.exceptions;

public class EmptyListException extends RuntimeException {

  private static final long serialVersionUID = 765193190812354703L;

  public EmptyListException() {
    super();
  }

  public EmptyListException(String message) {
    super(message);
  }

  public EmptyListException(String message, Throwable cause) {
    super(message, cause);
  }

  public EmptyListException(Throwable cause) {
    super(cause);
  }

}
