package com.project.exception;

public class BasketEmptyException extends RuntimeException {

  public BasketEmptyException(String message) {
    super(message);
  }
}
