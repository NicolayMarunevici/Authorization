package com.auth.moto.exception;

import org.springframework.http.HttpStatus;

public class MotoSharingException extends RuntimeException{

  private HttpStatus status;

  public MotoSharingException(String message, HttpStatus status) {
    super(message);
    this.status = status;
  }
}
