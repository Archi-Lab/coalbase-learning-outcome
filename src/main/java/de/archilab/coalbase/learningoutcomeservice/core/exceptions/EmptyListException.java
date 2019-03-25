package de.archilab.coalbase.learningoutcomeservice.core.exceptions;

public class EmptyListException extends RuntimeException {

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
